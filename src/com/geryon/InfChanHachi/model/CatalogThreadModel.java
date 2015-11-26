package com.geryon.InfChanHachi.model;

import com.geryon.InfChanHachi.utils.Utils;


public class CatalogThreadModel {
	private String no;
	private String sub;
	private String com;
	private String name;
	private int replies;
	private int images;
	private AttachmentModel attachment;

	

	public CatalogThreadModel(){

	}

	public AttachmentModel getAttachment() {
		return attachment;
	}

	public void setAttachment(AttachmentModel attachment) {
		this.attachment = attachment;
	}
	
	public String getJsonUrl(String boardName){
		return Utils.const_serveraddress +"/"+boardName+"/res/"+this.no+".json";
	}
	
	public String getHtmlUrl(String boardName){
		return Utils.const_serveraddress +"/"+boardName+"/"+this.no+".html";
	}
		
	public String getNo() {
		return no;
	}

	public String getSub() {
		return sub;
	}

	public String getCom() {
		return com;
	}

	public String getName() {
		return name;
	}

	public int getReplies() {
		return replies;
	}

	public int getImages() {
		return images;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReplies(int replies) {
		this.replies = replies;
	}

	public void setImages(int images) {
		this.images = images;
	}

	public String getFormattedModel() {
		return "no: "+ this.no +"\n"+
				"sub: "+ this.sub +"\n"+
				"name: "+ this.name +"\n"+
				"com: "+ this.com +"\n"+
				"replies: "+ this.replies +"\n"+
				"images: "+ this.images +"\n\n";	

	};
}

