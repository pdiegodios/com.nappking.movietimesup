package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.nappking.movietimesup.R;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.facebook.widget.WebDialog;
import com.facebook.widget.LoginButton.OnErrorListener;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.database.DBHelper;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.task.LoadUserDataTask;

/**
 *  Fragment to be shown once the user is logged in on the social version of the game or
 *  the start screen for the non-social version of the game
 */
public class HomeFragment extends Fragment {	
	// Store the Application 
    private MovieTimesUpApplication application;    
    
    View progressContainer;
    private AnimationDrawable billboard;
    private ProfilePictureView userImage;
    private TextView userPoints;
    private TextView userLevel;
    private TextView userSeconds;
    private TextView userMovies;

	// Buttons ...
    private ImageView redboxButton;
    private ImageView playButton;
    private ImageView scoresButton;
    private ImageView loginButton;
    private LoginButton login;

	// Parameters of a WebDialog that should be displayed
    private WebDialog dialog = null;
    private String dialogAction = null;
    private Bundle dialogParams = null;

	// Attributes for posting back to Facebook
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final String PENDING_POST_KEY = "pendingPost";
	private boolean pendingPost = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		application = (MovieTimesUpApplication) getActivity().getApplication();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v;		
		v = inflater.inflate(R.layout.fragment_home, parent, false);	
		// Set an error listener for the login button
		login = (LoginButton) v.findViewById(R.id.login);	
		loginButton = (ImageView) v.findViewById(R.id.loginButton);	

		progressContainer = (FrameLayout)v.findViewById(R.id.progressContainer);
     	
     	//User Info
		userImage = (ProfilePictureView) v.findViewById(R.id.userImage);
		userImage.setVisibility(View.INVISIBLE);	
		userPoints = (TextView)v.findViewById(R.id.userPoints);
		userMovies = (TextView)v.findViewById(R.id.userMovies);
		userSeconds = (TextView)v.findViewById(R.id.userSeconds);
		userLevel = (TextView)v.findViewById(R.id.userLevel);	
		
		//Buttons
     	redboxButton = (ImageView) v.findViewById(R.id.redbox);
		scoresButton = (ImageView)v.findViewById(R.id.scoresButton);
		playButton = (ImageView)v.findViewById(R.id.playButton);
		
		//Animations
     	billboard = (AnimationDrawable) v.findViewById(R.id.billboard).getBackground();
     	
		// Personalize this HomeFragment
		personalizeHomeFragment();	
		setListeners();		
		updateButtonVisibility();
		
		// Restore the state
		restoreState(savedInstanceState);
		
		return v;
	}

	private void setListeners(){
		redboxButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
			public boolean onTouch(View v, MotionEvent event) {
            	onRedboxButtonTouched();
				return false;
			}
        });		
		scoresButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
			public boolean onTouch(View v, MotionEvent event) {
            	onScoresButtonTouched();
				return false;
			}
        });					
		playButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
			public boolean onTouch(View v, MotionEvent event) {
            	onPlayButtonTouched();
				return false;
			}
        });	
		
		login.setOnErrorListener(new OnErrorListener() {	
			@Override
			public void onError(FacebookException error) {
				if (error != null && !(error instanceof FacebookOperationCanceledException)) {
					// Failed probably due to network error (rather than user canceling dialog which would throw a FacebookOperationCanceledException)
					((HomeActivity)getActivity()).showError(getResources().getString(R.string.network_error), false);
				}
			}
			
		});
		
		loginButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				login.performClick();
			}
		});
	}
	
	private void checkUser(){
		//If there is not user associated, one is created in the local database
		DBHelper helper = OpenHelperManager.getHelper(application.getBaseContext(), DBHelper.class);
		try {
			Dao <User,Integer> daoUser = helper.getUserDAO();
			User user = daoUser.queryForId(1);
			if(user==null && application.getCurrentFBUser()!=null){
				Dao <Movie,Integer> daoMovie = helper.getMovieDAO();
				int moviesCount = daoMovie.queryForAll().size();
				user = new User();
				user.setName(application.getCurrentFBUser().getName());
				user.setLockedMovies(new ArrayList<String>());
				user.setUnlockedMovies(new ArrayList<String>());
				user.setLastUpdate(Long.valueOf("10"));
				user.setLastForeground(Long.valueOf("10"));
				user.setDays(0);
				user.setScore(0);
				user.setSeconds(MovieTimesUpApplication.SECONDS_FOR_LEVEL);
				user.setMovies(moviesCount);
				user.setWildcard(1);
				user.setMasterpiece(0);
				user.setMasterpieceSold(0);
				user.setCult(0);
				user.setCultSold(0);
				user.setAmerica(0);
				user.setEurope(0);
				user.setAsia(0);
				user.setExotic(0);
				user.setPassword("");
				user.setEmail("");
				user.setUser(application.getCurrentFBUser().getId());
				daoUser.create(user);
				Log.i("HomeFragment", "LoadUserData called");
				new LoadUserDataTask(this.getActivity(), user).execute();
			}
			helper = null;
			OpenHelperManager.releaseHelper();
		} catch (SQLException e) {
			e.printStackTrace();
			helper = null;
            OpenHelperManager.releaseHelper();
		}
	}

	// Personalize this HomeFragment (social-version only)
	void personalizeHomeFragment() {
		checkUser();  
		if (application.getCurrentFBUser() != null) {	
			if(userImage!=null){
	            userImage.setProfileId(application.getCurrentFBUser().getId());
	            userImage.setCropped(true);
				userImage.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void updatePoints(){
		DBHelper helper = OpenHelperManager.getHelper(application.getBaseContext(), DBHelper.class);
		try {
			Dao <User,Integer> daoUser = helper.getUserDAO();
			User user = daoUser.queryForId(1);
			if(user!=null){
				application.setScore(user.getScore());
				application.setUnlockedMovies(user.getTotalSolved());
				application.setSeconds(user.getSeconds());
				application.setLevel(user.getTotalCinemas());
			}
			helper = null;
			OpenHelperManager.releaseHelper();
		} catch (SQLException e) {
			e.printStackTrace();
			helper = null;
	        OpenHelperManager.releaseHelper();
		}	
		userPoints.setText(application.getScore()+"");
		userMovies.setText(application.getUnlockedMovies()+"");
		userSeconds.setText(application.getSeconds()+"");
		userLevel.setText(application.getLevel()+"");
	}

	// Restores the state during onCreateView
	private void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			pendingPost = savedInstanceState.getBoolean(PENDING_POST_KEY, false);
		}
	}

	@Override
	public void onPause() {
     	billboard.stop();
     	//redbox.stop();
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.i("HomeFragment", "onResume --> Update Points");
		// Hide the progressContainer & start animations
		progressContainer.setVisibility(View.INVISIBLE);
     	billboard.start();
		updatePoints();
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();		
		if (dialog != null) {
			showDialogWithoutNotificationBar(dialogAction, dialogParams);
		}
	}

	@Override
	public void onStop() {
		super.onStop();		
		// If a dialog exists and is showing, dismiss it
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_POST_KEY, pendingPost);
	}

	// Called when the Play button is touched
	private void onPlayButtonTouched() {
		startFilmMenu();
    }

	private void startFilmMenu() {
        Intent i = new Intent(getActivity(), SelectorCinemaActivity.class);
        startActivity(i);
	}

	// Called when the Challenge button is touched
	private void onChallengeButtonTouched() {
		sendChallenge();
	}

	// Called when the Brag button is touched
	private void onBragButtonTouched() {
		sendBrag();
	}
	
	// Called when the redbox button is touched
	private void onRedboxButtonTouched() {
		Log.i(this.getActivity().toString(), "onRedboxButtonTouched");
		Intent i = new Intent(getActivity(), ShopActivity.class);
		startActivity(i);
	}

	// Called when the Scores button is touched
	private void onScoresButtonTouched() {
		facebookPostAll();
		Log.i(this.getActivity().toString(), "onScoresButtonTouched");
		Intent i = new Intent(getActivity(), ScoreboardActivity.class);
		startActivity(i);
	}

	// Called when the Activity is returned to - needs to be caught for the following two scenarios:
	// Returns from an authentication dialog requesting write permissions - tested with
	// requestCode == REAUTH_ACTIVITY_CODE - if successfully got permissions, execute a session
	// state change callback to then attempt to post their information to Facebook (again)
	public void onActivityResult(int requestCode, int resultCode, Intent data) {		
		if (requestCode == REAUTH_ACTIVITY_CODE) {
            // This ensures a session state change is recorded so that the tokenUpdated() callback is triggered
			// to attempt a post if the write permissions have been granted
			Log.i(MovieTimesUpApplication.TAG, "Reauthorized with publish permissions.");
			Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
        }
	}

	// Hide/show buttons based on whether the user has played a game yet or not
	void updateButtonVisibility() {
		if (scoresButton != null && loginButton!=null) {
			if(!application.isLoggedIn()){
				scoresButton.setVisibility(View.INVISIBLE);
				userImage.setVisibility(View.INVISIBLE);
				loginButton.setBackgroundResource(R.drawable.login_fb2);
			}
			else{
				//TODO: Change visibility in the future
				scoresButton.setVisibility(View.VISIBLE);
				loginButton.setBackgroundResource(R.drawable.logout_fb2);
				userImage.setVisibility(View.VISIBLE);
			}
		}
	}


	/* Facebook Integration */

	// Pop up a request dialog for the user to invite their friends to smash them back in Friend Smash
	private void sendChallenge() {
    	Bundle params = new Bundle();
    	
    	// Uncomment following link once uploaded on Google Play for deep linking
    	// params.putString("link", "https://play.google.com/store/apps/details?id=com.facebook.android.friendsmash");
    	
    	// 1. No additional parameters provided - enables generic Multi-friend selector
    	params.putString("message", "I just smashed " + application.getScore() + " friends! Can you beat it?");
    	
    	// 2. Optionally provide a 'to' param to direct the request at a specific user
//    	params.putString("to", "515768651");
    	
    	// 3. Suggest friends the user may want to request - could be game specific
    	// e.g. players you are in a match with, or players who recently played the game etc.
    	// Normally this won't be hardcoded as follows but will be context specific
//    	String [] suggestedFriends = {
//		    "695755709",
//		    "685145706",
//		    "569496010",
//		    "286400088",
//		    "627802916",
//    	};
//    	params.putString("suggestions", TextUtils.join(",", suggestedFriends));
//    	
    	// Show FBDialog without a notification bar
    	showDialogWithoutNotificationBar("apprequests", params);
	}

	// Pop up a filtered request dialog for the user to invite their friends that have Android devices
	// to smash them back in Friend Smash
	/*private void sendFilteredChallenge() {
		// Okay, we're going to filter our friends by their device, we're looking for friends with an Android device
		// Show the progressContainer during the network call
		progressContainer.setVisibility(View.VISIBLE);

		// Get a list of the user's friends' names and devices
		final Session session = Session.getActiveSession();
		Request friendDevicesGraphPathRequest = Request.newGraphPathRequest(session, "me/friends", new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				// Hide the progressContainer now that the network call has completed
				progressContainer.setVisibility(View.INVISIBLE);

				FacebookRequestError error = response.getError();
				if (error != null) {
					Log.e(MovieTimesUpApplication.TAG, error.toString());
					((HomeActivity)getActivity()).handleError(error, false);
				} else if (session == Session.getActiveSession()) {
					if (response != null) {
						// Get the result
						GraphObject graphObject = response.getGraphObject();
						JSONArray dataArray = (JSONArray)graphObject.getProperty("data");

						if (dataArray.length() > 0) {
							// Ensure the user has at least one friend ...

							// Store the filtered friend ids in the following List
							ArrayList<String> filteredFriendIDs = new ArrayList<String>();

							for (int i=0; i<dataArray.length(); i++) {
								JSONObject currentUser = dataArray.optJSONObject(i);
								if (currentUser != null) {
									JSONArray currentUserDevices = currentUser.optJSONArray("devices");
									if (currentUserDevices != null) {
										// The user has at least one (mobile) device logged into Facebook
										for (int j=0; j<currentUserDevices.length(); j++) {
											JSONObject currentUserDevice = currentUserDevices.optJSONObject(j);
											if (currentUserDevice != null) {
												String currentUserDeviceOS = currentUserDevice.optString("os");
												if (currentUserDeviceOS != null) {
													if (currentUserDeviceOS.equals("Android")) {
														filteredFriendIDs.add(currentUser.optString("id"));
													}
												}
											}
										}
									}
								}
							}

							// Now we have a list of friends with an Android device, we can send requests to them
					    	Bundle params = new Bundle();

					    	// Uncomment following link once uploaded on Google Play for deep linking
					    	// params.putString("link", "https://play.google.com/store/apps/details?id=com.facebook.android.friendsmash");

					    	// We create our parameter dictionary as we did before
					    	params.putString("message", "I just smashed " + application.getScore() + " friends! Can you beat it?");

					    	// We have the same list of suggested friends
					    	String [] suggestedFriends = {
					    		    "695755709",
					    		    "685145706",
					    		    "569496010",
					    		    "286400088",
					    		    "627802916",
					    	};

					    	// Of course, not all of our suggested friends will have Android devices - we need to filter them down
					    	ArrayList<String> validSuggestedFriends = new ArrayList<String>();

		                    // So, we loop through each suggested friend
		                    for (String suggestedFriend : suggestedFriends){
		                        // If they are on our device filtered list, we know they have an Android device
		                        if (filteredFriendIDs.contains(suggestedFriend)){
		                            // So we can call them valid
		                        	validSuggestedFriends.add(suggestedFriend);
		                        }
		                    }
		                    params.putString("suggestions", TextUtils.join(",", validSuggestedFriends.toArray(new String[validSuggestedFriends.size()])));

					    	// Show FBDialog without a notification bar
					    	showDialogWithoutNotificationBar("apprequests", params);
						}
					}
				}
			}
		});
		// Pass in the fields as extra parameters, then execute the Request
		Bundle extraParamsBundle = new Bundle();
		extraParamsBundle.putString("fields", "name,devices");
		friendDevicesGraphPathRequest.setParameters(extraParamsBundle);
		Request.executeBatchAsync(friendDevicesGraphPathRequest);
	}*/

	// Pop up a feed dialog for the user to brag to their friends about their score and to offer
	// them the opportunity to smash them back in Friend Smash
	private void sendBrag() {
		// This function will invoke the Feed Dialog to post to a user's Timeline and News Feed
	    // It will attempt to use the Facebook Native Share dialog
	    // If that's not supported we'll fall back to the web based dialog.

		GraphUser currentFBUser = application.getCurrentFBUser();

		// This first parameter is used for deep linking so that anyone who clicks the link will start smashing this user
    	// who sent the post
		String link = "https://apps.facebook.com/friendsmashsample/?challenge_brag=";
		if (currentFBUser != null) {
			link += currentFBUser.getId();
		}

		// Define the other parameters
		String name = "Checkout my Friend Smash greatness!";
		String caption = "Come smash me back!";
		String description = "I just smashed " + application.getScore() + " friends! Can you beat my score?";
	    String picture = "http://www.friendsmash.com/images/logo_large.jpg";

	    if (FacebookDialog.canPresentShareDialog(getActivity(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
	    	// Create the Native Share dialog
			FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(getActivity())
			.setLink(link)
			.setName(name)
			.setCaption(caption)
			.setPicture(picture)
			.build();

			// Show the Native Share dialog
			((HomeActivity)getActivity()).getFbUiLifecycleHelper().trackPendingDialogCall(shareDialog.present());
		} else {
			// Prepare the web dialog parameters
			Bundle params = new Bundle();
	    	params.putString("link", link);
	    	params.putString("name", caption);
	    	params.putString("caption", caption);
	    	params.putString("description", description);
	    	params.putString("picture", picture);

	    	// Show FBDialog without a notification bar
	    	showDialogWithoutNotificationBar("feed", params);
		}
	}

	// Show a dialog (feed or request) without a notification bar (i.e. full screen)
	private void showDialogWithoutNotificationBar(String action, Bundle params) {
		// Create the dialog
		dialog = new WebDialog.Builder(getActivity(), Session.getActiveSession(), action, params).setOnCompleteListener(
				new WebDialog.OnCompleteListener() {

			@Override
			public void onComplete(Bundle values, FacebookException error) {
				if (error != null && !(error instanceof FacebookOperationCanceledException)) {
					((HomeActivity)getActivity()).showError(getResources().getString(R.string.network_error), false);
				}
				dialog = null;
				dialogAction = null;
				dialogParams = null;
			}
		}).build();

		// Hide the notification bar and resize to full screen
		Window dialog_window = dialog.getWindow();
    	dialog_window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	// Store the dialog information in attributes
    	dialogAction = action;
    	dialogParams = params;
    	
    	// Show the dialog
    	dialog.show();
	}

	// Called when the session state has changed
	void tokenUpdated() {
		if (pendingPost) {
			facebookPostAll();
        }
	}

	// Post all information to Facebook for the user (score, achievement and custom OG action)
	private void facebookPostAll() {
		pendingPost = false;
		Session session = Session.getActiveSession();
		if (session == null || !session.isOpened()) {
            return;
        }
        List<String> permissions = session.getPermissions();
        if (!permissions.containsAll(PERMISSIONS)) {
            pendingPost = true;
            requestPublishPermissions(session);
            return;
        }
        // If you get this far, then you'll have write permissions to post        
		// Post the score to Facebook
		postScore();
		// Post Achievemnt to Facebook
		//postAchievement();
	}

	void requestPublishPermissions(Session session) {
		final Dialog dialog = new Dialog(this.getActivity(), R.style.SlideDialog);
        dialog.setContentView(R.layout.dialog_clapper_one_option);
        dialog.setCancelable(true);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
		TextView text = (TextView) dialog.findViewById(R.id.text);			
		TextView subText = (TextView) dialog.findViewById(R.id.subText);				
		//set values & actions
        cancelButton.setText(android.R.string.ok);
		text.setText(getResources().getString(R.string.upload_score));
		subText.setText(getResources().getString(R.string.permissions_needed));
		cancelButton.setOnClickListener(new OnClickListener() {	//Cancel				
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
        dialog.show();
        
        if (session != null) {
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS)
                    // demonstrate how to set an audience for the publish permissions,
                    // if none are set, this defaults to FRIENDS
                    .setDefaultAudience(SessionDefaultAudience.FRIENDS)
                    .setRequestCode(REAUTH_ACTIVITY_CODE);
            session.requestNewPublishPermissions(newPermissionsRequest);
        }
    }

	// Post score to Facebook
	private void postScore() {
		final int score = application.getScore();
		if (score > 0) {
			// Post the score to FB (for score stories and distribution)
			Bundle fbParams = new Bundle();
			fbParams.putString("score", "" + score);
			Request postScoreRequest = new Request(Session.getActiveSession(),
					"me/scores",
					fbParams,
                    HttpMethod.POST,
                    new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							FacebookRequestError error = response.getError();
							if (error != null) {
								Log.e(MovieTimesUpApplication.TAG, "Posting Score to Facebook failed: " + error.getErrorMessage());
								((HomeActivity)getActivity()).handleError(error, false);
							} else {
								Log.i(MovieTimesUpApplication.TAG, "Score posted successfully to Facebook");
							}
						}
					});
			Request.executeBatchAsync(postScoreRequest);
		}
	}

	// Post achievement to Facebook
	private void postAchievement() {
		int score = application.getScore();
		String achievementURL = null;
		if (score >=200) {
			achievementURL = "http://www.friendsmash.com/opengraph/achievement_200.html";
		} else if (score >=150) {
			achievementURL = "http://www.friendsmash.com/opengraph/achievement_150.html";
		} else if (score >=100) {
			achievementURL = "http://www.friendsmash.com/opengraph/achievement_100.html";
		} else if (score >=50) {
			achievementURL = "http://www.friendsmash.com/opengraph/achievement_50.html";
		}
		if (achievementURL != null) {
			// Only post the relevant achievement if the user has achieved one
			Bundle params = new Bundle();
			params.putString("achievement", achievementURL);
			Request postScoreRequest = new Request(Session.getActiveSession(),
					"me/achievements",
					params,
                    HttpMethod.POST,
                    new Request.Callback() {

						@Override
						public void onCompleted(Response response) {
							FacebookRequestError error = response.getError();
							if (error != null) {
								Log.e(MovieTimesUpApplication.TAG, "Posting Achievement failed: " + error.getErrorMessage());
							} else {
								Log.i(MovieTimesUpApplication.TAG, "Achievement posted successfully");
							}
						}
					});
			Request.executeBatchAsync(postScoreRequest);
		}
	}


	// Getters & Setters

	public boolean isPendingPost() {
		return pendingPost;
	}

	public void setPendingPost(boolean pendingPost) {
		this.pendingPost = pendingPost;
	}
}
