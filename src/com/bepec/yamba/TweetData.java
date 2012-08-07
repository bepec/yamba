package com.bepec.yamba;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class TweetData
{
	static final String TAG          = TweetData.class.getSimpleName();
	static final String DB_NAME      = "timeline.db";
	static final int    DB_VERSION   = 2;
	static final String TABLE        = "timeline";
// table columns	
	static final String C_ID         = BaseColumns._ID;
	static final String C_CREATED_AT = "created_at";
	static final String C_SOURCE     = "source";
	static final String C_USER       = "user";
	static final String C_TEXT       = "text";

	private static final String GET_ALL_ORDERED = C_CREATED_AT + " desc";

	private static final String[] 
		MAX_CREATED_AT_COLUMNS = { "max(" + C_CREATED_AT + ")" };

	private static final String[] DB_TEXT_COLUMNS = { C_TEXT };

	class DbHelper extends SQLiteOpenHelper
	{
		public DbHelper(Context context) {
			super(context, TweetData.DB_NAME, null, TweetData.DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "create table " + TABLE + " (" + 
				C_ID + " int primary key, " + C_CREATED_AT + " int, " + 
				C_SOURCE + " string, " + C_USER + " string, " + 
				C_TEXT + " string)";
			db.execSQL(sql);
			Log.d(TAG, "onCreate database: " + sql);

		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists " + TABLE);
			Log.d(TAG, "onUpgrade");
			onCreate(db);
		}

	};

	private final DbHelper dbHelper;

	public TweetData(Context context) {
		dbHelper = new DbHelper(context);
	}

	public void close()
	{
		dbHelper.close();
	}

	public void insertOrIgnore(ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			if (db.insertWithOnConflict(TABLE, null, values, 
					SQLiteDatabase.CONFLICT_IGNORE) != -1) {
				Log.d(TAG, "insertOrIgnore " + values);
			}
		}
		finally {
			db.close();
		}
		
	}

	public Cursor getTweetUpdates() {
		return dbHelper.getReadableDatabase().query(
			TABLE, null, null, null, null, null, GET_ALL_ORDERED);
	}

	public long getLatestTweetCreationTime() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE, MAX_CREATED_AT_COLUMNS,
				null, null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getLong(0) : Long.MIN_VALUE;
			}
			finally {
				cursor.close();
			}
		}
		finally {
			db.close();
		}
	}

	public String getTweetTextById(long id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, C_ID + "=" + id,
				null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getString(0) : null;
			}
			finally {
				cursor.close();
			}
		}
		finally {
			db.close();
		}
	}


}