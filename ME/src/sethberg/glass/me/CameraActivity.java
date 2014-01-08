package sethberg.glass.me;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});
		buttonClick.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int a) {
				//takePictureRepeatedly();
				Log.d(TAG, "about to call takePicture()");
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				Log.d(TAG, "called takePicture()");
			}
		});
		
		Log.d(TAG, "onCreate'd");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		camera.release();
		camera = null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (camera == null) {
			camera = Camera.open();
		}
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
			try {
				// write to local sandbox file system
				// outStream =
				// CameraDemo.this.openFileOutput(String.format("%d.jpg",
				// System.currentTimeMillis()), 0);
				// Or write to sdcard
				outStream = new FileOutputStream(String.format(
						"/mnt/sdcard/memora/images/%d.jpg", System.currentTimeMillis()));
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
			camera.release();
			Log.d(TAG, "released camera");
			finish();
			Log.d(TAG, "called finish()");
		}
	};

	public void takePictureRepeatedly() {
		boolean pictureTaken = false;
		int i = 0;
		while (!pictureTaken) {
			try {
				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				pictureTaken = true;
			}
			catch (NullPointerException e) {
				++i;
				Log.d(TAG, "camera not ready: " + i);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}