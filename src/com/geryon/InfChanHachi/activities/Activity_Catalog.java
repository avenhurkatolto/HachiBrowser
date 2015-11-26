package com.geryon.InfChanHachi.activities;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.geryon.InfChanHachi.R;
import com.geryon.InfChanHachi.model.BoardModel;
import com.geryon.InfChanHachi.model.CatalogModel;
import com.geryon.InfChanHachi.model.CatalogThreadModel;
import com.geryon.InfChanHachi.parsing.HachiJSONParser;
import com.geryon.InfChanHachi.sqlite.BoardsDAO;
import com.geryon.InfChanHachi.volley.VolleySingleton;

public class Activity_Catalog extends ActionBarActivity{
	private ListView catalog_list;
	private GridView catalog_grid;
	private boolean isGridSelected = false;
	private CatalogAdapter catalogAdapter;
	private String boardName;
	public static final String REQUEST_TAG = "Activity_Catalog" ;
	//Temporary
	private static final String LOG_TAG = "Activity_Catalog";

	private RequestQueue mQueue;
	private String url;
	String uri;
	private SearchView mSearchView;
	private ActionBar mActionBar;
	private Menu mMenu;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_catalog);
		Intent intent = this.getIntent();

		uri = intent.getStringExtra("intentUri");

		url = "http://8ch.net/" +uri +"/catalog.json";
		Log.w(this.getClass().getName()+" uri",url);
		if (intent.hasExtra("title")){
			setTitle(intent.getStringExtra("title"));
			boardName = intent.getStringExtra("title");
		}


		catalog_list = (ListView) findViewById(R.id.activity_catalog_listview);
		catalog_grid = (GridView) findViewById(R.id.activity_catalog_gridview);
		mActionBar = getSupportActionBar();
		mActionBar.show();
		mQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		LoadCatalog();


	}

	private void LoadCatalog() {
		mQueue.cancelAll(REQUEST_TAG);

		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.i("Download","Finished");

				CatalogModel catalogModel;
				try {
					catalogModel = HachiJSONParser.getCatalogModel(response, url);
					initializeList(catalogModel);
					Log.w(LOG_TAG, String.valueOf(catalogModel.getThreadList().size()));
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
				Log.i("Catalog-Parse", "complete");
				Log.i("Volley","finished");

			}
		},

		new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getBaseContext(), "Something went FullMcIntosh!\nPlease check your connection and try again.", Toast.LENGTH_LONG).show();
				Log.w("Volley-Error", error.toString());
			}
		});
		//RetryPolicy policy = new DefaultRetryPolicy(5000, 3, 2);
		//stringRequest.setRetryPolicy(policy);
		mQueue.add(stringRequest);



	}

	private void initializeList(final CatalogModel inCatalogModel) {
		catalogAdapter = new CatalogAdapter(getBaseContext(), R.layout.boards_row, inCatalogModel.getThreadList());
		catalog_list.setAdapter(catalogAdapter);
		catalog_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getBaseContext(), inCatalogModel.getThreadList().get(position).getJsonUrl(uri), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getApplicationContext(), Activity_Thread.class);
				intent.putExtra("intentUrl", inCatalogModel.getThreadList().get(position).getJsonUrl(uri));
				intent.putExtra("title", inCatalogModel.getThreadList().get(position).getSub());
				intent.putExtra("uri", uri);
				startActivity(intent);
			}
		});
		catalog_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
				return true;
			}
		});

	}
	private class CatalogAdapter extends ArrayAdapter<CatalogThreadModel>{
		Context context;
		ImageLoader imageLoader = VolleySingleton.getInstance(context).getImageLoader();


		public CatalogAdapter(Context context, int resourceId,
				List<CatalogThreadModel> items) {
			super(context, resourceId, items);
			this.context = context;
		}

		/*private view holder class*/
		private class ViewHolder {
			SimpleDraweeView  image;
			TextView title;
			TextView description;
			TextView replies_and_images;

		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			CatalogThreadModel rowItem = getItem(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (imageLoader == null ){
				imageLoader = VolleySingleton.getInstance(context).getImageLoader();
			}
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.catalog_row, null);

				holder = new ViewHolder();
				holder.image = (SimpleDraweeView) convertView.findViewById(R.id.catalog_row_image);
				holder.title = (TextView) convertView.findViewById(R.id.catalog_row_title);
				holder.replies_and_images = (TextView) convertView.findViewById(R.id.catalog_row_replies_and_images);
				holder.description = (TextView) convertView.findViewById(R.id.catalog_row_content);
				convertView.setTag(holder);
			} else{
				holder = (ViewHolder) convertView.getTag();

			}


			//holder.image.setImageUrl(rowItem.getAttachment().getFullPathThumb(uri), imageLoader);
			holder.image.setImageURI(Uri.parse(rowItem.getAttachment().getFullPathThumb(uri)));
			holder.title.setText(rowItem.getSub());
			holder.description.setText(Html.fromHtml(rowItem.getCom()),TextView.BufferType.SPANNABLE);
			holder.replies_and_images.setText("R:"+rowItem.getReplies()+" / I:"+rowItem.getImages());

			return convertView;
		} 
	}
	private final SearchView.OnQueryTextListener mOnQueryTextListener =
			new SearchView.OnQueryTextListener() {

		@Override
		public boolean onQueryTextChange(String newText) {
			return true;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			Toast.makeText(Activity_Catalog.this,
					"Searching for: " + query + "...", Toast.LENGTH_SHORT).show();
			//mSearchView.setIconified(true);
			mSearchView.clearFocus();


			// collapse the action view
			if (mMenu != null) { 
				MenuItemCompat.collapseActionView(mMenu.findItem(R.id.action_boards_search));
			}

			return true;
		}
	};

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
		case R.id.action_catalog_search:
			Log.w("search","pressed");

			return true;
		case R.id.action_boards_refresh:
			if (mQueue != null) {
				mQueue.cancelAll(REQUEST_TAG);
				Log.w("Volley", "cancelling");
			}
			if (catalogAdapter != null){
				catalogAdapter.clear();
				catalogAdapter.notifyDataSetChanged();
			}
			LoadCatalog();	
			Toast.makeText(getApplicationContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
			return true;

		case R.id.action_catalog_help:
			Toast.makeText(getApplicationContext(), "HALP", Toast.LENGTH_SHORT).show();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mQueue != null) {
			mQueue.cancelAll(REQUEST_TAG);
			Log.w("Volley", "cancelling");
		}
	}
}