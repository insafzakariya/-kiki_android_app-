package lk.mobilevisions.kiki.modules.api;

import lk.mobilevisions.kiki.modules.api.dto.HeaderResponse;
import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Created by Chatura Dilan Perera on 23/3/2017.
 */

public interface ComAPI {

    @GET("/")
    Call<HeaderResponse> getInfoHeaders();
}
