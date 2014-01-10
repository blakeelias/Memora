package sethberg.glass.me;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.view.View.OnSystemUiVisibilityChangeListener;

public class CameraActivity extends Activity {
	private static final String TAG = "CameraDemo";
	Camera camera;
	Preview preview;
	Button buttonClick;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		preview = new Preview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);
		camera = preview.camera;

		buttonClick = (Button) findViewById(R.id.buttonClick);
		
		buttonClick.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
			private boolean tookPicture = false;

			@Override
			public void onSystemUiVisibilityChange(int a) {
				Log.d(TAG, "about to takePicture()");
				if (!tookPicture ) {
					preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				}
				tookPicture = true;
				Log.d(TAG, "takePicture()'d");
			}
		});
		
		Log.d(TAG, "onCreate'd");
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			String filepath = "/mnt/sdcard/DCIM/Camera/test1_" + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date(System.currentTimeMillis())) + ".jpg";
			try {
				// write to local sandbox file system
				// outStream =
				// CameraDemo.this.openFileOutput(String.format("%d.jpg",
				// System.currentTimeMillis()), 0);
				// Or write to sdcard
				outStream = new FileOutputStream(filepath);
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
				//new PhotoLocationTagging(getBaseContext()).setLocation(filepath);
				Log.d(TAG, "set location tag");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
			finish();
			Log.d(TAG, "finish()'d");
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()'d");
	}
	
	@Override
	public void onStop() {
		super.onDestroy();
		Log.d(TAG, "onStop()'d");
	}
}