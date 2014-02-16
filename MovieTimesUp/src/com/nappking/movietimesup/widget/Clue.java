package com.nappking.movietimesup.widget;

import android.widget.ImageButton;

public class Clue{
	public final ImageButton button;
	public final int seconds;
	
	public Clue(ImageButton button, Integer seconds){
		this.button = button;
		this.seconds = seconds;
	}	
	
	public ImageButton getButton(){
		return button;
	}
	
	public int getSeconds(){
		return seconds;
	}
}
