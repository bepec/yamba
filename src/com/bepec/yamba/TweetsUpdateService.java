package com.bepec.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import winterwell.jtwitter.Status;
import android.app.Service;
import android.content.Intent;
import android.content.ContentValues;
import android.os.IBinder;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import java.util.List;

public class TweetsUpdateService 
extends Service
{
	static public final String TAG = TweetsUpdateService.class.getSimpleName();
	static private final int DELAY_MS = 6000;
	private YambaApplication yamba;
	private boolean runFlag = false;
	private Updater updater;
	private DbHelper dbHelper;
	private SQLiteDatabase db;

	@Override
	public void onCreate()
	{
		super.onCreate();
		this.updater = new Updater();
		this.yamba = (YambaApplication)getApplication();
		this.dbHelper = new DbHelper(this);
		Log.i(TAG, "onCreate");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		this.updater.interrupt();
		this.yamba.setUpdatingServiceRunning(false);
		Log.i(TAG, "onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);
		this.runFlag = true;
		this.updater.start();
		this.yamba.setUpdatingServiceRunning(true);
		Log.i(TAG, "onStartCommand");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	private class Updater extends Thread
	{
		List <Status> timeline;
		TweetsUpdateService  service = TweetsUpdateService.this;

		public Updater()
		{
			super("TweetsUpdateService-Thread");
		}

		@Override
		public void run()
		{
			while (service.runFlag)
			{
				try {
					this.iterate();
				}
				catch (InterruptedException e) {
					Log.d(TAG, "loop is fucked up!");
					service.runFlag = false;
				}
			}
		}

		private void iterate() throws InterruptedException
		{
			Log.d(TAG, "updater loop");
			try
			{
				timeline = yamba.getTwitter().getHomeTimeline();
			}
			catch (TwitterException e)
			{
				Log.e(TAG, "Failed to get the twitter timeline");
			}

			db = dbHelper.getWritableDatabase();

			for (Status tweet: timeline) {
				processTweet(tweet);
			}
			db.close();
			Thread.sleep(DELAY_MS);
		}

		void processTweet(Status tweet) {
			ContentValues values = new ContentValues();
			values.put(DbHelper.C_ID, tweet.id.longValue());
			values.put(DbHelper.C_CREATED_AT, tweet.createdAt.getTime());
			values.put(DbHelper.C_SOURCE, tweet.source);
			values.put(DbHelper.C_USER, tweet.user.name);
			values.put(DbHelper.C_TEXT, tweet.text);

			try {
				db.insertOrThrow(DbHelper.TABLE, null, values);
			}
			catch (SQLiteException e) {
				// ignore
			}

			Log.d(TAG, String.format("%s: %s", tweet.user.name, tweet.text));
		}
	}
	
}