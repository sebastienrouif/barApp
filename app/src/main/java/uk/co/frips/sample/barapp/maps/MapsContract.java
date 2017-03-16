package uk.co.frips.sample.barapp.maps;

import uk.co.frips.sample.barapp.base.BasePresenter;
import uk.co.frips.sample.barapp.base.BaseView;
import uk.co.frips.sample.barapp.data.entity.MyBarsResult;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface MapsContract {

    interface View extends BaseView<Presenter> {

        void showBars(MyBarsResult myBarsResult);

        void clearBars();

        boolean isActive();

        void startMap();

        void showError();

        void centerMapOn(double myLat, double myLong);

        void requestLocationPermission();
    }

    interface Presenter extends BasePresenter {

        void onMapReady();

        void onLocateMe();

        void onLocationPermissionGranted();

    }
}
