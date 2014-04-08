package com.nappking.movietimesup;

import java.sql.SQLException;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.widget.carousel.*;
import com.nappking.movietimesup.widget.carousel.CarouselAdapter.OnItemSelectedListener;

public class StatsActivity extends DBActivity{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carousel);
        Carousel carousel = (Carousel)findViewById(R.id.carousel);
        try {
        	final int totalMovies = (int)getHelper().getMovieDAO().countOf();
        	final int totalMoviesSolved = getHelper().getUserDAO().queryForId(1).getTotalSolved();
        	final int americanSolved = getHelper().getUserDAO().queryForId(1).getAmerica();
        	final int europeanSolved = getHelper().getUserDAO().queryForId(1).getEurope();
        	final int asianSolved = getHelper().getUserDAO().queryForId(1).getAsia();
        	final int exoticSolved = getHelper().getUserDAO().queryForId(1).getExotic();

	        carousel.setOnItemSelectedListener(new OnItemSelectedListener(){
				public void onItemSelected(CarouselAdapter<?> parent, View view, int position, long id) {	
					final ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
					final TextView number = (TextView)findViewById(R.id.number);
			        final TextView achieve_10 = (TextView)findViewById(R.id.achieve_10);		     			
			        final TextView achieve_25 = (TextView)findViewById(R.id.achieve_25);		     			
			        final TextView achieve_50 = (TextView)findViewById(R.id.achieve_50);		     			
			        final TextView achieve_100 = (TextView)findViewById(R.id.achieve_100);	     			
			        final LinearLayout prizes = (LinearLayout)findViewById(R.id.prizes);	        			
			        final TextView description = (TextView)findViewById(R.id.achieve_description);		        
					switch(position){
					case 0:
						progress.setMax(totalMovies);
						progress.setProgress(totalMoviesSolved);
						number.setText(totalMoviesSolved+"");
						description.setText(R.string.all_description);
						achieve_10.setText("10%");
						achieve_25.setText("25%");
						achieve_50.setText("50%");
						achieve_100.setText(totalMovies+"");					
						prizes.setVisibility(View.INVISIBLE);
						break;
					case 1:
						progress.setMax(200);
						if(americanSolved>200)
							progress.setProgress(200);
						else
							progress.setProgress(americanSolved);
						number.setText(americanSolved+"");
						description.setText(R.string.america_description);
						achieve_10.setText("20");
						achieve_25.setText("50");
						achieve_50.setText("100");
						achieve_100.setText("200");					
						prizes.setVisibility(View.VISIBLE);
						break;
					case 2:
						progress.setMax(50);
						if(europeanSolved>50)
							progress.setProgress(50);
						else
							progress.setProgress(europeanSolved);
						number.setText(europeanSolved+"");
						description.setText(R.string.europe_description);
						achieve_10.setText("5");
						achieve_25.setText("12");
						achieve_50.setText("25");
						achieve_100.setText("50");			
						prizes.setVisibility(View.VISIBLE);
						break;
					case 3:
						progress.setMax(30);
						if(asianSolved>30)
							progress.setProgress(30);
						else
							progress.setProgress(asianSolved);
						number.setText(asianSolved+"");
						description.setText(R.string.asia_description);
						achieve_10.setText("3");
						achieve_25.setText("7");
						achieve_50.setText("15");
						achieve_100.setText("30");			
						prizes.setVisibility(View.VISIBLE);
						break;
					case 4:
						progress.setMax(20);
						if(exoticSolved>20)
							progress.setProgress(20);
						else
							progress.setProgress(exoticSolved);
						number.setText(exoticSolved+"");
						description.setText(R.string.exotic_description);
						achieve_10.setText("2");
						achieve_25.setText("5");
						achieve_50.setText("10");
						achieve_100.setText("20");			
						prizes.setVisibility(View.VISIBLE);
						break;
					default:
						break;
					}
					
				}
	
				public void onNothingSelected(CarouselAdapter<?> parent) {
					
				}
	        	
	        }
	        );

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
}
