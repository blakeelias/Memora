package sethberg.glass.me;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	
	private static final String LOG_TAG = "Main Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
    public void onResume() {
        super.onResume();
        openOptionsMenu();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop:
            	return quit();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	private boolean quit() {
		Log.d(LOG_TAG, "Quitting ME");
		stopService(new Intent(this, CameraTimerService.class));
		closeOptionsMenu();
		finish();
		return true;
	}
	
	public void onOptionsMenuClosed(Menu menu) {
        finish();
    }
}
