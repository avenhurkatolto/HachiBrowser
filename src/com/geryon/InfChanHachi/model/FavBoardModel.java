package com.geryon.InfChanHachi.model;

public class FavBoardModel {
	String name;
	String URL;
	
	public FavBoardModel (String inName, String inURL){
		this.name = inName;
		this.URL = inURL;
	}

	public String getName() {
		return name;
	}

	public String getURL() {
		return URL;
	}
	
	
}
