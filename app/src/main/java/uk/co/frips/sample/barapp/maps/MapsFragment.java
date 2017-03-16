package uk.co.frips.sample.barapp.maps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import uk.co.frips.sample.barapp.R;
import uk.co.frips.sample.barapp.base.BaseFragment;
import uk.co.frips.sample.barapp.data.PlacesServicesAdapter;
import uk.co.frips.sample.barapp.data.entity.Bar;
import uk.co.frips.sample.barapp.data.entity.MyBarsResult;
import uk.co.frips.sample.barapp.location.LocationManagerWrapperImpl;
import uk.co.frips.sample.barapp.util.schedulers.SchedulerProvider;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;
import static android.widget.Toast.LENGTH_LONG;
import static com.google.common.base.Preconditions.checkNotNull;

public class MapsFragment extends BaseFragment implements MapsContract.View {
    public static String FRAGMENT_TAG = "MapsFragment.FRAGMENT_TAG";
    public static final float MAP_ZOOM_DEFAULT = 15.0f;
    public static int PERMISSION_REQUEST_CODE = 12;
    @BindView(R.id.map) MapView mMapView;
    private GoogleMap mGoogleMap;
    private MapsContract.Presenter mPresenter;
    private LocationManagerWrapperImpl mLocationManagerWrapper;

    public MapsFragment() {
        // Requires empty public constructor
    }

    public static MapsFragment newInstance() {
        Bundle args = new Bundle();
        MapsFragment fragment = new MapsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManagerWrapper = new LocationManagerWrapperImpl(getContext().getApplicationContext());
        new MapsPresenter(this, PlacesServicesAdapter.getInstance(), mLocationManagerWrapper, SchedulerProvider.getInstance());
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_map;
    }

    @StringRes
    @Override
    public int getTitle() {
        return R.string.bar_map_title;
    }

    @Override
    public void setPresenter(@NonNull MapsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.locate_me_menu_item:
                mPresenter.onLocateMe();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.location, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void startMap() {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mPresenter.onMapReady();
                if (!mLocationManagerWrapper.hasLocationPermission()) {
                    return;
                }
                mGoogleMap.setMyLocationEnabled(true);
            }
        });
    }

    @Override
    public void centerMapOn(final double myLat, final double myLong) {
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(myLat, myLong), MAP_ZOOM_DEFAULT);
        mGoogleMap.animateCamera(cu);
    }

    @Override
    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION, LOCATION_SERVICE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.onLocationPermissionGranted();
                }
            }
        }
    }

    @Override
    public void showBars(MyBarsResult myBarsResult) {
        if (mGoogleMap == null) return;
        for (Bar bar : myBarsResult.getMyBars()) {
            addBarMarker(bar, myBarsResult.getMyLat(), myBarsResult.getMyLong());
        }
    }

    private void addBarMarker(Bar bar, double myLat, double myLong) {
        if (bar == null) return;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(bar.latitude, bar.longitude));
        float distances[] = new float[]{0f};
        Location.distanceBetween(bar.latitude, bar.longitude, myLat, myLong, distances);
        markerOptions.title(getString(R.string.maps_marker_info, bar.name, distances[0]));
        mGoogleMap.addMarker(markerOptions);
    }

    @Override
    public void clearBars() {
        if (mGoogleMap == null) return;
        mGoogleMap.clear();
    }

    @Override
    public void showError() {
        Toast.makeText(getContext(),
                R.string.there_was_an_error_finding_the_bars,
                LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mMapView.onCreate(savedInstanceState);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        mPresenter.subscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mPresenter.unsubscribe();
        mMapView.onStop();
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        mMapView.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
