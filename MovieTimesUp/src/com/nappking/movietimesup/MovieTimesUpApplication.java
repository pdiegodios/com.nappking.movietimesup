package com.nappking.movietimesup;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Application;

import com.facebook.FacebookRequestError;
import com.facebook.model.GraphUser;
import com.nappking.movietimesup.entities.Achievement;
import com.nappking.movietimesup.entities.User;

/**
 *  Use a custom Application class to pass state data between Activities.
 */
public class MovieTimesUpApplication extends Application {	
	// Tag used when logging all messages with the same tag (e.g. for demoing purposes)
	public static final String TAG = "MovieTimesUp";	
    public static final String URL = "http://movietimesup.gestores.cloudbees.net/rest/";
	// Switch between the non-social and social Facebook versions of the game
	public static final int TIME_FOR_SERVICE=15*60*1000; //15min
    public static final int UNLOCK_LEVEL = 10;
    public static final int SECONDS_FOR_LEVEL = 2400;
    public static final boolean IS_SOCIAL = true;
	// Player's current score
    private boolean soundEnabled = true;
	private int score = 0;
	private int seconds = 0;
	private int unlockedMovies = 0;
	private int level = 0;
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
	private List<ScoreboardEntry> friendlyUsers = null;	
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

	public int getUnlockedMovies() {
		return unlockedMovies;
	}

	public void setUnlockedMovies(int unlockedMovies) {
		this.unlockedMovies = unlockedMovies;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean getSoundEnabled() {
		return soundEnabled;
	}

	public void setSoundEnabled(boolean enabled) {
		this.soundEnabled = enabled;
	}
	
	public long getLastUpdateCall() {
		return lastUpdateCall;
	}

	public void setLastUpdateCall(long millis) {
		this.lastUpdateCall = millis;
	}
	
	public ArrayList<Achievement> getAchievements(){
		ArrayList<Achievement> achievements = new ArrayList<Achievement>();
		achievements.add(new Achievement(User.AMERICA, 20, 200, R.drawable.america20, R.string.american_achievement));
		achievements.add(new Achievement(User.AMERICA, 50, 500, R.drawable.america50, R.string.american_achievement));
		achievements.add(new Achievement(User.AMERICA, 100, 1000, R.drawable.america100, R.string.american_achievement));
		achievements.add(new Achievement(User.AMERICA, 200, 2000, R.drawable.america200, R.string.american_achievement));
		achievements.add(new Achievement(User.EUROPE, 5, 200, R.drawable.europe5, R.string.european_achievement));
		achievements.add(new Achievement(User.EUROPE, 12, 500, R.drawable.europe12, R.string.european_achievement));
		achievements.add(new Achievement(User.EUROPE, 25, 1000, R.drawable.europe25, R.string.european_achievement));
		achievements.add(new Achievement(User.EUROPE, 50, 2000, R.drawable.europe50, R.string.european_achievement));
		achievements.add(new Achievement(User.ASIA, 3, 200, R.drawable.asia3, R.string.asian_achievement));
		achievements.add(new Achievement(User.ASIA, 7, 500, R.drawable.asia7, R.string.asian_achievement));
		achievements.add(new Achievement(User.ASIA, 15, 1000, R.drawable.asia15, R.string.asian_achievement));
		achievements.add(new Achievement(User.ASIA, 30, 2000, R.drawable.asia30, R.string.asian_achievement));
		achievements.add(new Achievement(User.EXOTIC, 2, 200, R.drawable.exotic2, R.string.exotic_achievement));
		achievements.add(new Achievement(User.EXOTIC, 5, 500, R.drawable.exotic5, R.string.exotic_achievement));
		achievements.add(new Achievement(User.EXOTIC, 10, 1000, R.drawable.exotic10, R.string.exotic_achievement));
		achievements.add(new Achievement(User.EXOTIC, 20, 2000, R.drawable.exotic20, R.string.exotic_achievement));	
		return achievements;
	}
	
	/* Facebook attribute getters & setters */
	
	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
		if (!loggedIn) {
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
		
	public String deAccent(String str) {
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    String result = pattern.matcher(nfdNormalizedString).replaceAll("");
	    String onlyAlphabetical = result.replaceAll("[^a-zA-Z]", "");
	    return onlyAlphabetical;
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

	public List<ScoreboardEntry> getFriendlyUserList() {
		return friendlyUsers;
	}

	public void setFriendlyUserList(List<ScoreboardEntry> entries) {
		this.friendlyUsers = entries;
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
