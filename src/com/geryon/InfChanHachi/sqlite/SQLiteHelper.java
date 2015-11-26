package com.geryon.InfChanHachi.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper{

	public static final String LOG = "SQLitehelper";
	public static final int DATABASE_VERSION = 1;

	//Database name
	public static final String DB_NAME= "8chanDB";

	//Table names
	public static final String TABLE_FAVBOARDS = "Favourite_Boards";
	public static final String TABLE_THREADS = "Saved_Threads";
	public static final String TABLE_BOARDS = "Boards";
	public static final String TABLE_MESSAGES = "Messages";
	public static final String TABLE_ATTACHMENTS = "Attachments";

	//Common column names
	public static final String KEY_ID = "id";

	//TABLE: FavBoards columns
	public static final String KEY_FAVBOARD_NAME = "Board_Name";
	public static final String KEY_FAVBOARD_URL = "URL";

	//TABLE: Boards columns
	public static final String KEY_BOARDS_NAME = "Board_Name";
	public static final String KEY_BOARDS_TITLE = "Title";
	public static final String KEY_BOARDS_TAGS = "Tags";
	public static final String KEY_BOARDS_LANGUAGE = "Language";
	public static final String KEY_BOARDS_ISSFW = "SFW";
	public static final String KEY_BOARDS_SUBTITLE = "Subtitle";
	
	//TABLE: Threads columns
	public static final String KEY_THREAD_TITLE = "Thread_Title";
	public static final String KEY_THREAD_URL = "Thread_URL";
	public static final String KEY_THREAD_ID = "Thread_id";
	public static final String KEY_THREAD_WATCHEDORSAVED = "Watched_or_saved"; //0 for saved, 1 for watched
	public static final String KEY_THREAD_REPLIES = "Replies";
	public static final String KEY_THREAD_LASTMESSAGE = "Last_Message";
	public static final String KEY_THREAD_PARENTBOARD = "Parent_Board";

	//TABLE: Messages columns
	public static final String KEY_MESSAGES_THREADID = "Thread_id";
	public static final String KEY_MESSAGES_POSTER = "Poster";
	public static final String KEY_MESSAGES_MESSAGE = "Message";
	public static final String KEY_MESSAGES_POSTTIME = "Post_time";
	public static final String KEY_MESSAGES_EMAIL = "Email";
	public static final String KEY_MESSAGES_POSTID = "PostID";
	public static final String KEY_MESSAGES_USERID = "UserID";
	
	//TABLE: Attachments columns
	public static final String KEY_ATTACHMENTS_ORIGINAL_NAME = "Original_Name";
	public static final String KEY_ATTACHMENTS_FILENAME = "Filename";
	public static final String KEY_ATTACHMENTS_MESSAGENUMBER = "MessageNumber";
	public static final String KEY_ATTACHMENTS_EXT = "Extension";

	//Create Favourite Boards
	private static final String CREATE_TABLE_FAVBOARDS = "CREATE TABLE "
			+ TABLE_FAVBOARDS + "(" 
			+ KEY_ID + " INTEGER PRIMARY KEY," 
			+ KEY_FAVBOARD_NAME	+ " TEXT," 
			+ KEY_FAVBOARD_URL + " TEXT"
			+ ")";

	private static final String CREATE_TABLE_BOARDS = "CREATE TABLE " 
			+ TABLE_BOARDS + "(" 
			+ KEY_ID + " INTEGER PRIMARY KEY," 
			+ KEY_BOARDS_NAME + " TEXT," 
			+ KEY_BOARDS_TITLE + " TEXT,"
			+ KEY_BOARDS_SUBTITLE + " TEXT,"
			+ KEY_BOARDS_TAGS + " TEXT,"
			+ KEY_BOARDS_LANGUAGE + " TEXT,"
			+ KEY_BOARDS_ISSFW + " INTEGER"
			+ ")";

	private static final String CREATE_TABLE_THREADS = "CREATE TABLE "
			+ TABLE_THREADS + "(" 
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_THREAD_TITLE + " TEXT, "
			+ KEY_THREAD_URL + " TEXT, "
			+ KEY_THREAD_ID + " INTEGER, "
			+ KEY_THREAD_WATCHEDORSAVED + " INTEGER, "
			+ KEY_THREAD_LASTMESSAGE + " INTEGER, "
			+ KEY_THREAD_REPLIES + " INTEGER, "
			+ KEY_THREAD_PARENTBOARD + " TEXT"
			+ ")";
	
	

	private static final String CREATE_TABLE_MESSAGES =  "CREATE TABLE "
			+ TABLE_MESSAGES + "("
			+ KEY_ID + " INTEGER PRIMARY KEY, "
			+ KEY_MESSAGES_THREADID + " INTEGER, "
			+ KEY_MESSAGES_POSTER + " TEXT, "
			+ KEY_MESSAGES_EMAIL + " TEXT, "
			+ KEY_MESSAGES_USERID + " TEXT, "
			+ KEY_MESSAGES_POSTTIME + " INTEGER, "
			+ KEY_MESSAGES_MESSAGE + " TEXT, "
			+ KEY_MESSAGES_POSTID + " INTEGER"
			+ ")";
	
	private static final String CREATE_TABLE_ATTACHMENTS = "CREATE TABLE "
			+ TABLE_ATTACHMENTS + "("
			+ KEY_ID + " INTEGER PRIMARY KEY, "
			+ KEY_ATTACHMENTS_ORIGINAL_NAME + " TEXT, "
			+ KEY_ATTACHMENTS_FILENAME + " TEXT, "
			+ KEY_ATTACHMENTS_EXT + " TEXT, "
			+ KEY_ATTACHMENTS_MESSAGENUMBER + " INTEGER"
			+ ")";
	
	

	public SQLiteHelper(Context context) {
		super(context,DB_NAME , null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_FAVBOARDS);
		db.execSQL(CREATE_TABLE_BOARDS);
		db.execSQL(CREATE_TABLE_THREADS);
		db.execSQL(CREATE_TABLE_MESSAGES);
		db.execSQL(CREATE_TABLE_ATTACHMENTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVBOARDS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOARDS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_THREADS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTACHMENTS);
		onCreate(db);
	}
}