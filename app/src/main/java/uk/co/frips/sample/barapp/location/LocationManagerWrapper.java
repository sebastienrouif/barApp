package uk.co.frips.sample.barapp.location;

import android.location.Location;

public interface LocationManagerWrapper {
    boolean hasLocationPermission();

    Location requestLastKnownLocation();
}
