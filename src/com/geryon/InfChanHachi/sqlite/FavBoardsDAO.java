package com.geryon.InfChanHachi.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geryon.InfChanHachi.model.FavBoardModel;



public class FavBoardsDAO {

	private SQLiteDatabase database;
	private SQLiteHelper helper;




	public FavBoardsDAO(Context context){
		helper = new SQLiteHelper(context);
	}


	public void open() throws SQLException {
		database = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}

	public String writeFavB(String inName, String inURL){
		String response = "";
		open();
		if (CheckRow(inName)){
			response = "already";
		} else {
			ContentValues values = new ContentValues();
			values.put(SQLiteHelper.KEY_FAVBOARD_NAME, inName);
			values.put(SQLiteHelper.KEY_FAVBOARD_URL, inURL);
			database.insert(SQLiteHelper.TABLE_FAVBOARDS, null, values);
			response = "success";
		}
		close();
		return response;
	}

	public boolean CheckRow(String inName) {
		Cursor cursor = database.rawQuery("select 1 from "+SQLiteHelper.TABLE_FAVBOARDS +" where "+SQLiteHelper.KEY_FAVBOARD_NAME +" = ?", 
				new String[] { inName });
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	}

	public ArrayList<FavBoardModel> readFavB(){
		open();
		String[] columns = new String[]{SQLiteHelper.KEY_FAVBOARD_NAME, SQLiteHelper.KEY_FAVBOARD_URL};

		ArrayList<FavBoardModel> outFavBoards = new ArrayList<FavBoardModel>();
		Cursor mCursor = database.query(SQLiteHelper.TABLE_FAVBOARDS, columns, "", null, null, null, null);
		if (mCursor.getCount()!=0){
			mCursor.moveToFirst();
		}

		int nameIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_FAVBOARD_NAME);
		int UrlIndex =  mCursor.getColumnIndex(SQLiteHelper.KEY_FAVBOARD_URL);
		while (!mCursor.isAfterLast()){
			outFavBoards.add(new FavBoardModel(mCursor.getString(nameIndex), mCursor.getString(UrlIndex)));
			mCursor.moveToNext();
		}
		//outFavBoards.add(new FavBoardInfo(mCursor.getString(nameIndex), mCursor.getString(UrlIndex)));
		close();
		return outFavBoards;
	}
	
	
	
	public void deleteFavB(String inName){
		open();
		//int delnum = database.delete(SQLiteHelper.TABLE_FAVBOARDS,SQLiteHelper.KEY_FAVBOARD_NAME + " = "+ inName , null);
		int delnum = database.delete(SQLiteHelper.TABLE_FAVBOARDS,SQLiteHelper.KEY_FAVBOARD_NAME + " =? ", new String[] { String.valueOf(inName) }); 
		Log.w("FavBoardsDAO","Deleted " + String.valueOf(delnum)+ "rows.");
		close();
	}
	
}
