package com.nappking.movietimesup;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.android.friendsmash.R;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;
import com.nappking.movietimesup.widget.AutoResizeTextView;

public class FirstFragment extends Fragment {
	
	View progressContainer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setRetainInstance(true);		
		// Hide the notification bar
		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_home_first, parent, false);
		progressContainer = v.findViewById(R.id.progressContainer);
		progressContainer.setVisibility(View.INVISIBLE);		
		// Set an error listener for the login button
		final ImageView loginButton = (ImageView) v.findViewById(R.id.loginButton);
		final AutoResizeTextView welcomeText = (AutoResizeTextView) v.findViewById(R.id.welcomeTextView);
		final ImageView closedGate = (ImageView) v.findViewById(R.id.closedGate);
		final Animation animSlideOut = AnimationUtils.loadAnimation(getActivity(), R.anim.slideouttop);
		animSlideOut.setDuration(3400);
		final LoginButton login = (LoginButton) v.findViewById(R.id.login);
		Animation bounce = AnimationUtils.loadAnimation(this.getActivity(), R.anim.bouncing);
     	final MediaPlayer slideSound = MediaPlayer.create(this.getActivity(), R.raw.slide_metal_gate);
     	slideSound.setOnCompletionListener(new OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
				login.performClick();
			}
     	});
		AnimationListener listenerClose = new AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {
				loginButton.setBackgroundResource(android.R.color.transparent);
				slideSound.start();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}			
			@Override
			public void onAnimationEnd(Animation animation) {
				closedGate.setVisibility(View.INVISIBLE);
			}
		};
		animSlideOut.setAnimationListener(listenerClose);

		if (loginButton != null && login!=null) {
			loginButton.startAnimation(bounce);
			loginButton.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {					
					welcomeText.setVisibility(View.INVISIBLE);
					closedGate.startAnimation(animSlideOut);
				}
			});
			login.setOnErrorListener(new OnErrorListener() {	
				@Override
				public void onError(FacebookException error) {
					if (error != null && !(error instanceof FacebookOperationCanceledException)) {
						// Failed probably due to network error (rather than user canceling dialog which would throw a FacebookOperationCanceledException)
						((HomeActivity)getActivity()).showError(getResources().getString(R.string.network_error), false);
					}
				}
				
			});
		}
		
		return v;
	}

}