package com.nappking.movietimesup.entities;

import android.widget.ImageButton;

public class Clue{
	public final ImageButton button;
	public final int seconds;
	public final int seconds_shown;
	
	public Clue(ImageButton button, int seconds, int milliseconds){
		this.button = button;
		this.seconds = seconds;
		this.seconds_shown = milliseconds;
	}	
	
	public ImageButton getButton(){
		return button;
	}
	
	public int getSeconds(){
		return seconds;
	}
	
	public int getSecondsShown(){
		return seconds_shown;
	}
}
