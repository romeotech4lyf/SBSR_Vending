package com.tech4lyf.sbsrvending;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private DiscreteScrollView productsScrollView;
    private ArrayList<Product> products;
    private TextView productsCountDecrease;
    private TextView productsCountIncrease;
    public static TextView productsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_product);

        //
        productsScrollView= findViewById(R.id.products_scrollview);
        productsCount= findViewById(R.id.products_count);
        productsCountDecrease= findViewById(R.id.products_count_decrease);
        productsCountIncrease= findViewById(R.id.products_count_increase);

        //
        productsCount.setText(String.valueOf(MainActivity.a[MainActivity.currentProductPosition]));
        ScrollViewAdapterProducts scrollViewAdapterProducts;
        scrollViewAdapterProducts = new ScrollViewAdapterProducts(products);
        productsScrollView.setAdapter(scrollViewAdapterProducts);

        productsScrollView.scrollToPosition(MainActivity.currentProductPosition);

        productsScrollView.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int i) {
                MainActivity.currentProductPosition = productsScrollView.getCurrentItem();
                productsCount.setText(String.valueOf(MainActivity.a[MainActivity.currentProductPosition]));


            }
        });

       productsCountIncrease.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               productsCount.setText(String.valueOf(++MainActivity.a[MainActivity.currentProductPosition]));
           }
       });

       productsCountDecrease.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(MainActivity.a[MainActivity.currentProductPosition]>0)
               productsCount.setText(String.valueOf(--MainActivity.a[MainActivity.currentProductPosition]));
           }
       });


        productsScrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.00f)
                .setMinScale(.7f)
                .setPivotX(Pivot.X.CENTER)
                .setPivotY(Pivot.Y.BOTTOM)
                .build());





    }
}
