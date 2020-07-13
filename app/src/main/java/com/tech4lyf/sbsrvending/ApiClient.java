package com.tech4lyf.sbsrvending;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ApiClient {

    public static final String BASE_URL = "ftp://suganthi%2540tech4lyf.in@tech4lyf.in/SBSR%20Vending";
    public static Retrofit retrofit = null;

    public static Retrofit getApiClient(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }  
        return retrofit;

    }
    public interface ApiInterface {

        @GET("get.php")
        Call<ArrayList<Product>> getProducts();
    }
}
