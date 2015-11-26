package com.geryon.InfChanHachi.activities;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.geryon.InfChanHachi.R;
import com.geryon.InfChanHachi.model.AttachmentModel;
import com.geryon.InfChanHachi.model.MessageModel;
import com.geryon.InfChanHachi.model.ThreadModel;
import com.geryon.InfChanHachi.parsing.HachiJSONParser;
import com.geryon.InfChanHachi.sqlite.ThreadDAO;
import com.geryon.InfChanHachi.volley.VolleySingleton;


public class Activity_Thread extends AppCompatActivity {
	private String LOG_TAG;
	private ListView thread_list;
	private RequestQueue mQueue;
	private ThreadModel threadInfo;
	private String url;	
	private ThreadAdapter threadAdapter;
	//	private Menu menu;
	private ActionBar actionbar; 
	private TextView mSearchText;
	private String uri;
	private String title;
	private ThreadDAO threadDAO;
	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG_TAG = getClass().getName();
		setContentView(R.layout.activity_thread);
		thread_list = (ListView) findViewById(R.id.activity_thread_listview);
		actionbar = getSupportActionBar();
		actionbar.show();
		mQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
		Intent intent = this.getIntent();


		if (intent.hasExtra("intentUrl")){
			url = intent.getStringExtra("intentUrl"); 
			title = intent.getStringExtra("title");
			uri = intent.getStringExtra("uri");



			mQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
			StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
					new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					Log.w("Download","Finished: "+response.length());
					//threadInfo = ThreadParser.getThread(response, url);
					try {
						threadInfo = HachiJSONParser.getThreadModel(response, url, title, uri);
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
					Log.w("Thread-Parse", "complete");
					initializeList(threadInfo);
					//Toast.makeText(getApplicationContext(), String.valueOf(threadInfo.getMessages().size()), Toast.LENGTH_LONG).show();
					Log.w("Volley","finished");
					//showMenuSave();		
				}


			},

			new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(getBaseContext(), "Some error happened!\nPlease check your connection and try again.", Toast.LENGTH_LONG).show();
					Log.w("Volley-Error", error.toString());			}
			});
			//RetryPolicy policy = new DefaultRetryPolicy(5000, 3, 2);
			//stringRequest.setRetryPolicy(policy);
			mQueue.add(stringRequest);



		}else if (intent.hasExtra("SavedUrl")){
			Log.w("Loading from save", intent.getStringExtra("SavedUrl"));
			ThreadDAO threadDAO = new ThreadDAO(getApplicationContext());
			threadInfo = threadDAO.getThreadByUrl(intent.getStringExtra("SavedUrl"));

			uri = intent.getStringExtra("uri");
			Log.w("uri", uri );
			Log.w(LOG_TAG, "threadinfo number"+String.valueOf(threadInfo.getMessageCount()) );

			initializeList(threadInfo);
			//showMenuSave();

		}
	}

	protected void initializeList(ThreadModel inThreadInfo) {
		Log.w("ThreadProcessing","started");
		inThreadInfo.finalize();
		threadAdapter = new ThreadAdapter(getApplicationContext(), R.layout.thread_row, inThreadInfo.getMessages());
		this.setTitle(inThreadInfo.getTitle());
		thread_list.setAdapter(threadAdapter);
		thread_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getBaseContext(), String.valueOf(threadAdapter.getItem(position).getAttachmentSize()), Toast.LENGTH_SHORT).show();

			}
		});
		thread_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		Log.w("ThreadProcessing","finished");
	}

	private class ThreadAdapter extends ArrayAdapter<MessageModel> {
		Context context;
		ImageLoader imageLoader = VolleySingleton.getInstance(context).getImageLoader();

		public ThreadAdapter(Context context, int resourceId,
				List<MessageModel> items) {
			super(context, resourceId, items);
			this.context = context;
		}

		private class ViewHolder{
			SimpleDraweeView image1;
			SimpleDraweeView image2;
			SimpleDraweeView image3;
			SimpleDraweeView image4;
			SimpleDraweeView image5;
			TextView name;
			TextView id;
			TextView datetime;
			TextView message;
			TextView messageNumber;
			TextView replies;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final MessageModel rowItem = getItem(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			if (imageLoader == null ){
				imageLoader = VolleySingleton.getInstance(context).getImageLoader();
			}
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.thread_row, null);


				holder = new ViewHolder();
				holder.image1 = (SimpleDraweeView)convertView.findViewById(R.id.threadrow_image1);
				holder.image2 = (SimpleDraweeView)convertView.findViewById(R.id.threadrow_image2);
				holder.image3 = (SimpleDraweeView)convertView.findViewById(R.id.threadrow_image3);
				holder.image4 = (SimpleDraweeView)convertView.findViewById(R.id.threadrow_image4);
				holder.image5 = (SimpleDraweeView)convertView.findViewById(R.id.threadrow_image5);
				holder.datetime = (TextView)convertView.findViewById(R.id.threadRow_datetime);
				holder.messageNumber = (TextView)convertView.findViewById(R.id.threadRow_messagecounter);
				holder.name =( TextView) convertView.findViewById(R.id.threadRow_name);
				holder.message = (TextView)convertView.findViewById(R.id.threadRow_message);
				holder.replies = (TextView)convertView.findViewById(R.id.threadRow_replies);
				holder.id = (TextView)convertView.findViewById(R.id.threadRow_id);
				convertView.setTag(holder);
			} else{
				holder = (ViewHolder) convertView.getTag();

			}

			if (!rowItem.getName().equalsIgnoreCase("")){
				holder.name.setText(rowItem.getName());
				holder.name.setVisibility(View.VISIBLE);
			} else {
				holder.name.setVisibility(View.GONE);
			}

			holder.image1.setVisibility(View.GONE);
			holder.image2.setVisibility(View.GONE);
			holder.image3.setVisibility(View.GONE);
			holder.image4.setVisibility(View.GONE);
			holder.image5.setVisibility(View.GONE);
			//Abusing that switch fall-through like there's no tomorrow, will need to cook up something more elegant (and flexible!) if/when more image can be uploaded

			switch (rowItem.getAttachmentSize()){
			case 5:{
				holder.image5.setVisibility(View.VISIBLE);

				holder.image5.setImageURI(Uri.parse(rowItem.getAttachments().get(4).getFullPathThumb(uri)));


			}
			case 4:{
				holder.image4.setVisibility(View.VISIBLE);

				holder.image4.setImageURI(Uri.parse(rowItem.getAttachments().get(3).getFullPathThumb(uri)));

			}
			case 3:{
				holder.image3.setVisibility(View.VISIBLE);

				holder.image3.setImageURI(Uri.parse(rowItem.getAttachments().get(2).getFullPathThumb(uri)));

			}
			case 2:{
				holder.image2.setVisibility(View.VISIBLE);

				holder.image2.setImageURI(Uri.parse(rowItem.getAttachments().get(1).getFullPathThumb(uri)));

			}
			case 1:{
				holder.image1.setVisibility(View.VISIBLE);

				holder.image1.setImageURI(Uri.parse(rowItem.getAttachments().get(0).getFullPathThumb(uri)));
				holder.image1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						/*Intent attachIntent = new Intent(Intent.ACTION_VIEW);
						attachIntent.setDataAndType(Uri.parse(rowItem.getAttachments().get(0).getFullPathImage(uri)), "image/*");
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rowItem.getAttachments().get(0).getFullPathImage(uri)))); /** replace with your own uri */
						//Toast.makeText(getApplicationContext(), rowItem.getAttachments().get(0).getFullPathImage(uri), Toast.LENGTH_SHORT).show();
						showDialog(rowItem.getAttachments().get(0));



					}
				});
				holder.image1.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						Toast.makeText(getApplicationContext(), rowItem.getAttachments().get(0).getFullPathThumb(uri), Toast.LENGTH_SHORT).show();
						return false;
					}
				});
			}
			break;
			default:break;
			}

			holder.id.setText(rowItem.getId());
			//holder.datetime.setText(String.valueOf(rowItem.getTime()));
			holder.datetime.setText(rowItem.getTimeFormatted());
			holder.messageNumber.setText(String.valueOf(rowItem.getNo()));
			holder.message.setText(Html.fromHtml(rowItem.getCom()), TextView.BufferType.SPANNABLE);
			holder.replies.setText(rowItem.getRepliesText());

			return convertView;
		}

	}

	/*private void showMenuSave(){
		MenuItem item = menu.findItem(R.id.menu_save_thread);
		item.setVisible(true);
		this.supportInvalidateOptionsMenu();
	}

	private void hideMenuSave(){
		MenuItem item = menu.findItem(R.id.menu_save_thread);
		item.setVisible(false);
		this.supportInvalidateOptionsMenu();
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_thread, menu);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_thread_search));
		searchView.setOnQueryTextListener(mOnQueryTextListener);
		return true;
	}
	//TODO Finish attachments 
	private void showDialog(AttachmentModel attachment){
		if (attachment.getContentType().equalsIgnoreCase("image")){		

			AlertDialog.Builder alertadd = new AlertDialog.Builder(Activity_Thread.this);
			LayoutInflater factory = LayoutInflater.from(Activity_Thread.this);
			View view = factory.inflate(R.layout.dialog_custom_image, null);


			SimpleDraweeView image = (SimpleDraweeView) view.findViewById(R.id.dialog_image);
			DraweeController controller = Fresco.newDraweeControllerBuilder()
					.setUri(Uri.parse(attachment.getFullPathImage(uri)))
					.setAutoPlayAnimations(true)
					.build();
			image.setController(controller);



			Log.w("dialog-URL",attachment.getFullPathImage(uri) );
			//image.setImageURI(Uri.parse(attachment.getFullPathImage(uri)));


			alertadd.setTitle(attachment.getFilename()+attachment.getExt());
			alertadd.setView(view);
			alertadd.show();
		} else  if (attachment.getContentType().equalsIgnoreCase("embed")){
			Toast.makeText(getApplicationContext(), attachment.getExt(), Toast.LENGTH_SHORT).show();
		} else if (attachment.getContentType().equalsIgnoreCase("pdf")) {

			Toast.makeText(getApplicationContext(), 
					"Downloading started to the specified folder, you will be noticed when it's ready.", 
					Toast.LENGTH_LONG).show();

			fireDownloadManager(attachment.getFullPathImage(uri), attachment.getFilename());
		} else if (attachment.getContentType().equalsIgnoreCase("webm")){
			Toast.makeText(getApplicationContext(), "webm", Toast.LENGTH_SHORT).show();
		} else{
			Toast.makeText(getApplicationContext(), "Sumthin else", Toast.LENGTH_SHORT).show();
		}
	}


	private void fireDownloadManager(String requestUri, final String filename) {
		//Fire download
		final DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		final long enqueue;
		android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(
				Uri.parse(requestUri));
		enqueue = dm.enqueue(request);

		//start broadcastreceiver
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
					long downloadId = intent.getLongExtra(
							DownloadManager.EXTRA_DOWNLOAD_ID, 0);
					Query query = new Query();
					query.setFilterById(enqueue);
					Cursor c = dm.query(query);
					if (c.moveToFirst()) {
						int columnIndex = c
								.getColumnIndex(DownloadManager.COLUMN_STATUS);
						if (DownloadManager.STATUS_SUCCESSFUL == c
								.getInt(columnIndex)) {

							//TODO something
							String uriString = c
									.getString(c
											.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
							//	view.setImageURI(Uri.parse(uriString));
							SharedPreferences prefs = context.getSharedPreferences(Activity_Thread.class.getName(), MODE_PRIVATE);
							String dlDir = prefs.getString("dlDir", Environment.getExternalStorageDirectory().toString());
							File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ filename);
							Intent target = new Intent(Intent.ACTION_VIEW);
							target.setDataAndType(Uri.fromFile(file),"application/pdf");
							target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

							Intent openIntent = Intent.createChooser(target, "Open File");
							try {
								startActivity(openIntent);
							} catch (ActivityNotFoundException e) {
								Toast.makeText(getApplicationContext(), "No PDF reader installed, please get one (or more).",Toast.LENGTH_LONG).show();
							}   
						}
					}
				}
				//TODO check if more queue is not null
				unregisterReceiver(receiver);
			}
		};

		registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));



	}



	public void showDownload(View view) {
		Intent i = new Intent();
		i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
		startActivity(i);
	}



	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_thread_search:

			return true;
		case R.id.action_thread_help:
			Toast.makeText(getApplicationContext(), "HALP", Toast.LENGTH_SHORT).show(); 

			return true;
		case R.id.action_thread_refresh:
			// load the data from server
			Toast.makeText(getApplicationContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_thread_watchthread:
			threadDAO = new ThreadDAO(getApplicationContext());
			threadDAO.WriteThread(threadInfo, true);

			return true;
		case R.id.action_thread_save:
			threadDAO = new ThreadDAO(getApplicationContext());
			threadDAO.WriteThread(threadInfo, false);
			Toast.makeText(getApplicationContext(), "Thread saved", Toast.LENGTH_SHORT).show();

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private final SearchView.OnQueryTextListener mOnQueryTextListener =
			new SearchView.OnQueryTextListener() {
		@Override
		public boolean onQueryTextChange(String newText) {
			newText = TextUtils.isEmpty(newText) ? "" : "Query so far: " + newText;
			mSearchText.setText(newText);
			return true;
		}
		@Override
		public boolean onQueryTextSubmit(String query) {
			Toast.makeText(Activity_Thread.this,
					"Searching for: " + query + "...", Toast.LENGTH_SHORT).show();
			return true;
		}
	};
}