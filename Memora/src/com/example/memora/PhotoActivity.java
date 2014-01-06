package com.example.memora;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;

public class PhotoActivity extends Activity {
	static final String LOG_TAG = "PhotoActivity";
	
	long millis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		long millis = getIntent().getExtras().getLong(MenuActivity.MILLIS_EXTRA_KEY);
		takePicture(millis);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}
	
	private static final int TAKE_PICTURE_REQUEST = 1;

	private void takePicture(long millis) {
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    File pictureFile = new File(photoFileName(millis));
	    try {
	        pictureFile.createNewFile();
	    } catch (IOException e) {
	    	// TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    Log.d(LOG_TAG, pictureFile.getPath());
	    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureFile));
	    startActivityForResult(intent, TAKE_PICTURE_REQUEST);
	    this.millis = millis;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
	        Log.d(LOG_TAG, "in onActivityResult()");
	        Log.d(LOG_TAG, "Image saved to:\n" + data.getData());
	    }

	    super.onActivityResult(requestCode, resultCode, data);
	}

	/*private void processPictureWhenReady(final String picturePath) {
		Log.d(LOG_TAG, "in processPictureWhenReady()");
	    final File pictureFile = new File(picturePath);

	    if (pictureFile.exists()) {
	        // The picture is ready; process it.
	    } else {
	        // The file does not exist yet. Before starting the file observer, you
	        // can update your UI to let the user know that the application is
	        // waiting for the picture (for example, by displaying the thumbnail
	        // image and a progress indicator).

	        final File parentDirectory = pictureFile.getParentFile();
	        FileObserver observer = new FileObserver(parentDirectory.getPath()) {
	            // Protect against additional pending events after CLOSE_WRITE is
	            // handled.
	            private boolean isFileWritten;

	            @Override
	            public void onEvent(int event, String path) {
	                if (!isFileWritten) {
	                    // For safety, make sure that the file that was created in
	                    // the directory is actually the one that we're expecting.
	                	Log.d(LOG_TAG, "parentDirectory not null? " + parentDirectory);
	                	Log.d(LOG_TAG, "path not null? " + path);
	                    File affectedFile = new File(parentDirectory, path);
	                    isFileWritten = (event == FileObserver.CLOSE_WRITE
	                            && affectedFile.equals(pictureFile));

	                    if (isFileWritten) {
	                        stopWatching();

	                        // Now that the file is ready, recursively call
	                        // processPictureWhenReady again (on the UI thread).
	                        runOnUiThread(new Runnable() {
	                            @Override
	                            public void run() {
	                                processPictureWhenReady(picturePath);
	                            }
	                        });
	                    }
	                }
	            }
	        };
	        observer.startWatching();
	    }
	    finish();
	}*/
	
	private String photoFileName(long millis) {
        return MenuActivity.memoraDirectoryImages + File.separator + String.valueOf(millis) + ".jpg";
    }

}
