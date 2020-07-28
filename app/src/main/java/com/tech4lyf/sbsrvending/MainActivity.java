package com.tech4lyf.sbsrvending;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<Drawable> drawables = new ArrayList<>();
    public static ArrayList<Integer> a = new ArrayList<>();
    public static ArrayList<Product> products = new ArrayList<>();
    public static int currentProductPosition = -1;
    public static int price = 0;
    public static Context context;
    private JSONArray jsonArray = new JSONArray();
    private RecyclerView recyclerViewProducts;
    private CardView mainRefresh;
    private CardView mainClear;
    private RecyclerViewAdapterProducts recyclerViewAdapterProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //
        recyclerViewProducts = findViewById(R.id.recycler_view_products);
        mainRefresh = findViewById(R.id.main_refresh);
        mainClear = findViewById(R.id.main_clear_all);
        context = MainActivity.this;


        mainRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();

            }
        });
        mainClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentProductPosition = -1;
                a = new ArrayList<>();

            }
        });

        getData();

    }

    private void getData() {

        {
            Gson gson = new Gson();
            String urlString = "https://developers.tech4lyf.com/romeotamizh/getProducts.php";
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        URL url = null;
                        try {
                            url = new URL(urlString);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                        httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
                        InputStreamReader reader = new InputStreamReader(httpcon.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        // Log.d("post", bufferedReader.readLine());
                        reader.getEncoding();
                        try {
                            jsonArray = new JSONArray(bufferedReader.readLine());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        a.add(0);
                                        drawables.add(null);
                                        recyclerViewAdapterProducts = new RecyclerViewAdapterProducts(products, context);
                                        recyclerViewProducts.setAdapter(recyclerViewAdapterProducts);
                                        recyclerViewAdapterProducts.setProducts(products);
                                        recyclerViewAdapterProducts.notifyDataSetChanged();

                                    }
                                });
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Gson gson1 = new Gson();
                                Product product;
                                product = gson1.fromJson(jsonObject.toString(), Product.class);
                                int finalI = i;
                                try {

                                Glide.with(context)
                                        .load(new URL(product.geturl()))
                                        .into(new CustomTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                                                drawables.add(finalI,resource);
                                                recyclerViewAdapterProducts.notifyDataSetChanged();

                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                                }catch (IllegalStateException e){

                                }
                                products.add(finalI,product);

                                Log.d("post", products.get(i).getName());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                }
            }).start();


        }





        /*{

            ApiClient.ApiInterface apiInterface = ApiClient.getApiClient().create(ApiClient.ApiInterface.class);
            Call<ArrayList<Product>> call = apiInterface.getProducts();
            call.enqueue(new Callback<ArrayList<Product>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Product>> call, @NonNull Response<ArrayList<Product>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        products = response.body();
                        Log.d("post", products.get(0).getName());
                        recyclerViewAdapterProducts = new RecyclerViewAdapterProducts(products, context);
                        recyclerViewProducts.setAdapter(recyclerViewAdapterProducts);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Product>> call, @NonNull Throwable t) {
                    Log.d("post", t.getMessage());


                }
            });
        }*/
    }

}
