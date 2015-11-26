package com.geryon.InfChanHachi.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.geryon.InfChanHachi.R;
import com.geryon.InfChanHachi.model.FavBoardModel;
import com.geryon.InfChanHachi.sqlite.FavBoardsDAO;

public class Activity_FavBoards extends AppCompatActivity {
	private ListView favboards_list;
	private FavBoardsAdapter FBAdapter;
	private ArrayList<FavBoardModel> FavBoards; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favboards);
		initializeList();



	}

	private void initializeList() {
		FavBoards = new FavBoardsDAO(getApplicationContext()).readFavB(); 
		FBAdapter = new FavBoardsAdapter(getApplicationContext(), R.layout.row_favboards, FavBoards);
		favboards_list = (ListView)findViewById(R.id.activity_favboards_listview);
		favboards_list.setAdapter(FBAdapter);
		
		favboards_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent catalogIntent = new Intent (getApplicationContext(), Activity_Catalog.class);
				catalogIntent.putExtra("intentUri", FBAdapter.getItem(position).getURL());
				catalogIntent.putExtra("title", FBAdapter.getItem(position).getName());
				startActivity(catalogIntent);
				
			}
		});


	}

	private class FavBoardsAdapter extends ArrayAdapter<FavBoardModel>{
		Context context;

		public FavBoardsAdapter(Context context, int resourceId,
				List<FavBoardModel> items) {
			super(context, resourceId, items);
			this.context = context;
		}

		private class ViewHolder {
			TextView name;
			ImageButton deleteButton;
		}	

		public View getView (final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			final FavBoardModel rowItem = getItem(position);
			
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(LAYOUT_INFLATER_SERVICE);

			if (convertView == null){
				convertView = mInflater.inflate(R.layout.row_favboards, null);
				holder = new ViewHolder();
				holder.name = (TextView)convertView.findViewById(R.id.row_favboards_textView);
				holder.deleteButton = (ImageButton)convertView.findViewById(R.id.row_favboards_deletebutton);
				holder.deleteButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						FavBoardsDAO FavBDao = new FavBoardsDAO(getApplicationContext());
						FavBDao.deleteFavB(rowItem.getName());
					//	Log.w("Name", rowItem.getName());
					//	Log.w("Favb_Size_before",String.valueOf(FavBoards.size()));
						//FavBoards.remove(getItem(position));
						
						
					//	Log.w("favb_size",String.valueOf(FavBoards.size()));
						//FBAdapter.remove(rowItem);
						
						//FBAdapter.addAll(FavBoards);
						initializeList();
					//	FBAdapter.notifyDataSetChanged();
					}
				});
				
				convertView.setTag(holder);	
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(rowItem.getName());
			return convertView;

		}
	}
}