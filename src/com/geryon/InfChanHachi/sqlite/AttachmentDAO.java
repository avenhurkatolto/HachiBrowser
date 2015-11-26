package com.geryon.InfChanHachi.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.geryon.InfChanHachi.model.AttachmentModel;

public class AttachmentDAO {

	private SQLiteDatabase database;
	private SQLiteHelper helper;

	public AttachmentDAO(Context context){
		helper = new SQLiteHelper(context);
	}


	public void open() throws SQLException {
		database = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}

	public ArrayList<AttachmentModel> getMessagesByNumber(int inMessageNum){


		ArrayList<AttachmentModel> outMessageInfo = new ArrayList<AttachmentModel>();
		String columns[] = {SQLiteHelper.KEY_ATTACHMENTS_FILENAME,
				SQLiteHelper.KEY_ATTACHMENTS_ORIGINAL_NAME,
				SQLiteHelper.KEY_ATTACHMENTS_EXT
				};
		
		Cursor mCursor = database.query(SQLiteHelper.TABLE_ATTACHMENTS, columns, SQLiteHelper.KEY_ATTACHMENTS_MESSAGENUMBER + " = " +inMessageNum, null,null,null,null);
		if (mCursor.getCount() != 0){
			mCursor.moveToFirst();
			int extensionColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_ATTACHMENTS_EXT);
			int originalnameColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_ATTACHMENTS_ORIGINAL_NAME);
			int filenameColumnID = mCursor.getColumnIndex(SQLiteHelper.KEY_ATTACHMENTS_FILENAME);


			while (mCursor.isAfterLast()){
				outMessageInfo.add(new AttachmentModel(mCursor.getString(originalnameColumnID), mCursor.getString(extensionColumnID), mCursor.getString(filenameColumnID)));
				mCursor.moveToNext();
			}


		}

		return outMessageInfo;
	} 


	public void writeAttachments(ArrayList<AttachmentModel> inAttachments, int inMessageNum){

		open();
		database.delete(SQLiteHelper.TABLE_ATTACHMENTS, SQLiteHelper.KEY_ATTACHMENTS_MESSAGENUMBER + " = "+inMessageNum, null);
		database.beginTransaction();

		try{

			for (int i = 0; i<inAttachments.size(); i++){
				ContentValues values = new ContentValues();
				values.put(SQLiteHelper.KEY_ATTACHMENTS_ORIGINAL_NAME, inAttachments.get(i).getOriginalFullName());
				values.put(SQLiteHelper.KEY_ATTACHMENTS_FILENAME, inAttachments.get(i).getFilename());
				values.put(SQLiteHelper.KEY_ATTACHMENTS_MESSAGENUMBER, inMessageNum);
				values.put(SQLiteHelper.KEY_ATTACHMENTS_EXT, inAttachments.get(i).getTim());
				database.insert(SQLiteHelper.TABLE_ATTACHMENTS, null, values);
			}
			database.setTransactionSuccessful();

		} finally {
			database.endTransaction();

		}



		//close();
	}

}
