package com.google.android.glass.sample.stopwatch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AutoPhotoService extends Service {
	public AutoPhotoService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		
	}

	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		return 0;
	}
}
