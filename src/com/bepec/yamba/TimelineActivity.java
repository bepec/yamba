package com.bepec.yamba;

import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.util.Log;

public class TimelineActivity extends Activity {

	static final String[] FROM= {
		DbHelper.C_CREATED_AT, DbHelper.C_USER, DbHelper.C_TEXT };

	static final int[] TO = {
		R.id.textCreatedAt, R.id.textUser, R.id.textText };

	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	SimpleCursorAdapter cursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);

		listTimeline = (ListView) findViewById(R.id.listTimeline);

		dbHelper = new DbHelper(this);
		db = dbHelper.getReadableDatabase();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();

		cursor = db.query(DbHelper.TABLE, null, null, null, null, null,
			DbHelper.C_CREATED_AT + " desc");
		startManagingCursor(cursor);

		cursorAdapter = new SimpleCursorAdapter(this, R.layout.tweet, cursor, FROM, TO);
		listTimeline.setAdapter(cursorAdapter);
	}
	
}