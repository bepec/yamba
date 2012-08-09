package com.bepec.yamba;

import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.util.Log;

public class TimelineActivity extends Activity {
	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	TextView textTimeline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);

		textTimeline = (TextView) findViewById(R.id.textTimeline);

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

		String user, text, output;
		while (cursor.moveToNext()) {
			user = cursor.getString(cursor.getColumnIndex(DbHelper.C_USER));
			text = cursor.getString(cursor.getColumnIndex(DbHelper.C_TEXT));
			textTimeline.append(String.format("%s: %s\n", user, text));
		}
	}
	
}