package com.example.memora;


import com.google.android.glass.timeline.LiveCard;
<<<<<<< HEAD
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.google.android.glass.timeline.TimelineManager;

import android.app.PendingIntent;
=======
import com.google.android.glass.timeline.TimelineManager;

>>>>>>> 09ed2f08ef663aa785768be782ec222a5966aa85
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class AudioRecorder extends Service {
	
	private LiveCard mLiveCard;

	private void publishCard(Context context) {
	    if (mLiveCard == null) {
	        String cardId = "my_card";
	        TimelineManager tm = TimelineManager.from(context);
	        
	        mLiveCard = tm.createLiveCard(cardId);
	        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.memora_live_card);
	        mLiveCard.setViews(rv);
	        Intent intent = new Intent(context, MainActivity.class);
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
		publishCard(this);
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
