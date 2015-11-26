package com.geryon.InfChanHachi.activities;

import net.bradgreco.directorypicker.DirectoryPicker;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.geryon.InfChanHachi.R;


public class Activity_Settings extends AppCompatActivity {
	private Button saveLocation;
	private TextView dir_textview; 


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings); 
		SharedPreferences prefs = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
		dir_textview = (TextView) findViewById(R.id.settings_textview_currentLocation);
		dir_textview.setText(prefs.getString("dlDir", ""));
		saveLocation = (Button) findViewById(R.id.settings_button_setlocation);
		saveLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), DirectoryPicker.class);
		    	startActivityForResult(intent, DirectoryPicker.PICK_DIRECTORY);
		    	
				
			}
		});
		
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == DirectoryPicker.PICK_DIRECTORY && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			String path = (String) extras.get(DirectoryPicker.CHOSEN_DIRECTORY);
			dir_textview = (TextView) findViewById(R.id.settings_textview_currentLocation);
			dir_textview.setText(path);
			SharedPreferences prefs = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("dlDir", path);
			editor.commit();


		}
	}
}
