package com.geryon.InfChanHachi.model;

import java.util.ArrayList;
import java.util.Arrays;

public class BoardModel {

	private String title;
	private String uri;
	private String sfw;
	private String subtitle;
	private String locale;
	private ArrayList<String> tags;

	public BoardModel(){
		title = "";
		uri = "";
		sfw = "";
		subtitle = "";
		locale = "";
		tags = new ArrayList<String>();
	}

	public BoardModel(String uri, String title, String subTitle,  String locale, ArrayList<String> tags, String sfw ){
		this.uri = uri;
		this.title = title;
		this.locale = locale;
		this.tags = tags;
		this.sfw = sfw;
		this.subtitle = subTitle;
	}

	public BoardModel(String uri, String title, String subTitle, String locale, String tags, String sfw ){
		this.uri = uri;
		this.title = title;
		this.locale = locale;
		this.tags = splitTags(tags);
		this.sfw = sfw;
		this.subtitle = subTitle;
	}

	public ArrayList<String> splitTags(String inTags) {
		ArrayList<String> splatTags = new ArrayList<String>();
		//splatTags.add(inTags);
		if (inTags.length()>1)
		{
			splatTags = new ArrayList<String>(Arrays.asList(inTags.substring(1, inTags.length()).split(">")));
		}
		return splatTags;		
	}

	public String MergeTags(){
		StringBuilder out = new StringBuilder();
		for (int i = 0; i< this.tags.size();i++){
			out.append(">"+this.tags.get(i));
		} 
		return out.toString();
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getSfw() {
		return sfw;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public String getTagsString(){
		//StringBuilder outTags = new StringBuilder("");
		/*for (int i = 0; i<this.tags.size();i++){
			outTags.append(this.tags.get(i)+", ");
		}*/
		return this.tags.toString();
	}

	public void setSfw(String sfw) {
		this.sfw = sfw;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFormattedString(){
		String out = "";
		out += this.title+"\n";
		out += this.subtitle+"\n";
		out += "uri "+this.uri+"\n";
		out += "locale "+this.locale+"\n";
		out += "sfw "+this.sfw+"\n";
		for (int i = 0; i<tags.size();i++){
			out += "tags "+tags.get(i)+"\n";
		}


		return out;
	}

	public String getFormattedTags(){
		StringBuilder outTags = new StringBuilder("");
		if (this.tags.size() > 0){
			outTags.append(this.tags.get(0));
			for (int i = 1; i<this.tags.size();i++){
				outTags.append(", "+this.tags.get(i));
			}

		}
		return outTags.toString();
	}

	public String getFullPath(){
		return "https://8ch.net/"+this.uri+"/catalog.json";

	}
	public Boolean isSfw(){
		if (!this.sfw.equals("0")){
			return true;
		} else {
			return false;
		}
	}


}
