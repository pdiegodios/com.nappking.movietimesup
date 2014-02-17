package com.nappking.movietimesup;

import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;

import com.facebook.android.friendsmash.R;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.task.WebServiceTask;
import com.nappking.movietimesup.widget.Clue;

import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FilmActivity extends DBActivity{
	private static int DEFAULT_TIME = 5000;
	
	//POINTS
	private static int GENRE = 15;
	private static int DATE = 12;
	private static int LOCATION = 6;
	private static int DIRECTOR = 20;
	private static int ACTOR = 15;
	private static int CHARACTER = 7;
	private static int TRIVIA = 5;
	private static int QUOTE = 7;
	private static int SYNOPSIS = 25;
	
	Movie movie;
	FrameLayout frame;
	InputMethodManager imm;
	Animation animFadeIn;
	Animation animFadeOut;
	Animation animZoomIn; 
	Animation animSlideInTop; 
	Animation animSlideInBottom; 
	Animation animSlideOutBottom;
	Animation animSlideOutTop; 
	Animation animSlideOutTopLifes; 
	AnimationDrawable transition;
	AnimationDrawable camerablink;
	AnimationListener listenerTitle;
	AnimationListener listenerLifes;
	AnimationListener listenerClose;
	AnimationListener listenerButtons;
	AnimationListener listenerHideLinear;
	MediaPlayer shotSound;
	LinearLayout linearClues;
	LinearLayout linearEnding;
	LinearLayout linearButtons;
	LinearLayout linearPrevious;
	ImageButton bgenre;
	ImageButton bdate;
	ImageButton blocation;
	ImageButton bdirector;
	ImageButton bactor;
	ImageButton bcharacter;
	ImageButton btrivia;
	ImageButton bquote;
	ImageButton bsynopsis;
	ImageView iAnswer;
	ImageView iLife1;
	ImageView iLife2;
	ImageView iLife3;
	ImageView iEnding;
	ImageView iCamera;
	TextView titleclue;
	TextView textclue;
	TextView endtext;
	TextView endsubtext;
	EditText title;
	
	//COUNTERS
	private int mCounterDate = 0;
	private int mCounterLocation = 0;
	private int mCounterSynopsis = 0;
	private int mCounterActor = 0;
	private int mCounterCharacter = 0;
	private int mCounterQuote = 0;
	private int mCounterOther = 0;
	private int mAttemps = 0;
	
	private ArrayList<Clue> mClues;
	private boolean mIsFinished = false;
	private boolean mInTime = false;
		
	//We start with some seconds to unveil some clues before bet
	private int mCurrentSeconds = 30;
	
	
	/**
	 *	LIFECYCLE METHODS 
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.movietemplate);		
		//Hide the notification bar && avoid to sleep or lock phone
     	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
     	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
     	//initiate elements
     	initiate();
		setListeners();
		displayInstructions();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(!mIsFinished){
			uploadUsers(true);	
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		//Show warning: This movie will be locked
		showLockWarning();
	}
	
	
	
	/**
	 * GAME ACTIONS METHODS
	 */
	
	
	private void displayInstructions(){
		//Show how to play and warnings about block screen 
		//TODO: Implement dialog
		showInitialWarning();
	}
	
	private void displayInitialClues(){
		camerablink.start();
		cleanClues();
		if(!mClues.isEmpty()){
			Random r = new Random();
			int selection = r.nextInt(mClues.size());
			Clue selectedClue = mClues.remove(selection);
			ImageButton button = selectedClue.getButton();
			mCurrentSeconds = mCurrentSeconds-selectedClue.getSeconds();
			cleanClues();
			button.performClick();
		    new Handler().postDelayed(new Runnable(){
		        @Override
		        public void run() {
		        	displayInitialClues();
		        }
		    }, DEFAULT_TIME);
		}else{
			mCurrentSeconds = 0;
			displaySelectedClue(null, null);
			camerablink.stop();
			makeBet();
		}
	}
	
	private void makeBet(){
		//Allow to bet your seconds to play during some seconds
		//TODO: Implement panel to bet
		startGame();
	}
	
	private void startGame(){
		this.linearPrevious.startAnimation(animFadeOut);
		
		//deploy title space and button to answer & buttons to get clues for seconds
		transition.start();
		this.linearButtons.startAnimation(animSlideInBottom);
		this.title.startAnimation(animSlideInTop);
		this.iAnswer.startAnimation(animSlideInTop);
		this.iLife1.startAnimation(animSlideInTop);
		this.iLife2.startAnimation(animSlideInTop);
		this.iLife3.startAnimation(animSlideInTop);
		
		this.linearButtons.setVisibility(View.VISIBLE);
		this.title.setVisibility(View.VISIBLE);
		this.iAnswer.setVisibility(View.VISIBLE);		
		this.iLife1.setVisibility(View.VISIBLE);
		this.iLife2.setVisibility(View.VISIBLE);		
		this.iLife3.setVisibility(View.VISIBLE);
	}
	
	private void endGame(){
		if(mInTime){
			//Unlock movie and add points to User score
			this.iEnding.setImageResource(R.drawable.happymask);
			this.endtext.setText(R.string.game_won);
			this.endsubtext.setText(getResources().getString(R.string.game_won_sub)+" "+
					movie.getPoints()+" "+getResources().getString(R.string.points));
			this.iAnswer.setImageResource(R.drawable.resolvetrue);
			uploadUsers(false);
		}
		else{
			//Lock movie
			this.iEnding.setImageResource(R.drawable.timesup);
			this.endtext.setText(null);
			this.endsubtext.setText(null);
			uploadUsers(true);
		}
		//undeploy title space and button to answer
		displaySelectedClue(null, null);
		this.iAnswer.startAnimation(animSlideOutTop);
		this.title.startAnimation(animSlideOutTop);
		this.linearEnding.startAnimation(animZoomIn);
		this.iEnding.startAnimation(animZoomIn);	
		this.linearEnding.setVisibility(View.VISIBLE);
		this.iEnding.setVisibility(View.VISIBLE);	
		
		mIsFinished=true;
	}
	
	private void displaySelectedClue(String title, String text){
		this.linearClues.setVisibility(View.INVISIBLE);
		this.titleclue.setText(title);
		this.textclue.setText(text);
		this.linearClues.startAnimation(animFadeIn);
		this.linearClues.setVisibility(View.VISIBLE);
	}
	
	
	
	

	/**
	 * AUXILIAR METHODS
	 */
	
	
	private void cleanClues(){
		ArrayList<Clue> cluesAvailable = new ArrayList<Clue>();
		for(Clue clue: mClues){
			if(clue.getSeconds()<=mCurrentSeconds){
				cluesAvailable.add(clue);
			}
		}
		mClues = cluesAvailable;
	}
	
	private String deAccent(String str) {
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
	
	
	
	
	/**
	 * COMPONENT BEHAVIOUR METHODS
	 */
	
	
	private void initiate(){
     	movie = 				(Movie) this.getIntent().getExtras().getSerializable(Movie.class.toString());
		imm = 					(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		animZoomIn = 			AnimationUtils.loadAnimation(this, R.anim.zoomin);
		animFadeIn = 			AnimationUtils.loadAnimation(this, R.anim.fadein);
		animFadeOut = 			AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
		animSlideInBottom = 	AnimationUtils.loadAnimation(this, R.anim.slideinbottom);
		animSlideOutBottom =	AnimationUtils.loadAnimation(this, R.anim.slideoutbottom);
		animSlideInTop = 		AnimationUtils.loadAnimation(this, R.anim.slideintop);
		animSlideOutTop = 		AnimationUtils.loadAnimation(this, R.anim.slideouttop);
		animSlideOutTopLifes = 	AnimationUtils.loadAnimation(this, R.anim.slideouttop);
     	frame = 				(FrameLayout) findViewById(R.id.frame);
     	linearClues = 			(LinearLayout) findViewById(R.id.clues);
     	linearEnding = 			(LinearLayout) findViewById(R.id.endline);
     	linearButtons = 		(LinearLayout) findViewById(R.id.buttons);
     	linearPrevious =		(LinearLayout) findViewById(R.id.previouslinear);
     	iLife1 = 				(ImageView) findViewById(R.id.life1);
     	iLife2 = 				(ImageView) findViewById(R.id.life2);
     	iLife3 = 				(ImageView) findViewById(R.id.life3);
     	iEnding = 				(ImageView) findViewById(R.id.endpicture);
     	iAnswer = 				(ImageView) findViewById(R.id.answer);
     	iCamera = 				(ImageView) findViewById(R.id.camera);
     	bgenre = 				(ImageButton) findViewById(R.id.genre);
     	bdate = 				(ImageButton) findViewById(R.id.date);
     	blocation = 			(ImageButton) findViewById(R.id.location);
     	bdirector = 			(ImageButton) findViewById(R.id.director);
     	bactor = 				(ImageButton) findViewById(R.id.actor);
     	bcharacter = 			(ImageButton) findViewById(R.id.character);
     	btrivia = 				(ImageButton) findViewById(R.id.others);
     	bquote = 				(ImageButton) findViewById(R.id.quotes);
     	bsynopsis = 			(ImageButton) findViewById(R.id.synopsis);
     	titleclue = 			(TextView) findViewById(R.id.titleclue);
     	textclue = 				(TextView) findViewById(R.id.textclue);
     	endtext = 				(TextView) findViewById(R.id.endtext);
     	endsubtext = 			(TextView) findViewById(R.id.endsubtext);
     	title = 				(EditText) findViewById(R.id.title);
     	title.setTextSize(14 * getResources().getDisplayMetrics().density);
     	transition = 			(AnimationDrawable) frame.getBackground();
     	camerablink = 			(AnimationDrawable) iCamera.getDrawable();
     	shotSound = 			MediaPlayer.create(this, R.raw.shot);
     	shotSound.setOnCompletionListener(new OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
				finish();
			}
     	});
		listenerTitle = new AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				iAnswer.setVisibility(View.INVISIBLE);
				title.setVisibility(View.INVISIBLE);
			}
		};
		listenerButtons = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				linearButtons.setVisibility(View.INVISIBLE);
			}
		};
		listenerClose = new AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(!mInTime){
					shotSound.start();
					iEnding.setImageResource(R.drawable.timesup_shot);
				}
				else{
					SystemClock.sleep(1200);
					finish();
				}
			}
		};
		listenerHideLinear = new AnimationListener() {		
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				linearPrevious.setVisibility(View.INVISIBLE);
			}
		};
		listenerLifes = new AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {
				iAnswer.setImageResource(R.drawable.resolvefalse);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(mAttemps>=3){
					iLife3.setVisibility(View.INVISIBLE);
					iLife2.setVisibility(View.INVISIBLE);
					iLife1.setVisibility(View.INVISIBLE);
					mInTime=false;
					endGame();
				}
				else {
					iAnswer.setImageResource(R.drawable.resolve);
					if(mAttemps==2){
						iLife2.setVisibility(View.INVISIBLE);
						iLife1.setVisibility(View.INVISIBLE);
					}	
					else if (mAttemps==1){
						iLife1.setVisibility(View.INVISIBLE);
					}
				}
			}
		};     	
		
		//Clues
		mClues = new ArrayList<Clue>();
		mClues.add(new Clue(bgenre, GENRE));
		mClues.add(new Clue(bdate, DATE));
		mClues.add(new Clue(blocation, LOCATION));
		mClues.add(new Clue(bdirector, DIRECTOR));
		mClues.add(new Clue(bactor, ACTOR));
		mClues.add(new Clue(bcharacter, CHARACTER));
		mClues.add(new Clue(bquote, QUOTE));
		mClues.add(new Clue(btrivia, TRIVIA));
		mClues.add(new Clue(bsynopsis, SYNOPSIS));
		
		//Initial visibility
     	linearClues.setVisibility(View.INVISIBLE);
     	linearButtons.setVisibility(View.INVISIBLE);
     	title.setVisibility(View.INVISIBLE);	
     	iAnswer.setVisibility(View.INVISIBLE);
		iLife1.setVisibility(View.INVISIBLE);
		iLife2.setVisibility(View.INVISIBLE);		
		iLife3.setVisibility(View.INVISIBLE);
     	iEnding.setVisibility(View.INVISIBLE);
     	linearEnding.setVisibility(View.INVISIBLE);
     	animFadeOut.setAnimationListener(listenerHideLinear);
		animSlideOutTop.setAnimationListener(listenerTitle);
		animSlideOutTopLifes.setAnimationListener(listenerLifes);
		animSlideOutBottom.setAnimationListener(listenerButtons);
		animZoomIn.setAnimationListener(listenerClose);		
	}
	
	private void setListeners(){
		bgenre.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(movie.getGenre()!=null){
					bgenre.setImageResource(R.drawable.ticket_empty);
					displaySelectedClue(getResources().getString(R.string.genre),movie.getGenre());
					movie.setGenre(null);
				}
			}
		});
		bdirector.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(movie.getDirector()!=null){
					bdirector.setImageResource(R.drawable.ticket_empty);
					displaySelectedClue(getResources().getString(R.string.director),movie.getDirector());
					movie.setDirector(null);
				}
			}
		});
		blocation.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mCounterLocation==0){
					mCounterLocation = 1;
					blocation.setImageResource(R.drawable.location1);
					displaySelectedClue(getResources().getString(R.string.continent),movie.getContinent());
				}
				else if (mCounterLocation ==1){
					mCounterLocation = 2;
					blocation.setImageResource(R.drawable.ticket_empty);
					displaySelectedClue(getResources().getString(R.string.country), movie.getCountry());
				}
			}
		});
		bdate.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mCounterDate==0){
					mCounterDate = 1;
					bdate.setImageResource(R.drawable.date1);
					int year = movie.getYear();
					year = year - (year % 10);
					displaySelectedClue(getResources().getString(R.string.decade),year+"");
				}
				else if (mCounterDate ==1){
					mCounterDate = 2;
					bdate.setImageResource(R.drawable.ticket_empty);
					displaySelectedClue(getResources().getString(R.string.year), movie.getYear()+"");
				}
			}
		});
		bactor.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mCounterActor<3){
					if(mCounterActor == 0)
						bactor.setImageResource(R.drawable.actor2);
					if(mCounterActor == 1)
						bactor.setImageResource(R.drawable.actor1);
					if(mCounterActor == 2)
						bactor.setImageResource(R.drawable.ticket_empty);
					
					displaySelectedClue(getResources().getString(R.string.actor), movie.getCast()[mCounterActor]);
					mCounterActor=mCounterActor+1;
				}
			}
		});
		bcharacter.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mCounterCharacter<3){
					if(mCounterCharacter == 0)
						bcharacter.setImageResource(R.drawable.character2);
					if(mCounterCharacter == 1)
						bcharacter.setImageResource(R.drawable.character1);
					if(mCounterCharacter == 2)
						bcharacter.setImageResource(R.drawable.ticket_empty);
					
					displaySelectedClue(getResources().getString(R.string.character), movie.getCharacters()[mCounterCharacter]);
					mCounterCharacter=mCounterCharacter+1;
				}
			}
		});
		bquote.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mCounterQuote<3){
					if(mCounterQuote == 0)
						bquote.setImageResource(R.drawable.quotes2);
					if(mCounterQuote == 1)
						bquote.setImageResource(R.drawable.quotes1);
					if(mCounterQuote == 2)
						bquote.setImageResource(R.drawable.ticket_empty);
					
					displaySelectedClue(getResources().getString(R.string.quote), movie.getQuotes()[mCounterQuote]);
					mCounterQuote=mCounterQuote+1;
				}
			}
		});
		btrivia.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mCounterOther<3){
					if(mCounterOther == 0)
						btrivia.setImageResource(R.drawable.others2);
					if(mCounterOther == 1)
						btrivia.setImageResource(R.drawable.others1);
					if(mCounterOther == 2)
						btrivia.setImageResource(R.drawable.ticket_empty);
					
					displaySelectedClue(getResources().getString(R.string.others), movie.getOthers()[mCounterOther]);
					mCounterOther=mCounterOther+1;
				}
			}
		});
		bsynopsis.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mCounterSynopsis<3){
					if(mCounterSynopsis == 0)
						bsynopsis.setImageResource(R.drawable.synopsis2);
					if(mCounterSynopsis == 1)
						bsynopsis.setImageResource(R.drawable.synopsis1);
					if(mCounterSynopsis == 2)
						bsynopsis.setImageResource(R.drawable.ticket_empty);
					int start=100*mCounterSynopsis;
					int end=start+100;
					if (movie.getPlot().length()<end){
						end=movie.getPlot().length();
					}
					String initialPoints="...";
					if (start==0)
						initialPoints="";
					displaySelectedClue(getResources().getString(R.string.synopsis), initialPoints+movie.getPlot().substring(start, end)+"...");
					mCounterSynopsis=mCounterSynopsis+1;
				}
			}
		});
		title.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				imm.showSoftInput(title, 0);
			}
		});
		iAnswer.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
				String titleAnswered = deAccent(title.getText().toString());
				String originalTitle = deAccent(movie.getOriginalTitle());
				String title = deAccent(movie.getTitle());
				String alternativeTitle = deAccent(movie.getAlternativeTitle());
				if(titleAnswered.equalsIgnoreCase(alternativeTitle)||
						titleAnswered.equalsIgnoreCase(originalTitle)||
						titleAnswered.equalsIgnoreCase(title)){
					//Correct Answer
					mInTime=true;
					endGame();
				}
				else{//Incorrect Answer
					mAttemps=mAttemps+1;
					if(mAttemps==1){
						iLife1.startAnimation(animSlideOutTopLifes);
					}
					else if(mAttemps==2){
						iLife2.startAnimation(animSlideOutTopLifes);
					}
					else if(mAttemps==3){
						iLife3.startAnimation(animSlideOutTopLifes);
					}
				}
			}
		});
	}	
	
	private void showInitialWarning(){
        final Dialog dialog = new Dialog(this, R.style.SlideDialog);
        dialog.setContentView(R.layout.clapperdialog);
        dialog.setCancelable(false);
        //instantiate elements in the dialog
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        Button playButton = (Button) dialog.findViewById(R.id.actionButton);
        playButton.setText(R.string.play);
		TextView text = (TextView) dialog.findViewById(R.id.text);			
		TextView subText = (TextView) dialog.findViewById(R.id.subText);				
		//set values & actions
		text.setText(getResources().getString(R.string.are_you_ready));
		subText.setText(getResources().getString(R.string.initial_warning));
		cancelButton.setOnClickListener(new OnClickListener() {	//Cancel				
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				mIsFinished=true;
				finish();
			}
		});
		playButton.setOnClickListener(new OnClickListener() {	//Unlock					
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				displayInitialClues();
			}
		});
        dialog.show();
	}
	
	private void showLockWarning(){
        final Dialog dialog = new Dialog(this, R.style.SlideDialog);
        dialog.setContentView(R.layout.clapperdialog);
        dialog.setCancelable(true);
        //instantiate elements in the dialog
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        Button lockButton = (Button) dialog.findViewById(R.id.actionButton);
        lockButton.setText(R.string.give_up);
		TextView text = (TextView) dialog.findViewById(R.id.text);			
		TextView subText = (TextView) dialog.findViewById(R.id.subText);				
		//set values & actions
		text.setText(getResources().getString(R.string.give_up_question));
		subText.setText(getResources().getString(R.string.movie_will_be_locked));
		cancelButton.setOnClickListener(new OnClickListener() {	//Cancel				
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		lockButton.setOnClickListener(new OnClickListener() {	//Unlock					
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();
			}
		});
        dialog.show();
	}

		
	
	/**
	 * BACKGROUND METHODS
	 */
	
	private void uploadUsers(boolean locked){
		try {
			List<User> users = new ArrayList<User>();
			Dao<User,Integer> daoUser = getHelper().getUserDAO();
			User user = daoUser.queryForId(1);
			if(locked){
				user.addLockedMovie(movie.getId());
			}
			else{
				user.addUnlockedMovie(movie.getId());
				user.setScore(user.getScore()+movie.getPoints());
			}
			user.setLastUpdate(System.currentTimeMillis());
			daoUser.update(user);
			users.add(user);			
			WebServiceTask wsUser = new WebServiceTask(WebServiceTask.POST_TASK);			
			JSONArray jsonArray = new JSONArray(new Gson().toJson(users));
			wsUser.addNameValuePair("users", jsonArray.toString());
			Log.i(this.toString(), jsonArray.toString());
	        wsUser.addNameValuePair("action", "UPDATE");        
	        wsUser.execute(new String[] {WebServiceTask.URL+"users"});	
		} catch (SQLException e) {
			e.printStackTrace();	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	
}
