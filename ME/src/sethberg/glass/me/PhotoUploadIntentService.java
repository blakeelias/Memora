package sethberg.glass.me;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.IntentService;
import android.content.Intent;

public class PhotoUploadIntentService extends IntentService {
	
	private ArrayList<File> mlsFiles;
	
	public PhotoUploadIntentService(String name) {
		super(name);
	}
	
	@Override
    protected void onHandleIntent(Intent workIntent) {
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
    			//currentFile.delete();
    		}
    	}
	}
}
