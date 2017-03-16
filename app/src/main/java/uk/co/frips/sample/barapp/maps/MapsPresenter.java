package uk.co.frips.sample.barapp.maps;

import android.location.Location;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import uk.co.frips.sample.barapp.data.PlacesServicesAdapter;
import uk.co.frips.sample.barapp.data.entity.MyBarsResult;
import uk.co.frips.sample.barapp.location.LocationManagerWrapper;
import uk.co.frips.sample.barapp.util.schedulers.BaseSchedulerProvider;

public class MapsPresenter implements MapsContract.Presenter {

    private final PlacesServicesAdapter mPlacesServicesAdapter;
    private final MapsContract.View mMapsView;
    private LocationManagerWrapper mLocationManagerWrapper;
    private BaseSchedulerProvider mSchedulerProvider;
    private final CompositeSubscription mSubscriptions;

    public MapsPresenter(MapsContract.View mapsView,
                         PlacesServicesAdapter placesServicesAdapter,
                         LocationManagerWrapper locationManagerWrapper,
                         BaseSchedulerProvider schedulerProvider) {
        mMapsView = mapsView;
        mLocationManagerWrapper = locationManagerWrapper;
        mSchedulerProvider = schedulerProvider;
        mSubscriptions = new CompositeSubscription();
        mPlacesServicesAdapter = placesServicesAdapter;
        mapsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        mMapsView.startMap();
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void onMapReady() {
        mSubscriptions.add(mPlacesServicesAdapter.getMyBarsObservable()
                .subscribe(new Action1<MyBarsResult>() {
                    @Override
                    public void call(MyBarsResult myBarsResult) {
                        mMapsView.clearBars();
                        mMapsView.showBars(myBarsResult);
                        mMapsView.centerMapOn(myBarsResult.getMyLat(), myBarsResult.getMyLong());
                    }
                }));
    }

    @Override
    public void onLocateMe() {
        if (mLocationManagerWrapper.hasLocationPermission()) {
            Location location = mLocationManagerWrapper.requestLastKnownLocation();
            loadBarsAround(location.getLatitude(), location.getLongitude());
        } else {
            mMapsView.requestLocationPermission();
        }
    }

    @Override
    public void onLocationPermissionGranted() {
        onLocateMe();
    }

    private void loadBarsAround(final double myLat, final double myLong) {
        mSubscriptions.add(mPlacesServicesAdapter.getPlaces(myLat, myLong)
                .subscribeOn(mSchedulerProvider.ui())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mMapsView.showError();
                    }
                }));
    }
}
