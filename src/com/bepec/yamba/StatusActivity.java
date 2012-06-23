package com.bepec.yamba;

import interwell.jtwitter.Twitter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class StatusActivity extends Activity
{
	EditText editText;
	Button buttonSend;
	Twitter twitter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);

        editText = (EditText) findByContentId(R.id.editText);
        buttonSend = (Button) findByContentId(R.id.buttonSend);

        updateButton.setOnClickListener(this);

        twitter = new Twitter("me", "passwd");
        twitter.setAPIRootUrl("http://yamba.bepec.com/api");
    }

    public void onClick()
    {
    	twitter.setStatus(editText.getText().toString());
    	Log.d("StatusActivity", onClicked);
    }
}
