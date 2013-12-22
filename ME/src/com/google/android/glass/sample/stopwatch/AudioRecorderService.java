package com.google.android.glass.sample.stopwatch;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

public class AudioRecorderService extends Service{
	
	private static final String LOG_TAG = "AudioRecordTest";
	private MediaRecorder recorder;
	//private final String PATH_NAME = "picture_file_path";
	
	@Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "Recording Service Beginning");
        
        recorder = new MediaRecorder();
        /*recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(PATH_NAME);
        try{
        recorder.prepare();
        }
        catch(IOException prepareFailed){
        	Log.d(LOG_TAG, "Recording prepare failed and caught");
        }
        recorder.start();   // Recording is now started
        */
        
       
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
    	/*recorder.stop();
        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
        recorder.release(); // Now the object cannot be reused*/
        Log.d(LOG_TAG, "Recording Stopped");
        super.onDestroy();
        
    }
}

