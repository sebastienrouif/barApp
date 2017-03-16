package uk.co.frips.sample.barapp.list;

import uk.co.frips.sample.barapp.base.BasePresenter;
import uk.co.frips.sample.barapp.base.BaseView;
import uk.co.frips.sample.barapp.data.entity.Bar;
import uk.co.frips.sample.barapp.data.entity.MyBarsResult;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface BarListContract {

    interface View extends BaseView<Presenter> {

        void requestLocationPermission();

        void showBars(MyBarsResult bars);

        void showEmpty();

        void showError();

        void openGoogleMaps(double lat, double longi);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void onBarClicked(Bar item);

        void onLocateMe();

        void onLocationPermissionGranted();
    }
}
