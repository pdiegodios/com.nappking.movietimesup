package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.facebook.android.friendsmash.R;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.adapter.MovieListAdapter;
import com.nappking.movietimesup.database.DBHelper;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.task.WebServiceTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class FilmGridActivity extends Activity{
	
	GridView grid;
	TextView txPoints;
	TextView txSeconds;
	User user;
	DBHelper helper;
	Dao<Movie, Integer> daoMovie;
	Dao<User, Integer> daoUser;
	static final int result_sent = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.filmgrid);		
		//Hide the notification bar
     	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
     	//initiate elements
     	grid = (GridView) findViewById(R.id.grid);
     	txPoints = (TextView) findViewById(R.id.points);
     	txSeconds = (TextView) findViewById(R.id.seconds);
		helper = OpenHelperManager.getHelper(this, DBHelper.class);
		update();
		setListeners();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		update();
	}
	
	private void update(){     	
		//We obtain all the movies & the user
		ArrayList<Movie> movies = new ArrayList<Movie>();
		try {
			daoMovie = helper.getMovieDAO();
			daoUser = helper.getUserDAO();
			movies = (ArrayList<Movie>) daoMovie.queryForAll();
			user = (User) daoUser.queryForAll().get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		//MovieListAdapter to show correctly the movies
		MovieListAdapter movieAdapter = new MovieListAdapter(this, android.R.layout.simple_list_item_1, movies);		
		grid.setAdapter(movieAdapter);
		txPoints.setText(user.getScore()+"");
		txSeconds.setText(user.getSeconds()+"");		 
	}
	
	public void setListeners(){
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				//Click over each film. The behaviour depends on the state.
				Movie movie = (Movie) grid.getAdapter().getItem(position);
				//Context context = getBaseContext();
				if(movie.isLocked(FilmGridActivity.this)){
		            final Dialog dialog = new Dialog(FilmGridActivity.this, R.style.SlideDialog);
		            dialog.setContentView(R.layout.clapperdialog);
		            dialog.setCancelable(true);
		            //instantiate elements in the dialog
		            Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
		            Button unlockButton = (Button) dialog.findViewById(R.id.actionButton);
					TextView text = (TextView) dialog.findViewById(R.id.text);			
					TextView subText = (TextView) dialog.findViewById(R.id.subText);				
					//set values & actions
					final String idMovie = movie.getId()+"";
					final int unlockSeconds = movie.getPoints()*50;
					text.setText("Movie #"+idMovie+" is locked!");
					subText.setText("Unlock costs "+unlockSeconds+" seconds");
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
							if(secondsAvailable>unlockSeconds){
								//Unlock movie
								user.setSeconds(secondsAvailable-unlockSeconds);
								user.removeLockedMovie(idMovie);
								try {
									daoUser.update(user);
									uploadUsers(daoUser.queryForAll());
								} catch (SQLException e) {
									e.printStackTrace();
								}
								dialog.dismiss();
								update();
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
								text.setText("You don't have enough seconds");
								subText.setText("Buy seconds to beat your friends");
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
				else{
					Intent myIntent = new Intent(getBaseContext(), GameActivity.class);
					Bundle myBundle = new Bundle();
					myBundle.putSerializable(Movie.class.toString(), movie);
					myIntent.putExtras(myBundle);
					startActivityForResult(myIntent,result_sent);
				}
			}
		});		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {   
    	super.onActivityResult(requestCode, resultCode, data); 
    	switch(requestCode) { 
        	case (result_sent) :  
        		if (resultCode == RESULT_OK){ 
        			
        		}	
        		break; 
    	} 		
	}
	
	private void uploadUsers(List<User> users){
		WebServiceTask wsUser = new WebServiceTask(WebServiceTask.POST_TASK);
		Gson gson = new Gson();
		try {							
			JSONArray jsonArray = new JSONArray(gson.toJson(users));
			wsUser.addNameValuePair("users", jsonArray.toString());
			Log.i(this.toString(), jsonArray.toString());
	        wsUser.addNameValuePair("action", "UPDATE");        
	        wsUser.execute(new String[] {WebServiceTask.URL+"users"});		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
