package com.nappking.movietimesup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nappking.movietimesup.R;
import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.loader.ImageLoader;

public class FilmInfoActivity extends DBActivity{
	
	private ImageView mPosterImage;
	private ProgressBar mProgress;
	private TextView mTitle, mOriginalTitle, mGenre, mCountryYear, mDirector,  mActor, 
			mCharacter, mStoryline, mQuotes, mTrivia, mPoints;
	private ImageButton mFilmaffinityButton, mImdbButton; 
	private Movie mMovie;
	private ImageLoader mImageLoader;
	

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.moviescheme);		
     	//initiate elements
     	initiate();
		setListeners();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	private void initiate() {

     	mMovie = (Movie) this.getIntent().getExtras().getSerializable(Movie.class.toString());
		mImageLoader = new ImageLoader(this.getApplicationContext());
		
		//Instantiate
     	mTitle = (TextView) findViewById(R.id.title);
     	mOriginalTitle = (TextView) findViewById(R.id.originalTitle);
     	mGenre = (TextView) findViewById(R.id.genre_text);
     	mCountryYear = (TextView) findViewById(R.id.country_year);
     	mDirector = (TextView) findViewById(R.id.director_text);
     	mActor = (TextView) findViewById(R.id.actor_text);
     	mCharacter = (TextView) findViewById(R.id.character_text);
     	mQuotes = (TextView) findViewById(R.id.quotes_text);
     	mTrivia = (TextView) findViewById(R.id.trivia_text);
     	mStoryline = (TextView) findViewById(R.id.storyline_text);
     	mPoints = (TextView) findViewById(R.id.points);
     	mProgress = (ProgressBar) findViewById(R.id.progress);
     	mPosterImage = (ImageView) findViewById(R.id.poster);
		mFilmaffinityButton = (ImageButton) findViewById(R.id.filmaffinity);
		mImdbButton = (ImageButton) findViewById(R.id.imdb);
		
		//set fields from movie
		String actors = "";
		String characters = "";
		String quotes = "";
		String trivia = "";
		for(String actor:mMovie.getCast()){
			if(actors.isEmpty()){
				actors=actors+actor;
			}
			else{
				actors=actors+" | "+actor;
			}
		}
		for(String character:mMovie.getCharacters()){
			if(characters.isEmpty()){
				characters=characters+character;
			}
			else{
				characters=characters+" | "+character;
			}
		}
		for(String other:mMovie.getOthers()){
			if(trivia.isEmpty()){
				trivia=trivia+other;
			}
			else{
				trivia=trivia+"\n"+other;
			}
		}
		for(String quote:mMovie.getQuotes()){
			if(quotes.isEmpty()){
				quotes=quotes+"\""+quote+"\"";
			}
			else{
				quotes=quotes+" \n\""+quote+"\"";
			}
		}
        mImageLoader.DisplayImage(mMovie.getId(), mMovie.getPoster(), mPosterImage, mProgress);
		Bitmap poster = getBitmapPoster(mMovie.getId()); 
		mPosterImage.setImageBitmap(poster);
		mTitle.setText(mMovie.getTitle());		
		mPoints.setText(mMovie.getPoints()+"pts");		
		mOriginalTitle.setText(mMovie.getOriginalTitle());
		mGenre.setText(mMovie.getGenre());	
		mCountryYear.setText(mMovie.getCountry()+", "+mMovie.getYear());	
		mDirector.setText(mMovie.getDirector());	
		mActor.setText(actors);	
		mCharacter.setText(characters);	
		mQuotes.setText(quotes);
		mTrivia.setText(trivia);
		mStoryline.setText(mMovie.getPlot());	
	}

	private void setListeners() {
		// TODO SetListener to buttons
		mFilmaffinityButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				String url=mMovie.getFilmaffinityURL("es");
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		
		mImdbButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				String url=mMovie.getImdbURL();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
	}
	
	private Bitmap getBitmapPoster(int id) {
	    // To be safe, check if the SDCard is mounted
	    File path = null;
		Bitmap bmap = null;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File storageDir = new File(Environment.getExternalStorageDirectory()
		            + "/Android/data/"+ this.getApplicationContext().getPackageName()
		            + "/Posters"); 
		    // Create the storage directory if it does not exist
		    if (! storageDir.exists()){
		        if (! storageDir.mkdirs()){
		            return null;
		        }
		    } 
		    path = new File(storageDir.getPath() + File.separator + id);  
		    
			if(path.exists()){
			    try {
			        bmap = BitmapFactory.decodeStream(new FileInputStream(path));
			    } 
			    catch (FileNotFoundException e) {
			        e.printStackTrace();
			    }
			}
		}
	    return bmap;
	}
}
