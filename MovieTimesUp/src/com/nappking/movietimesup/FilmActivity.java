package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

import com.nappking.movietimesup.R;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.Achievement;
import com.nappking.movietimesup.entities.Clue;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.task.WebServiceTask;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class FilmActivity extends DBActivity{
	private static final String AMERICA = "AM";
	private static final String ASIA = "AS";
	private static final String EUROPE = "EU";
	private static final String AFRICA = "AF";
	private static final String OCEANIA = "OC";
	private static int SHORT_TIME = 2500;
	private static int LONG_TIME = 6500;
    private static final int TIME_TO_BET = 10000;
    private static final int INTERVAL = 100;
    
	//Seconds to unveil some clues before bet
	private int mCurrentSeconds = 30;
    
	//POINTS
	private static int GENRE = 10;
	private static int DATE = 10;
	private static int LOCATION = 5;
	private static int DIRECTOR = 15;
	private static int ACTOR = 15;
	private static int CHARACTER = 7;
	private static int TRIVIA = 10;
	private static int QUOTE = 8;
	private static int SYNOPSIS = 25;
	
	//COUNTERS
	private int mCounterDate = 0;
	private int mCounterLocation = 0;
	private int mCounterSynopsis = 0;
	private int mCounterActor = 0;
	private int mCounterCharacter = 0;
	private int mCounterQuote = 0;
	private int mCounterOther = 0;
	private int mAttemps = 0;
	
	Movie movie;
	FrameLayout frame;
	InputMethodManager imm;
	Animation animFadeIn;
	Animation animFadeOutInfo;
	Animation animFadeOut;
	Animation animZoomIn; 
	Animation animZoomOut; 
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
	AnimationListener listenerCloseVictory;
	AnimationListener listenerButtons;
	AnimationListener listenerHideLinear;
	CountDownTimer mCountDown;
	MediaPlayer applause;
	MediaPlayer closeSound;
	MediaPlayer projector;
	MediaPlayer beeps;
	LinearLayout linearClues;
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
	ImageView iPoints;
	ImageView iCamera;
	TextView textseconds;
	TextView texttitleclue;
	TextView textclue;
	TextView infogenre;
	TextView infodate;
	TextView infolocation;
	TextView infodirector;
	TextView infoactor;
	TextView infocharacter;
	TextView infotrivia;
	TextView infoquote;
	TextView infosynopsis;
	EditText title;
	
	private ArrayList<Clue> mClues;
	private List<String> quotes;
	private List<String> actors;
	private List<String> characters;
	private List<String> trivia;
	private boolean mIsFinished = false;
	private boolean mInTime = false;
	private List<Achievement> achievements;
	private int mIndex=-1;	
	private User mUser;
			
	
	//LIFECYCLE METHODS 
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_game_film);	
     	initiate();
		setListeners();
		showInitialWarning();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(!mIsFinished){
			mIsFinished=true;
			uploadUsers(true);	
		}
		releaseAll();
		if(!this.isFinishing()){
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		showLockWarning();
	}
	
	
	
	//GAME ACTIONS METHODS
	
	/**
	 * Display random clues while there are seconds available.
	 * After each clue the method clean all the clues more expensive than seconds still available.
	 */
	private void displayInitialClues(){
		if(!mIsFinished){
			cleanClues();
			if(!mClues.isEmpty()){
				Random r = new Random();
				int selection = r.nextInt(mClues.size());
				Clue selectedClue = mClues.remove(selection);
				ImageButton button = selectedClue.getButton();
				int time = selectedClue.getSecondsShown();
				button.performClick();
			    new Handler().postDelayed(new Runnable(){
			        @Override
			        public void run() {
			        	displayInitialClues();
			        }
			    }, time);
			}else{
				mCurrentSeconds = 0;
				displaySelectedClue(null, null, null, 0);
				camerablink.stop();
				makeBet();
			}
		}
	}
	
	/**
	 * Dialog to choose your seconds to play. It will be showed during 10 seconds,
	 * after that you will start to play.
	 */
	private void makeBet(){
		if(!mIsFinished){
			//Allow to bet your seconds to play during some seconds
	        final Dialog dialog = new Dialog(this, R.style.SlideDialog);
	        dialog.setContentView(R.layout.dialog_clapper_bet);
	        dialog.setCancelable(false);
	        //instantiate elements in the dialog
	        final NumberPicker secondsPicked = (NumberPicker) dialog.findViewById(R.id.picker);
	        secondsPicked.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
			final Animation fadeInText = AnimationUtils.loadAnimation(this, R.anim.fadein);
	        final TextView textLights = (TextView) dialog.findViewById(R.id.lightstext);
	        final TextView textCamera = (TextView) dialog.findViewById(R.id.cameratext);
	        final TextView textAction = (TextView) dialog.findViewById(R.id.actiontext);
	        final ImageView play = (ImageView) dialog.findViewById(R.id.play);
	        textLights.setVisibility(View.INVISIBLE);
	        textCamera.setVisibility(View.INVISIBLE);
	        textAction.setVisibility(View.INVISIBLE);
			fadeInText.setDuration(INTERVAL*4);		
	    	double limit = mUser.getSeconds();
	    	if(limit>500){
	    		limit = 500;
	    	}
	    	int iter = (int) Math.ceil(limit/5d);
	    	String[] values = new String[iter];
	    	for(int i=1; i<iter; i++){
	    		values[i-1] = Integer.toString(5*i);
	    	}
	    	values[iter-1] = Integer.toString((int)limit);
	    	secondsPicked.setDisplayedValues(values);
	    	secondsPicked.setMaxValue(iter-1);
	    	secondsPicked.setMinValue(0);
	    	int selection = 19;
	    	switch(movie.getPoints()){
	    	case 1: break;
	    	case 2: selection = selection+2;break;
	    	case 3: selection = selection+4;break;
	    	case 4: selection = selection+6;break;
	    	case 5: selection = selection+8;break;
	    	default: break;
	    	}
	    	if(iter>=selection){
	    		secondsPicked.setValue(selection);
	    	}    
	    	else{
	    		secondsPicked.setValue(iter);
	    	}
	        final ProgressBar progress = (ProgressBar) dialog.findViewById(R.id.progress);
	    	final int max = TIME_TO_BET/INTERVAL;
	    	final int half = max/2;
	    	progress.setMax(max);
	    	progress.setProgress(max);
	    	if(!this.isFinishing()){
	    		dialog.show();
	    	}
	        
	        mCountDown=new CountDownTimer(TIME_TO_BET,INTERVAL) {
	        	int currentProgress = max;
		        @Override
		        public void onTick(long millisUntilFinished) {
		        	if(currentProgress==max-15){
		        		textLights.setVisibility(View.VISIBLE);
		        		textLights.startAnimation(fadeInText);
		        	}
		        	else if(currentProgress==half){
		        		textCamera.setVisibility(View.VISIBLE);
		        		textCamera.startAnimation(fadeInText);
		        	}
		        	else if(currentProgress==15){
		        		textAction.setVisibility(View.VISIBLE);
		        		textAction.startAnimation(fadeInText);
		        	}
		        	if(currentProgress>0)
		        		currentProgress = currentProgress-1;
		            progress.setProgress(currentProgress);
		        }	
		        @Override
		        public void onFinish() {
			        progress.setProgress(0);
			        if(beeps!=null && beeps.isPlaying()){
		        		beeps.stop();
			        }
					dialog.dismiss();
					String[] values = secondsPicked.getDisplayedValues();
					mCurrentSeconds = Integer.parseInt(values[secondsPicked.getValue()]);
					mUser.setSeconds(mUser.getSeconds()-mCurrentSeconds);
					startGame();
			    }
			};
			mCountDown.start();
			beeps.start();
			
			play.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					if(mCountDown!=null){
						mCountDown.cancel();
						mCountDown.onFinish();
					}
				}
			});
		}
	}
	
	/**
	 * Deploy buttons to get new clues for seconds, lifes & space to write your
	 * answer. It starts the countdown to end your game with the seconds selected in the
	 * previous dialog (makeBet() method)
	 */
	private void startGame(){
		if(!mIsFinished){
			this.linearPrevious.startAnimation(animFadeOut);		
			//deploy title space and button to answer & buttons to get clues for seconds
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
					
			initClues();
			
	        mCountDown=new CountDownTimer((mCurrentSeconds+1)*1000,1000) {
	        	boolean lastSecondsSounding = false;
	        	boolean toFinish = true;
		        @Override
		        public void onTick(long millisUntilFinished) {
		        	if(mCurrentSeconds<11 && !lastSecondsSounding){
		        		lastSecondsSounding = true;
		        		beeps.reset();
		        		beeps = MediaPlayer.create(getBaseContext(), R.raw.final_beeps);
		        		beeps.start();
		        	}
		        	if(mCurrentSeconds<=0 && toFinish){
		        		textseconds.setText("");
		        		onFinish();
		        	}
		        	else if(toFinish){
		        		ArrayList<Clue> cluesToHide = cleanClues();
		        		for(Clue clue: cluesToHide){
		        			clue.getButton().setImageResource(R.drawable.ticket_empty);
		        			clue.getButton().setClickable(false);
		        		}
			            textseconds.setText(mCurrentSeconds+"");
			        	mCurrentSeconds = mCurrentSeconds-1;
		        	}
		        }	
		        @Override
		        public void onFinish() {
		        	if(toFinish){
		        		if(beeps!=null && beeps.isPlaying()){
			        		beeps.stop();
		        		}
		        		toFinish=false;
		        		this.cancel();
		        		if(!mIsFinished){
			        		mInTime=false;
			        		endGame();
		        		}
		        	}
			    }
			};
			mCountDown.start();
		}
	}
	
	/**
	 * Method call to finish game.
	 * If you call this method while game is not finished yet and you also got the correct
	 * answer in time -> you'll earn points for your movie.
	 * If you call this method after lose your lifes/opportunities or it is called out of time ->
	 * the movie will be blocked.
	 */
	private void endGame(){
		if(!mIsFinished){
			mIsFinished=true;
			if(mCountDown!=null){
				mCountDown.cancel();
			}	
			if(beeps!=null && beeps.isPlaying()){
				beeps.stop();
			}
			textseconds.setText("");
			this.iAnswer.setClickable(false);
			if(mInTime){ //USER WIN POINTS & UNLOCK THE MOVIE
				//Unlock movie and add points to User score
				this.iEnding.setImageResource(R.drawable.coin);
				int points = android.R.color.transparent;
				switch(movie.getPoints()){
					case 1: points = R.drawable.point1;break;
					case 2: points = R.drawable.point2;break;
					case 3: points = R.drawable.point3;break;
					case 4: points = R.drawable.point4;break;
					case 5: points = R.drawable.point5;break;
					default:break;
				}
				this.iPoints.setImageResource(points);
				this.iAnswer.setImageResource(R.drawable.resolvetrue);
				uploadUsers(false);
				applause = MediaPlayer.create(getBaseContext(), R.raw.applause);
				projector.stop();
				applause.start();
			}
			else{//USER LOSE & LOCK THE MOVIE
				//Lock movie
				this.iEnding.setImageResource(R.drawable.timesup);
				uploadUsers(true);
			}
			//undeploy title space and button to answer
			displaySelectedClue(null, null, null, 0);
			this.iAnswer.startAnimation(animSlideOutTop);
			this.title.startAnimation(animSlideOutTop);
			this.iEnding.startAnimation(animZoomIn);	
			this.iEnding.setVisibility(View.VISIBLE);
			Intent i = new Intent();
			i.putExtra(CinemaActivity.POSITION, mIndex);
			//putSerializable Achieve!!
			setResult(RESULT_OK, i);
		}
	}
	
	/**
	 * Display on screen the selected movie & update the countdown. Every clue costs
	 * an amount of seconds.
	 * @param title: title of clue. Ex.:GENRE/TRIVIA/DIRECTOR...
	 * @param text: Specific clue related with the movie. Ex.: Sci-Fi/2 Oscar Winner/Stanley Kubrick...
	 * @param textview: TextView to show seconds spent over clue button selected
	 * @param seconds: Seconds spent. The countdown will lose that amount of seconds
	 */
	private void displaySelectedClue(String title, String text, TextView textview, int seconds){
		if(!mIsFinished){
			this.linearClues.setVisibility(View.INVISIBLE);
			this.texttitleclue.setText(title);
			this.textclue.setText(text);
			if(textview!=null){
				textview.setText("-"+seconds+"s");
				textview.startAnimation(animFadeOutInfo);
			}
			this.linearClues.startAnimation(animFadeIn);
			this.linearClues.setVisibility(View.VISIBLE);
			this.mCurrentSeconds = mCurrentSeconds-seconds;
		}
	}
	
		

	//AUXILIAR METHODS
		
	private ArrayList<Clue> cleanClues(){
		ArrayList<Clue> cluesAvailable = new ArrayList<Clue>();
		ArrayList<Clue> cluesRemoved = new ArrayList<Clue>();
		for(Clue clue: mClues){
			if(clue.getSeconds()<=mCurrentSeconds){
				cluesAvailable.add(clue);
			}
			else{
				cluesRemoved.add(clue);
			}
		}
		mClues = cluesAvailable;
		return cluesRemoved;
	}	
	
	
	
	//COMPONENT BEHAVIOUR METHODS
	
	private void initiate(){
		try {
			Dao<User, Integer> daoUser = getHelper().getUserDAO();
			mUser = daoUser.queryForId(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
     	movie = 				(Movie) this.getIntent().getExtras().getSerializable(Movie.class.toString());
		mIndex = 				this.getIntent().getIntExtra(CinemaActivity.POSITION, -1);
		achievements = 			new ArrayList<Achievement>();
     	imm = 					(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		animZoomIn = 			AnimationUtils.loadAnimation(this, R.anim.zoomin);
		animZoomOut = 			AnimationUtils.loadAnimation(this, R.anim.zoomout);
		animFadeIn = 			AnimationUtils.loadAnimation(this, R.anim.fadein);
		animFadeOutInfo = 		AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
		animFadeOutInfo.setDuration(1500);
		animFadeOut = 			AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
		animSlideInBottom = 	AnimationUtils.loadAnimation(this, R.anim.slideinbottom);
		animSlideOutBottom =	AnimationUtils.loadAnimation(this, R.anim.slideoutbottom);
		animSlideInTop = 		AnimationUtils.loadAnimation(this, R.anim.slideintop);
		animSlideOutTop = 		AnimationUtils.loadAnimation(this, R.anim.slideouttop);
		animSlideOutTopLifes = 	AnimationUtils.loadAnimation(this, R.anim.slideouttop);
     	frame = 				(FrameLayout) findViewById(R.id.frame);
     	linearClues = 			(LinearLayout) findViewById(R.id.clues);
     	linearButtons = 		(LinearLayout) findViewById(R.id.buttons);
     	linearPrevious =		(LinearLayout) findViewById(R.id.previouslinear);
     	iLife1 = 				(ImageView) findViewById(R.id.life1);
     	iLife2 = 				(ImageView) findViewById(R.id.life2);
     	iLife3 = 				(ImageView) findViewById(R.id.life3);
     	iEnding = 				(ImageView) findViewById(R.id.endpicture);
     	iPoints = 				(ImageView) findViewById(R.id.points);
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
     	infogenre = 			(TextView) findViewById(R.id.genreinfo);
     	infodate = 				(TextView) findViewById(R.id.dateinfo);
     	infolocation = 			(TextView) findViewById(R.id.locationinfo);
     	infodirector = 			(TextView) findViewById(R.id.directorinfo);
     	infoactor = 			(TextView) findViewById(R.id.actorinfo);
     	infocharacter = 		(TextView) findViewById(R.id.characterinfo);
     	infotrivia = 			(TextView) findViewById(R.id.triviainfo);
     	infoquote = 			(TextView) findViewById(R.id.quoteinfo);
     	infosynopsis = 			(TextView) findViewById(R.id.synopsisinfo);
     	textseconds = 			(TextView) findViewById(R.id.seconds);
     	texttitleclue = 		(TextView) findViewById(R.id.titleclue);
     	textclue = 				(TextView) findViewById(R.id.textclue);
     	title = 				(EditText) findViewById(R.id.title);
     	transition = 			(AnimationDrawable) frame.getBackground();
     	camerablink = 			(AnimationDrawable) iCamera.getDrawable();   	
     	projector =				MediaPlayer.create(this, R.raw.projector);
     	beeps = 				MediaPlayer.create(this, R.raw.final_beeps);
     	final OnCompletionListener finishSound = new OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer mp) { //SHOW ACHIEVEMENTS & EXIT
				mp.stop();
				showEndingDialogs();
			}     		
     	};
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
					projector.stop();
					closeSound = MediaPlayer.create(getBaseContext(), R.raw.shot);
					closeSound.setOnCompletionListener(finishSound);
					closeSound.start();
					iEnding.setImageResource(R.drawable.timesup_shot);
				}
				else{
					iPoints.startAnimation(animZoomOut);
					iPoints.setVisibility(View.VISIBLE);
				}
			}
		};
		listenerCloseVictory = new AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(mInTime){
					closeSound = MediaPlayer.create(getBaseContext(), R.raw.coindrop);
					closeSound.setOnCompletionListener(finishSound);
					closeSound.start();
					applause.stop();
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
				iAnswer.setClickable(false);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(mAttemps>=3){
					iLife3.setVisibility(View.INVISIBLE);
					iLife2.setVisibility(View.INVISIBLE);
					iLife1.setVisibility(View.INVISIBLE);
					if(!mIsFinished){
						mInTime=false;
						endGame();
					}
				}
				else {
					title.setText("");
					iAnswer.setImageResource(R.drawable.resolve);
					iAnswer.setClickable(true);
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
		quotes = new ArrayList<String>();
		trivia = new ArrayList<String>();
		actors = new ArrayList<String>();
		characters = new ArrayList<String>();
		for(int i=0;i<3;i++){
			quotes.add(movie.getQuotes()[i]);
			trivia.add(movie.getOthers()[i]);
			actors.add(movie.getCast()[i]);
			characters.add(movie.getCharacters()[i]);
		}
		Collections.shuffle(quotes);
		Collections.shuffle(trivia);
		Collections.shuffle(actors);
		Collections.shuffle(characters);
		
		initClues();
		
		//Initial visibility
     	linearClues.setVisibility(View.INVISIBLE);
     	linearButtons.setVisibility(View.INVISIBLE);
     	title.setVisibility(View.INVISIBLE);	
     	iAnswer.setVisibility(View.INVISIBLE);
		iLife1.setVisibility(View.INVISIBLE);
		iLife2.setVisibility(View.INVISIBLE);		
		iLife3.setVisibility(View.INVISIBLE);
     	iEnding.setVisibility(View.INVISIBLE);
     	iPoints.setVisibility(View.INVISIBLE);
     	infogenre.setVisibility(View.INVISIBLE);
     	infodate.setVisibility(View.INVISIBLE);
     	infolocation.setVisibility(View.INVISIBLE);
     	infodirector.setVisibility(View.INVISIBLE);
     	infoactor.setVisibility(View.INVISIBLE);
     	infocharacter.setVisibility(View.INVISIBLE);
     	infotrivia.setVisibility(View.INVISIBLE);
     	infoquote.setVisibility(View.INVISIBLE);
     	infosynopsis.setVisibility(View.INVISIBLE);     	
	}
	
	private void initClues(){
		//Clues
		mClues = new ArrayList<Clue>();
		mClues.add(new Clue(bgenre, GENRE, SHORT_TIME));
		mClues.add(new Clue(bdate, DATE, SHORT_TIME));
		mClues.add(new Clue(blocation, LOCATION, SHORT_TIME));
		mClues.add(new Clue(bdirector, DIRECTOR, SHORT_TIME));
		mClues.add(new Clue(bactor, ACTOR, SHORT_TIME));
		mClues.add(new Clue(bcharacter, CHARACTER, SHORT_TIME));
		mClues.add(new Clue(bquote, QUOTE, LONG_TIME));
		mClues.add(new Clue(btrivia, TRIVIA, LONG_TIME));
		mClues.add(new Clue(bsynopsis, SYNOPSIS, LONG_TIME));
	}
	
	private void setListeners(){
     	animFadeOut.setAnimationListener(listenerHideLinear);
		animSlideOutTop.setAnimationListener(listenerTitle);
		animSlideOutTopLifes.setAnimationListener(listenerLifes);
		animSlideOutBottom.setAnimationListener(listenerButtons);
		animZoomIn.setAnimationListener(listenerClose);		
		animZoomOut.setAnimationListener(listenerCloseVictory);
		bgenre.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(movie.getGenre()!=null){
					bgenre.setImageResource(R.drawable.ticket_empty);
					displaySelectedClue(getResources().getString(R.string.genre).toUpperCase(Locale.getDefault()),
							movie.getGenre(), infogenre, GENRE);
					movie.setGenre(null);
				}
			}
		});
     	title.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE){
					iAnswer.performClick();
				}
				return false;
			}
     	});
		bdirector.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(movie.getDirector()!=null){
					bdirector.setImageResource(R.drawable.ticket_empty);
					displaySelectedClue(getResources().getString(R.string.director).toUpperCase(Locale.getDefault()),
							movie.getDirector(),infodirector, DIRECTOR);
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
					displaySelectedClue(getResources().getString(R.string.continent).toUpperCase(Locale.getDefault()),
							getContinent(movie.getContinent()),infolocation, LOCATION);
				}
				else if (mCounterLocation ==1){
					mCounterLocation = 2;
					blocation.setImageResource(R.drawable.ticket_empty);
					displaySelectedClue(getResources().getString(R.string.country).toUpperCase(Locale.getDefault()),
							movie.getCountry(),infolocation, LOCATION);
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
					if(year<2000){
						year=year-1900;
					}
					displaySelectedClue(getResources().getString(R.string.decade).toUpperCase(Locale.getDefault()),
							year+"'s", infodate, DATE);
				}
				else if (mCounterDate ==1){
					mCounterDate = 2;
					bdate.setImageResource(R.drawable.ticket_empty);
					displaySelectedClue(getResources().getString(R.string.year).toUpperCase(Locale.getDefault()), 
							movie.getYear()+"", infodate, DATE/2);
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
					
					displaySelectedClue(getResources().getString(R.string.actor).toUpperCase(Locale.getDefault()),
							actors.get(mCounterActor), infoactor, ACTOR);
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
					
					displaySelectedClue(getResources().getString(R.string.character).toUpperCase(Locale.getDefault()), 
							characters.get(mCounterCharacter), infocharacter, CHARACTER);
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
					
					displaySelectedClue(getResources().getString(R.string.quote).toUpperCase(Locale.getDefault()), 
							quotes.get(mCounterQuote), infoquote, QUOTE);
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
					
					displaySelectedClue(getResources().getString(R.string.trivia).toUpperCase(Locale.getDefault()), 
							trivia.get(mCounterOther), infotrivia, TRIVIA);
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
					displaySelectedClue(getResources().getString(R.string.synopsis).toUpperCase(Locale.getDefault()), 
							initialPoints+movie.getPlot().substring(start, end)+"...", infosynopsis, SYNOPSIS);
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
				String titleAnswered = ((MovieTimesUpApplication)getApplication()).deAccent(title.getText().toString());
				String originalTitle = ((MovieTimesUpApplication)getApplication()).deAccent(movie.getOriginalTitle());
				String title = ((MovieTimesUpApplication)getApplication()).deAccent(movie.getTitle());
				String alternativeTitle = ((MovieTimesUpApplication)getApplication()).deAccent(movie.getAlternativeTitle());
				if(titleAnswered.equalsIgnoreCase(alternativeTitle)||
						titleAnswered.equalsIgnoreCase(originalTitle)||
						titleAnswered.equalsIgnoreCase(title)){
					//Correct Answer
					if(!mIsFinished){
						mInTime=true;
						endGame();
					}
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
	
	/**
	 * Once you start to play if you stop/pause the game, you'll lose &
	 * the movie will be locked. 
	 */
	private void showInitialWarning(){
		if(!mIsFinished){
	        final Dialog dialog = new Dialog(this, R.style.SlideDialog);
	        dialog.setContentView(R.layout.dialog_clapper_option);
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
					Intent i = new Intent();
					i.putExtra(CinemaActivity.POSITION, mIndex);
					setResult(RESULT_CANCELED, i);
					finish();
				}
			});
			playButton.setOnClickListener(new OnClickListener() {	//play				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					camerablink.start();
					projector.setLooping(true);
					projector.start();
					transition.start();	
					displayInitialClues();
				}
			});
	        dialog.show();
		}
	}
	
	/**
	 * Showed if you are trying to leave this game while it is still running.
	 * Warning about this movie which will be locked.
	 */
	private void showLockWarning(){
		if(!mIsFinished){
	        final Dialog dialog = new Dialog(this, R.style.SlideDialog);
	        dialog.setContentView(R.layout.dialog_clapper_option);
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
			lockButton.setOnClickListener(new OnClickListener() {	//Lock					
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent i = new Intent();
					i.putExtra(CinemaActivity.POSITION, mIndex);
					setResult(RESULT_OK, i);
					finish();
				}
			});
	        dialog.show();
		}
	}

	
	private void showEndingDialogs(){
		if(achievements!=null && !achievements.isEmpty()){
			showAchievement(achievements.remove(0));
		}
		else{
			finish();
		}
	}
	
	private void showAchievement(Achievement a){  
        final Dialog dialog = new Dialog(this, R.style.SlideDialog);
        dialog.setContentView(R.layout.dialog_achievement);
        dialog.setCancelable(false);
        //instantiate elements in the dialog
        Button shareButton = (Button) dialog.findViewById(R.id.shareButton);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		TextView message = (TextView) dialog.findViewById(R.id.message);
		TextView foreground_text = (TextView) dialog.findViewById(R.id.foreground_text);
		ImageView image = (ImageView) dialog.findViewById(R.id.image_achievement);
		
		//set values & actions
		if(a.getField().equals(User.CULT)){
			title.setText(getResources().getString(R.string.new_cult_unlocked));
		}
		else if(a.getField().equals(User.MASTERPIECE)){
			title.setText(getResources().getString(R.string.new_masterpiece_unlocked));
		}
		else if(a.getField().equals(User.CINEMAS)){
			int cinemas = mUser.getTotalCinemas();
			title.setText(getResources().getString(R.string.level_number)+cinemas+" "+
					getResources().getString(R.string.unlocked)+"\n");
			foreground_text.setText(cinemas+"");
		}
		else{
			title.setText(getResources().getString(R.string.youvewon)+" "+a.getReward()+" "+
					getResources().getString(R.string.secondswon));
		}
		message.setText(getResources().getString(a.getMessage()));
		image.setImageResource(a.getResource());
		
		/*
		shareButton.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				//TODO: Share && then continue showEndingDialogs
			}
		});*/
		
		cancelButton.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				showEndingDialogs();
			}
		});
		dialog.show();
    }
	
	
	//BACKGROUND METHODS
	/**
	 * Method to send updated user to WS
	 * @param locked: the movie is now locked or if it is false you've won the points
	 */
	private void uploadUsers(boolean locked){
		try {
			List<User> users = new ArrayList<User>();
			Dao<User,Integer> daoUser = getHelper().getUserDAO();
			if(mUser==null){
				mUser = daoUser.queryForId(1);
			}
			if(locked){
				mUser.addLockedMovie(movie.getId());
			}
			else{
				String continent = movie.getContinent();
				String field="";
				int goal=0;
				mUser.addUnlockedMovie(movie.getId());
				mUser.setScore(mUser.getScore()+movie.getPoints());
				if(movie.getMasterpiece()){
					mUser.setMasterpiece(mUser.getMasterpiece()+1);
					achievements.add(new Achievement(User.MASTERPIECE, 0, 0, R.drawable.masterpiece_big, R.string.use_them_to_get_extras));
				}
				else if(movie.getCult()){
					mUser.setCult(mUser.getCult()+1);
					achievements.add(new Achievement(User.CULT, 0, 0, R.drawable.cult_movie_big, R.string.use_them_to_get_extras));
				}
				if(continent.equals(AMERICA)){
					mUser.setAmerica(mUser.getAmerica()+1);
					field = User.AMERICA;
					goal = mUser.getAmerica();
				}
				else if(continent.equals(EUROPE)){
					mUser.setEurope(mUser.getEurope()+1);
					field = User.EUROPE;
					goal = mUser.getEurope();
				}
				else if(continent.equals(ASIA)){
					mUser.setAsia(mUser.getAsia()+1);
					field = User.ASIA;
					goal = mUser.getAsia();
				}
				else{
					mUser.setExotic(mUser.getExotic()+1);
					field = User.EXOTIC;
					goal = mUser.getExotic();
				}
				int totalSolved = mUser.getTotalSolved();
				if((totalSolved % MovieTimesUpApplication.UNLOCK_LEVEL) == 0){ //Unlock new level
					achievements.add(new Achievement(User.CINEMAS, 0, 0, R.drawable.cinema_enable25, R.string.nextlevel));
					mUser.setSeconds(mUser.getSeconds()+MovieTimesUpApplication.SECONDS_FOR_LEVEL);
				}	
				if(!field.isEmpty() && goal>0){
					for(Achievement a: ((MovieTimesUpApplication)getApplication()).getAchievements()){
						if(a.getField().equals(field) && a.getGoal()==goal){
							//If user got an achievement it will get a reward
							achievements.add(a); 
							mUser.setSeconds(mUser.getSeconds()+a.getReward());
							break;
						}
					}
				}			
				//Post Score to Facebook
				postScore();				
			}
			mUser.setLastUpdate(System.currentTimeMillis());
			daoUser.update(mUser);
			users.add(mUser);			
			WebServiceTask wsUser = new WebServiceTask(WebServiceTask.POST_TASK);			
			JSONArray jsonArray = new JSONArray(new Gson().toJson(users));
			wsUser.addNameValuePair("users", jsonArray.toString());
			Log.i(this.toString(), jsonArray.toString());
	        wsUser.addNameValuePair("action", "UPDATE");        
	        wsUser.execute(new String[] {MovieTimesUpApplication.URL+"users"});	
		} catch (SQLException e) {
			e.printStackTrace();	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	
	
	private void postScore(){
		// Post the score to FB (for score stories and distribution)
		if(Session.getActiveSession()!=null){
			Bundle fbParams = new Bundle();
			fbParams.putString("score", "" + mUser.getScore());
			//fbParams.putString("access_token", "" + Session.getActiveSession().getAccessToken());
			Request postScoreRequest = new Request(Session.getActiveSession(),
				//mUser.getUser()+"/scores",
				"me/scores",
				fbParams,
                HttpMethod.POST,
                new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						FacebookRequestError error = response.getError();
						if (error != null) {
							Log.e(MovieTimesUpApplication.TAG, "Posting Score to Facebook failed: " + error.getErrorMessage());
						} else {
							Log.i(MovieTimesUpApplication.TAG, "Score posted successfully to Facebook");
						}
					}
				});
			Request.executeBatchAsync(postScoreRequest);
		}
	}
	
	private void releaseAll() {
		if(projector!=null){
			if(projector.isPlaying()){
				projector.stop();
			}
			projector.release();
		}
		if(applause!=null){
			if(applause.isPlaying()){
				applause.stop();
			}
			applause.release();
		}
		if(beeps!=null){
			if(beeps.isPlaying()){
				beeps.stop();
			}
			beeps.release();
		}
		if(closeSound!=null){
			if(closeSound.isPlaying()){
				closeSound.stop();
			}
			closeSound.release();
		}
		if(mCountDown!=null){
			mCountDown.cancel();
			mCountDown=null;
		}
		if(camerablink!=null){
			if(camerablink.isRunning()){
				camerablink.stop();
			}
		}
		if(transition!=null){
			if(transition.isRunning()){
				transition.stop();
			}
		}
		mClues.clear();
	}
	
	private String getContinent(String continent){
		int resource=R.string.america;
		if(continent.equals(ASIA)){
			resource= R.string.asia;
		}
		else if(continent.equals(EUROPE)){
			resource= R.string.europe;
		}
		else if(continent.equals(ASIA)){
			resource= R.string.asia;
		}
		else if(continent.equals(AFRICA)){
			resource= R.string.africa;
		}
		else if(continent.equals(OCEANIA)){
			resource= R.string.oceania;
		}
		return getString(resource);
	}
}
