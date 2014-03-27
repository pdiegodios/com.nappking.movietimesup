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
import com.nappking.movietimesup.adapter.ScoreboardEntryAdapter;
import com.nappking.movietimesup.widget.ListView3d;
import com.facebook.model.GraphObject;
import com.facebook.widget.ProfilePictureView;

/**
 *  Fragment shown once a user opens the scoreboard
 */
public class ScoreboardFragment extends Fragment {
	private MovieTimesUpApplication application;    
	private ListView3d scoreboardList;
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
		scoreboardList = (ListView3d)v.findViewById(android.R.id.list);
		progressContainer = (FrameLayout)v.findViewById(R.id.progressContainer);
		
		pictureFirst = (ProfilePictureView)v.findViewById(R.id.userImageFirst);
		nameFirst = (TextView)v.findViewById(R.id.userNameFirst);
		pointsFirst = (TextView)v.findViewById(R.id.userPointsFirst);

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
		progressContainer.setVisibility(View.VISIBLE);
		fetchScoreboardEntries();
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
	

	private void fetchScoreboardEntries () {
		// Fetch the scores ...		
		
		if(Session.getActiveSession()!=null){
			// Get the attributes used for the HTTP GET
			//String currentUserFBID = application.getCurrentFBUser().getId();
			//String currentUserAccessToken = Session.getActiveSession().getAccessToken();
			Request getScoreRequest = new Request(Session.getActiveSession(),
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
					                int score = json.getInt("score");
					                JSONObject user = json.getJSONObject("user");
					                String id = user.getString("id");
					                String name = user.getString("name");
					                ScoreboardEntry entry = new ScoreboardEntry(id, name, score);
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
		scoreboardList.removeAllViewsInLayout();
		progressContainer.setVisibility(View.INVISIBLE);
		
		// User id to show a different View
		//TODO: Star user
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
			application.getFriendlyUserList().add(entry);
			application.getFriendlyUserList().add(entry2);
			application.getFriendlyUserList().add(entry);
			
			scoreboardList.setAdapter(new ScoreboardEntryAdapter(this.getActivity(),application.getFriendlyUserList()));
		}
	}
}
