package com.bepec.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.os.Bundle;
import android.os.AsyncTask;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.util.Log;

public class StatusActivity 
	extends Activity
	implements OnSharedPreferenceChangeListener, OnClickListener, TextWatcher
{
    Twitter twitter;

    // SharedPreferences preferences;

    /* Widgets */
    EditText editStatus;
    Button buttonSend;
    TextView textCharsLeft;
    private final YambaApplication application = (YambaApplication)getApplication();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        logd("starting activity");

        super.onCreate(savedInstanceState);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);

        prepareUi();
        applySettings(settings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.xml.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                showSettings();
                break;
            case R.id.terminate:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.startService:

                startService(new Intent(this, TweetsUpdateService.class));
                break;
            case R.id.stopService:
                stopService(new Intent(this, TweetsUpdateService.class));
                break;
            default:
                assert true : "unexpected menu item!";
        }
        return true;
    }

//    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key)
    {
        logd("settings changed: " + key);
        applySettings(preferences);
    }

    public void onClick(View view)
    {
        String status = editStatus.getText().toString();
        new PostStatus().execute(status);
        logd("onClicked");
    }

    public void afterTextChanged(Editable text)
    {
        assert text != null : "null text!";
        int charsLeftCount = Twitter.MAX_CHARS - text.length();
        textCharsLeft.setText(Integer.toString(charsLeftCount));
        textCharsLeft.setTextColor(ColorPicker.pick(charsLeftCount));
    }

    private void prepareUi()
    {
        setContentView(R.layout.status);
        
        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(this);

        editStatus = (EditText) findViewById(R.id.editStatus);
        editStatus.addTextChangedListener(this);

        textCharsLeft = (TextView) findViewById(R.id.textCharsLeft);

        afterTextChanged(editStatus.getText());
    }

    private void applySettings(SharedPreferences settings)
    {
        int color;
        try
        {
            color = Color.parseColor(
                settings.getString("prefEditColor", getString(R.string.prefEditColor)));
        }
        catch (IllegalArgumentException e)
        {
            logd(e.getMessage());
            Toast.makeText(this, R.string.badColorValue, Toast.LENGTH_LONG).show();
            color = Color.parseColor(getString(R.string.prefEditColor));
        }
        editStatus.setBackgroundColor(color);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutScreen);
        String value = settings.getString("prefBackgroundImage", "blank");
        logd("background value = " + value);
        if (value.equals("blank"))
        {
            layout.setBackgroundColor(0);
        }
        else if (value.equals("image"))
        {
            layout.setBackgroundResource(R.drawable.background);
        }
        else
        {
            logd("oh shit!");
        }
    }

    private void showSettings()
    {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void logd(String text)
    {
        Log.d("StatusActivity", text);
    }

    private void loge(String text)
    {
        Log.e("StatusActivity", text);
    }

    class PostStatus extends AsyncTask < String, Integer, String >
    {
        @Override
        protected String doInBackground(String... statuses)
        {
            logd("start async task");

            Twitter twitter = application.getTwitter();

            try {
                Twitter.ITweet status = twitter.updateStatus(statuses[0]);
                return status.getText();
            }
            catch(TwitterException e) {
                loge(e.toString());
                e.printStackTrace();
                return "failed to post!";
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            logd("repoort: " + values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result)
        {
            logd("async task finished");
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }

    static class ColorPicker
    {
        static class ColorCode {
            public final int charsCount;
            public final int color;
            ColorCode(int cc, int col)
            {
                charsCount = cc;
                color = col;
            }
        }

        static final ColorCode[] colors = {
            new ColorCode(10, Color.RED),
            new ColorCode(30, Color.YELLOW),
            new ColorCode(Twitter.MAX_CHARS, Color.GREEN)
        };

        static int pick(int count) {
            for (ColorCode code: colors) {
                if (count <= code.charsCount)
                    return code.color;
            }
            assert false : "Unexpected chars left count: " + count;
            return 0;
        }

    }

    public void beforeTextChanged(CharSequence chars, int start, int count, int after)
    {
//        
    }

    public void onTextChanged(CharSequence chars, int start, int count, int after)
    {
//        
    }

}