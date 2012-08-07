package com.bepec.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
//import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TweetsUpdateService 
extends Service {

	static public final String TAG = TweetsUpdateService.class.getSimpleName();
	static private final int DELAY_MS = 6000;
	private YambaApplication yamba;
	private boolean runFlag = false;
	private Updater updater;

	@Override
	public void onCreate() {
		super.onCreate();
		this.updater = new Updater();
		this.yamba = (YambaApplication)getApplication();
		Log.i(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.updater.interrupt();
		this.yamba.setUpdatingServiceRunning(false);
		Log.i(TAG, "onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		this.runFlag = true;
		this.updater.start();
		this.yamba.setUpdatingServiceRunning(true);
		Log.i(TAG, "onStartCommand");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class Updater extends Thread {

		TweetsUpdateService  service = TweetsUpdateService.this;

		public Updater() {
			super("TweetsUpdateService-Thread");
		}

		@Override
		public void run() {
			while (service.runFlag) {
				try {
					this.iterate();
				}
				catch (InterruptedException e) {
					Log.d(TAG, "loop is fucked up!");
					service.runFlag = false;
				}
			}
		}

		private void iterate() throws InterruptedException {
			Log.d(TAG, "updater loop");

			int newTweets = yamba.fetchTweetUpdates();
			if (newTweets > 0) {
				Log.d(TAG, "New tweets: " + newTweets);
			}

			Thread.sleep(DELAY_MS);
		}

	}
	
}