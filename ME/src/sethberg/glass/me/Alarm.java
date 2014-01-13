package sethberg.glass.me;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm extends BroadcastReceiver {
	private static final String LOG_TAG = "Alarm";
	private static final int SECONDS_PER_PICTURE = 20;
	
	private void sendIntent(Context context){
		Intent mServiceIntent = new Intent(context, CameraTimerService.class);
		mServiceIntent.putExtra("job", CameraTimerService.TAKE_PICTURE);
		context.startService(mServiceIntent);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "Alarm fired");
		sendIntent(context);
	}
	
	public void SetAlarm(Context context) {
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, Alarm.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * SECONDS_PER_PICTURE, pi); 
	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, Alarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}