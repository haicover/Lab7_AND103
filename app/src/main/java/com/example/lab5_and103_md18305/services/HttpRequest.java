package com.example.lab5_and103_md18305.services;

import static com.example.lab5_and103_md18305.services.ApiServices.BASE_URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRequest {
    private ApiServices apiServices;

    public HttpRequest(){
        apiServices = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiServices.class);
    }

    public ApiServices callAPI(){
        return apiServices;
    }
}
