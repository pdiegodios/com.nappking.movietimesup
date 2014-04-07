package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nappking.movietimesup.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.nappking.movietimesup.adapter.CinemaGridAdapter;
import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.Cinema;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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
	private ImageView search;
	private Animation bounce;
    private AnimationDrawable statsAnimation;
    private int moviesToUnlock;
    private int unlockedCinemas;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_grid_cinema);		
     	grid = 	(GridView) findViewById(R.id.grid);	
     	search = (ImageView) findViewById(R.id.search);
		bounce = AnimationUtils.loadAnimation(this, R.anim.bouncing_bigger);
     	statsAnimation = (AnimationDrawable) findViewById(R.id.stats).getBackground();
		setListeners();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
     	statsAnimation.start();
		search.startAnimation(bounce);
		update();
		updateUser();
	}
	
	@Override
	protected void onPause() {
		search.clearAnimation();
     	statsAnimation.stop();
		super.onPause();
	}
	
	private void createCinemas(){
		mCinemas = new ArrayList<Cinema>();
		int totalCinemas = 0;
		try {
			Dao<Movie, Integer> daoMovie = getHelper().getMovieDAO();
			Dao<User, Integer> daoUser = getHelper().getUserDAO();
			GenericRawResults<String[]> rawResults = daoMovie.queryRaw("SELECT COUNT(DISTINCT "+Movie.CINEMA+") FROM "+Movie.TABLE);
			User user = daoUser.queryForId(1);
			unlockedCinemas = user.getTotalCinemas();
			List<String> unlockedMovies = user.getUnlockedMovies();
			totalCinemas = Integer.parseInt(rawResults.getFirstResult()[0]);
			
			moviesToUnlock = unlockedMovies.size() % MovieTimesUpApplication.UNLOCK_LEVEL;
			Cinema cinema;
			boolean unlocked;
			int solvedMovies;
			
			Log.i("UNLOCKED CINEMAS", unlockedCinemas+"");
			for(int index=1;index<=totalCinemas;index++){	
				if(index<=unlockedCinemas){
					unlocked=true;
					solvedMovies = 0;
					if(!unlockedMovies.isEmpty()){
						String ids = "(";
						for(String id: unlockedMovies){
							ids=ids+"\'"+id+"\',";
						}
						ids=ids.substring(0, ids.length()-1);
						ids=ids+")";
						Log.i("QUERY", "SELECT "+Movie.ID+" FROM "+Movie.TABLE+" WHERE "+Movie.CINEMA+" = "+index+
								" AND "+Movie.ID+" IN "+ids);
						rawResults = daoMovie.queryRaw("SELECT "+Movie.ID+" FROM "+Movie.TABLE+" WHERE "+Movie.CINEMA+" = "+index+
								" AND "+Movie.ID+" IN "+ids);
						solvedMovies=rawResults.getResults().size();
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
		CinemaGridAdapter cinemaAdapter = new CinemaGridAdapter(this, mCinemas);		
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
		            dialog.setContentView(R.layout.dialog_clapper_one_option);
		            dialog.setCancelable(true);
		            //instantiate elements in the dialog
		            Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
		            cancelButton.setText(android.R.string.ok);
					TextView text = (TextView) dialog.findViewById(R.id.text);	
					text.setText(getResources().getString(R.string.cinema_number)+cinema.getId()+" "+getResources().getString(R.string.is_locked)+"\n"+
							getResources().getString(R.string.need_hit)+" "+(moviesToUnlock+(cinema.getId()-unlockedCinemas)*12)+" "+getResources().getString(R.string.film_more));
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
		search.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getBaseContext(), FilmSearchActivity.class);
				startActivity(myIntent);				
			}
		});
	}
}
