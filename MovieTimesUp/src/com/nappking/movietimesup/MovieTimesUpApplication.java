/**
 * Copyright 2012 Facebook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nappking.movietimesup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Application;

import com.facebook.FacebookRequestError;
import com.facebook.model.GraphUser;

/**
 *  Use a custom Application class to pass state data between Activities.
 */
public class MovieTimesUpApplication extends Application {

	/* Static Attributes */
	
	// Tag used when logging all messages with the same tag (e.g. for demoing purposes)
	static final String TAG = "FriendSmash";
	
	// Switch between the non-social and social Facebook versions of the game
	static final boolean IS_SOCIAL = true;

	
	/* Friend Smash application attributes */
	
	// Player's current score
	private int score = -1;
	
	
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
		
	// ID of the last friend smashed (linked to the current score)
	private String lastFriendSmashedID = null;
	
	// List of ordered ScoreboardEntry objects in order from highest to lowest score to
	// be shown in the ScoreboardFragment
	private ArrayList<ScoreboardEntry> scoreboardEntriesList = null;
	
	// FacebookRequestError to show when the GameFragment closes
	private FacebookRequestError gameFragmentFBRequestError = null;
		

	/* Friend Smash application attribute getters & setters */
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	
	/* Facebook attribute getters & setters */
	
	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
		if (!loggedIn) {
			// If the user is logged out, reset the score and nullify all the logged-in user's values
			setScore(-1);
			setCurrentFBUser(null);
        	setFriends(null);
        	setLastFriendSmashedID(null);
        	setScoreboardEntriesList(null);
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

	public String getLastFriendSmashedID() {
		return lastFriendSmashedID;
	}

	public void setLastFriendSmashedID(String lastFriendSmashedID) {
		this.lastFriendSmashedID = lastFriendSmashedID;
	}

	public ArrayList<ScoreboardEntry> getScoreboardEntriesList() {
		return scoreboardEntriesList;
	}

	public void setScoreboardEntriesList(ArrayList<ScoreboardEntry> scoreboardEntriesList) {
		this.scoreboardEntriesList = scoreboardEntriesList;
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
