package com.google.android.glass.sample.stopwatch;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class AudioRecorderService extends Service{
	
	private static final String LOG_TAG = "AudioRecordTest";

	private static final int RECORDER_SAMPLERATE = 8000;
	//private static final int CHANNEL_TYPE = AudioFormat.CHANNEL_IN_MONO;
	private static final int ENCODING_TYPE = AudioFormat.ENCODING_PCM_16BIT;
	private final int CHANNEL_TYPE = AudioFormat.CHANNEL_IN_MONO;
	
	//TODO Implement logic to determine number of channels.
	private final int numChannels = 1; //This is hardcoded because I am using MONO. 
    private final long longSampleRate = 8000;
    private int bitsPerSample= 16;
    private byte RECORDER_BPP = 16;
    private int byteRate = (int)longSampleRate * numChannels * (int)(RECORDER_BPP)/8; //Changed from channels to numChannels
    
    private final int bufferSize = 160; //Should probably keep this size the same and change numBuffers
    private final int numBuffers = 256 * 4; //256 gives about 2 seconds
    
    private RingBufferRecord audioThread;
	
	private String AudioFileName() {
		String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += ("/" + Environment.DIRECTORY_PICTURES + "/audiorecordtest_" + String.valueOf(System.currentTimeMillis()) + ".wav");
        Log.d(LOG_TAG, mFileName);
        return mFileName;
    }

	@Override
    public void onCreate() {
        super.onCreate();
        audioThread = new RingBufferRecord();
		audioThread.start();
    }

    @Override
    public void onDestroy() {
    	audioThread.interrupt();
        super.onDestroy();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	return 0;
    }
    
    private class RingBufferRecord extends Thread{ 
    	
        /**
         * Give the thread high priority so that it's not canceled unexpectedly, and start it
         */
    	
    	private byte[][] buffers;
    	private byte[] totalBuffer;
    	
        public RingBufferRecord()
        { 
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        }

        @Override
        public void run()
        { 
            Log.d("Audio", "Running Audio Thread");
            AudioRecord recorder = null;
            buffers  = new byte[numBuffers][bufferSize];
            totalBuffer = new byte[numBuffers * bufferSize];
            int ix = 0;

            try
            {
                int N = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,CHANNEL_TYPE,ENCODING_TYPE);
                recorder = new AudioRecord(AudioSource.MIC, RECORDER_SAMPLERATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, N*200);
                
                recorder.startRecording();
                /*
                 * Loops until something outside of this thread stops it.
                 */
                while(!interrupted())
                { 
                	//TODO make sure that ix gets reset so that it doesn't overflow.
                    Log.d("Audio", "Writing new data to buffer");
                    byte[] buffer = buffers[ix++ % bufferSize];
                    recorder.read(buffer,0,buffer.length);
                }
            }
            catch(Throwable x)
            { 
                Log.d("Audio", "Error reading voice audio", x);
            }
            /*
             * Frees the thread's resources after the loop completes so that it can be run again
             */
            finally
            { 
            	//TODO This is where I will iterate through buffers and pull the last n seconds of audio and save them.
                recorder.stop();
                recorder.release();
                pollRingBuffer(ix);
                writeAudioDataToFile();
                Log.d("Audio", "Thread Terminated");
            }
        }
        
        private void pollRingBuffer(int ix){
        	int i;
        	int j;
        	for(i = 0; i<numBuffers; i++){
        		for(j = 0; j<bufferSize; j++){
        			totalBuffer[i*bufferSize + j] = buffers[(ix + i)%numBuffers][j];
        		}
        	}
        }

        private void writeAudioDataToFile() {
        	int totalAudioLen = numBuffers * bufferSize;//BufferElements2Rec;
            int totalDataLen = (totalAudioLen * numChannels * bitsPerSample / 8) + 36;
    	    String filePath = AudioFileName();
    	    byte header[] = new byte[44];
    	    byte wavFile[] = new byte[totalBuffer.length + header.length];
    	    
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
            header[32] = (byte) (2 * 16 / 8);  // block align
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
    	    
    	    System.arraycopy(header, 0, wavFile, 0, header.length);
    	    System.arraycopy(totalBuffer, 0, wavFile, header.length, totalBuffer.length);
    	    
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
    }
}



