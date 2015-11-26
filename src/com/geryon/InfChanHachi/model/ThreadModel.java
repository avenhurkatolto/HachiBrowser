package com.geryon.InfChanHachi.model;
import java.util.ArrayList;

import android.util.Log;


public class ThreadModel {

	String url;
	String title;
	String parentboard;
	ArrayList<MessageModel> messages = new ArrayList<MessageModel>();

	public ThreadModel (String inUrl, String inTitle, String inBoardName){
		url = inUrl;
		title = inTitle;
		parentboard = inBoardName;
	}

	public void addMessage(MessageModel inMessage){
		this.messages.add(inMessage);
	}

	public void addMessagesBatched(ArrayList<MessageModel> inMessages){
		this.messages.addAll(inMessages);
	} 

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}
	public String getParentboard() {
		return parentboard;
	}

	public ArrayList<MessageModel> getMessages(){
		return this.messages;

	}

	public int getMessageCount(){
		return this.messages.size();

	}
	public void finalize(){
		Log.w("Threadmodel","finalize started");
		setReplies();
		Log.w("Threadmodel","finalize finished");

	}

	public int getThreadID(){
		return this.messages.get(0).getNo();
	}

	private void setReplies(){
		//String tempNum;
		for (int i = 1; i<getMessageCount()-1;i++){
			for (int j=i; j<getMessageCount();j++){

				if (this.getMessages().get(j).getCom().contains("&gt;&gt;"+String.valueOf(this.getMessages().get(i).getNo()))){
					//Log.w("Finalize", "added"+String.valueOf(this.getMessages().get(i).getNo()));
					this.getMessages().get(i).addReply(this.getMessages().get(j).getNo());
				}
			}
		}
	}		
}