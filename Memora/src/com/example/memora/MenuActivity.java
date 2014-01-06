package com.example.memora;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MenuActivity extends Activity {
	
	public static final String MILLIS_EXTRA_KEY = "millis";
	public static final String memoraDirectory = Environment.getExternalStorageDirectory()+File.separator+"memora";
	public static final String memoraDirectoryAudio = memoraDirectory+File.separator+"audio";
	public static final String memoraDirectoryImages = memoraDirectory+File.separator+"images";
	private static final String LOG_TAG = "Menu Activity";

	@Override
    public void onResume() {
        super.onResume();
        openOptionsMenu();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memora_live_card);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		Log.d(LOG_TAG, "asdfg");
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop:
                stopService(new Intent(this, AudioRecorder.class));
                finish();
                closeOptionsMenu();
                //What is the result of calling finish before the return statement?
                //It does get past the finish statement.
                return true;
            case R.id.moments:
            	Intent myIntent = new Intent(this, MomentsImmersion.class);
            	startActivity(myIntent);
            	return true;
            case R.id.capture:
            	long millis = System.currentTimeMillis();
            	captureAudioMesssage(millis);
            	capturePhoto(millis);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	public void onOptionsMenuClosed(Menu menu) {
        finish();
    }
	
	private void captureAudioMesssage(long millis) {
		  Log.d("sender", "Broadcasting message");
		  Intent intent = new Intent("save_audio_intent");
		  intent.putExtra(MILLIS_EXTRA_KEY, millis);
		  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void capturePhoto(long millis) {
		Intent intent = new Intent(this, PhotoActivity.class);
		intent.putExtra(MILLIS_EXTRA_KEY, millis);
		startActivity(intent);
	}
}


