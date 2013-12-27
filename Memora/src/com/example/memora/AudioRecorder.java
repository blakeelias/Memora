package com.example.memora;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.google.android.glass.timeline.TimelineManager;

public class AudioRecorder extends Service {
	
	private LiveCard mLiveCard;

	private void publishCard(Context context) {
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
        publishCard(this);
    }

    @Override
    public void onDestroy() {
    	Log.d("Memora", "Service Destroy");
    	unpublishCard(this);
        super.onDestroy();
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	
}
