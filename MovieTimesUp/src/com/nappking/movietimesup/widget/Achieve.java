package com.nappking.movietimesup.widget;

public class Achieve {
	private String field;
	private int goal;
	private int reward;
	private int resource;
	
	public Achieve(String field, int goal, int reward, int resource){
		this.field=field;
		this.goal = goal;
		this.reward = reward;
		this.resource = resource;
	}
	
	public String getField(){
		return field;
	}
	
	public int getGoal(){
		return goal;
	}
	
	public int getReward(){
		return reward;
	}
	
	public int getResource(){
		return resource;
	}
}
