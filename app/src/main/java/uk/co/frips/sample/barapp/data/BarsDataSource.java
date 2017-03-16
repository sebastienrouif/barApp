package uk.co.frips.sample.barapp.data;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;
import uk.co.frips.sample.barapp.data.entity.Bar;

public interface BarsDataSource {
    Observable<List<Bar>> getBars(@NonNull String LatLong);

    Observable<List<Bar>> getExistingBars(@NonNull String LatLong);
}
