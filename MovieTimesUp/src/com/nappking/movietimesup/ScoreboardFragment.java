package com.nappking.movietimesup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.nappking.movietimesup.R;
import com.facebook.model.GraphObject;
import com.facebook.widget.ProfilePictureView;

/**
 *  Fragment shown once a user opens the scoreboard
 */
public class ScoreboardFragment extends Fragment {
	
    // Store the Application (as you can't always get to it when you can't access the Activity - e.g. during rotations)
	private MovieTimesUpApplication application;    
	// LinearLayout as the container for the scoreboard entries
	private LinearLayout scoreboardContainer;	
	// FrameLayout of the progress container to show the spinner
	private FrameLayout progressContainer;
	private ProfilePictureView pictureFirst;
	private TextView nameFirst;
	private TextView pointsFirst;
	
	// Handler for putting messages on Main UI thread from background thread after fetching the scores
	private Handler uiHandler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(getActivity().toString(), "OnCreate");
		super.onCreate(savedInstanceState);		
		application = (MovieTimesUpApplication) getActivity().getApplication();		
		// Instantiate the handler
		uiHandler = new Handler();		
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		Log.i(getActivity().toString(), "OnCreateView");		
		View v = inflater.inflate(R.layout.fragment_scoreboard, parent, false);		
		scoreboardContainer = (LinearLayout)v.findViewById(R.id.scoreboardContainer);
		progressContainer = (FrameLayout)v.findViewById(R.id.progressContainer);
		
		pictureFirst = (ProfilePictureView)v.findViewById(R.id.userImageFirst);
		nameFirst = (TextView)v.findViewById(R.id.userNameFirst);
		pointsFirst = (TextView)v.findViewById(R.id.userPointsFirst);

		// Set the progressContainer as invisible by default
		progressContainer.setVisibility(View.INVISIBLE);
		
		return v;
	}
	
	// Close the game and show the specified error to the user
	private void closeAndShowError(String error) {
		Bundle bundle = new Bundle();
		bundle.putString("error", error);
		
		Intent i = new Intent();
		i.putExtras(bundle);
		getActivity().finish();
	}
	
	@Override
	public void onResume() {
		Log.i(getActivity().toString(), "onResume");		
		super.onResume();
		// Populate scoreboard - fetch information if necessary ...
		//if (application.getFriendlyUserList() == null) {
			progressContainer.setVisibility(View.VISIBLE);
			fetchScoreboardEntries();
		//} else {
			// Information has already been fetched, so populate the scoreboard
		//	populateScoreboard();
		//}
	}	
	 
    private String inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            // Read response until the end
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
        	e.printStackTrace();
        }
        // Return full string
        return total.toString();
    }
	
	// Fetch a List of ScoreboardEntry objects with the scores and details
	// of the user and their friends' scores who have played FriendSmash
	private void fetchScoreboardEntries () {
		Log.i(getActivity().toString(), "fetchScoreBoardEntries");
		// Fetch the scores ...		
		
		if(Session.getActiveSession()!=null){
			// Get the attributes used for the HTTP GET
			//String currentUserFBID = application.getCurrentFBUser().getId();
			//String currentUserAccessToken = Session.getActiveSession().getAccessToken();
			Request getScoreRequest = new Request(Session.getActiveSession(),
					//mUser.getUser()+"/scores",
					getResources().getString(R.string.app_id)+"/scores",
					null,
	                HttpMethod.GET,
	                new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							//TODO: Make WORK
					        Log.d("log_tag", "response: " + response.toString());
					        try{
					            GraphObject graphObject  = response.getGraphObject();
					            JSONObject  innerJson = graphObject.getInnerJSONObject();
					            JSONArray   jsonArray = innerJson.getJSONArray( "data" );
					            List<ScoreboardEntry> entries = new ArrayList<ScoreboardEntry>();
					            for (int i=0; i <(jsonArray.length()); i++){
					                JSONObject json = jsonArray.getJSONObject( i );
					                int score     = json.getInt("score");
					                JSONObject user   = json.getJSONObject("user");
					                String id = user.getString("id");
					                String name = user.getString("name");
					                ScoreboardEntry entry = new ScoreboardEntry(id, name, score);
					                Log.i("Element", "id:"+id+" name:"+name+" score:"+score);
					                entries.add(entry);
					            }
					    		Comparator<ScoreboardEntry> comparator = Collections.reverseOrder();
					    		Collections.sort(entries, comparator);
					            application.setFriendlyUserList(entries);					    		
					    		// Populate the scoreboard on the UI thread
					    		uiHandler.post(new Runnable() {
					    			@Override
					    			public void run() {
					    				populateScoreboard();
					    			}
					    		});
					        }
					        catch ( Throwable t ){
					            t.printStackTrace();
					        }
							FacebookRequestError error = response.getError();
							if (error != null) {
								Log.e(MovieTimesUpApplication.TAG, "Getting Scoreboard to Facebook failed: " + error.getErrorMessage());
							} else {
								Log.i(MovieTimesUpApplication.TAG, "Scoreboard got successfully to Facebook");
							}
						}
					});
			Request.executeBatchAsync(getScoreRequest);
		}
	}

	private void populateScoreboard() {
		// Ensure all components are firstly removed from scoreboardContainer
		scoreboardContainer.removeAllViews();		
		// Ensure the progress spinner is hidden
		progressContainer.setVisibility(View.INVISIBLE);
		// User id to show a different View
		String userFBID = application.getCurrentFBUser().getId(); 
		// Ensure scoreboardEntriesList is not null and not empty first
		if (application.getFriendlyUserList() == null || application.getFriendlyUserList().size() <= 0) {
			closeAndShowError(getResources().getString(R.string.error_no_scores));
		} else {
			// Iterate through scoreboardEntriesList, creating new UI elements for each entry
			int index = 0;
			ScoreboardEntry first = application.getFriendlyUserList().remove(0);
			pictureFirst.setProfileId(first.getId());
			pictureFirst.setCropped(true);
			pointsFirst.setText(getResources().getString(R.string.points)+": "+first.getScore());
			nameFirst.setText(first.getName());
			
			
			//EXAMPLE
			ScoreboardEntry entry = new ScoreboardEntry("1040076773", "Lino Villar Martinez", 41);
			ScoreboardEntry entry2 = new ScoreboardEntry("100002705642205", "Pablo Diego Dios", 25);
			//ScoreboardEntry entry = new ScoreboardEntry("1040076773", "Lino Villar Martinez", 41);
			application.getFriendlyUserList().add(entry);
			application.getFriendlyUserList().add(entry2);
			
			
			Iterator<ScoreboardEntry> userIterator = application.getFriendlyUserList().iterator();
			while (userIterator.hasNext()) {
				// Get the current scoreboard entry
				final ScoreboardEntry currentUser = userIterator.next();				
				// FrameLayout Container for the currentScoreboardEntry ...				
				// Create and add a new FrameLayout to display the details of this entry
				FrameLayout frameLayout = new FrameLayout(getActivity());
				scoreboardContainer.addView(frameLayout);
				
				// Set the attributes for this frameLayout
				int topPadding = getResources().getDimensionPixelSize(R.dimen.scoreboard_entry_top_margin);
				frameLayout.setPadding(0, topPadding, 0, 0);
				
				// ImageView background image ...
				{
					// Create and add an ImageView for the background image to this entry
					ImageView backgroundImageView = new ImageView(getActivity());
					frameLayout.addView(backgroundImageView);
					
					// Set the image of the backgroundImageView
					String uri = "drawable/scores_stub_even";
					if (index % 2 != 0) {
						// Odd entry
						uri = "drawable/scores_stub_odd";
					}
				    int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
				    Drawable image = getResources().getDrawable(imageResource);
				    backgroundImageView.setImageDrawable(image);
					
				    // Other attributes of backgroundImageView to modify
				    FrameLayout.LayoutParams backgroundImageViewLayoutParams = new FrameLayout.LayoutParams(
				    		FrameLayout.LayoutParams.WRAP_CONTENT,
				    		FrameLayout.LayoutParams.WRAP_CONTENT);
				    int backgroundImageViewMarginTop = getResources().getDimensionPixelSize(R.dimen.scoreboard_background_imageview_margin_top);
				    backgroundImageViewLayoutParams.setMargins(0, backgroundImageViewMarginTop, 0, 0);
				    backgroundImageViewLayoutParams.gravity = Gravity.LEFT;
					if (index % 2 != 0) {
						// Odd entry
						backgroundImageViewLayoutParams.gravity = Gravity.RIGHT;
					}
					backgroundImageView.setLayoutParams(backgroundImageViewLayoutParams);
				}
				
			    // ProfilePictureView of the current user ...
				{
				    // Create and add a ProfilePictureView for the current user entry's profile picture
				    ProfilePictureView profilePictureView = new ProfilePictureView(getActivity());
				    frameLayout.addView(profilePictureView);
				    
				    // Set the attributes of the profilePictureView
				    int profilePictureViewWidth = getResources().getDimensionPixelSize(R.dimen.scoreboard_profile_picture_view_width);
				    FrameLayout.LayoutParams profilePictureViewLayoutParams = new FrameLayout.LayoutParams(profilePictureViewWidth, profilePictureViewWidth);
				    int profilePictureViewMarginLeft = 0;
				    int profilePictureViewMarginTop = getResources().getDimensionPixelSize(R.dimen.scoreboard_profile_picture_view_margin_top);
				    int profilePictureViewMarginRight = 0;
				    int profilePictureViewMarginBottom = 0;
				    if (index % 2 == 0) {
				    	profilePictureViewMarginLeft = getResources().getDimensionPixelSize(R.dimen.scoreboard_profile_picture_view_margin_left);
					} else {
						profilePictureViewMarginRight = getResources().getDimensionPixelSize(R.dimen.scoreboard_profile_picture_view_margin_right);
					}
				    profilePictureViewLayoutParams.setMargins(profilePictureViewMarginLeft, profilePictureViewMarginTop,
				    		profilePictureViewMarginRight, profilePictureViewMarginBottom);
				    profilePictureViewLayoutParams.gravity = Gravity.LEFT;
					if (index % 2 != 0) {
						// Odd entry
						profilePictureViewLayoutParams.gravity = Gravity.RIGHT;
					}
					profilePictureView.setLayoutParams(profilePictureViewLayoutParams);
				    
				    // Finally set the id of the user to show their profile pic
				    profilePictureView.setProfileId(currentUser.getId()+"");
				}
				
				// LinearLayout to hold the text in this entry
				
				// Create and add a LinearLayout to hold the TextViews
				LinearLayout textViewsLinearLayout = new LinearLayout(getActivity());
				frameLayout.addView(textViewsLinearLayout);
				
				// Set the attributes for this textViewsLinearLayout
				FrameLayout.LayoutParams textViewsLinearLayoutLayoutParams = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.WRAP_CONTENT,
						FrameLayout.LayoutParams.WRAP_CONTENT);
				int textViewsLinearLayoutMarginLeft = 0;
			    int textViewsLinearLayoutMarginTop = getResources().getDimensionPixelSize(R.dimen.scoreboard_textviews_linearlayout_margin_top);
			    int textViewsLinearLayoutMarginRight = 0;
			    int textViewsLinearLayoutMarginBottom = 0;
			    if (index % 2 == 0) {
			    	textViewsLinearLayoutMarginLeft = getResources().getDimensionPixelSize(R.dimen.scoreboard_textviews_linearlayout_margin_left);
				} else {
					textViewsLinearLayoutMarginRight = getResources().getDimensionPixelSize(R.dimen.scoreboard_textviews_linearlayout_margin_right);
				}
			    textViewsLinearLayoutLayoutParams.setMargins(textViewsLinearLayoutMarginLeft, textViewsLinearLayoutMarginTop,
			    		textViewsLinearLayoutMarginRight, textViewsLinearLayoutMarginBottom);
			    textViewsLinearLayoutLayoutParams.gravity = Gravity.LEFT;
				if (index % 2 != 0) {
					// Odd entry
					textViewsLinearLayoutLayoutParams.gravity = Gravity.RIGHT;
				}
				textViewsLinearLayout.setLayoutParams(textViewsLinearLayoutLayoutParams);
				textViewsLinearLayout.setOrientation(LinearLayout.VERTICAL);
				
				// TextView with the position and name of the current user
				{
					// Set the text that should go in this TextView first
					int position = index+2;
					String currentScoreboardEntryTitle = position + ". " + currentUser.getName();
					
					// Create and add a TextView for the current user position and first name
				    TextView titleTextView = new TextView(getActivity());
				    textViewsLinearLayout.addView(titleTextView);
				    
				    // Set the text and other attributes for this TextView
				    titleTextView.setText(currentScoreboardEntryTitle);
				    titleTextView.setTextAppearance(getActivity(), R.style.ScoreboardPlayerNameFont);
				}
				
				// TextView with the score of the current user
				{
					// Create and add a TextView for the current user score
				    TextView scoreTextView = new TextView(getActivity());
				    textViewsLinearLayout.addView(scoreTextView);
				    
				    // Set the text and other attributes for this TextView
				    scoreTextView.setText(getResources().getString(R.string.points)+": "+ currentUser.getScore());
				    scoreTextView.setTextAppearance(getActivity(), R.style.ScoreboardPlayerScoreFont);
				}			    
			    // Increment the index before looping back
				index++;
			}
		}
	}
}
