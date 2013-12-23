package com.google.android.glass.sample.stopwatch;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class AudioRecorderService extends Service{
	
	private static final String LOG_TAG = "AudioRecordTest";

	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private AudioRecord recorder = null;
	
	int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
	int BytesPerElement = 2; // 2 bytes in 16bit format

	private void startRecording() {

	    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
	            RECORDER_SAMPLERATE, RECORDER_CHANNELS,
	            RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

	    recorder.startRecording();
	}

	//convert short to byte
	private byte[] short2byte(short[] sData) {
	    int shortArrsize = sData.length;
	    byte[] bytes = new byte[shortArrsize * 2];
	    for (int i = 0; i < shortArrsize; i++) {
	        bytes[i * 2] = (byte) (sData[i] & 0x00FF);
	        bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
	        sData[i] = 0;
	    }
	    return bytes;

	}

	private void writeAudioDataToFile() {
	    // Write the output audio in byte

	    String filePath = AudioFileName();
	    short sData[] = new short[BufferElements2Rec];

	    FileOutputStream os = null;
	    try {
	        os = new FileOutputStream(filePath);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }

        // gets the voice output from microphone to byte format

        recorder.read(sData, 0, BufferElements2Rec);
        System.out.println("Short wirting to file" + sData.toString());
        try {
            // // writes the data to file from buffer
            // // stores the voice buffer
            byte bData[] = short2byte(sData);
            os.write(bData, 0, BufferElements2Rec * BytesPerElement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    
	    try {
	        os.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void stopRecording() {
	    // stops the recording activity
	    if (null != recorder) {
	        recorder.stop();
	        recorder.release();
	        recorder = null;
	    }
	}
	
	private String AudioFileName() {
		String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += ("/" + Environment.DIRECTORY_PICTURES + "/audiorecordtest_" + String.valueOf(System.currentTimeMillis()) + ".pcm");
        Log.d(LOG_TAG, mFileName);
        return mFileName;
    }

	@Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "Recording Service onCreate Called");
        startRecording();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	return 0;
    }

    @Override
    public void onDestroy() {
    	Log.d(LOG_TAG, "Recording Service onDestroy Called");
    	writeAudioDataToFile();	//I wonder if these two lines need to be flipped.
    	stopRecording();
        super.onDestroy();
    }
}

