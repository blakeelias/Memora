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
	
	private static final String LOG_TAG = "Audio";

	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int ENCODING_TYPE = AudioFormat.ENCODING_PCM_16BIT;
	private final int CHANNEL_TYPE = AudioFormat.CHANNEL_IN_MONO;
	private final int NUM_CHANNELS = 1;
	private byte BITS_PER_SAMPLE = 16;  
    private final int BYTE_RATE = RECORDER_SAMPLERATE * NUM_CHANNELS * (BITS_PER_SAMPLE / 8);
    private final int secondsOfRecording = 5;
    
    private final int bufferSize = 160; //Each buffer holds 1/100th of a second.
    private final int numBuffers = 100 * secondsOfRecording; 
    
    private RingBufferRecord audioThread;
	
	@Override
    public void onCreate() {
        super.onCreate();
        audioThread = new RingBufferRecord();
		audioThread.start();
    }

    @Override
    public void onDestroy() {
    	audioThread.interrupt();
    	//Possibly want to block here until audioThread has terminated.
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
            Log.d(LOG_TAG, "Running Audio Thread");
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
                    Log.d(LOG_TAG, "Writing new data to buffer");
                    //byte[] buffer = buffers[ix++ % bufferSize];
                    recorder.read(buffers[ix++ % numBuffers],0,bufferSize);
                }
            }
            catch(Throwable x)
            { 
                Log.d(LOG_TAG, "Error reading voice audio", x);
            }
            /*
             * Frees the thread's resources after the loop completes so that it can be run again
             */
            finally
            { 
                recorder.stop();
                recorder.release();
                pollRingBuffer(ix);
                writeAudioDataToFile();
                Log.d(LOG_TAG, "Thread Terminated");
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
          
        private String AudioFileName() {
    		String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += ("/" + Environment.DIRECTORY_PICTURES + "/audiorecordtest_" + String.valueOf(System.currentTimeMillis()) + ".wav");
            Log.d(LOG_TAG, mFileName);
            return mFileName;
        }

        private void writeAudioDataToFile() {
        	int totalAudioLen = numBuffers * bufferSize;
            int totalDataLen = (totalAudioLen * NUM_CHANNELS * BITS_PER_SAMPLE / 8) + 36;
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
            header[22] = (byte) NUM_CHANNELS;
            header[23] = 0;
            header[24] = (byte) (RECORDER_SAMPLERATE & 0xff);
            header[25] = (byte) ((RECORDER_SAMPLERATE >> 8) & 0xff);
            header[26] = (byte) ((RECORDER_SAMPLERATE >> 16) & 0xff);
            header[27] = (byte) ((RECORDER_SAMPLERATE >> 24) & 0xff);
            header[28] = (byte) (BYTE_RATE & 0xff);
            header[29] = (byte) ((BYTE_RATE >> 8) & 0xff);
            header[30] = (byte) ((BYTE_RATE >> 16) & 0xff);
            header[31] = (byte) ((BYTE_RATE >> 24) & 0xff);
            header[32] = (byte) (NUM_CHANNELS * BITS_PER_SAMPLE / 8);//(2 * 16 / 8);  // block align (might be half what it should be)
            header[33] = 0;
            header[34] = BITS_PER_SAMPLE;  // bits per sample
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



