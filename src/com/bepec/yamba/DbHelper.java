package com.bepec.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

class DbHelper extends SQLiteOpenHelper
{
	static final String TAG          = "DbHelper";
	static final String DB_NAME      = "timeline.db";
	static final int    DB_VERSION   = 2;
	static final String TABLE        = "timeline";
	static final String C_ID         = BaseColumns._ID;
	static final String C_CREATED_AT = "created_at";
	static final String C_SOURCE     = "source";
	static final String C_USER       = "user";
	static final String C_TEXT       = "text";

	Context context;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
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
}