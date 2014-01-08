package sethberg.glass.me;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class CameraTimerService extends Service {
	
	private static final String LOG_TAG = "Camera Timer Service";
	private static final int SECONDS_PER_PICTURE = 15;
	
	private LiveCard mLiveCard;	
	private Handler timerHandler;
	private Context thisContext = this;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
  	  	public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Got message");
 	  
  	  	}
	};
	
	public CameraTimerService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public void onCreate(){
		Log.d(LOG_TAG, "Service Started");
        super.onCreate();
        
        timerHandler = new Handler();
        timerRunnable.run();
        
        publishMainActivityCard(this);
        
	}
	
	@Override
    public void onDestroy() {
    	Log.d(LOG_TAG, "ME Service Destroyed");
    	timerHandler.removeCallbacks(timerRunnable);
    	unpublishCard(this);
        super.onDestroy();
    }

	
	private void publishMainActivityCard(Context context) {
		
	    if (mLiveCard == null) {
	        String cardId = "my_card";
	        TimelineManager tm = TimelineManager.from(context);
	        
	        mLiveCard = tm.createLiveCard(cardId);
	        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.activity_main);
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
	
	private Runnable timerRunnable = new Runnable() 
	{

	    public void run() 
	    {
	         Log.d(LOG_TAG, "Timer Fired");
	         //TODO Blake, this is where you should put your picture taking call.
	         Intent cameraIntent = new Intent(thisContext, CameraActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	         startActivity(cameraIntent);
	         timerHandler.postDelayed(this, SECONDS_PER_PICTURE*1000);
	    }
	};
}
