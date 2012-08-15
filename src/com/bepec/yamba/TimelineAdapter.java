package com.bepec.yamba;

import android.widget.SimpleCursorAdapter;
import android.content.Context;
import android.view.View;
import android.database.Cursor;
import android.widget.TextView;
import android.text.format.DateUtils;

public class TimelineAdapter extends SimpleCursorAdapter {
	
	static final String[] FROM= {
		DbHelper.C_CREATED_AT, DbHelper.C_USER, DbHelper.C_TEXT };

	static final int[] TO = {
		R.id.textCreatedAt, R.id.textUser, R.id.textText };

	public TimelineAdapter(Context context, Cursor cursor) {
		super(context, R.layout.tweet, cursor, FROM, TO);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);
		long timestamp = cursor.getLong(cursor.getColumnIndex(DbHelper.C_CREATED_AT));
		TextView textCreatedAt = (TextView) row.findViewById(R.id.textCreatedAt);
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
	}
}