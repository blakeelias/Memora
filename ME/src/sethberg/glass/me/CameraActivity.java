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
import android.view.KeyEvent;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends Activity {
	
	public static final SimpleDateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault());
	private static final String LOG_TAG = "CameraDemo";
	public static String startTime = null;
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_CAMERA) {
	        // Stop the preview and release the camera.
	        // Execute your logic as quickly as possible
	        // so the capture happens quickly.
	    	
	    	//TODO(Blake): allow the activity to resume one time
	    	/*interrupted = true;
	    	preview.surfaceDestroyed(preview.getHolder());*/
	    	finish();
	        return false;
	    } else {
	        return super.onKeyDown(keyCode, event);
	    }
	}

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;

			String captureTime = FILE_NAME_DATE_FORMAT.format(new Date(System.currentTimeMillis()));
			String commitHash = "v0.1.1"; // This string will be one commit behind when checked out from commit history. Replace with current commit hash from 'git status' before running.

			String filepath = String.format("%s/%s_%s_%s.jpg", CameraTimerService.PHOTO_DIRECTORY, startTime, commitHash, captureTime);


			try {
				outStream = new FileOutputStream(filepath);
				outStream.write(data);
				outStream.close();
				Log.d(LOG_TAG, "onPictureTaken - wrote bytes: " + data.length);
				//new PhotoLocationTagging(getBaseContext()).setLocation(filepath);
				//Log.d(TAG, "set location tag");
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