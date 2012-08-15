package com.bepec.yamba;

import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.util.Log;

public class TimelineActivity extends Activity {

	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	TimelineAdapter tweetAdapter;

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

		tweetAdapter = new TimelineAdapter(this, cursor);
		listTimeline.setAdapter(tweetAdapter);
	}
	
}