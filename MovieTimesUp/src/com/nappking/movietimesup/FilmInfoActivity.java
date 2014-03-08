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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.android.friendsmash.R;
import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.loader.ImageLoader;

public class FilmInfoActivity extends DBActivity{
	
	private ImageView mPosterImage, mGenreImage, mDirectorImage, 
			mActorImage, mCharacterImage, mStorylineImage, mQuotesImage, mTriviaImage;
	private ProgressBar mProgress;
	private TextView mTitle, mOriginalTitle, mGenreHeader, mGenre, mCountryYear, mDirectorHeader, 
			mDirector, mActorHeader, mActor, mCharacter, mCharacterHeader, mStorylineHeader, 
			mStoryline, mQuotes, mQuotesHeader, mTrivia, mTriviaHeader, mPoints;
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

	private void initiate() {

     	mMovie = (Movie) this.getIntent().getExtras().getSerializable(Movie.class.toString());
		mImageLoader = new ImageLoader(this.getApplicationContext());
		
		//Instantiate
     	mTitle = (TextView) findViewById(R.id.title);
     	mOriginalTitle = (TextView) findViewById(R.id.originalTitle);
     	mGenreHeader = (TextView) findViewById(R.id.genre_header);
     	mGenre = (TextView) findViewById(R.id.genre_text);
     	mCountryYear = (TextView) findViewById(R.id.country_year);
     	mDirectorHeader = (TextView) findViewById(R.id.director_header);
     	mDirector = (TextView) findViewById(R.id.director_text);
     	mActorHeader = (TextView) findViewById(R.id.actor_header);
     	mActor = (TextView) findViewById(R.id.actor_text);
     	mCharacterHeader = (TextView) findViewById(R.id.character_header);
     	mCharacter = (TextView) findViewById(R.id.character_text);
     	mQuotesHeader = (TextView) findViewById(R.id.quotes_header);
     	mQuotes = (TextView) findViewById(R.id.quotes_text);
     	mTriviaHeader = (TextView) findViewById(R.id.trivia_header);
     	mTrivia = (TextView) findViewById(R.id.trivia_text);
     	mStorylineHeader = (TextView) findViewById(R.id.storyline_header);
     	mStoryline = (TextView) findViewById(R.id.storyline_text);
     	mPoints = (TextView) findViewById(R.id.points);
     	mProgress = (ProgressBar) findViewById(R.id.progress);
     	mPosterImage = (ImageView) findViewById(R.id.poster);
		mGenreImage = (ImageView) findViewById(R.id.genre_icon);
		mQuotesImage = (ImageView) findViewById(R.id.quotes_icon);
		mTriviaImage = (ImageView) findViewById(R.id.trivia_icon);
		mDirectorImage = (ImageView) findViewById(R.id.director_icon);
		mActorImage = (ImageView) findViewById(R.id.actor_icon);
		mCharacterImage = (ImageView) findViewById(R.id.character_icon);
		mStorylineImage = (ImageView) findViewById(R.id.storyline_icon);
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
