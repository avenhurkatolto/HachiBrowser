package com.geryon.InfChanHachi.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.geryon.InfChanHachi.model.ThreadModel;
import com.geryon.InfChanHachi.parsing.HachiJSONParser;
import com.geryon.InfChanHachi.sqlite.ThreadDAO;
import com.geryon.InfChanHachi.utils.Utils;
import com.geryon.InfChanHachi.volley.VolleySingleton;


public class ThreadWatcherService extends Service {

	public static final String ACTION_START = "com.geryon.infchandroid.services.ThreadWatcherService.ACTION_START";
	public static final String ACTION_STOP = "com.geryon.infchandroid.services.ThreadWatcherService.ACTION_STOP";
	public static final String ACTION_UPDATE = "com.geryon.infchandroid.services.ThreadWatcherService.ACTION_UPDATE";
	public static final String ACTION_GETINFO = "com.geryon.infchandroid.services.ThreadWatcherService.ACTION_GETINFO";
	
	
	long refreshDelay = 5000;
	private RequestQueue mQueue;
	public static final String REQUEST_TAG = "ThreadWatcherService" ;
	private static final String LOG_TAG = "ThreadWatcherService";
	ArrayList<String> urls;
	boolean isServiceRunning = false;
	Handler handler = new Handler();
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@Override
	public void onCreate(){

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.w(REQUEST_TAG, "starting");
		String action = "";
		if (intent == null){
			stopSelf();
		}else {
			action = intent.getAction();
		}

		if (action.equals(ACTION_START)){
			start();

		} else if (action.equals(ACTION_STOP)){
			stop();
			stopSelf();
		} else if (action.equals(ACTION_UPDATE)){
			stop();
			start();
		} else if (action.equals(ACTION_GETINFO)){
			Log.w(LOG_TAG, "update");
			
			Intent WatcherInfoIntent = new Intent(ACTION_GETINFO);
		
			Random rand = new Random();
			WatcherInfoIntent.putExtra("isStarted", isServiceRunning );
			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(WatcherInfoIntent);
			if (isServiceRunning == false){
				stopSelf();
						
			}
		}
		return START_STICKY;

	}

	private void start(){
		mQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();	
		urls  = new ArrayList<String>(readURLsFromDB());
		Log.w("ThreadWatcherService","Started");
		handler.post(readThreads);
		isServiceRunning = true;
	}

	private void stop(){
		isServiceRunning = false;
		mQueue.cancelAll(REQUEST_TAG);
		handler.removeCallbacks(readThreads);
		
		}

	private Runnable readThreads = new Runnable() {

		@Override
		public void run() {
			Log.w("Runnable","run'd" );
			for (int i=0; i<urls.size(); i++){
				final String tempUrl = urls.get(i);
				StringRequest stringRequest = new StringRequest(Request.Method.GET, urls.get(i),
						new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.w("Download","Finished");
						Log.w("Doc-Parse", "complete");
						//ThreadParser threadParse = new ThreadParser();

						try {
							ThreadModel ThreadInfo = HachiJSONParser.getThreadModel(response, tempUrl, "whateffer", "");
						} catch (JsonParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Log.w("Boards-Parse", "complete");

						Log.w("Volley","Init_complete");
						//copyToDB(boardArray);
						Log.w("copytoDB","completed");
						Log.w("Volley","finished");

					}
				},

				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						//Fail silently

					}
				});
				//RetryPolicy policy = new DefaultRetryPolicy(5000, 3, 2);
				//stringRequest.setRetryPolicy(policy);
				mQueue.add(stringRequest);
			}
			handler.postDelayed(readThreads, refreshDelay);	
		}
	};




	private ArrayList<String> readURLsFromDB(){
		ArrayList<String> urls = new ArrayList<String>();
		ThreadDAO threadDAO = new ThreadDAO(getApplicationContext());
		ArrayList<ThreadModel> threadInfoArray = threadDAO.getWatchedThreads();
		for (int i = 0; i<threadInfoArray.size();i++){
			urls.add(threadInfoArray.get(i).getUrl());
		}
		return urls;

	}
	private void writeDataToDB(String inResponse){
		ThreadDAO threadDAO = new ThreadDAO(getApplicationContext());

	}
	/*
	
	private BroadcastReceiver infoReceiver = new BroadcastReceiver() {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent WatcherInfoIntent = new Intent(ACTION_GETINFO);
		Random rand = new Random();
		WatcherInfoIntent.putExtra("Data", rand.nextInt(50) );
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(WatcherInfoIntent);
		
	}
};*/
	
	
	@Override
	public void onDestroy(){
		Log.w(LOG_TAG, "Stopping");
		super.onDestroy();
	}

}