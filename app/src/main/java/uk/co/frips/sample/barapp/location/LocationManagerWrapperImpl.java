package uk.co.frips.sample.barapp.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

public class LocationManagerWrapperImpl implements uk.co.frips.sample.barapp.location.LocationManagerWrapper {

    private final Context mContext;
    private final LocationManager mLocationManager;

    public LocationManagerWrapperImpl(Context context) {
        mContext = context.getApplicationContext();
        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    @Override
    public boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(mContext, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public Location requestLastKnownLocation() {
        try {
            return mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            Log.e("LocationManagerWrapper", "you don;t have location permission");
            return null;
        }
    }
}
