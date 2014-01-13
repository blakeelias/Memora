package sethberg.glass.me;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class PhotoUploadIntentService extends IntentService {
	
	private static final String LOG_TAG = "Photo Upload Intent Service";
	private ArrayList<File> mlsFiles;
	private boolean uploading = false;
	
	public PhotoUploadIntentService(String name) {
		super(name);
	}
	
	public PhotoUploadIntentService(){
		super("default");
	}
	
	@Override
    protected void onHandleIntent(Intent workIntent) {
		//Ensures that the PhotoUploadServiceIntent will not queue requests, and will die after handling one upload request.
		Log.d(LOG_TAG, "Intent Received");
		if(uploading) {
			return;
		}
		Log.d(LOG_TAG, "Not already uploading");
    	mlsFiles = new ArrayList<File>(Arrays.asList((new File(CameraTimerService.PHOTO_DIRECTORY)).listFiles()));
		uploadPhotos();
    }
	
	private void uploadPhotos(){
		while (mlsFiles.size() > 0 && CameraTimerService.wifiConnected){
    		for (File currentFile : mlsFiles){
    			if(!CameraTimerService.wifiConnected){
    				break;
    			}
    			//upload(currentFile);
    			Log.d(LOG_TAG, currentFile.getName() + ": Deleted");
    			currentFile.delete();
    		}
    		mlsFiles = new ArrayList<File>(Arrays.asList((new File(CameraTimerService.PHOTO_DIRECTORY)).listFiles()));
    	}
	}
}
