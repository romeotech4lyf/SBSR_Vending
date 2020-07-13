package com.tech4lyf.sbsrvending;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewProducts;
    private CardView mainPay;
    private CardView mainClear;
    public static int[] a;
    private ArrayList<Product> products;
    public static int currentProductPosition = -1;
    public static Context context;
    private RecyclerViewAdapterProducts recyclerViewAdapterProducts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //
        a = new int[12];
        recyclerViewProducts = findViewById(R.id.recycler_view_products);
        mainPay = findViewById(R.id.main_pay);
        mainClear = findViewById(R.id.main_clear_all);
        context = MainActivity.this;
        recyclerViewAdapterProducts = new RecyclerViewAdapterProducts(products, context);
        recyclerViewProducts.setAdapter(recyclerViewAdapterProducts);


      /*  mainPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseContext().startActivity(new Intent(MainActivity.this,ProductActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });*/
        mainClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentProductPosition = -1;
                a = new int[12];

            }
        });

       /* {

            ApiClient.ApiInterface apiInterface = ApiClient.getApiClient().create(ApiClient.ApiInterface.class);
            Call<ArrayList<Product>> call = apiInterface.getProducts();
            call.enqueue(new Callback<ArrayList<Product>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Product>> call, @NonNull Response<ArrayList<Product>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        products = response.body();
                        recyclerViewAdapterProducts = new RecyclerViewAdapterProducts(products, context);
                        recyclerViewProducts.setAdapter(recyclerViewAdapterProducts);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Product>> call, @NonNull Throwable t) {

                }
            });
        }*/
    }
}
