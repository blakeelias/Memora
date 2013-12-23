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

	//private static final int RECORDER_SAMPLERATE = 8000;
	//private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	
	int BufferElements2Rec = 1024 * 8 * 20; // want to play 2048 (2K) since 2 bytes we use only 1024
	int BytesPerElement = 2; // 2 bytes in 16bit format

	
	private final int channels = AudioFormat.CHANNEL_IN_MONO;
	//TODO Implement logic to determine number of channels.
	private final int numChannels = 1; //This is hardcoded because I am using 1 channel. 
    private final long longSampleRate = 8000;
    private int bitsPerSample= 16;
    private byte RECORDER_BPP = 16;
    private int byteRate = (int)longSampleRate * channels * (int)(RECORDER_BPP)/8;
    private int totalAudioLen = BufferElements2Rec;//clipData.length;
    private int totalDataLen = (totalAudioLen * channels * bitsPerSample / 8) + 36;
	
	private AudioRecord recorder = null;
	
	
	
	
	private void startRecording() {
		
	    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
	    		(int)longSampleRate, channels,
	            RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

	    recorder.startRecording();
	}

	private void writeAudioDataToFile() {
	    String filePath = AudioFileName();
	    byte clipData[] = new byte[BufferElements2Rec * BytesPerElement];
	    byte header[] = new byte[44];
	    byte wavFile[] = new byte[clipData.length + header.length];
	    
	    //TODO: Wrap the FileOutputStream in a BufferedOutputStream (Not sure why, but it's recommended by Eclipse)
	    FileOutputStream os = null;
	    try {
	        os = new FileOutputStream(filePath);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) numChannels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8);  // block align\
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

	    recorder.read(clipData, 0, BufferElements2Rec*2); //Added the *2
	    
	    System.arraycopy(header, 0, wavFile, 0, header.length);
	    System.arraycopy(clipData, 0, wavFile, header.length, clipData.length);
	    
        try {
            os.write(wavFile, 0, wavFile.length);
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
        mFileName += ("/" + Environment.DIRECTORY_PICTURES + "/audiorecordtest_" + String.valueOf(System.currentTimeMillis()) + ".wav");
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

