package sethberg.glass.me;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm extends BroadcastReceiver {
	private static final String LOG_TAG = "Alarm";
	private static final int SECONDS_PER_PICTURE = 20;
	private PendingIntent toSend;
	
	private void sendIntent(Context context){
		Intent mServiceIntent = new Intent(context, CameraTimerService.class);
		mServiceIntent.putExtra("job", CameraTimerService.TAKE_PICTURE);
		context.startService(mServiceIntent);
	}
	
	@SuppressLint("Wakelock")
	@Override
	public void onReceive(Context context, Intent intent) {
		//CameraTimerService.mWakeLock.acquire(); throws a NullPointerException
		Log.d(LOG_TAG, "Alarm fired");
		Log.d(LOG_TAG, "extra: " + intent.getIntExtra(CameraTimerService.JOB_EXTRA, -1));
		sendIntent(context);
	}
	
	public void SetAlarm(Context context) {
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, Alarm.class).putExtra(CameraTimerService.JOB_EXTRA, CameraTimerService.TAKE_PICTURE);
		toSend = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); // changed PendingIntent.FLAG_UPDATE_CURRENT from 0 per http://stackoverflow.com/a/20157735/1476167
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * SECONDS_PER_PICTURE, toSend); 
	}

	public void CancelAlarm(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(toSend);
	}
}