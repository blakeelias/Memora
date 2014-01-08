package sethberg.glass.me;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

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
            	Log.d(LOG_TAG, "Quitting ME");
                stopService(new Intent(this, CameraTimerService.class));
                finish();
                closeOptionsMenu();
                return true;
            case R.id.capture:
            	//Intent cameraIntent = new Intent(this, CameraActivity.class);
            	//cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	//startActivity(cameraIntent);
            	PhotoLocationTagging tag = new PhotoLocationTagging(getApplicationContext());
            	tag.getLocation();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	/**
	 * @param brightness and integer between 0 and 100.
	 * values below 0 set screen to default brightness. 
	 * values above 100 set screen to max brightness.
	 */
	@SuppressWarnings("unused")
	//This is currently unused because Glass automatically sets screen brightness.
	private void changeBrightness(int brightness){
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = Math.min(brightness, 1) / 100.0f;
		getWindow().setAttributes(lp);
	}
	
	public void onOptionsMenuClosed(Menu menu) {
        finish();
    }

}
