package com.nappking.movietimesup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Application;

import com.facebook.FacebookRequestError;
import com.facebook.model.GraphUser;
import com.nappking.movietimesup.entities.User;

/**
 *  Use a custom Application class to pass state data between Activities.
 */
public class MovieTimesUpApplication extends Application {	
	// Tag used when logging all messages with the same tag (e.g. for demoing purposes)
	static final String TAG = "MovieTimesUp";	
    public static final String URL = "http://movietimesup.gestores.cloudbees.net/rest/";
	// Switch between the non-social and social Facebook versions of the game
	static final boolean IS_SOCIAL = true;
	static final int TIME_FOR_SERVICE=15*60*1000; //15min
	// Player's current score
	private int score = 0;
	private int seconds = 0;
	// Last time LoadUserDataTask was called
	private long lastUpdateCall = -1;	
	
	/* Facebook application attributes */

	// Logged in status of the user
	private boolean loggedIn = false;
	private static final String LOGGED_IN_KEY = "logged_in";	
	// Current logged in FB user and key for saving/restoring during the Activity lifecycle
	private GraphUser currentFBUser;
	private static final String CURRENT_FB_USER_KEY = "current_fb_user";	
	// List of the logged in user's friends and key for saving/restoring during the Activity lifecycle
	private List<GraphUser> friends;
	private static final String FRIENDS_KEY = "friends";	
	// List of ordered ScoreboardEntry objects in order from highest to lowest score to
	// be shown in the ScoreboardFragment
	private List<User> friendlyUsers = null;	
	// FacebookRequestError to show when the GameFragment closes
	private FacebookRequestError gameFragmentFBRequestError = null;
		

	/*getters & setters */
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	public long getLastUpdateCall() {
		return lastUpdateCall;
	}

	public void setLastUpdateCall(long millis) {
		this.lastUpdateCall = millis;
	}

	
	/* Facebook attribute getters & setters */
	
	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
		if (!loggedIn) {
			setLastUpdateCall(-1);
			setCurrentFBUser(null);
        	setFriends(null);
        	setFriendlyUserList(null);
		}
	}

	public GraphUser getCurrentFBUser() {
		return currentFBUser;
	}

	public void setCurrentFBUser(GraphUser currentFBUser) {
		this.currentFBUser = currentFBUser;
	}

	public List<GraphUser> getFriends() {
		return friends;
	}
	
	// Method to get the list of friends in an ArrayList<String> where each entry
	// is an inner JSON objects of each friend represented as a string - used for
	// saving/restoring each friend during the Activity lifecycle
	public ArrayList<String> getFriendsAsArrayListOfStrings() {
		ArrayList<String> friendsAsArrayListOfStrings = new ArrayList<String>();
		
		Iterator<GraphUser> friendsIterator = friends.iterator();
		while (friendsIterator.hasNext()) {
			friendsAsArrayListOfStrings.add(friendsIterator.next().getInnerJSONObject().toString());
		}
		
		return friendsAsArrayListOfStrings;
	}
	
	public GraphUser getFriend(int index) {
		if (friends != null && friends.size() > index) {
			return friends.get(index);
		} else {
			return null;
		}
	}
	
	public void setFriends(List<GraphUser> friends) {
		this.friends = friends;
	}

	public List<User> getFriendlyUserList() {
		return friendlyUsers;
	}

	public void setFriendlyUserList(List<User> users) {
		this.friendlyUsers = users;
	}

	public FacebookRequestError getGameFragmentFBRequestError() {
		return gameFragmentFBRequestError;
	}

	public void setGameFragmentFBRequestError(FacebookRequestError gameFragmentFBRequestError) {
		this.gameFragmentFBRequestError = gameFragmentFBRequestError;
	}

	public static String getLoggedInKey() {
		return LOGGED_IN_KEY;
	}
	
	public static String getCurrentFbUserKey() {
		return CURRENT_FB_USER_KEY;
	}

	public static String getFriendsKey() {
		return FRIENDS_KEY;
	}
	
}
