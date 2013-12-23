package com.google.android.glass.sample.stopwatch;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class AudioRecorderService extends Service{
	
	private static final String LOG_TAG = "AudioRecordTest";
	private MediaRecorder recorder;
	private String mFileName = null;
	
	private void AudioRecordTest() {

		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += ("/" + Environment.DIRECTORY_PICTURES + "/audiorecordtest_" + String.valueOf(System.currentTimeMillis()) + ".mp4");
        Log.d(LOG_TAG, mFileName);
    }

	@Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "Recording Service Beginning");
        
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        this.AudioRecordTest();
        recorder.setOutputFile(mFileName);
        try{
        	recorder.prepare();
        }
        catch(IOException prepareFailed){
        	Log.d(LOG_TAG, prepareFailed.toString());
        	recorder = null;
        	return;
        }
        recorder.start();   // Recording is now started
        Log.d(LOG_TAG, "Recording Service Beginning");  
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	return 0;
    }

    @Override
    public void onDestroy() {
    	Log.d(LOG_TAG, "Recording Service onDestroy Called");
    	if(recorder != null){
	    	recorder.stop();
	        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
	        recorder.release(); // Now the object cannot be reused
	        Log.d(LOG_TAG, "Recording Stopped");
    	}
        super.onDestroy();
        
    }
}

