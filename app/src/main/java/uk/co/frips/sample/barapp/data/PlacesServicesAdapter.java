package uk.co.frips.sample.barapp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;
import uk.co.frips.sample.barapp.data.entity.Bar;
import uk.co.frips.sample.barapp.data.entity.MyBarsResult;
import uk.co.frips.sample.barapp.data.places.PlacesResponse;
import uk.co.frips.sample.barapp.data.places.Result;

public class PlacesServicesAdapter {
    private static PlacesServicesAdapter INSTANCE;

    private static final String ROOT_URL = "https://maps.googleapis.com";
    //TODO place your key here
    private static final String API_KEY = null;
    private static final String TYPE = "bar";
    private static final String RADIUS = "1000";
    private final PlacesService mPlacesService;
    private final ReplaySubject<MyBarsResult> mMyBarsResultReplaySubject;

    public static PlacesServicesAdapter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlacesServicesAdapter();
        }
        return INSTANCE;
    }


    public PlacesServicesAdapter() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ROOT_URL)
                .build();

        mPlacesService = retrofit.create(PlacesService.class);
        mMyBarsResultReplaySubject = ReplaySubject.createWithSize(1);
    }

    public Observable<MyBarsResult> getMyBarsObservable() {
        return mMyBarsResultReplaySubject;
    }

    private void updateBars(MyBarsResult myBarsResult) {
        mMyBarsResultReplaySubject.onNext(myBarsResult);
    }

    public Observable<Void> getPlaces(final double myLat, final double myLong) {
        Observable<PlacesResponse> places = mPlacesService.getPlaces("",
                API_KEY,
                TYPE,
                String.format(Locale.ENGLISH, "%f,%f", myLat, myLong), RADIUS);

        Observable<Void> barObservable = places.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<PlacesResponse, Void>() {
                    @Override
                    public Void call(PlacesResponse placesResponse) {
                        List<Result> results = placesResponse.getResults();
                        List<Bar> bars = new ArrayList<>();
                        for (Result result : results) {
                            bars.add(new Bar(result.getName(),
                                    result.getName(),
                                    result.getGeometry().getLocation().getLat(),
                                    result.getGeometry().getLocation().getLng()));
                        }

                        MyBarsResult myBarsResult = new MyBarsResult(bars, myLat, myLong);

                        updateBars(myBarsResult);
                        return null;
                    }
                });
        return barObservable;
    }
}
