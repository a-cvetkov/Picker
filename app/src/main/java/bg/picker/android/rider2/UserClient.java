package bg.picker.android.rider2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by bashticata on 3/29/2017.
 */

public interface UserClient {

    @POST("/picker-backend/locations/sync")
    Call<List<Driver>> syncUsers(@Body Rider rider);
}
