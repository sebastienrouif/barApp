package uk.co.frips.sample.barapp.maps;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import uk.co.frips.sample.barapp.data.PlacesServicesAdapter;
import uk.co.frips.sample.barapp.data.entity.Bar;
import uk.co.frips.sample.barapp.data.entity.MyBarsResult;
import uk.co.frips.sample.barapp.location.LocationManagerWrapper;
import uk.co.frips.sample.barapp.util.schedulers.ImmediateSchedulerProvider;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MapsPresenterTest {
    private static final double TEST_LAT = 12d;
    private static final double TEST_LONG = 13d;
    private static final String TEST_NAME = "name";
    private static final String TEST_ID = "id";

    @Mock private MapsContract.View mMapsView;
    @Mock private LocationManagerWrapper mLocationManagerWrapper;
    @Mock private PlacesServicesAdapter mPlacesServicesAdapter;
    @Mock private Location mLocation;
    private MapsPresenter mMapsPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ImmediateSchedulerProvider schedulerProvider = new ImmediateSchedulerProvider();

        mMapsPresenter = new MapsPresenter(mMapsView, mPlacesServicesAdapter, mLocationManagerWrapper, schedulerProvider);

    }

    @Test
    public void subscribe() throws Exception {
        //act
        mMapsPresenter.subscribe();

        //assert
        verify(mMapsView).startMap();
    }

    @Test
    public void onMapReady_with_cached_value() throws Exception {
        //arrange
        List<Bar> bars = new ArrayList<>();
        MyBarsResult myBarsResult = new MyBarsResult(bars, TEST_LAT, TEST_LONG);
        when(mPlacesServicesAdapter.getMyBarsObservable()).thenReturn(Observable.just(myBarsResult));

        //act
        mMapsPresenter.onMapReady();

        //assert
        verify(mMapsView).clearBars();
        verify(mMapsView).showBars(myBarsResult);
        verify(mMapsView).centerMapOn(TEST_LAT, TEST_LONG);
    }

    @Test
    public void onLocateMe_without_permission() throws Exception {
        //arrange
        when(mLocationManagerWrapper.hasLocationPermission()).thenReturn(false);

        //act
        mMapsPresenter.onLocateMe();

        //assert
        verify(mMapsView).requestLocationPermission();
    }

    @Test
    public void onLocateMe_with_permission() throws Exception {
        //arrange
        when(mLocationManagerWrapper.hasLocationPermission()).thenReturn(true);
        when(mLocation.getLatitude()).thenReturn(TEST_LAT);
        when(mLocation.getLongitude()).thenReturn(TEST_LONG);
        when(mLocationManagerWrapper.hasLocationPermission()).thenReturn(true);
        when(mLocationManagerWrapper.requestLastKnownLocation()).thenReturn(mLocation);
        when(mPlacesServicesAdapter.getPlaces(anyDouble(), anyDouble())).thenReturn(Observable.<Void>just(null));

        //act
        mMapsPresenter.onLocateMe();

        //assert
        verify(mPlacesServicesAdapter).getPlaces(TEST_LAT, TEST_LONG);
    }


    @Test
    public void onLocateMe_with_permission_with_error_shows_error() throws Exception {
        //arrange
        when(mLocationManagerWrapper.hasLocationPermission()).thenReturn(true);
        when(mLocation.getLatitude()).thenReturn(TEST_LAT);
        when(mLocation.getLongitude()).thenReturn(TEST_LONG);
        when(mLocationManagerWrapper.hasLocationPermission()).thenReturn(true);
        when(mLocationManagerWrapper.requestLastKnownLocation()).thenReturn(mLocation);
        when(mPlacesServicesAdapter.getPlaces(anyDouble(), anyDouble())).thenReturn(Observable.<Void>error(new RuntimeException("an error")));

        //act
        mMapsPresenter.onLocateMe();

        //assert
        verify(mMapsView).showError();
    }


}