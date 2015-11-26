package com.geryon.InfChanHachi.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.geryon.InfChanHachi.R;
import com.geryon.InfChanHachi.model.BoardModel;
import com.geryon.InfChanHachi.parsing.HachiJSONParser;
import com.geryon.InfChanHachi.sqlite.BoardsDAO;
import com.geryon.InfChanHachi.sqlite.FavBoardsDAO;
import com.geryon.InfChanHachi.volley.VolleySingleton;



public class Activity_Boards extends ActionBarActivity implements Response.Listener, Response.ErrorListener{

	String url = "http://8ch.net/boards.json";
	private ListView boards_list;
	private BoardAdapter boardAdapter;
	public static final String REQUEST_TAG = "Activity_Boards" ;
	//Temporary
	private static final String LOG_TAG = "Activity_Boards"; 
	private RequestQueue mQueue;
	private boolean loaded = false;
	private Menu mMenu;
	private SearchView mSearchView;
	private ActionBar mActionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_boards);
		boards_list = (ListView) findViewById(R.id.activity_boards_listview);
		mActionBar = getSupportActionBar();
		mActionBar.show();

	}
	@Override
	protected void onResume() {
		super.onResume();

		if (loaded == false){
			downloadBoards();
		} else {
			BoardsDAO BDAO = new BoardsDAO(getApplicationContext());
			ArrayList<BoardModel> boardArray = BDAO.getBoardsSFW();
			initializeList(boardArray);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {

		this.mMenu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_boards, menu);
		mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_boards_search));
		mSearchView.setOnQueryTextListener(mOnQueryTextListener);
		mSearchView.setIconifiedByDefault(true);
		mSearchView.setIconified(true);
		return true;
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_boards_search:
			Log.w("search","pressed");

			return true;
		case R.id.action_boards_help:

			Toast.makeText(getApplicationContext(), "HALP", Toast.LENGTH_SHORT).show(); 

			return true;
		case R.id.action_boards_refresh:
			if (mQueue != null) {
				mQueue.cancelAll(REQUEST_TAG);
			}
			Toast.makeText(getApplicationContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
			downloadBoards();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	protected void copyToDB(ArrayList<BoardModel> inBoardArray) {
		BoardsDAO BDAO = new BoardsDAO(getApplicationContext());
		BDAO.writeBoards(inBoardArray);
	}

	private void downloadBoards(){
		mQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		Log.w("Volley", "starting");
		BoardsDAO BDAO = new BoardsDAO(getApplicationContext());
		ArrayList<BoardModel> boardArray = BDAO.getBoardsSFW();
		initializeList(boardArray);
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.w("Download","Finished");
				ArrayList<BoardModel> boardArray;
				try {
					boardArray = HachiJSONParser.getBoardsInfo(response);
					Log.w("Doc-Parse", "complete");
					initializeList(boardArray);
					Log.w("Volley","Init_complete");
					copyToDB(boardArray);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					Log.w(LOG_TAG, "JsonParseException");
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					Log.w(LOG_TAG, "JsonMappingException");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.w(LOG_TAG, "IOException");
				}
			}
		},

		new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_LONG).show();
				/*Log.w("Volley-Error", error.toString());
				BoardsDAO BDAO = new BoardsDAO(getApplicationContext());
				ArrayList<BoardModel> boardArray = BDAO.getBoards();
				initializeList(boardArray);*/

			}
		});
		//RetryPolicy policy = new DefaultRetryPolicy(5000, 3, 2);
		//stringRequest.setRetryPolicy(policy);
		mQueue.add(stringRequest);
	}

	
	private void initializeList(ArrayList<BoardModel> inBoardArray){

		boardAdapter = new BoardAdapter(getBaseContext(), R.layout.boards_row, inBoardArray);
		boards_list.setAdapter(boardAdapter);
		boards_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getBaseContext(), boardAdapter.getItem(position).getUri(), Toast.LENGTH_SHORT).show();
				mQueue.cancelAll(REQUEST_TAG);
				Intent catalogIntent = new Intent(getApplicationContext(), Activity_Catalog.class);
				Log.w("URI", boardAdapter.getItem(position).getUri());
				catalogIntent.putExtra("intentUri", boardAdapter.getItem(position).getUri());
				catalogIntent.putExtra("title", boardAdapter.getItem(position).getTitle());
				startActivity(catalogIntent);
			}
		});
		boards_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				FavBoardsDAO FavBDao = new FavBoardsDAO(getApplicationContext());
				Log.w("longclick", boardAdapter.getItem(position).getTitle());
				if (FavBDao.writeFavB(boardAdapter.getItem(position).getTitle(), boardAdapter.getItem(position).getUri()).equalsIgnoreCase("success")){

					Toast.makeText(getApplicationContext(), "Item added", Toast.LENGTH_SHORT).show();
				} else {

					Toast.makeText(getApplicationContext(), "Item was already in the favourites", Toast.LENGTH_SHORT).show();
				}
			//	Log.w("Board-tag", boardAdapter.getItem(position).getFormattedTags());
				return true;
			}
		});

	}

	private class BoardAdapter extends ArrayAdapter<BoardModel>{
		Context context;

		public BoardAdapter(Context context, int resourceId,
				List<BoardModel> items) {
			super(context, resourceId, items);
			this.context = context;
		}

		/*private view holder class*/
		private class ViewHolder {
			TextView language;
			ImageView sfw;
			TextView BoardName;
			TextView BoardSubtitle;
			TextView BoardTags;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			BoardModel rowItem = getItem(position);

			/*LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);*/
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.boards_row, null);
				holder = new ViewHolder();
				holder.language = (TextView) convertView.findViewById(R.id.boards_row_image_language);
				holder.sfw = (ImageView) convertView.findViewById(R.id.boards_row_image_sfw);
				holder.BoardName = (TextView) convertView.findViewById(R.id.boards_row_textview_name);
				holder.BoardSubtitle = (TextView) convertView.findViewById(R.id.boards_row_textview_subtitle);
				holder.BoardTags = (TextView) convertView.findViewById(R.id.boards_row_textview_tags);
				convertView.setTag(holder);
			} else{
				holder = (ViewHolder) convertView.getTag();
			}

			holder.language.setText(rowItem.getLocale());
			holder.BoardName.setText("/" + rowItem.getUri()+"/ - "+rowItem.getTitle());
			holder.BoardSubtitle.setText( " subtitle: "+rowItem.getSubtitle());
			holder.BoardTags.setText(rowItem.getTagsString());

			if (rowItem.isSfw() == false){
				holder.sfw.setImageResource(R.drawable.nsfw);	
			} else {
				holder.sfw.setImageResource(R.drawable.sfw);
			}

			return convertView;
		} 
	}
	
	private final SearchView.OnQueryTextListener mOnQueryTextListener =
			new SearchView.OnQueryTextListener() {
		@Override
		public boolean onQueryTextChange(String newText) {
			newText = TextUtils.isEmpty(newText) ? "" : "Query so far: " + newText;

			return true;
		}
		@Override
		public boolean onQueryTextSubmit(String query) {
			Toast.makeText(Activity_Boards.this,
					"Searching for: " + query + "...", Toast.LENGTH_SHORT).show();
			//mSearchView.setIconified(true);
			mSearchView.clearFocus();
			BoardsDAO boardsDAO = new BoardsDAO(getApplicationContext());
			ArrayList<BoardModel> searchArray = new ArrayList<BoardModel>(boardsDAO.searchBoards(query));
			initializeList(searchArray);

			// collapse the action view
			if (mMenu != null) { 
				MenuItemCompat.collapseActionView(mMenu.findItem(R.id.action_boards_search));
			}

			return true;
		}
	};


	@Override
	public void onErrorResponse(VolleyError error) {
		Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onResponse(Object response) {
		Log.w("Sumthing", "happened");
	}

	@Override
	protected void onPause(){
		super.onPause();
		loaded = true;

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mQueue != null) {
			mQueue.cancelAll(REQUEST_TAG);
		}
		boardAdapter = null;
	}
}