package sethberg.glass.me;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

public class PhotoLocationTagging {
	
	private static final String LOG_TAG = "Location Tagger";
	private LocationManager locationManager;
	private Criteria criteria;
	private LocationListener locationListener;
	
	public PhotoLocationTagging(Context context){
		//There is a warning here, because it wants a static context. The argument to this must be
		//getApplicationContext()
		locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		      Log.d(LOG_TAG, "Got location update: " + location.toString());
		      locationManager.removeUpdates(locationListener);
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };
		
	}
	
	public void getLocation(){
		List<String> providers = locationManager.getAllProviders();
		for (String provider : providers) {
		    if (locationManager.isProviderEnabled(provider)) {
		        locationManager.requestLocationUpdates(provider, 1000, 1, locationListener);
		    }
		}
	}
}
