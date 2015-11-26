package com.geryon.InfChanHachi.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.geryon.InfChanHachi.R;
/**
 * @version 0.1
 * @author Geryon
 * MainActivity. MenuItems so far: Save This Thread!; Favourites; Settings; 
 * Main Page; Thread Manager (this one needs some (a lot of) clarification), Help
 */
//TODO long-back, back functionality, how can one scroll to a specified post?

public class MainActivity extends Activity {
	private Button favBoards, boards, savedThreads, threadWatcher, settings, about;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fresco.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		favBoards = (Button) findViewById(R.id.button_main_favboards);
		boards = (Button)findViewById(R.id.button_main_boards);
		savedThreads = (Button) findViewById(R.id.button_main_savedThreads);
		//threadWatcher = (Button) findViewById(R.id.button_main_threadWatcher);
		settings = (Button) findViewById(R.id.button_main_settings);
		//about = (Button) findViewById(R.id.button_main_about);


		favBoards.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Toast.makeText(getBaseContext(), "FavB", Toast.LENGTH_SHORT).show();
				Intent FBintent = new Intent(getApplicationContext(), Activity_FavBoards.class);
				startActivity(FBintent);


			}
		});

		boards.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//	Toast.makeText(getBaseContext(), "boards", Toast.LENGTH_SHORT).show();
				Intent BoardIntent = new Intent(getBaseContext(), Activity_Boards.class);
				startActivity(BoardIntent);
			}
		});
		savedThreads.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent SavedThreadsIntent = new Intent(getApplicationContext(), Activity_SavedThreads.class);
				startActivity(SavedThreadsIntent);
				

			}
		});

	/*	threadWatcher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent ThreadWatcherIntent = new Intent(getApplicationContext(), Activity_ThreadWatcher.class);
				startActivity(ThreadWatcherIntent);

			}
		});*/


		settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent SettingIntent = new Intent(getBaseContext(), Activity_Settings.class);
				startActivity(SettingIntent);

			}
		});
		/*about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), "about", Toast.LENGTH_SHORT).show();

			}
		});*/
	}
	 
}