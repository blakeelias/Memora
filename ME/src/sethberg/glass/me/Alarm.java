package sethberg.glass.me;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import sethberg.glass.me.*;

public class Alarm extends BroadcastReceiver {
	private static final String LOG_TAG = "Alarm";
	private static final int SECONDS_PER_PICTURE = 20;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "onReceive'd");
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();

		// Put here YOUR code.
		Log.d(LOG_TAG, "alarm fired");
		FileLog.println("alarm fired");
		//Add logic to check if screen is on
		if (!pm.isScreenOn()){
			Log.d(LOG_TAG, "Screen off registered");
			Intent cameraIntent = new Intent(context, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(cameraIntent);
			Log.d(LOG_TAG, "Pic activity started");
		}

		wl.release();
	}

	public void SetAlarm(Context context) {
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, Alarm.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * SECONDS_PER_PICTURE, pi); // Millisec * Second * Minute
	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, Alarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}