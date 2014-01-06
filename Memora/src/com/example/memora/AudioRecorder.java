package com.example.memora;

import java.io.File;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.google.android.glass.timeline.TimelineManager;

public class AudioRecorder extends Service {
	
	private final String LOG_TAG = "AudioRecorder";
	private LiveCard mLiveCard;	

    private AudioRecordThread recorder;
    
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    	  @Override
    	  public void onReceive(Context context, Intent intent) {
    		  Log.d(LOG_TAG, "Got message");
    		  long millis = intent.getExtras().getLong(MenuActivity.MILLIS_EXTRA_KEY);
    		  String filepath = recorder.startPolling(millis);
    		  
    		  //Publish timeline card with filepath in it.
    	  }
    };
    
	private void publishMainMemoraCard(Context context) {
		
	    if (mLiveCard == null) {
	        String cardId = "my_card";
	        TimelineManager tm = TimelineManager.from(context);
	        
	        mLiveCard = tm.createLiveCard(cardId);
	        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.memora_live_card);
	        mLiveCard.setViews(rv);
	        Intent intent = new Intent(context, MenuActivity.class);
	        mLiveCard.setAction(PendingIntent.getActivity(context, 0, intent, 0));
	        mLiveCard.publish(PublishMode.SILENT);

	    } else {
	        // Card is already published.
	        return;
	    }
	}

	private void unpublishCard(Context context) {
	    if (mLiveCard != null) {
	        mLiveCard.unpublish();
	        mLiveCard = null;
	    }
	}
	
	public AudioRecorder() {
	}
	
	@Override
    public void onCreate() {
		Log.d("Memora", "Service Started");
        super.onCreate();
        
        createMemoraDirectory();
        
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("save_audio_intent"));
        
        recorder = new AudioRecordThread();
		recorder.start();
        publishMainMemoraCard(this);
    }

    @Override
    public void onDestroy() {
    	Log.d("Memora", "Service Destroy");
    	unpublishCard(this);
    	recorder.interrupt();
        super.onDestroy();
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	private void createMemoraDirectory(){
		/*
        File directory = new File(MenuActivity.memoraDirectory);
        if (!directory.isDirectory()){
        	directory.mkdirs();
        }
        */
        File directory = new File(MenuActivity.memoraDirectoryAudio);
        if (!directory.isDirectory()){
        	directory.mkdirs();
        }
        directory = new File(MenuActivity.memoraDirectoryImages);
        if (!directory.isDirectory()){
        	directory.mkdirs();
        }
	}
	
}
