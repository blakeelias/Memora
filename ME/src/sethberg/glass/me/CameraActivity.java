package sethberg.glass.me;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends Activity {
	
	private static final String LOG_TAG = "CameraDemo";
	private Preview preview;
	private Button buttonClick;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		preview = new Preview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);

		buttonClick = (Button) findViewById(R.id.buttonClick);
		
		buttonClick.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
			private boolean tookPicture = false;

			@Override
			public void onSystemUiVisibilityChange(int a) {
				Log.d(LOG_TAG, "about to takePicture()");
				if (!tookPicture ) {
					preview.camera.takePicture(null, null, jpegCallback);
				}
				tookPicture = true;
				Log.d(LOG_TAG, "takePicture()'d");
			}
		});
		
		Log.d(LOG_TAG, "onCreate'd");
	}

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			String filepath = CameraTimerService.PHOTO_DIRECTORY + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(new Date(System.currentTimeMillis())) + ".jpg";
			try {
				outStream = new FileOutputStream(filepath);
				outStream.write(data);
				outStream.close();
				Log.d(LOG_TAG, "onPictureTaken - wrote bytes: " + data.length);
				//new PhotoLocationTagging(getBaseContext()).setLocation(filepath);
				//Log.d(LOG_TAG, "set location tag");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {}
			
			Log.d(LOG_TAG, "onPictureTaken - jpeg");
			cameraActivityCompleteCallback();
			finish();
			Log.d(LOG_TAG, "finish()'d");
		}
	};
	
	private void cameraActivityCompleteCallback(){
		Intent mServiceIntent = new Intent(this, CameraTimerService.class);
		mServiceIntent.putExtra(CameraTimerService.JOB_EXTRA, CameraTimerService.PICTURE_TAKEN);
		this.startService(mServiceIntent);
	}
}