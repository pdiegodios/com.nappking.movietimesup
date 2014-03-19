package com.nappking.movietimesup.entities;

public class Achievement {
	private String field;
	private int goal;
	private int reward;
	private int resource;
	private int message;
	
	public Achievement(String field, int goal, int reward, int resource, int message){
		this.field=field;
		this.goal = goal;
		this.reward = reward;
		this.resource = resource;
		this.message = message;
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
	
	public int getMessage(){
		return message;
	}
}
