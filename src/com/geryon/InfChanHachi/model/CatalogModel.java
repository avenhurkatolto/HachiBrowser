package com.geryon.InfChanHachi.model;


import java.util.ArrayList;

public class CatalogModel {
	private String url;
	private ArrayList<CatalogThreadModel> threadList;
	
	

	public CatalogModel(String url){
		this.url = url;
		threadList = new ArrayList<CatalogThreadModel>();
	}

	 
	
	


	public String getUrl() {
		return url;
	}

	/*public String getThreadURL(int numberOfThread){
		return this.url+"/"+threadList.get(numberOfThread).getNo()+".json";
	}*/

	public ArrayList<CatalogThreadModel> getThreadList() {
		return threadList;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public void setThreadList(ArrayList<CatalogThreadModel> threadList) {
		this.threadList = threadList;
	}

	public void setThread(CatalogThreadModel inCatalogThread){
		this.threadList.add(inCatalogThread);
	}

	public String getFormattedModel() {
		String output = this.url+"\n";
		
		for (int i=0; i<this.threadList.size();i++){
			output += this.threadList.get(i).getFormattedModel();

		}
		return output;
	};

}