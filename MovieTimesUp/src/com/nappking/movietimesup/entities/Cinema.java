package com.nappking.movietimesup.entities;

public class Cinema {
	private int id;
	private boolean unlocked;
	private int solvedMovies;
	
	public Cinema(int id, boolean unlocked, int solvedMovies){
		this.id = id;
		this.unlocked = unlocked;
		this.solvedMovies = solvedMovies;
	}	
	
	public int getId(){
		return id;
	}
	
	public boolean isUnlocked(){
		return unlocked;
	}
	
	public int getSolvedMovies(){
		return solvedMovies;
	}
}
