package com.nappking.movietimesup;

import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.nappking.movietimesup.R;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

/**
 *  Fragment shown once a user starts playing a game
 */
public class GameFragment extends Fragment {	
    
	// FrameLayout as the container for the game
	private FrameLayout gameFrame;

	// FrameLayout of the progress container to show the spinner
	private FrameLayout progressContainer;
	
	// TextView for the score
	private TextView scoreTextView;
	
	// LinearyLayout containing the lives images
	private LinearLayout livesContainer;
	
	// Icon width for the friend images to smash
	private int iconWidth;

	// Screen Dimensions
	private int screenWidth;
	private int screenHeight;
	
	
	// Handler for putting messages on Main UI thread from background threads periodically
	private Handler timerHandler;
	
	// Runnable task used to produce images to fly across the screen
	private Runnable fireImageTask = null;
	
	// Boolean indicating whether images have started firing
	private boolean imagesStartedFiring = false;
	
	// ID of the friend to smash (if passed in as an attribute)
	private String friendToSmashIDProvided = null;
	
	// Name of the friend to smash
	private String friendToSmashFirstName = null;		
	
	// Score for the user
	private int score = 0;
	
	// Lives the user has remaining
	private int lives = 3;
	
	// Boolean set to true if first image has been fired
	private boolean firstImageFired = false;
	
	// Boolean indicating that the first image to be fired is pending (i.e. a Request is
	// in the process of executing in a background thread to fetch the images / information)
	private boolean firstImagePendingFiring = false;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		
		// Instantiate the handlers
		timerHandler = new Handler();
	}
	
	@SuppressWarnings({ "deprecation" })
	@TargetApi(13)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_game, parent, false);
		
		gameFrame = (FrameLayout)v.findViewById(R.id.gameFrame);
		progressContainer = (FrameLayout)v.findViewById(R.id.progressContainer);
		scoreTextView = (TextView)v.findViewById(R.id.scoreTextView);
		livesContainer = (LinearLayout)v.findViewById(R.id.livesContainer);
		
		// Set the progressContainer as invisible by default
		progressContainer.setVisibility(View.INVISIBLE);
		
		// Set the icon width (for the images to be smashed)
		setIconWidth(getResources().getDimensionPixelSize(R.dimen.icon_width));
		
		// Set the screen dimensions
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			setScreenWidth(size.x);
			setScreenHeight(size.y);
		}
		else {
			setScreenWidth(display.getWidth());
			setScreenHeight(display.getHeight());
		}
		
		// Always keep the Action Bar hidden
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActivity().getActionBar().hide();
		}
		
		// Instantiate the fireImageTask for future fired images
		fireImageTask = new Runnable()
		{
			public void run()
			{
				spawnImage(false);
			}
		};
		
		// Refresh the score board
		setScore(getScore());
		
		// Refresh the lives
		setLives(getLives());
		
		// Note: Images will start firing in the onResume method below
		
		return v;
	}

	// Called when the first image should be fired (only called during onResume)
	// If the game has been deep linked into (i.e. a user has clicked on a feed post or request in
	// Facebook), then fetch the specific user that should be smashed
	private void fireFirstImage() {
		if (MovieTimesUpApplication.IS_SOCIAL) {
			// Get any bundle parameters there are
			Bundle bundle = getActivity().getIntent().getExtras();
			
			String requestID = null;
			String userID = null;
			if (bundle != null) {
				requestID = bundle.getString("request_id");
				userID = bundle.getString("user_id");
			}
			
			if (requestID != null && friendToSmashIDProvided == null) {
				// Deep linked from request
				// Make a request to get a specific user to smash if they haven't been fetched already
				
				// Show the spinner for this part
				progressContainer.setVisibility(View.VISIBLE);
				
				// Get and set the id of the friend to smash and start firing the image
				fireFirstImageWithRequestID(requestID);
			} else if (userID != null && friendToSmashIDProvided == null) {
				// Deep linked from feed post
				// Make a request to get a specific user to smash if they haven't been fetched already
				
				// Show the spinner for this part
				progressContainer.setVisibility(View.VISIBLE);
				
				// Get and set the id of the friend to smash and start firing the image
				fireFirstImageWithUserID(userID);
			} else {
				// requestID is null, userID is null or friendToSmashIDProvided is already set,
				// so use the randomly generated friend of the user or the already set friendToSmashIDProvided
				// So set the smashPlayerNameTextView text and hide the progress spinner as there is nothing to fetch
				progressContainer.setVisibility(View.INVISIBLE);	
				
				// Now you're ready to fire the first image
				spawnImage(false);
			}
		} else {
			// Non-social, so set the smashPlayerNameTextView text and hide the progress spinner as there is nothing to fetch
			progressContainer.setVisibility(View.INVISIBLE);	
			
			// Now you're ready to fire the first image
			spawnImage(false);
		}
	}
	
	// Fires the first image in a game with a given request id  (from a user deep linking by clicking
	// on a request from a specific user)
	private void fireFirstImageWithRequestID(String requestID) {
		final Session session = Session.getActiveSession();
		Request requestIDGraphPathRequest = Request.newGraphPathRequest(session, requestID, new Request.Callback() {

			@Override
			public void onCompleted(Response response) {
				FacebookRequestError error = response.getError();
				if (error != null) {
					Log.e(MovieTimesUpApplication.TAG, error.toString());
					closeAndHandleError(error);
				} else if (session == Session.getActiveSession()) {
					if (response != null) {
						// Extract the user id from the response
						GraphObject graphObject = response.getGraphObject();
						JSONObject fromObject = (JSONObject)graphObject.getProperty("from");
						try {
							friendToSmashIDProvided = fromObject.getString("id");
						} catch (JSONException e) {
							Log.e(MovieTimesUpApplication.TAG, e.toString());
							closeAndShowError(getResources().getString(R.string.network_error));
						}
						
						// With the user id, fetch and set their name
						Request userGraphPathRequest = Request.newGraphPathRequest(session, friendToSmashIDProvided, new Request.Callback() {

							@Override
							public void onCompleted(Response response) {
								FacebookRequestError error = response.getError();
								if (error != null) {
									Log.e(MovieTimesUpApplication.TAG, error.toString());
									closeAndHandleError(error);
								} else if (session == Session.getActiveSession()) {
									if (response != null) {
										// Extract the user name from the response
										friendToSmashFirstName = response.getGraphObjectAs(GraphUser.class).getFirstName();
									}
									if (friendToSmashFirstName != null) {
										// If the first name of the friend to smash has been set, set the text in the smashPlayerNameTextView
										// and hide the progress spinner now that the user's details have been fetched
										progressContainer.setVisibility(View.INVISIBLE);
										
										// Now you're ready to fire the first image
										spawnImage(false);
									}
								}
							}
						});
						Request.executeBatchAsync(userGraphPathRequest);
					}
				}
			}
		});
		Request.executeBatchAsync(requestIDGraphPathRequest);
	}
	
	// Fires the first image in a game with a given user id  (from a user deep linking by clicking
	// on a feed post from a specific user)
	private void fireFirstImageWithUserID(String userID) {
		final Session session = Session.getActiveSession();
		
		// With the user id, fetch and set their name, then start the firing of images
		friendToSmashIDProvided = userID;
		Request userGraphPathRequest = Request.newGraphPathRequest(session, friendToSmashIDProvided, new Request.Callback() {

			@Override
			public void onCompleted(Response response) {
				FacebookRequestError error = response.getError();
				if (error != null) {
					Log.e(MovieTimesUpApplication.TAG, error.toString());
					closeAndHandleError(error);
				} else if (session == Session.getActiveSession()) {
					if (response != null) {
						// Extract the user name from the response
						friendToSmashFirstName = response.getGraphObjectAs(GraphUser.class).getFirstName();
					}
					if (friendToSmashFirstName != null) {
						// If the first name of the friend to smash has been set, set the text in the smashPlayerNameTextView
						// and hide the progress spinner now that the user's details have been fetched
						progressContainer.setVisibility(View.INVISIBLE);
						
						// Now you're ready to fire the first image
						spawnImage(false);
					}
				}
			}
		});
		Request.executeBatchAsync(userGraphPathRequest);
	}
	
	// Spawn a new UserImageView, set its bitmap (fetch it from Facebook if it hasn't already been fetched)
	// and fire it once the image has been set (and fetched if appropriate)
	@SuppressWarnings("unused")
	private void spawnImage(final boolean extraImage) {
		// Instantiate Random Generator
        Random randomGenerator = new Random(System.currentTimeMillis());
        
        // 1 in every 5 images should be a celebrity the user should not smash - calculate that here
        // Unless it is the first image fired, in which case it should always be the smashable image
        boolean shouldSmash = true;
        if (firstImageFired) {
        	if (randomGenerator.nextInt(5) == 4 && firstImageFired) {
            	shouldSmash = false;
            } 
        } else if (!firstImageFired) {
        	shouldSmash = true;
        	firstImageFired = true;
        }			        
        
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
	
	// Close the game and show the specified FacebookRequestException to the user
	private void closeAndHandleError(FacebookRequestError error) {
		// Store the FacebookRequestError in the FacebookApplication before closing out this GameFragment so that
		// it is shown to the user once exited
		((MovieTimesUpApplication) getActivity().getApplication()).setGameFragmentFBRequestError(error);
		
		getActivity().setResult(Activity.RESULT_CANCELED);
		getActivity().finish();
	}
	
		
	@Override
	public void onPause() {
		super.onPause();

		// Stop the firing images
		stopTheFiringImages();
	}
	
	@Override
	public void onResume() {
		super.onResume();

		// Stop any firing images (even though this is called in onPause, there might be new firing images
		// if they were pending while onPause was called
		stopTheFiringImages();
		
		if (!imagesStartedFiring) {
			// Fire first image
			if (MovieTimesUpApplication.IS_SOCIAL) {
				// Only fire for the social game if there isn't a first image pending firing
				if (!firstImagePendingFiring) {
					// ... and also set the firstImagePendingFiring to true - will be set back
					// to false later once the images have actually started firing (i.e. all network
					// calls have executed) - note, this is only relevant for the social version
					firstImagePendingFiring = true;
					imagesStartedFiring = true;
					fireFirstImage();
				}
			} else {
				imagesStartedFiring = true;
				fireFirstImage();
			}
		}
	}
	
	// Stop the firing of all images (and mark the existing ones as void) - called when the game is paused
	private void stopTheFiringImages() {
		// Stop new animations and indicate that images have not started firing
		timerHandler.removeCallbacks(fireImageTask);
		imagesStartedFiring = false;
	}
	
	// Get the current score
	int getScore() {
		return score;
	}

	// Set the score and if the score is divisible by 10, spawn more images ...
	// ... the higher the score, the more images that will be spawned
	void setScore(int score) {
		this.score = score;
		
		// Update the scoreTextView
		scoreTextView.setText("Score: " + score);
		
		// If they start scoring well, spawn more images
		if (score > 0 && score % 10 == 0) {
			// Every multiple of 10, spawn extra images ...
			for (int i=0; i<score/20; i++) {
				spawnImage(true);
			}
		}
	}

	// Get the user's number of lives they have remaining
	int getLives() {
		return lives;
	}

	// Set the number of lives that the user has, update the display appropriately and
	// end the game if they have run out of lives
	void setLives(int lives) {
		this.lives = lives;
		
		if (getActivity() != null) {
			// Update the livesContainer
			livesContainer.removeAllViews();
			for (int i=0; i<lives; i++) {
				ImageView heartImageView = new ImageView(getActivity());
				heartImageView.setImageResource(R.drawable.heart_red);
			    livesContainer.addView(heartImageView);
			}
			
			if (lives <= 0) {
				// User has no lives left, so end the game, passing back the score
				Bundle bundle = new Bundle();
				bundle.putInt("score", getScore());
				
				Intent i = new Intent();
				i.putExtras(bundle);
			
				getActivity().setResult(Activity.RESULT_OK, i);
				getActivity().finish();
			}
		}
	}
	
	
	/* Standard Getters & Setters */
	
	public int getIconWidth() {
		return iconWidth;
	}

	public void setIconWidth(int iconWidth) {
		this.iconWidth = iconWidth;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}
	
	public FrameLayout getGameFrame() {
		return gameFrame;
	}
}
