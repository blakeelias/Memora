package com.example.memora;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MomentsImmersion extends Activity {

    private ArrayList<Card> mlcCards = new ArrayList<Card>();
    private ArrayList<File> mlsFiles = new ArrayList<File>(Arrays.asList((new File(MenuActivity.memoraDirectoryAudio)).listFiles()));
    
    private static final String LOG_TAG = "Moments Immersion";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Collections.reverse(mlsFiles);
        for (int i = 0; i < mlsFiles.size(); i++)
        {
            Card newCard = new Card(this);
            File file = mlsFiles.get(i);
            newCard.setImageLayout(Card.ImageLayout.FULL);
            newCard.setText(timeFromFile(file));
            //Uri image = new Uri.Builder().path(MenuActivity.memoraDirectoryImages + File.separator + nameFromFile(file)).build();
            //newCard.addImage(image);
            mlcCards.add(newCard);
        }

        CardScrollView csvCardsView = new CardScrollView(this);
        csaAdapter cvAdapter = new csaAdapter();
        csvCardsView.setAdapter(cvAdapter);
        csvCardsView.activate();
        csvCardsView.setOnItemClickListener(new OnItemClickListener() 
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
            {
            	Log.d(LOG_TAG, "Click recognized on moment #" + position);
            	
            	MediaPlayer mpPlayProgram = MediaPlayer.create(getBaseContext(), Uri.fromFile(mlsFiles.get(position)));
            	mpPlayProgram.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                    	Log.d(LOG_TAG, "MP Released");
                        mp.release();
                    }

                });
            	mpPlayProgram.start();
            	Log.d(LOG_TAG, "MP Started");
            }
       });
        
        setContentView(csvCardsView);
    }
    
    private String nameFromFile(File file){
    	String[] split = file.toString().split("/");
    	String timestamp = split[split.length-1];
    	timestamp = split[split.length-1];
    	timestamp = timestamp.substring(0, timestamp.length() - 4);
    	Log.d(LOG_TAG, timestamp);
    	return timestamp;
    }

    private String timeFromFile(File file){
    	//Add timezone compatibility
    	String[] split = file.toString().split("/");
    	String timestamp = split[split.length-1];
    	timestamp = split[split.length-1];
    	timestamp = timestamp.substring(0, timestamp.length() - 4);
    	Date date=new Date(Long.parseLong(timestamp, 10));
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a");
    	timestamp = sdf.format(date);
    	return timestamp;
    }
    
    private class csaAdapter extends CardScrollAdapter
    {
        @Override
        public int findIdPosition(Object id)
        {
            return -1;
        }

        @Override
        public int findItemPosition(Object item)
        {
            return mlcCards.indexOf(item);
        }

        @Override
        public int getCount()
        {
            return mlcCards.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mlcCards.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return mlcCards.get(position).toView();
        }
    }
}
