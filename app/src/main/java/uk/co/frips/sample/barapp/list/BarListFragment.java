package uk.co.frips.sample.barapp.list;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import uk.co.frips.sample.barapp.R;
import uk.co.frips.sample.barapp.base.BaseFragment;
import uk.co.frips.sample.barapp.data.PlacesServicesAdapter;
import uk.co.frips.sample.barapp.data.entity.Bar;
import uk.co.frips.sample.barapp.data.entity.MyBarsResult;
import uk.co.frips.sample.barapp.location.LocationManagerWrapperImpl;
import uk.co.frips.sample.barapp.util.schedulers.SchedulerProvider;
import uk.co.frips.sample.barapp.widget.MultiView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;
import static android.widget.Toast.LENGTH_LONG;
import static com.google.common.base.Preconditions.checkNotNull;

public class BarListFragment extends BaseFragment implements BarListContract.View, BarRecyclerViewAdapter.OnListFragmentInteractionListener {
    public static String FRAGMENT_TAG = "BarListFragment.FRAGMENT_TAG";
    public static int PERMISSION_REQUEST_CODE = 13;
    private BarListContract.Presenter mPresenter;

    @BindView(R.id.bar_list_content) RecyclerView mRecyclerView;
    @BindView(R.id.bar_list_multiview) MultiView mMultiView;
    private BarRecyclerViewAdapter mBarRecyclerViewAdapter;

    public static BarListFragment newInstance() {
        Bundle args = new Bundle();
        BarListFragment fragment = new BarListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public BarListFragment() {
        // Requires empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new BarListPresenter(this,
                PlacesServicesAdapter.getInstance(),
                new LocationManagerWrapperImpl(getContext().getApplicationContext()),
                SchedulerProvider.getInstance());
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_bar_list;
    }

    @StringRes
    @Override
    public int getTitle() {
        return R.string.bar_list_title;
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
    public void onStart() {
        super.onStart();
        mPresenter.subscribe();
    }

    @Override
    public void onStop() {
        mPresenter.unsubscribe();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Set the adapter
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBarRecyclerViewAdapter = new BarRecyclerViewAdapter(new ArrayList<Bar>(), this);
        mRecyclerView.setAdapter(mBarRecyclerViewAdapter);
        return view;
    }

    @Override
    public void setPresenter(BarListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
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
        mBarRecyclerViewAdapter.replaceData(myBarsResult.getMyBars());
        mMultiView.setActiveView(R.id.bar_list_content);
    }

    @Override
    public void showEmpty() {
        mMultiView.setActiveView(R.id.bar_list_empty);
    }

    @Override
    public void showError() {
        mMultiView.setActiveView(R.id.bar_list_error);
    }

    @OnClick({R.id.bar_list_retry, R.id.bar_list_empty_retry})
    public void retry() {
        mPresenter.onLocateMe();
    }

    @Override
    public void onBarClicked(Bar item) {
        mPresenter.onBarClicked(item);

    }

    @Override
    public void openGoogleMaps(double lat, double longi) {
        String uri = "http://maps.google.com/maps?q=loc:" + lat + "," + longi;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(),
                    R.string.install_google_maps,
                    LENGTH_LONG).show();
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

}
