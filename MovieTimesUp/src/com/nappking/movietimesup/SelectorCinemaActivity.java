package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.nappking.movietimesup.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.nappking.movietimesup.adapter.CinemaListAdapter;
import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.task.LoadUserDataTask;
import com.nappking.movietimesup.widget.Cinema;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectorCinemaActivity extends DBActivity{
	public static final String POSITION="POSITION";
	
	private List<Cinema> mCinemas;
	private GridView grid;
	private ImageView lookfor;
	private Animation bounce;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_grid_cinema);		
     	grid = 	(GridView) findViewById(R.id.grid);	
     	lookfor = 	(ImageView) findViewById(R.id.lookfor);
		bounce = AnimationUtils.loadAnimation(this, R.anim.bouncing);
		update();
		setListeners();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		lookfor.startAnimation(bounce);
		update();
		updateUser();
	}
	
	@Override
	protected void onPause() {
		lookfor.clearAnimation();
		super.onPause();
	}

	private void updateUser(){
		Dao<User, Integer> daoUser;
		try {
			daoUser = getHelper().getUserDAO();
			int totalMovies = (int)getHelper().getMovieDAO().countOf();
			User user = daoUser.queryForId(1);
			if(user!=null){
				Calendar now = GregorianCalendar.getInstance();
				if((now.getTimeInMillis()>(((MovieTimesUpApplication)getApplication()).getLastUpdateCall()
						+MovieTimesUpApplication.TIME_FOR_SERVICE)) || (totalMovies>user.getMovies())){
					Log.i("UPDATE USER", "IT'S TIME TO CHECK WS");
					//It's more than 10min since last time it was updated or there are new movies
					((MovieTimesUpApplication)getApplication()).setLastUpdateCall(System.currentTimeMillis());
					new LoadUserDataTask(this, user).execute();
				}						
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createCinemas(){
		mCinemas = new ArrayList<Cinema>();
		int totalCinemas = 0;
		int unlockedCinemas = 0;
		try {
			Dao<Movie, Integer> daoMovie = getHelper().getMovieDAO();
			Dao<User, Integer> daoUser = getHelper().getUserDAO();
			GenericRawResults<String[]> rawResults = daoMovie.queryRaw("SELECT COUNT(DISTINCT "+Movie.CINEMA+") FROM "+Movie.TABLE);
			User user = daoUser.queryForId(1);
			unlockedCinemas = user.getCinemas();
			List<String> unlockedMovies = user.getUnlockedMovies();
			totalCinemas = Integer.parseInt(rawResults.getFirstResult()[0]);
			
			Cinema cinema;
			boolean unlocked;
			int solvedMovies;
			for(int index=1;index<=totalCinemas;index++){	
				if(index<=unlockedCinemas){
					unlocked=true;
					solvedMovies = 0;
					rawResults = daoMovie.queryRaw("SELECT "+Movie.ID+" FROM "+Movie.TABLE+" WHERE "+Movie.CINEMA+" = "+index);
					for(String[] resultColumns: rawResults){
						String movieId = resultColumns[0];
						if(unlockedMovies.contains(movieId))
							solvedMovies=solvedMovies+1;
					}
				}else{
					unlocked=false;
					solvedMovies=0;
				}
				
				cinema = new Cinema(index, unlocked, solvedMovies);
				mCinemas.add(cinema);
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void update(){     	
		//We obtain all the cinemas 
		createCinemas();
		//CinemaListAdapter to show correctly the cinemas
		CinemaListAdapter cinemaAdapter = new CinemaListAdapter(this, mCinemas);		
		grid.setAdapter(cinemaAdapter);
	}
	
	public void setListeners(){
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				//Click over each film. The behaviour depends on the state.
				Cinema cinema = (Cinema) grid.getAdapter().getItem(position);
				
				if(!cinema.isUnlocked()){ //Locked cinema
		            final Dialog dialog = new Dialog(SelectorCinemaActivity.this, R.style.SlideDialog);
		            dialog.setContentView(R.layout.dialog_clapper_option);
		            dialog.setCancelable(true);
		            //instantiate elements in the dialog
		            Button actionButton = (Button) dialog.findViewById(R.id.actionButton);
		            Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
		            actionButton.setVisibility(View.INVISIBLE);
		            cancelButton.setText(android.R.string.ok);
					TextView text = (TextView) dialog.findViewById(R.id.text);	
					text.setText("Cinema #"+cinema.getId()+" is locked.");
					cancelButton.setOnClickListener(new OnClickListener() {	//Action				
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
		            dialog.show();
				}				
				else{ //Movie ready to play: We start the bet and we send the movie to play
					Intent myIntent = new Intent(getBaseContext(),CinemaActivity.class);
					Bundle myBundle = new Bundle();
					myBundle.putInt(Cinema.class.toString(), cinema.getId());
					myIntent.putExtras(myBundle);
					startActivity(myIntent);
				}
			}
		});
	}
}
