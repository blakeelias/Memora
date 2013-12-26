package com.example.memora;


import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AudioRecorder extends Service {
	
	private LiveCard mLiveCard;

	private void publishCard(Context context) {
	    if (mLiveCard == null) {
	        String cardId = "my_card";
	        TimelineManager tm = TimelineManager.from(context);
	        mLiveCard = tm.getLiveCard(cardId);

	        mLiveCard.setViews(new RemoteViews(context.getPackageName(), R.layout.card_text));
	        Intent intent = new Intent(context, EntryActivity.class);
	        mLiveCard.setAction(PendingIntent.getActivity(context, 0,
	                intent, 0));
	        mLiveCard.publish();
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
        
    }

    @Override
    public void onDestroy() {
    	Log.d("Memora", "Service Destroy");
        super.onDestroy();
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
