package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;

import com.facebook.android.friendsmash.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.adapter.MovieListAdapter;
import com.nappking.movietimesup.database.DBHelper;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class FilmGridActivity extends Activity{
	
	GridView grid;
	TextView txPoints;
	TextView txSeconds;
	User user;
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
		DBHelper helper = OpenHelperManager.getHelper(this, DBHelper.class);
		Dao<Movie, Integer> daoMovie;
		Dao<User, Integer> daoUser;
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
		MovieListAdapter movieAdapter = new MovieListAdapter(this, 
				android.R.layout.simple_list_item_1, movies);		
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
		            final Dialog dialog = new Dialog(FilmGridActivity.this, R.style.PauseDialog);
		            dialog.setContentView(R.layout.clapperdialog);
		            dialog.setCancelable(true);
					TextView text = (TextView) dialog.findViewById(R.id.text);
					text.setText("The movie #"+movie.getId()+" is locked!");
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
	
}
