package uk.co.frips.sample.barapp.list;

import android.location.Location;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import uk.co.frips.sample.barapp.data.PlacesServicesAdapter;
import uk.co.frips.sample.barapp.data.entity.Bar;
import uk.co.frips.sample.barapp.data.entity.MyBarsResult;
import uk.co.frips.sample.barapp.location.LocationManagerWrapper;
import uk.co.frips.sample.barapp.util.schedulers.BaseSchedulerProvider;

public class BarListPresenter implements BarListContract.Presenter {
    private final PlacesServicesAdapter mPlacesServicesAdapter;
    private final BarListContract.View mBarListView;
    private LocationManagerWrapper mLocationManagerWrapper;
    private BaseSchedulerProvider mSchedulerProvider;
    private final CompositeSubscription mSubscriptions;

    public BarListPresenter(BarListContract.View barListView,
                            PlacesServicesAdapter placesServicesAdapter,
                            LocationManagerWrapper locationManagerWrapper,
                            BaseSchedulerProvider schedulerProvider) {
        mBarListView = barListView;
        mLocationManagerWrapper = locationManagerWrapper;
        mSchedulerProvider = schedulerProvider;
        mSubscriptions = new CompositeSubscription();
        mPlacesServicesAdapter = placesServicesAdapter;
        barListView.setPresenter(this);
    }


    @Override
    public void subscribe() {
        mSubscriptions.add(mPlacesServicesAdapter.getMyBarsObservable()
                .subscribe(new Action1<MyBarsResult>() {
                    @Override
                    public void call(MyBarsResult myBarsResult) {
                        if (myBarsResult.getMyBars().isEmpty()) {
                            mBarListView.showEmpty();
                        } else {
                            mBarListView.showBars(myBarsResult);
                        }
                    }
                }));
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void onBarClicked(Bar item) {
        mBarListView.openGoogleMaps(item.latitude, item.longitude);
    }

    @Override
    public void onLocateMe() {
        if (mLocationManagerWrapper.hasLocationPermission()) {
            Location location = mLocationManagerWrapper.requestLastKnownLocation();
            loadBarsAround(location.getLatitude(), location.getLongitude());
        } else {
            mBarListView.requestLocationPermission();
        }
    }

    @Override
    public void onLocationPermissionGranted() {
        onLocateMe();
    }

    private void loadBarsAround(final double myLat, final double myLong) {
        mSubscriptions.add(mPlacesServicesAdapter.getPlaces(myLat, myLong)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mBarListView.showError();
                    }
                }));
    }
}
