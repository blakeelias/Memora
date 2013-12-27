package com.example.memora;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class MenuActivity extends Activity {
	
	@Override
    public void onResume() {
        super.onResume();
        openOptionsMenu();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memora_live_card);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        switch (item.getItemId()) {
            case R.id.stop:
                stopService(new Intent(this, AudioRecorder.class));
                finish();
                //What is the result of calling finish before the return statement?
                //It does get past the finish statement.
                return true;
            case R.id.capture:
            	return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}


