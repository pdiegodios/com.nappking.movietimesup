package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.facebook.android.friendsmash.R;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.adapter.MovieListAdapter;
import com.nappking.movietimesup.database.DBActivity;
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
import android.widget.ImageView;
import android.widget.TextView;

public class FilmGridActivity extends DBActivity{
	
	GridView grid;
	TextView txPoints;
	TextView txSeconds;
	ImageView leftCurtain;
	ImageView rightCurtain;
	ImageView spectators;
	User user;
	List<String> lockedMovies;
	List<String> unlockedMovies;
	static final int result_sent = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.filmgrid);		
		
     	//initiate elements
     	grid = 			(GridView) findViewById(R.id.grid);
     	txPoints = 		(TextView) findViewById(R.id.points);
     	txSeconds = 	(TextView) findViewById(R.id.seconds);
     	leftCurtain = 	(ImageView) findViewById(R.id.leftcurtain);
     	rightCurtain = 	(ImageView) findViewById(R.id.rightcurtain);
     	spectators = 	(ImageView) findViewById(R.id.spectators);

		//Density to resize
		float density = getResources().getDisplayMetrics().density;
		
		//sizes
		int widthSize = (int) getResources().getDimension(R.dimen.grid_curtain_width);
		int heightSize = (int) getResources().getDimension(R.dimen.grid_spectators_height);
		int resize = Math.round(widthSize*density);
     	leftCurtain.getLayoutParams().width = resize;
     	rightCurtain.getLayoutParams().width = resize;
     	resize = Math.round(heightSize*density);
     	spectators.getLayoutParams().height = resize;
		update();
		setListeners();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		update();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private void update(){     	
		//We obtain all the movies & the user
		ArrayList<Movie> movies = new ArrayList<Movie>();
		try {
			Dao<Movie,Integer> daoMovie = getHelper().getMovieDAO();
			Dao<User,Integer> daoUser = getHelper().getUserDAO();
			movies = (ArrayList<Movie>) daoMovie.queryForAll();
			user = (User) daoUser.queryForId(1);
			lockedMovies = user.getLockedMovies();
			unlockedMovies = user.getUnlockedMovies();
		} catch (SQLException e) {
			e.printStackTrace();
		}		

		//MovieListAdapter to show correctly the movies
		if (grid.getAdapter() == null) {
			MovieListAdapter movieAdapter = new MovieListAdapter(this, movies, lockedMovies, unlockedMovies);		
			grid.setAdapter(movieAdapter);
		} else {
			((MovieListAdapter)grid.getAdapter()).reload(movies, lockedMovies, unlockedMovies);
		}
		txPoints.setText(user.getScore()+"");
		txSeconds.setText(user.getSeconds()+"");		 
	}
	
	public void setListeners(){
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				//Click over each film. The behaviour depends on the state.
				Movie movie = (Movie) grid.getAdapter().getItem(position);
				
				if(lockedMovies.contains(movie.getId()+"")){ //Locked movie
		            final Dialog dialog = new Dialog(FilmGridActivity.this, R.style.SlideDialog);
		            dialog.setContentView(R.layout.clapperdialog);
		            dialog.setCancelable(true);
		            //instantiate elements in the dialog
		            Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
		            Button unlockButton = (Button) dialog.findViewById(R.id.actionButton);
					TextView text = (TextView) dialog.findViewById(R.id.text);						
					//set values & actions
					final int idMovie = movie.getId();
					final int unlockSeconds = movie.getPoints()*50;
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
								update();
								List<User> users = new ArrayList<User>();
								users.add(user);
								uploadUsers(users);
							}
							else{
								//Dialog inviting to buy seconds
								dialog.dismiss();
								final Dialog dialogBuy = new Dialog(FilmGridActivity.this, R.style.SlideDialog);
								dialogBuy.setContentView(R.layout.clapperdialog);
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
				
				else if(unlockedMovies.contains(movie.getId()+"")){ //Solved movie: We show the film information
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
					myIntent.putExtras(myBundle);
					startActivity(myIntent);
				}
			}
		});		
	}
	
	private void uploadUsers(List<User> users){
		WebServiceTask wsUser = new WebServiceTask(WebServiceTask.POST_TASK);
		try {							
			JSONArray jsonArray = new JSONArray(new Gson().toJson(users));
			wsUser.addNameValuePair("users", jsonArray.toString());
			Log.i(this.toString(), jsonArray.toString());
	        wsUser.addNameValuePair("action", "UPDATE");        
	        wsUser.execute(new String[] {WebServiceTask.URL+"users"});		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	
}
