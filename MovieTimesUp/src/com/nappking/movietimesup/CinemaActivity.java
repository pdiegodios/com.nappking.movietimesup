package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.nappking.movietimesup.R;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.adapter.MovieGridAdapter;
import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.Cinema;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.task.WebServiceTask;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

public class CinemaActivity extends DBActivity{
	private static final int GAME_CODE=1;
	private static final int ALL = 1;
	private static final int READY = 2;
	private static final int UNLOCKED = 3;	
	private static final int LOCKED = 4;
	private static final int UNLOCK_COST=25;
	public static final String POSITION="POSITION";
    
	private GridView grid;
	private TextView txPoints;
	private TextView txSeconds;
	private TextView txNumItems;
	private TextView txNumItemsReady;
	private TextView txNumItemsLocked;
	private TextView txNumItemsUnlocked;
	private ImageButton selectAll;
	private ImageButton selectReady;
	private ImageButton selectLocked;
	private ImageButton selectUnlocked;
	private User user;
	private List<String> mLockedMovies;
	private List<String> mUnlockedMovies;
	private List<Movie> mMovies;
	private List<Movie> mSelectedMovies;
	private int mState;
	private int mCinemaId;	
	private int numItemsLocked;		
	private int numItemsUnlocked;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_grid_film);		
		
     	//initiate elements
     	mCinemaId = 		(int) this.getIntent().getExtras().getInt(Cinema.class.toString());
     	grid = 				(GridView) findViewById(R.id.grid);
     	txPoints = 			(TextView) findViewById(R.id.points);
     	txSeconds = 		(TextView) findViewById(R.id.seconds);
    	txNumItems = 		(TextView) findViewById(R.id.numItems);
    	txNumItemsReady = 	(TextView) findViewById(R.id.numItemsReady);
    	txNumItemsLocked = 	(TextView) findViewById(R.id.numItemsLocked);
    	txNumItemsUnlocked =(TextView) findViewById(R.id.numItemsUnlocked);
    	selectAll =			(ImageButton) findViewById(R.id.items);
    	selectReady =		(ImageButton) findViewById(R.id.itemsReady);
    	selectLocked =		(ImageButton) findViewById(R.id.itemsLocked);
    	selectUnlocked =	(ImageButton) findViewById(R.id.itemsUnlocked);
    	
    	mSelectedMovies = new ArrayList<Movie>();
    	mState = ALL;		
		setListeners();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		update(false);
		updateUser();
	}
	
	private void update(boolean stateChanged){     	
		//We obtain all the movies & the user
		try {
			if(mMovies==null){
				Dao<Movie,Integer> daoMovie = getHelper().getMovieDAO();				
				mMovies = (ArrayList<Movie>) daoMovie.queryForEq(Movie.CINEMA, mCinemaId);
			}
			Dao<User,Integer> daoUser = getHelper().getUserDAO();
			user = (User) daoUser.queryForId(1);
			mLockedMovies = user.getLockedMovies();
			mUnlockedMovies = user.getUnlockedMovies();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		if(stateChanged || mSelectedMovies.isEmpty()){
			filter();
		}
		//MovieListAdapter to show correctly the movies
		if (grid.getAdapter() == null || stateChanged) {
			MovieGridAdapter movieAdapter = new MovieGridAdapter(this, mSelectedMovies, mLockedMovies, mUnlockedMovies);		
			grid.setAdapter(movieAdapter);
		}else{
			((MovieGridAdapter)grid.getAdapter()).setValues(mLockedMovies, mUnlockedMovies);
		}
		txPoints.setText(user.getScore()+"");
		txSeconds.setText(user.getSeconds()+"");		
		numItemsLocked = 0;		
		numItemsUnlocked = 0;
		for(Movie movie:mMovies){
			if(mLockedMovies.contains(movie.getId()+""))
				numItemsLocked=numItemsLocked+1;
			else if(mUnlockedMovies.contains(movie.getId()+""))
				numItemsUnlocked=numItemsUnlocked+1;			
		}
		int numItems = mMovies.size();
		txNumItems.setText(numItems+"");
		txNumItemsLocked.setText(numItemsLocked+"");
		txNumItemsUnlocked.setText(numItemsUnlocked+"");
		txNumItemsReady.setText(numItems-numItemsLocked-numItemsUnlocked+"");
	}
	
	
	//To update item returned from FilmActivity or unlocked from this class
	private void updateItemAt(int index){
		update(false);
		Log.i("UPDATE ITEM", "Index:"+index);
		if(index!=-1){
		    View v = grid.getChildAt(index - grid.getFirstVisiblePosition());
		    grid.getAdapter().getView(index, v, grid);
		}
	}
	
	public void setListeners(){
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				//Click over each film. The behaviour depends on the state.
				Movie movie = (Movie) grid.getAdapter().getItem(position);
				final int index = position;
				
				if(mLockedMovies.contains(movie.getId()+"")){ //Locked movie
		            final Dialog dialog = new Dialog(CinemaActivity.this, R.style.SlideDialog);
		            dialog.setContentView(R.layout.dialog_clapper_option);
		            dialog.setCancelable(true);
		            //instantiate elements in the dialog
		            Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
		            Button unlockButton = (Button) dialog.findViewById(R.id.actionButton);
					TextView text = (TextView) dialog.findViewById(R.id.text);						
					//set values & actions
					final int idMovie = movie.getId();
					final int unlockSeconds = movie.getPoints()*UNLOCK_COST + UNLOCK_COST;
					text.setText(getResources().getString(R.string.unlock_cost)+" "+unlockSeconds+" "+
							getResources().getString(R.string.seconds));
					cancelButton.setOnClickListener(new OnClickListener() {	//Cancel				
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					unlockButton.setOnClickListener(new OnClickListener() {	//Unlock					
						@Override
						public void onClick(View v) {
							int secondsAvailable = user.getSeconds();
							if(secondsAvailable>=unlockSeconds){
								//Unlock movie
								user.setSeconds(secondsAvailable-unlockSeconds);
								user.removeLockedMovie(idMovie);
								try {
									Dao<User,Integer> daoUser = getHelper().getUserDAO();
									user.setLastUpdate(System.currentTimeMillis());
									daoUser.update(user);
								} catch (SQLException e) {
									e.printStackTrace();
								}
								dialog.dismiss();
								//Make another update()!!! for unlock movie
								updateItemAt(index);
								List<User> users = new ArrayList<User>();
								users.add(user);
								uploadUsers(users);
							}
							else{
								//Dialog inviting to buy seconds
								dialog.dismiss();
								final Dialog dialogBuy = new Dialog(CinemaActivity.this, R.style.SlideDialog);
								dialogBuy.setContentView(R.layout.dialog_clapper_option);
								dialogBuy.setCancelable(true);
					            Button cancelButton = (Button) dialogBuy.findViewById(R.id.cancelButton);
					            Button buyButton = (Button) dialogBuy.findViewById(R.id.actionButton);
								TextView text = (TextView) dialogBuy.findViewById(R.id.text);			
								TextView subText = (TextView) dialogBuy.findViewById(R.id.subText);
								buyButton.setText(R.string.buy);
								text.setText(getResources().getString(R.string.not_enough_seconds));
								subText.setText(getResources().getString(R.string.buy_seconds));
								cancelButton.setOnClickListener(new OnClickListener() {	//Cancel				
									@Override
									public void onClick(View v) {
										dialogBuy.dismiss();
									}
								});
								buyButton.setOnClickListener(new OnClickListener() { //Buy									
									@Override
									public void onClick(View v) { //Call operations to buy some seconds
										
									}
								});
								dialogBuy.show();
							}
						}
					});
		            dialog.show();
				}
				
				else if(mUnlockedMovies.contains(movie.getId()+"")){ //Solved movie: We show the film information
					Intent myIntent = new Intent(getBaseContext(),FilmInfoActivity.class);
					Bundle myBundle = new Bundle();
					myBundle.putSerializable(Movie.class.toString(), movie);
					myIntent.putExtras(myBundle);
					startActivity(myIntent);
				}
				
				else{ //Movie ready to play: We start the bet and we send the movie to play
					Intent myIntent = new Intent(getBaseContext(),FilmActivity.class);
					Bundle myBundle = new Bundle();
					myBundle.putSerializable(Movie.class.toString(), movie);
					myBundle.putInt(POSITION, index);
					myIntent.putExtras(myBundle);
					startActivityForResult(myIntent, GAME_CODE);
				}
			}
		});
		
		selectAll.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(!selectAll.isSelected()){
					mState = ALL;
					update(true);
				}
			}
		});
		
		selectReady.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(!selectReady.isSelected()){
					mState = READY;
					update(true);
				}
			}
		});
		
		selectLocked.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(!selectLocked.isSelected()){
					mState = LOCKED;
					update(true);
				}
			}
		});
		
		selectUnlocked.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(!selectUnlocked.isSelected()){
					mState = UNLOCKED;
					update(true);
				}
			}
		});
	}
	
	private void filter(){
		mSelectedMovies.clear();
		switch(mState){
		case ALL:
			txNumItems.setTextColor(getResources().getColor(R.color.white));
			txNumItems.setTextSize((getResources().getDimension(R.dimen.grid_text_size_bigger) / getResources().getDisplayMetrics().density));
			txNumItemsLocked.setTextColor(getResources().getColor(R.color.black));
			txNumItemsLocked.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			txNumItemsUnlocked.setTextColor(getResources().getColor(R.color.black));
			txNumItemsUnlocked.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			txNumItemsReady.setTextColor(getResources().getColor(R.color.black));
			txNumItemsReady.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			selectAll.setSelected(true);
			selectReady.setSelected(false);
			selectUnlocked.setSelected(false);
			selectLocked.setSelected(false);
			mSelectedMovies.addAll(mMovies);
			break;
		case READY:
			txNumItems.setTextColor(getResources().getColor(R.color.black));
			txNumItems.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			txNumItemsLocked.setTextColor(getResources().getColor(R.color.black));
			txNumItemsLocked.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			txNumItemsUnlocked.setTextColor(getResources().getColor(R.color.black));
			txNumItemsUnlocked.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			txNumItemsReady.setTextColor(getResources().getColor(R.color.white));
			txNumItemsReady.setTextSize((getResources().getDimension(R.dimen.grid_text_size_bigger) / getResources().getDisplayMetrics().density));
			selectAll.setSelected(false);
			selectReady.setSelected(true);
			selectUnlocked.setSelected(false);
			selectLocked.setSelected(false);
			for(Movie movie:mMovies){
		    	if(!mLockedMovies.contains(movie.getId()+"")&&
		    			!mUnlockedMovies.contains(movie.getId()+"")){
		    		mSelectedMovies.add(movie);
		    	}
			}
			break;
		case UNLOCKED:
			txNumItems.setTextColor(getResources().getColor(R.color.black));
			txNumItems.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			txNumItemsLocked.setTextColor(getResources().getColor(R.color.black));
			txNumItemsLocked.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			txNumItemsUnlocked.setTextColor(getResources().getColor(R.color.white));
			txNumItemsUnlocked.setTextSize((getResources().getDimension(R.dimen.grid_text_size_bigger) / getResources().getDisplayMetrics().density));
			txNumItemsReady.setTextColor(getResources().getColor(R.color.black));
			txNumItemsReady.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			selectAll.setSelected(false);
			selectReady.setSelected(false);
			selectUnlocked.setSelected(true);
			selectLocked.setSelected(false);
			for(Movie movie:mMovies){
		    	if(mUnlockedMovies.contains(movie.getId()+"")){
		    		mSelectedMovies.add(movie);
		    	}
			}
			break;
		case LOCKED:
			txNumItems.setTextColor(getResources().getColor(R.color.black));
			txNumItems.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			txNumItemsLocked.setTextColor(getResources().getColor(R.color.white));
			txNumItemsLocked.setTextSize((getResources().getDimension(R.dimen.grid_text_size_bigger) / getResources().getDisplayMetrics().density));
			txNumItemsUnlocked.setTextColor(getResources().getColor(R.color.black));
			txNumItemsUnlocked.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			txNumItemsReady.setTextColor(getResources().getColor(R.color.black));
			txNumItemsReady.setTextSize((getResources().getDimension(R.dimen.grid_text_size_default) / getResources().getDisplayMetrics().density));
			selectAll.setSelected(false);
			selectReady.setSelected(false);
			selectUnlocked.setSelected(false);
			selectLocked.setSelected(true);
			for(Movie movie:mMovies){
		    	if(mLockedMovies.contains(movie.getId()+"")){
		    		mSelectedMovies.add(movie);
		    	}
			}
			break;
		default: 
			mSelectedMovies = mMovies;
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("RESULT", "requestCode:"+requestCode+"; resultCode:"+resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GAME_CODE:
                	int index = data.getIntExtra(POSITION, -1);
                	//GET ACHIEVE IF THIS IS DIFFERENT TO NULL
                	updateItemAt(index);
                    break;
                default: 
                	break;
            }
        }
	}
	
	private void uploadUsers(List<User> users){
		WebServiceTask wsUser = new WebServiceTask(WebServiceTask.POST_TASK);
		try {							
			JSONArray jsonArray = new JSONArray(new Gson().toJson(users));
			wsUser.addNameValuePair("users", jsonArray.toString());
			Log.i(this.toString(), jsonArray.toString());
	        wsUser.addNameValuePair("action", "UPDATE");        
	        wsUser.execute(new String[] {MovieTimesUpApplication.URL+"users"});		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	
}
