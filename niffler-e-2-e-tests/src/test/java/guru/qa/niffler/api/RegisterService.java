package guru.qa.niffler.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RegisterService {

    @POST("/register")
    @FormUrlEncoded
    Call<Void> register(
            @Header("Cookie") String cookie,
            @Field("_csrf") String csrf,
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit
    );
}
