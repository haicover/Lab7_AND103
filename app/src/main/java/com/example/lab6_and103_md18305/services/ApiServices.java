package com.example.lab5_and103_md18305.services;

import com.example.lab5_and103_md18305.model.Distributor;
import com.example.lab5_and103_md18305.model.Fruit;
import com.example.lab5_and103_md18305.model.Page;
import com.example.lab5_and103_md18305.model.Response;
import com.example.lab5_and103_md18305.model.User;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiServices {
    public static String BASE_URL= "http://192.168.1.6:3000/api/";

    @GET("get-list-distributor")
    Call<Response<ArrayList<Distributor>>> getListDistributor();

    @GET("search-distributor")
    Call<Response<ArrayList<Distributor>>> searchDistributor(@Query("key") String key);

    @POST("add-distributor")
    Call<Response<Distributor>> addDistributor(@Body Distributor distributor);

    @PUT("update-distributor-by-id/{id}")
    Call<Response<Distributor>> updateDistributor(@Path("id") String id,@Body Distributor distributor);

    @DELETE("delete-distributor-by-id/{id}")
    Call<Response<Distributor>> deleteDistributor(@Path("id") String id);

    @GET("get-list-fruit")
    Call<Response<ArrayList<Fruit>>> getListFruit(@Header("Authorization")String token);

    @GET("search-fruit")
    Call<Response<ArrayList<Fruit>>> searchFruit(@Query("key") String key);

    @PUT("update-fruit-by-id/{id}")
    Call<Response<Fruit>> updateFruit(@Path("id") String id,@Body Fruit fruit);

    @DELETE("delete-fruit-by-id/{id}")
    Call<Response<Fruit>> deleteFruit(@Path("id") String id);

    @GET("get-page-fruit")
    Call<Response<Page<ArrayList<Fruit>>>> getPageFruit(@QueryMap Map<String,String> stringMap);

    @Multipart
    @POST("register-send-email")
    Call<Response<User>> register(@Part("username")RequestBody username,
                                  @Part("password")RequestBody password,
                                  @Part("email")RequestBody email,
                                  @Part("name")RequestBody name,
                                  @Part MultipartBody.Part avatar);
    @POST("login")
    Call<Response<User>> login(@Body User user);

    @Multipart
    @POST("add-fruit-with-file-image")
    Call<Response<Fruit>> addFruitWithFileImage(@PartMap Map<String,RequestBody> requestBodyMap,
                                                @Part ArrayList<MultipartBody.Part> ds_hinh);
}
