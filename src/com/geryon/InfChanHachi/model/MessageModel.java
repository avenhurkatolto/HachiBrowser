package com.geryon.InfChanHachi.model;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;



public class MessageModel {
	private int no;
	private String sub;
	private String com;
	private String name;
	private long time;
	private String id;
	private ArrayList<AttachmentModel> attachments; 
	@JsonIgnore
	private ArrayList<Integer> replies;
	private String email;

	public MessageModel(){
		no = 0;
		sub = "";
		com = "";
		name = "";
		time = 0;
		id = "";
		this.attachments = new ArrayList<AttachmentModel>();
		replies = new ArrayList<Integer>();
		email = "";
	}

	public MessageModel(int inNo, String inCom,
			String inName, int inTime, String inID, String InEmail) {
		sub = "";
		replies = new ArrayList<Integer>();
		no = inNo;
		com = inCom;
		name = inName;
		time = inTime;
		id = inID;
		email = InEmail;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public int getNo() {
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

	public long getTime() {
		return time;
	}

	public String getId() {
		return id;
	}

	public void setNo(int no) {
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

	public void setTime(long time) {
		this.time = time;
	}

	public String getTimeFormatted(){
		return new java.util.Date(this.time*1000).toString();
	}
	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<AttachmentModel> getAttachments() {
		return attachments;
	}

	public void setAttachments(ArrayList<AttachmentModel> attachments) {
		this.attachments = attachments;
	}

	public void setAttachment(AttachmentModel attachment){
		this.attachments.add(attachment);
	}

	public String getFormattedModel(){
		return "no : "+this.no +"\n" 
				+"sub: "+this.sub +"\n"
				+"com: "+this.com +"\n"
				+"name: "+this.name +"\n"
				+"id: "+this.id + "\n"+
				getFormattedAttachments()
				+"\n\n";
	}

	private String getFormattedAttachments() {
		String out ="";
		for (int i = 0; i<this.attachments.size();i++){
			out += this.attachments.get(i).getFormattedTest();
		}
		return out;
	}

	public int getAttachmentSize(){
		return attachments.size();
	}

	public void addReply(int replyNum){
		this.replies.add(replyNum);
	}

	public ArrayList<Integer> getReplies(){
		return this.replies;
	}

	public String getRepliesText() {
		String replies = "Replies: ";
		for (int i = 0; i< this.replies.size();i++){
			replies += " >>"+this.replies.get(i);

		}
		return replies;
	} 

}
