package com.geryon.InfChanHachi.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geryon.InfChanHachi.model.AttachmentModel;
import com.geryon.InfChanHachi.model.MessageModel;
import com.geryon.InfChanHachi.model.ThreadModel;

public class ThreadDAO {

	private SQLiteDatabase database;
	private SQLiteHelper helper;
	private Context context;



	public ThreadDAO(Context inContext){
		context = inContext;
		helper = new SQLiteHelper(context);
	}


	public void open() throws SQLException {
		database = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}

	public ThreadModel getThreadByUrl(String inUrl){
		open();
		Log.w("URL",inUrl);
		String columns[] = {SQLiteHelper.KEY_THREAD_TITLE, SQLiteHelper.KEY_THREAD_ID, SQLiteHelper.KEY_THREAD_PARENTBOARD}; 

		Cursor mCursor = database.query(SQLiteHelper.TABLE_THREADS, columns, SQLiteHelper.KEY_THREAD_URL +" = \""+inUrl+"\"", null, null, null, null);
		mCursor.moveToFirst();
		ThreadModel outThreadInfo = new ThreadModel(inUrl, 
				mCursor.getString(mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_TITLE)),
				mCursor.getString(mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_PARENTBOARD)));
		int thread_id = mCursor.getInt(mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_ID));
		outThreadInfo.addMessagesBatched(getMessagesByID(thread_id));
		//mCursor = database.query(SQLiteHelper.TABLE_MESSAGES, columns, selection, selectionArgs, groupBy, having, orderBy)



		close();
		return outThreadInfo;
	}

	public ThreadModel getThreadById(int inId){
		open();
		String columns[] = {SQLiteHelper.KEY_THREAD_TITLE, SQLiteHelper.KEY_THREAD_ID}; 
		Cursor mCursor = database.query(SQLiteHelper.TABLE_THREADS, columns, SQLiteHelper.KEY_THREAD_ID +" = "+String.valueOf(inId), null, null, null, null);
		ThreadModel outThreadInfo = new ThreadModel(mCursor.getString(mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_URL)),
				mCursor.getString(mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_TITLE)),
				mCursor.getString(mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_PARENTBOARD)));
		int thread_id = mCursor.getInt(mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_ID));
		outThreadInfo.addMessagesBatched(getMessagesByID(thread_id));
		//mCursor = database.query(SQLiteHelper.TABLE_MESSAGES, columns, selection, selectionArgs, groupBy, having, orderBy)

		close();
		return outThreadInfo;
	}

	//Requires an already opened database
	private ArrayList<MessageModel> getMessagesByID(int inThreadId){
		ArrayList<MessageModel> messages = new ArrayList<MessageModel>();
		String columns[] = {SQLiteHelper.KEY_MESSAGES_POSTER,
				SQLiteHelper.KEY_MESSAGES_POSTTIME, 
				SQLiteHelper.KEY_MESSAGES_MESSAGE, 
				SQLiteHelper.KEY_MESSAGES_EMAIL,
				SQLiteHelper.KEY_MESSAGES_POSTID,
				SQLiteHelper.KEY_MESSAGES_USERID
				
		};

		Cursor mCursor = database.query(SQLiteHelper.TABLE_MESSAGES, columns, SQLiteHelper.KEY_MESSAGES_THREADID + "= \""+inThreadId+"\"", null, null, null, null);
		Log.w("mCursor size:", String.valueOf(mCursor.getCount()));
		int posterNameColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_MESSAGES_POSTER);
		int emailColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_MESSAGES_EMAIL);
		int posttimeColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_MESSAGES_POSTTIME);
		int messageColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_MESSAGES_MESSAGE);
		int postIDColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_MESSAGES_POSTID);
		int userIDColumndID = mCursor.getColumnIndex(SQLiteHelper.KEY_MESSAGES_USERID);
		if (mCursor.getCount() !=0){
			mCursor.moveToFirst();
			while (!mCursor.isAfterLast()){
				MessageModel tempMessage = new MessageModel(
							mCursor.getInt(postIDColumnID),
							mCursor.getString(messageColumnID),
							mCursor.getString(posterNameColumnID),
							mCursor.getInt(posttimeColumnID),
							mCursor.getString(userIDColumndID),
							mCursor.getString(emailColumnID));
						
						/*
						mCursor.getString(posterNameColumnID),
						mCursor.getString(emailColumnID), 
						mCursor.getString(userIDColumndID),
						mCursor.getString(posttimeColumnID), 
						mCursor.getInt(postIDColumnID), 
						mCursor.getString(messageColumnID));*/
				tempMessage.setAttachments(getAttachmentsbyID(mCursor.getInt(postIDColumnID)));
				messages.add(tempMessage);
				Log.i("Message", tempMessage.getFormattedModel());
				mCursor.moveToNext();
			}
		}

		Log.w("ThreadDAO message size",String.valueOf(messages.size()) );
		return messages;
	}

	private ArrayList<AttachmentModel> getAttachmentsbyID(int inPostID){
		ArrayList<AttachmentModel> attachments = new ArrayList<AttachmentModel>();
		String columns[] = {SQLiteHelper.KEY_ATTACHMENTS_FILENAME,
				SQLiteHelper.KEY_ATTACHMENTS_EXT,
				SQLiteHelper.KEY_ATTACHMENTS_ORIGINAL_NAME
		};
		Cursor mCursor = database.query(SQLiteHelper.TABLE_ATTACHMENTS, columns, SQLiteHelper.KEY_ATTACHMENTS_MESSAGENUMBER + "= \""+String.valueOf(inPostID)+"\"", null, null, null, null);
		if (mCursor.getCount() != 0){
			int filenameColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_ATTACHMENTS_FILENAME);
			int originalnameColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_ATTACHMENTS_ORIGINAL_NAME);
			int extColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_ATTACHMENTS_EXT);

			mCursor.moveToFirst();
			while (!mCursor.isAfterLast()){
				AttachmentModel tempAttach = new AttachmentModel(mCursor.getString(originalnameColumnID), 
						mCursor.getString(extColumnID),
						mCursor.getString(filenameColumnID));

				attachments.add(tempAttach);

				mCursor.moveToNext();
			}

		}

		return attachments;




	}

	public ArrayList<ThreadModel> getWatchedThreads(){
		ArrayList<ThreadModel> watchedThreads = new ArrayList<ThreadModel>();
		open();
		String columns[] = {SQLiteHelper.KEY_THREAD_TITLE, SQLiteHelper.KEY_THREAD_URL};
		Cursor mCursor = database.query(SQLiteHelper.TABLE_THREADS, 
				columns, SQLiteHelper.KEY_THREAD_WATCHEDORSAVED + " = 1", null, null, null, null);

		if (mCursor.getCount() !=0){
			int titleColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_TITLE);
			int utlColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_URL);

			mCursor.moveToFirst();

			while (mCursor.isAfterLast()){
				watchedThreads.add(new ThreadModel(mCursor.getString(utlColumnID),
						mCursor.getString(titleColumnID),
						mCursor.getString(mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_PARENTBOARD))));	
				mCursor.moveToNext();
			}
		}

		close();
		return watchedThreads;
	}

	public ArrayList<ThreadModel> getSavedThreads(){
		ArrayList<ThreadModel> watchedThreads = new ArrayList<ThreadModel>();
		open();
		String columns[] = {SQLiteHelper.KEY_THREAD_TITLE, SQLiteHelper.KEY_THREAD_URL, SQLiteHelper.KEY_THREAD_PARENTBOARD};
		Cursor mCursor = database.query(SQLiteHelper.TABLE_THREADS, 
				columns, SQLiteHelper.KEY_THREAD_WATCHEDORSAVED + " = 0", null, null, null, null);

		if (mCursor.getCount() !=0){
			Log.w("ThreadDAO", "belement");
			int titleColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_TITLE);
			int urlColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_URL);
			int boardColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_PARENTBOARD);
			
			mCursor.moveToFirst();

			while (!mCursor.isAfterLast()){
				watchedThreads.add(new ThreadModel(mCursor.getString(urlColumnID),
						mCursor.getString(titleColumnID),
						mCursor.getString(boardColumnID)));	
				Log.w("Item", "Added");
				mCursor.moveToNext();
			}
		}

		close();
		return watchedThreads;
	}
	public void WriteThread(ThreadModel inThreadInfo, boolean isWatched){
		open();
		String columns[] = {SQLiteHelper.KEY_THREAD_ID};
		Cursor mCursor = database.query(SQLiteHelper.TABLE_THREADS, columns, SQLiteHelper.KEY_THREAD_URL +"=?", new String[]{inThreadInfo.getUrl()}, null, null, null); 
		if (mCursor.getCount() !=0){
			mCursor.moveToFirst();
			String thread_id = mCursor.getString(mCursor.getColumnIndex(SQLiteHelper.KEY_THREAD_ID));
			database.delete(SQLiteHelper.TABLE_MESSAGES, SQLiteHelper.KEY_MESSAGES_THREADID+ " = "+ thread_id, null );
		}

		ArrayList<MessageModel> messages = inThreadInfo.getMessages();

		database.beginTransaction();
		Log.w("Transaction", "starting...");
		try{
			ContentValues threadValues = new ContentValues();
			threadValues.put(SQLiteHelper.KEY_THREAD_ID, inThreadInfo.getThreadID());
			threadValues.put(SQLiteHelper.KEY_THREAD_TITLE, inThreadInfo.getTitle());
			threadValues.put(SQLiteHelper.KEY_THREAD_URL, inThreadInfo.getUrl());
			threadValues.put(SQLiteHelper.KEY_THREAD_WATCHEDORSAVED, isWatched);
			threadValues.put(SQLiteHelper.KEY_THREAD_PARENTBOARD, inThreadInfo.getParentboard());
			database.insert(SQLiteHelper.TABLE_THREADS, null, threadValues);



			for (int i =0; i<inThreadInfo.getMessageCount(); i++){
				Log.w("Database", String.valueOf(i));
				ContentValues values = new ContentValues();
				values.put(SQLiteHelper.KEY_MESSAGES_POSTER , messages.get(i).getName());
				values.put(SQLiteHelper.KEY_MESSAGES_EMAIL, messages.get(i).getEmail());
				values.put(SQLiteHelper.KEY_MESSAGES_MESSAGE, messages.get(i).getCom());
				values.put(SQLiteHelper.KEY_MESSAGES_POSTID, messages.get(i).getNo());
				values.put(SQLiteHelper.KEY_MESSAGES_THREADID, inThreadInfo.getThreadID());
				values.put(SQLiteHelper.KEY_MESSAGES_POSTTIME, messages.get(i).getTime());
				values.put(SQLiteHelper.KEY_MESSAGES_USERID, messages.get(i).getId());

				database.insert(SQLiteHelper.TABLE_MESSAGES, null, values);
				writeAttachments(inThreadInfo.getMessages().get(i).getAttachments(), inThreadInfo.getMessages().get(i).getNo());
			}
			Log.w("Transaction", "finished");
			database.setTransactionSuccessful();

		} finally {
			database.endTransaction();
			Log.w("ThreadDAO", "transaction finished");
		}


		close();
	}




	public ArrayList<AttachmentModel> getMessagesByNumber(int inMessageNum){


		ArrayList<AttachmentModel> outMessageInfo = new ArrayList<AttachmentModel>();
		String columns[] = {SQLiteHelper.KEY_ATTACHMENTS_ORIGINAL_NAME, 
				SQLiteHelper.KEY_ATTACHMENTS_EXT, 
				SQLiteHelper.KEY_ATTACHMENTS_FILENAME,
		};
		Cursor mCursor = database.query(SQLiteHelper.TABLE_ATTACHMENTS, columns, SQLiteHelper.KEY_ATTACHMENTS_MESSAGENUMBER + " = " +inMessageNum, null,null,null,null);
		if (mCursor.getCount() != 0){
			mCursor.moveToFirst();
			int originalnameColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_ATTACHMENTS_ORIGINAL_NAME);
			int extUrlColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_ATTACHMENTS_EXT);
			int filenameColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_ATTACHMENTS_FILENAME);


			while (mCursor.isAfterLast()){
				outMessageInfo.add(new AttachmentModel(mCursor.getString(originalnameColumnID), mCursor.getString(extUrlColumnID), mCursor.getString(filenameColumnID)));
				mCursor.moveToNext();
			}


		}

		return outMessageInfo;
	} 


	public void writeAttachments(ArrayList<AttachmentModel> inAttachments, int inMessageNum){

		Log.w("AttachmentWriter", "started");
		database.delete(SQLiteHelper.TABLE_ATTACHMENTS, SQLiteHelper.KEY_ATTACHMENTS_MESSAGENUMBER + " = "+inMessageNum, null);
		for (int i = 0; i<inAttachments.size(); i++){
			ContentValues values = new ContentValues();
			values.put(SQLiteHelper.KEY_ATTACHMENTS_EXT, inAttachments.get(i).getExt());
			values.put(SQLiteHelper.KEY_ATTACHMENTS_ORIGINAL_NAME, inAttachments.get(i).getFilename());
			values.put(SQLiteHelper.KEY_ATTACHMENTS_FILENAME, inAttachments.get(i).getTim());
			values.put(SQLiteHelper.KEY_ATTACHMENTS_MESSAGENUMBER, inMessageNum);
			database.insert(SQLiteHelper.TABLE_ATTACHMENTS, null, values);

		}
	}
}