package com.geryon.InfChanHachi.activities;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geryon.InfChanHachi.R;
import com.geryon.InfChanHachi.model.ThreadModel;
import com.geryon.InfChanHachi.sqlite.ThreadDAO;

public class Activity_SavedThreads extends AppCompatActivity {
	private ListView mSavedThreadsListView;
	private SavedThreadsAdapter savedAdapter;
	private ThreadDAO threadDAO;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favboards);
		mSavedThreadsListView = (ListView) findViewById(R.id.activity_favboards_listview);
		threadDAO = new ThreadDAO(getApplicationContext());
		savedAdapter = new SavedThreadsAdapter(getBaseContext(), R.layout.saved_threads_row, threadDAO.getSavedThreads());
		mSavedThreadsListView.setAdapter(savedAdapter);
		mSavedThreadsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getBaseContext(), savedAdapter.getItem(position).getUrl(), Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(getApplicationContext(), Activity_Thread.class);
				intent.putExtra("SavedUrl", savedAdapter.getItem(position).getUrl());
				intent.putExtra("uri", savedAdapter.getItem(position).getParentboard());
				Log.w("uri2", savedAdapter.getItem(position).getParentboard());
				startActivity(intent);
			}
		});
	
	
	}
	
	private class SavedThreadsAdapter extends ArrayAdapter<ThreadModel>{
		Context context;
		


		public SavedThreadsAdapter(Context context, int resourceId,
				List<ThreadModel> items) {
			super(context, resourceId, items);
			this.context = context;
		}

		/*private view holder class*/
		private class ViewHolder {
			TextView title;
			}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			ThreadModel rowItem = getItem(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(LAYOUT_INFLATER_SERVICE);

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.saved_threads_row, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.savedthreadsrow_textview_threadname);
				convertView.setTag(holder);
			} else{
				holder = (ViewHolder) convertView.getTag();
							
			}
			holder.title.setText(rowItem.getTitle());
			return convertView;
		} 
	}
}
