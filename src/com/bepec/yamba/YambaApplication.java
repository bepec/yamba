package com.bepec.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Status;
import android.app.Application;
import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import java.util.List;

public class YambaApplication 
extends Application
{
	static public final String TAG = YambaApplication.class.getSimpleName();
	private Twitter twitter;
	private boolean updatingServiceRunning;
	private TweetData tweetData;

	@Override
	public void onCreate()
	{
		super.onCreate();
        tweetData = new TweetData(this);
		Log.i(TAG, "onCreate");
	}
	
	@Override
	public void onTerminate()
	{
		super.onTerminate();
		Log.i(TAG, "onTerminate");
	}

    public Twitter getTwitter()
    {
    	if (twitter == null)
    	{
    		OAuthSignpostClient client = new OAuthSignpostClient(
	            "iqxpvnIjkS2MLrHgGlIWg",
	            "zMCtg5WNj6fstFqevXjWaNm9z6bgE93unRZ8LP9Rw",
	            "609065754-XrDxvmikzQLbf3D6mprFvnIOk7iFDvHIO5bOGRKw",
	            "bgfJWF3Uct9Dtx6TAM66UVIuOSfkttcPvXNldCF6A");
    		twitter = new Twitter(null, client);
    	}
        return twitter;
    }

    public TweetData getTweetData() {
    	return tweetData;
    }

    public synchronized int fetchTweetUpdates() {
    	Log.d(TAG, "fetchTweetUpdates");
    	Twitter twitter = this.getTwitter();
    	try {
    		List <Status> tweetUpdates = twitter.getHomeTimeline();
    		long latestTweetCreationTime = 
    			this.getTweetData().getLatestTweetCreationTime();
    		int count = 0;
    		for (Status tweet: tweetUpdates) {
    			if (tweet.createdAt.getTime() > latestTweetCreationTime) {
                    processTweet(tweet);
    				count++;
    			}
    		}
    		return count;
    	}
    	catch (RuntimeException e) {
    		Log.w("Cant process tweets", e);
    	}
    	return 0;
    }

    public boolean isUpdatingServiceRunning()
    {
    	return updatingServiceRunning;
    }

    public void setUpdatingServiceRunning(boolean value)
    {
    	this.updatingServiceRunning = value;
    }

	private void processTweet(Status tweet) {
		ContentValues values = new ContentValues();
		values.put(TweetData.C_ID, tweet.id.longValue());
		values.put(TweetData.C_CREATED_AT, tweet.createdAt.getTime());
		values.put(TweetData.C_SOURCE, tweet.source);
		values.put(TweetData.C_USER, tweet.user.name);
		values.put(TweetData.C_TEXT, tweet.text);

		getTweetData().insertOrIgnore(values);
	}

}