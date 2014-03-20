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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.nappking.movietimesup.R;
import com.nappking.movietimesup.entities.User;
import com.facebook.model.GraphUser;
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
	
	// Handler for putting messages on Main UI thread from background thread after fetching the scores
	private Handler uiHandler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		application = (MovieTimesUpApplication) getActivity().getApplication();
		
		// Instantiate the handler
		uiHandler = new Handler();
		
		setRetainInstance(true);
	}
	
	@TargetApi(13)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_scoreboard, parent, false);
		
		scoreboardContainer = (LinearLayout)v.findViewById(R.id.scoreboardContainer);
		progressContainer = (FrameLayout)v.findViewById(R.id.progressContainer);

		// Set the progressContainer as invisible by default
		progressContainer.setVisibility(View.INVISIBLE);
		
		// Note: Scoreboard is populated during onResume below
		
		return v;
	}
	
	// Close the game and show the specified error to the user
	private void closeAndShowError(String error) {
		Bundle bundle = new Bundle();
		bundle.putString("error", error);
		
		Intent i = new Intent();
		i.putExtras(bundle);
		
		getActivity().setResult(Activity.RESULT_CANCELED, i);
		getActivity().finish();
	}
	
	@Override
	public void onResume() {
		super.onResume();

		// Populate scoreboard - fetch information if necessary ...
		if (application.getFriendlyUserList() == null) {
			// scoreboardEntriesList is null, so fetch the information from Facebook (scoreboard will be updated in
			// the scoreboardEntriesFetched callback) and show the progress spinner while doing so
			progressContainer.setVisibility(View.VISIBLE);
			fetchScoreboardEntries();
		} else {
			// Information has already been fetched, so populate the scoreboard
			populateScoreboard();
		}
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
		// Fetch the scores ...
		AsyncTask.execute(new Runnable() {
			public void run() {
				try {
					List<User> users = new ArrayList<User>();
					
					// Get the attributes used for the HTTP GET
					String currentUserFBID = application.getCurrentFBUser().getId();
					String currentUserAccessToken = Session.getActiveSession().getAccessToken();

					HttpClient httpclient = new DefaultHttpClient();
					HttpPost post = new HttpPost("http://movietimesup.gestores.cloudbees.net/rest/users/scores");
					
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					for(GraphUser graph:application.getFriends()){
						nameValuePairs.add(new BasicNameValuePair("id", graph.getId()));
					}       
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        // Execute HTTP Post Request
			        HttpResponse response = httpclient.execute(post);
			        
				    //RESPONSE
				    Log.i("Response --> ", inputStreamToString(response.getEntity().getContent()));
				    
					/*
					// Execute the HTTP Get to our server for the scores of the user's friends
					HttpClient client = new DefaultHttpClient();
					String getURL = "http://www.friendsmash.com/scores?fbid=" + currentUserFBID + "&access_token=" + currentUserAccessToken;
					HttpGet get = new HttpGet(getURL);
					HttpResponse responseGet = client.execute(get);
					
					// Parse the response
					HttpEntity responseEntity = responseGet.getEntity();
					String response = EntityUtils.toString(responseEntity);
					if (!response.equals(null)) {
						JSONArray responseJSONArray = new JSONArray(response);
						
						// Go through the response JSON Array to populate the scoreboard
						if (responseJSONArray != null && responseJSONArray.length() > 0) {
							
							// Loop through all users that have been retrieved
							for (int i=0; i<responseJSONArray.length(); i++) {
								// Store the user details in the following attributes
								String userID = null;
								String userName = null;
								int userScore = -1;
								
								// Extract the user information
								JSONObject currentUser = responseJSONArray.optJSONObject(i);
								if (currentUser != null) {
									userID = currentUser.optString("fbid");
									userName = currentUser.optString("first_name");
									String fetchedScoreAsString = currentUser.optString("highscore");
									if (fetchedScoreAsString != null) {
										userScore = Integer.parseInt(fetchedScoreAsString);
									}
									if (userID != null && userName != null && userScore >= 0) {
										// All attributes have been successfully fetched, so create a new
										// ScoreboardEntry and add it to the List
										ScoreboardEntry currentUserScoreboardEntry =
												new ScoreboardEntry(userID, userName, userScore);
										scoreboardEntriesList.add(currentUserScoreboardEntry);
									}
								}
							}
						}
					}*/
				
					// Now that all scores should have been fetched and added to the scoreboardEntriesList, sort it,
					// set it within scoreboardFragment and then callback to scoreboardFragment to populate the scoreboard
					Comparator<ScoreboardEntry> comparator = Collections.reverseOrder();
					//Collections.sort(scoreboardEntriesList, comparator);
					//application.setFriendlyUserList(scoreboardEntriesList);
					
					// Populate the scoreboard on the UI thread
					uiHandler.post(new Runnable() {
						@Override
						public void run() {
							populateScoreboard();
						}
					});
				} catch (Exception e) {
					Log.e(MovieTimesUpApplication.TAG, e.toString());
					closeAndShowError(getResources().getString(R.string.network_error));
				}
			}
		});
	}

	private void populateScoreboard() {
		// Ensure all components are firstly removed from scoreboardContainer
		scoreboardContainer.removeAllViews();
		
		// Ensure the progress spinner is hidden
		progressContainer.setVisibility(View.INVISIBLE);
		
		// Ensure scoreboardEntriesList is not null and not empty first
		if (application.getFriendlyUserList() == null || application.getFriendlyUserList().size() <= 0) {
			closeAndShowError(getResources().getString(R.string.error_no_scores));
		} else {
			// Iterate through scoreboardEntriesList, creating new UI elements for each entry
			int index = 0;
			Iterator<User> userIterator = application.getFriendlyUserList().iterator();
			while (userIterator.hasNext()) {
				// Get the current scoreboard entry
				final User currentUser = userIterator.next();
				
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
					int position = index+1;
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
				    scoreTextView.setText("Score: " + currentUser.getScore());
				    scoreTextView.setTextAppearance(getActivity(), R.style.ScoreboardPlayerScoreFont);
				}
				
				// Finally make this frameLayout clickable so that a game starts with the user smashing
				// the user represented by this frameLayout in the scoreContainer
				frameLayout.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							Bundle bundle = new Bundle();
							bundle.putString("user_id", currentUser.getId()+"");

							Intent i = new Intent();
							i.putExtras(bundle);
						
							getActivity().setResult(Activity.RESULT_FIRST_USER , i);
							getActivity().finish();
							return false;
						} else {
							return true;
						}
					}
					
				});
			    
			    // Increment the index before looping back
				index++;
			}
		}
	}
}
