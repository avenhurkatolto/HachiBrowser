package com.geryon.InfChanHachi.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.geryon.InfChanHachi.model.BoardModel;
import com.geryon.InfChanHachi.utils.Utils;

public class BoardsDAO {

	private SQLiteDatabase database;
	private SQLiteHelper helper;

	public BoardsDAO(Context context){
		helper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}

	public void writeBoards (ArrayList<BoardModel> inBoards){

		open();
		database.delete(SQLiteHelper.TABLE_BOARDS, null, null);
		database.beginTransaction();
		try {
			for (int i = 0; i<inBoards.size();i++){
				ContentValues values = new ContentValues();
				values.put(SQLiteHelper.KEY_BOARDS_NAME, inBoards.get(i).getUri());
				values.put(SQLiteHelper.KEY_BOARDS_TITLE, inBoards.get(i).getTitle());
				values.put(SQLiteHelper.KEY_BOARDS_LANGUAGE, inBoards.get(i).getLocale());
				values.put(SQLiteHelper.KEY_BOARDS_SUBTITLE, inBoards.get(i).getSubtitle());
				values.put(SQLiteHelper.KEY_BOARDS_ISSFW, inBoards.get(i).isSfw());
				values.put(SQLiteHelper.KEY_BOARDS_TAGS, inBoards.get(i).MergeTags());
				database.insert(SQLiteHelper.TABLE_BOARDS, null, values);

			}
			database.setTransactionSuccessful();

		} finally {
			database.endTransaction();
		}

		close();
	}

	public ArrayList<BoardModel> searchBoards(String searchCrit){
		ArrayList<BoardModel> boards = new ArrayList<BoardModel>();
		ArrayList<String> searchArray = Utils.strToArrayList(searchCrit);
		
		String[] columns = new String[]{SQLiteHelper.KEY_BOARDS_NAME, 
				SQLiteHelper.KEY_BOARDS_TITLE, 
				SQLiteHelper.KEY_BOARDS_LANGUAGE, 
				SQLiteHelper.KEY_BOARDS_ISSFW, 
				SQLiteHelper.KEY_BOARDS_TAGS,
				SQLiteHelper.KEY_BOARDS_SUBTITLE};

		if ((searchCrit == null)  ||  (searchArray.size() == 0))  {
			
			boards = getBoardsSFW();
		} else {
		open();	
		Cursor mCursor = null;
		if (searchArray.size() == 1){
			mCursor = database.query(SQLiteHelper.TABLE_BOARDS, columns, 
					SQLiteHelper.KEY_BOARDS_TITLE + " like '%" + searchArray.get(0) + "%' OR " + SQLiteHelper.KEY_BOARDS_TAGS+ " like '%" + searchArray.get(0) + "%'", null, null, null, null);			
		} else {
			String temp = SQLiteHelper.KEY_BOARDS_TITLE + " like '%" + searchArray.get(0) + "%' OR " + SQLiteHelper.KEY_BOARDS_TAGS+ " like '%" + searchArray.get(0) + "%'";
			 for (int i = 1; i<searchArray.size(); i++){
				 temp += " OR "+ SQLiteHelper.KEY_BOARDS_TITLE + " like '%" + searchArray.get(i) + "%' OR " + SQLiteHelper.KEY_BOARDS_TAGS+ " like '%" + searchArray.get(i) + "%'";
				 Log.w("Search criteria:", temp);
				 
			 }
			 mCursor = database.query(SQLiteHelper.TABLE_BOARDS, columns, temp,null, null, null, null);			
			
		}
		
		if (mCursor.getCount()!=0){
			mCursor.moveToFirst();
		}
		int nameIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_NAME);
		int titleIndex =  mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_TITLE);
		int langIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_LANGUAGE);
		int sfwIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_ISSFW);
		int tagsIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_TAGS);
		int subtitleIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_SUBTITLE);
	
		while (!mCursor.isAfterLast()){
			boards.add(new BoardModel(mCursor.getString(nameIndex), 
					mCursor.getString(titleIndex),
					mCursor.getString(subtitleIndex),
					mCursor.getString(langIndex), 
					String.valueOf(mCursor.getInt(sfwIndex)),
					mCursor.getString(tagsIndex)));

			mCursor.moveToNext();
		}
		mCursor.close();
		close();
		}
		return boards;

	}

	public ArrayList<BoardModel> getBoardsSFW(){
		ArrayList<BoardModel> boardInfo = new ArrayList<BoardModel>();
		open();

		String[] columns = new String[]{SQLiteHelper.KEY_BOARDS_NAME, 
				SQLiteHelper.KEY_BOARDS_TITLE, 
				SQLiteHelper.KEY_BOARDS_LANGUAGE, 
				SQLiteHelper.KEY_BOARDS_ISSFW, 
				SQLiteHelper.KEY_BOARDS_TAGS,
				SQLiteHelper.KEY_BOARDS_SUBTITLE
				};

		Cursor mCursor = database.query(SQLiteHelper.TABLE_BOARDS, columns, "SFW = 1", null, null, null, null);
		if (mCursor.getCount()!=0){
			mCursor.moveToFirst();
		}
		int nameIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_NAME);
		int titleIndex =  mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_TITLE);
		int langIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_LANGUAGE);
		int sfwIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_ISSFW);
		int tagsIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_TAGS);
		int subtitleIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_SUBTITLE);

		while (!mCursor.isAfterLast()){
			boardInfo.add(new BoardModel(mCursor.getString(nameIndex), 
					mCursor.getString(titleIndex),
					mCursor.getString(subtitleIndex),
					mCursor.getString(langIndex),
					mCursor.getString(tagsIndex),
					String.valueOf(mCursor.getInt(sfwIndex))
					));
			//Log.w("BDAO", mCursor.getString(tagsIndex));
			mCursor.moveToNext();
		}
        mCursor.close();
		close();
		return boardInfo;
	}
	
	public ArrayList<BoardModel> getBoardsAll(){
		ArrayList<BoardModel> boardInfo = new ArrayList<BoardModel>();
		open();

		String[] columns = new String[]{SQLiteHelper.KEY_BOARDS_NAME, 
				SQLiteHelper.KEY_BOARDS_TITLE, 
				SQLiteHelper.KEY_BOARDS_LANGUAGE, 
				SQLiteHelper.KEY_BOARDS_ISSFW, 
				SQLiteHelper.KEY_BOARDS_TAGS,
				SQLiteHelper.KEY_BOARDS_SUBTITLE
				};

		Cursor mCursor = database.query(SQLiteHelper.TABLE_BOARDS, columns, null, null, null, null, null);
		if (mCursor.getCount()!=0){
			mCursor.moveToFirst();
		}
		int nameIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_NAME);
		int titleIndex =  mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_TITLE);
		int langIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_LANGUAGE);
		int sfwIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_ISSFW);
		int tagsIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_TAGS);
		int subtitleIndex = mCursor.getColumnIndex(SQLiteHelper.KEY_BOARDS_SUBTITLE);

		while (!mCursor.isAfterLast()){
			boardInfo.add(new BoardModel(mCursor.getString(nameIndex), 
					mCursor.getString(titleIndex),
					mCursor.getString(subtitleIndex),
					mCursor.getString(langIndex),
					mCursor.getString(tagsIndex),
					String.valueOf(mCursor.getInt(sfwIndex))
					));
			//Log.w("BDAO", mCursor.getString(tagsIndex));
			mCursor.moveToNext();
		}

		close();
		return boardInfo;
	}
}