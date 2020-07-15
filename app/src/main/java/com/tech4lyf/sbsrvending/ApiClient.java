package com.tech4lyf.sbsrvending;

import android.util.JsonReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ApiClient {

    public static final String BASE_URL = "https://www.developers.tech4lyf.com/romeotamizh/";
    public static Retrofit retrofit = null;


    public ApiClient() throws MalformedURLException {
    }

    public static Retrofit getApiClient(){
        if(retrofit==null){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;

    }
    public interface ApiInterface {

        @GET("getProducts.php")
        Call<List<Product>> getProducts();
    }
}
