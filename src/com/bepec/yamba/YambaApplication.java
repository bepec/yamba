package com.bepec.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.OAuthSignpostClient;
import android.app.Application;
import android.util.Log;

public class YambaApplication 
extends Application
{
	static public final String TAG = YambaApplication.class.getSimpleName();
	private Twitter twitter;
	private boolean updatingServiceRunning;

	@Override
	public void onCreate()
	{
		super.onCreate();
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

    public boolean isUpdatingServiceRunning()
    {
    	return updatingServiceRunning;
    }

    public void setUpdatingServiceRunning(boolean value)
    {
    	this.updatingServiceRunning = value;
    }
}