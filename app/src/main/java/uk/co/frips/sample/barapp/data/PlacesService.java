package uk.co.frips.sample.barapp.data;


import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import uk.co.frips.sample.barapp.data.places.PlacesResponse;

public interface PlacesService {

    // https://maps.googleapis.com/maps/api/place/textsearch/
    // json?query=&key=AIzaSyA3NJeob7czKysXlW7b2ADVPaSIj5eBmf4&type=bar&location=51.531982, -0.120620&radius=500
    @GET("maps/api/place/textsearch/json?")
    Observable<PlacesResponse> getPlaces(
            @Query("query") String query,
            @Query("key") String key,
            @Query("type") String type,
            @Query("location") String location,
            @Query("radius") String radius);
}
