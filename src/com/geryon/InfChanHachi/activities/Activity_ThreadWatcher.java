package com.geryon.InfChanHachi.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.geryon.InfChanHachi.R;
import com.geryon.InfChanHachi.services.ThreadWatcherService;


public class Activity_ThreadWatcher extends AppCompatActivity {
	private ListView mThreadWatcherListView;
	private Button btn_startstop;
	private String LOG_TAG = this.getClass().getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_threadwatcher);
		mThreadWatcherListView = (ListView) findViewById(R.id.activity_threadwatcher_listview);
		btn_startstop = (Button) findViewById(R.id.button_threadwatcher_start);

	}

	@Override
	protected void onResume(){
		super.onResume();

		IntentFilter ServiceIntent_Getinfo= new IntentFilter(ThreadWatcherService.ACTION_GETINFO);
		LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(ServiceInfoReceiver, ServiceIntent_Getinfo);

		startService(new Intent(ThreadWatcherService.ACTION_GETINFO));



	}


	private BroadcastReceiver ServiceInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isServiceStarted = intent.getBooleanExtra("isStarted", false);
			if (isServiceStarted == true){
				setButtonToStop();

			} else {
				setButtonToStart();

			}
		}
	};  

	private void setButtonToStart(){
		btn_startstop.setText(R.string.threadatcher_button_start);
		btn_startstop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.w("startbutton", "has been pressed");
				startService(new Intent(ThreadWatcherService.ACTION_START));
				Log.w(LOG_TAG, "Start signal send");
				setButtonToStop();

			}
		});
	}

	private void setButtonToStop(){
		btn_startstop.setText(R.string.threadatcher_button_stop);
		btn_startstop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.w("startbutton", "has been pressed");
				startService(new Intent(ThreadWatcherService.ACTION_STOP));
				Log.w(LOG_TAG, "Stop signal send");
				setButtonToStart();

			}
		});		


	}


}