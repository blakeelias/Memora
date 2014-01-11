package sethberg.glass.me;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.util.Log;

public class PhotoLocationTagging {
	
	private static final String LOG_TAG = "Location Tagger";
	private LocationManager locationManager;
	private Criteria criteria;
	private LocationListener locationListener;
	private String filepath;
	
	public PhotoLocationTagging(Context context){
		//There is a warning here, because it wants a static context. The argument to this must be
		locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
	}
	
	public void setLocation(String filepath){
		this.filepath = filepath;
		String provider = locationManager.getBestProvider(criteria, true);	
		locationManager.requestSingleUpdate(provider, new LocationListener(){

	        @Override
	        public void onLocationChanged(Location location) {
	            Log.d(LOG_TAG, location.toString());
	            setGpsExif(location);
	        }

	        @Override
	        public void onProviderDisabled(String provider) {}

	        @Override
	        public void onProviderEnabled(String provider) {}

	        @Override
	        public void onStatusChanged(String provider, int status, Bundle extras) {}

	    }, null);
	}
	
	
	//Change this to void, or even better do some callback thing
	private boolean setGpsExif(Location location){
		
		ExifInterface exif;
		try
		{
			exif = new ExifInterface(filepath);
		}
		catch(IOException e)
		{
			Log.d(LOG_TAG, "ExifInterface not initialized with exception: " + e.toString());
			return false;
		}
        //String latitudeStr = "90/1,12/1,30/1";
        double lat = location.getLatitude();
        double alat = Math.abs(lat);
        String dms = Location.convert(alat, Location.FORMAT_SECONDS);
        String[] splits = dms.split(":");
        String[] secnds = (splits[2]).split("\\.");
        String seconds;
        
        if(secnds.length==0){ seconds = splits[2]; }
        else{ seconds = secnds[0]; }

        String latitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitudeStr);
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, lat>0?"N":"S");

        double lon = location.getLongitude();
        double alon = Math.abs(lon);

        dms = Location.convert(alon, Location.FORMAT_SECONDS);
        splits = dms.split(":");
        secnds = (splits[2]).split("\\.");

        if(secnds.length==0){ seconds = splits[2]; }
        else{ seconds = secnds[0]; }
        String longitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";

        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitudeStr);
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lon>0?"E":"W");

        try
        {
        	exif.saveAttributes();
        }
        catch(IOException e)
        {
        	Log.d(LOG_TAG, "exif.saveAttributes failed with exception: " + e.toString());
        	return false;
        }
        Log.d(LOG_TAG, "Method ran to completion");
        return true;

    }
}
