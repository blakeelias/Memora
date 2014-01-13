package sethberg.glass.me;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.google.android.glass.timeline.TimelineManager;

public class CameraTimerService extends Service {
	
	private static final String LOG_TAG = "Camera Timer Service";
	public static final String PHOTO_DIRECTORY = "something?";
	//Intent extra constants
	public static final int DEFAULT = 0;
	public static final int TAKE_PICTURE = 1;	
	public static final int PICTURE_TAKEN = 2;
	//
	public boolean isUploading = false;
	private LiveCard mLiveCard;
	private Alarm alarm = new Alarm();
	
	public static boolean wifiConnected;
	private ConnectivityManager connectivityManager;
	
	BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	        networkStateChange();
	    }
	};

	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		int returnVal = super.onStartCommand(intent, flags, startId);
		int extra = intent.getIntExtra("job", DEFAULT);
		switch(extra){
			case DEFAULT: 		break;
			case TAKE_PICTURE: 	takePicture();
								break;
			case PICTURE_TAKEN: pictureTaken();
								break;
		}
		return returnVal;
    }
	
	@Override
    public IBinder onBind(Intent intent) {
        return null;
    }
	
	@Override
	public void onCreate(){
		Log.d(LOG_TAG, "Service Started");
        super.onCreate();
        publishMainActivityCard(this);
        //Setup connectivity broadcastReceiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);        
    	registerReceiver(networkStateReceiver, filter);
    	
        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiConnected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        
        alarm.SetAlarm(this);
	}
	
	@Override
    public void onDestroy() {
    	Log.d(LOG_TAG, "ME Service Destroyed");
    	alarm.CancelAlarm(this);
    	unpublishCard(this);
    	super.onDestroy();
    }
	
	@SuppressLint("Wakelock")
	private void takePicture(){
		Log.d(LOG_TAG, "onReceive'd");
		PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();
		
		Log.d(LOG_TAG, "onAlarm'd");
		FileLog.println("onAlarm'd");
		
		if (!pm.isScreenOn()){
			Log.d(LOG_TAG, "Screen off registered");
			Intent cameraIntent = new Intent(this, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(cameraIntent);
			Log.d(LOG_TAG, "Pic activity started");
		}
		wl.release();
	}
	
	private void pictureTaken(){
		Log.d(LOG_TAG, "Picture taken callback'd");
	}
	
	private void networkStateChange(){
		wifiConnected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		if(wifiConnected){
			//Send intent to begin upload
			Intent mServiceIntent = new Intent(this, PhotoUploadIntentService.class);
			this.startService(mServiceIntent);
		}
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
}
