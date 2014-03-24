package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.nappking.movietimesup.R;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.database.DBFragmentActivity;
import com.nappking.movietimesup.entities.User;

/**
 *  Entry point for the app that represents the home screen with the Play button etc. and
 *  also the login screen for the social version of the app - these screens will switch
 *  within this activity using Fragments.
 */
public class HomeActivity extends DBFragmentActivity {   
    private static final String TAG = "HomeActivity";
    // Uri used in handleError() below
    private static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");
	// Declare the UiLifecycleHelper for Facebook session management
    private UiLifecycleHelper fbUiLifecycleHelper;

	// Fragment attributes
    private static final int HOME = 0;
    private static final int FIRST = 1;
    private static final int FRAGMENT_COUNT = 2;
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
 	
 	// Boolean recording whether the activity has been resumed so that
 	// the logic in onSessionStateChange is only executed if this is the case
 	private boolean isResumed = false;
    
 	// Constructor
 	public HomeActivity() {
 		super();
 		Log.i(TAG, "HomeActivity");
 	}
 	
 	// Getter for the fbUiLifecycleHelper
 	public UiLifecycleHelper getFbUiLifecycleHelper() {
 		Log.i(TAG, "getFbUiLifecycleHelper");
		return fbUiLifecycleHelper;
	}
 	
 	@Override
    public void onCreate(Bundle savedInstanceState) {
 		Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    	// Instantiate the fbUiLifecycleHelper and call onCreate() on it
        fbUiLifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
    		@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				// Add code here to accommodate session changes
    			updateView();
    			if (fragments[HOME] != null) {
    				if (state.isOpened()) {
	    				if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
	    					// Only callback if the opened token has been updated - i.e. the user
	    					// has provided write permissions
	    					((HomeFragment) fragments[HOME]).tokenUpdated();
	                    }
    				}
    			}
			}
    	});
        fbUiLifecycleHelper.onCreate(savedInstanceState);
        
		setContentView(R.layout.home);

		FragmentManager fm = getSupportFragmentManager();
        fragments[FIRST] = fm.findFragmentById(R.id.firstFragment);
        fragments[HOME] = fm.findFragmentById(R.id.homeFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();

		// Restore the logged-in user's information if it has been saved and the existing data in the application
		// has been destroyed (i.e. the app hasn't been used for a while and memory on the device is low)
		if (savedInstanceState != null) {
			boolean loggedInState = savedInstanceState.getBoolean(MovieTimesUpApplication.getLoggedInKey(), false);
 			((MovieTimesUpApplication)getApplication()).setLoggedIn(loggedInState);

	 		if (((MovieTimesUpApplication)getApplication()).isLoggedIn() &&
	 			 (((MovieTimesUpApplication)getApplication()).getFriends() == null ||
	 			   ((MovieTimesUpApplication)getApplication()).getCurrentFBUser() == null) ) {
 				try {
 					// currentFBUser
 					String currentFBUserJSONString = savedInstanceState.getString(MovieTimesUpApplication.getCurrentFbUserKey());
 					if (currentFBUserJSONString != null) {
	 					GraphUser currentFBUser = GraphObject.Factory.create(new JSONObject(currentFBUserJSONString), GraphUser.class);
	 					((MovieTimesUpApplication)getApplication()).setCurrentFBUser(currentFBUser);
 					}

 			        // friends
 					ArrayList<String> friendsJSONStringArrayList = savedInstanceState.getStringArrayList(MovieTimesUpApplication.getFriendsKey());
 					if (friendsJSONStringArrayList != null) {
	 					ArrayList<GraphUser> friends = new ArrayList<GraphUser>();
	 					Iterator<String> friendsJSONStringArrayListIterator = friendsJSONStringArrayList.iterator();
	 					while (friendsJSONStringArrayListIterator.hasNext()) {
	 							friends.add(GraphObject.Factory.create(new JSONObject(friendsJSONStringArrayListIterator.next()), GraphUser.class));
	 					}
	 					((MovieTimesUpApplication)getApplication()).setFriends(friends);
 					}
 				} catch (JSONException e) {
 					Log.e(MovieTimesUpApplication.TAG, e.toString());
 				}
			}
 		}
    }
 	
 	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
 		super.onActivityResult(requestCode, resultCode, data); 		
 		// Call onActivityResult on fbUiLifecycleHelper
  		fbUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }
 	
 	@Override
 	protected void onResumeFragments() {
		super.onResumeFragments();
		
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened() && ((MovieTimesUpApplication)getApplication()).getCurrentFBUser() != null) {
			((MovieTimesUpApplication)getApplication()).setLoggedIn(true);
			showFragment(HOME, false);
		} else {
			((MovieTimesUpApplication)getApplication()).setLoggedIn(false);
			Boolean isFirst = false;
			try {
				Dao<User,Integer> daoUser = getHelper().getUserDAO();
				User user = daoUser.queryForId(1);
				if(user!=null && user.getUser()!=null && !user.getUser().equals("")){
					showFragment(HOME,false);
					((MovieTimesUpApplication)getApplication()).setScore(user.getScore());
					((MovieTimesUpApplication)getApplication()).setSeconds(user.getSeconds());
					((MovieTimesUpApplication)getApplication()).setUnlockedMovies(user.getTotalSolved());
					((MovieTimesUpApplication)getApplication()).setLevel(user.getTotalCinemas());
				}
				else isFirst=true;
			} catch (SQLException e) {
				isFirst=true;
				e.printStackTrace();
			}
			if(isFirst)
				showFragment(FIRST, false);
		}
    }

	@Override
    public void onResume() {
 		Log.i(TAG, "onResume");
        super.onResume();
        isResumed = true;        
  		fbUiLifecycleHelper.onResume();        
        // Hide the notification bar
 		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
 		
  		// Measure mobile app install ads
 		// Ref: https://developers.facebook.com/docs/tutorials/mobile-app-ads/
 		AppEventsLogger.activateApp(this, ((MovieTimesUpApplication)getApplication()).getString(R.string.app_id));
 		
 		updateUser();
    }

    @Override
    public void onPause() {
 		Log.i(TAG, "onPause");
        super.onPause();
        isResumed = false;
  		fbUiLifecycleHelper.onPause();
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
 		Log.i(TAG, "onSaveInstanceState");
		super.onSaveInstanceState(outState);		
  		fbUiLifecycleHelper.onSaveInstanceState(outState);        
  		// Save the logged-in state
  		outState.putBoolean(MovieTimesUpApplication.getLoggedInKey(), ((MovieTimesUpApplication)getApplication()).isLoggedIn());
  		// Save the currentFBUser
        if (((MovieTimesUpApplication)getApplication()).getCurrentFBUser() != null) {
	        outState.putString(MovieTimesUpApplication.getCurrentFbUserKey(),
	        		((MovieTimesUpApplication)getApplication()).getCurrentFBUser().getInnerJSONObject().toString());
        }        
        // Save the logged-in user's list of friends
        if (((MovieTimesUpApplication)getApplication()).getFriends() != null) {
	        outState.putStringArrayList(MovieTimesUpApplication.getFriendsKey(),
	        		((MovieTimesUpApplication)getApplication()).getFriendsAsArrayListOfStrings());
        }
	}

	@Override
    public void onDestroy() {
 		Log.i(TAG, "onDestroy");
 		super.onDestroy();
  		fbUiLifecycleHelper.onDestroy();
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
 		Log.i(TAG, "showFragment "+fragmentIndex);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        if(fragmentIndex==HOME){
        	((HomeFragment)fragments[HOME]).progressContainer.setVisibility(View.INVISIBLE);
        	((HomeFragment)fragments[HOME]).updateButtonVisibility();
        }
    }

	/* Facebook Integration Only ... */

	// Call back on HomeActivity when the session state changes to update the view accordingly
	private void updateView() {
 		Log.i(TAG, "updateView");
		if (isResumed) {
			Session session = Session.getActiveSession();
			if(fragments[HOME]!=null){
				if (session.isOpened() && !((MovieTimesUpApplication)getApplication()).isLoggedIn()) {
					((MovieTimesUpApplication)getApplication()).setLoggedIn(true);
					// Not logged in, but should be, so fetch the user information and log in (load the HomeFragment)
					fetchUserInformationAndLogin();
		        } else if(session.isClosed() && ((MovieTimesUpApplication)getApplication()).isLoggedIn()){
					((MovieTimesUpApplication)getApplication()).setLoggedIn(false);
					// Logged in, but shouldn't be, so reload the HomeFragment
		        	showFragment(HOME, false);
		        }
			}
		}
	}

	// Fetch user information and login (i.e switch to the personalized HomeFragment)
	private void fetchUserInformationAndLogin() {
 		Log.i(TAG, "fetchUserInformationAndLogin");
		final Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// If the session is open, make an API call to get user information required for the app

			// Show the progress spinner during this network call
			if (fragments[HOME] != null && ((HomeFragment)fragments[HOME]).progressContainer != null) {
				((HomeFragment)fragments[HOME]).progressContainer.setVisibility(View.VISIBLE);
			}

			// Get the user's list of friends
			Request friendsRequest = Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
				@Override
				public void onCompleted(List<GraphUser> users, Response response) {
					FacebookRequestError error = response.getError();
					if (error != null) {
						Log.e(MovieTimesUpApplication.TAG, error.toString());
						handleError(error, true);
					} else if (session == Session.getActiveSession()) {
						// Set the friends attribute
						((MovieTimesUpApplication)getApplication()).setFriends(users);
					}
				}
			});
			Bundle params = new Bundle();
			params.putString("fields", "name,first_name,last_name");
			friendsRequest.setParameters(params);

			// Get current logged in user information
			Request meRequest = Request.newMeRequest(session, new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					FacebookRequestError error = response.getError();
					if (error != null) {
						Log.e(MovieTimesUpApplication.TAG, error.toString());
						handleError(error, true);
					} else if (session == Session.getActiveSession()) {
						// Set the currentFBUser attribute
						((MovieTimesUpApplication)getApplication()).setCurrentFBUser(user);
					}
				}
			});

			// Create a RequestBatch and add a callback once the batch of requests completes
			RequestBatch requestBatch = new RequestBatch(friendsRequest, meRequest);
			requestBatch.addCallback(new RequestBatch.Callback() {
				@Override
				public void onBatchCompleted(RequestBatch batch) {
					if ( ((MovieTimesUpApplication)getApplication()).getCurrentFBUser() != null &&
						 ((MovieTimesUpApplication)getApplication()).getFriends() != null ) {
						// Login by switching to the personalized HomeFragment
						loadPersonalizedFragment();
					} else {
						showError(getString(R.string.error_fetching_profile), true);
					}
				}
			});

			// Execute the batch of requests asynchronously
			requestBatch.executeAsync();
		}
	}

	// Switches to the personalized HomeFragment as the user has just logged in
	private void loadPersonalizedFragment() {
 		Log.i(TAG, "loadPersonalizedFragment");
		if (isResumed) {
			// Personalize the HomeFragment
			((HomeFragment)fragments[HOME]).personalizeHomeFragment();
			// Load the HomeFragment personalized
			showFragment(HOME, false);
		} else {
			showError(getString(R.string.error_switching_screens), true);
		}
	}

    void handleError(FacebookRequestError error, boolean logout) {
 		Log.i(TAG, "handleError");
        DialogInterface.OnClickListener listener = null;
        String dialogBody = null;

        if (error == null) {
            dialogBody = getString(R.string.error_dialog_default_text);
        } else {
            switch (error.getCategory()) {
                case AUTHENTICATION_RETRY:
                    // tell the user what happened by getting the message id, and
                    // retry the operation later
                    String userAction = (error.shouldNotifyUser()) ? "" :
                            getString(error.getUserActionMessageId());
                    dialogBody = getString(R.string.error_authentication_retry, userAction);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, M_FACEBOOK_URL);
                            startActivity(intent);
                        }
                    };
                    break;

                case AUTHENTICATION_REOPEN_SESSION:
                    // close the session and reopen it.
                    dialogBody = getString(R.string.error_authentication_reopen);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Session session = Session.getActiveSession();
                            if (session != null && !session.isClosed()) {
                                session.closeAndClearTokenInformation();
                            }
                        }
                    };
                    break;

                case PERMISSION:
                    // request the publish permission
                    dialogBody = getString(R.string.error_permission);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        	if (fragments[HOME] != null) {
                        		((HomeFragment) fragments[HOME]).setPendingPost(true);
                        		((HomeFragment) fragments[HOME]).requestPublishPermissions(Session.getActiveSession());
                        	}
                        }
                    };
                    break;

                case SERVER:
                case THROTTLING:
                    // this is usually temporary, don't clear the fields, and
                    // ask the user to try again
                    dialogBody = getString(R.string.error_server);
                    break;

                case BAD_REQUEST:
                    // this is likely a coding error, ask the user to file a bug
                    dialogBody = getString(R.string.error_bad_request, error.getErrorMessage());
                    break;

                case CLIENT:
                	// this is likely an IO error, so tell the user they have a network issue
                	dialogBody = getString(R.string.network_error);
                    break;
                    
                case OTHER:
                default:
                    // an unknown issue occurred, this could be a code error, or
                    // a server side issue, log the issue, and either ask the
                    // user to retry, or file a bug
                    dialogBody = getString(R.string.error_unknown, error.getErrorMessage());
                    break;
            }
        }

        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.error_dialog_button_text, listener)
                .setTitle(R.string.error_dialog_title)
                .setMessage(dialogBody)
                .show();
        
        if (logout) {
        	logout();
        }
    }

	// Show user error message as a toast
	void showError(String error, boolean logout) {
 		Log.i(TAG, "showError");
		Toast.makeText(this, error, Toast.LENGTH_LONG).show();
		if (logout) {
			logout();
		}
	}

    private void logout() {
 		Log.i(TAG, "logout");
    	// Close the session, which will cause a callback to show the logout screen
		Session.getActiveSession().closeAndClearTokenInformation();

		// Clear any permissions associated with the LoginButton
		LoginButton loginButton = (LoginButton) findViewById(R.id.loginButton);
		Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bouncing);
		if (loginButton != null) {
			loginButton.startAnimation(bounce);
			loginButton.clearPermissions();
		}
    }

}