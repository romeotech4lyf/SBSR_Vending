package com.tech4lyf.sbsrvending;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private DiscreteScrollView productsScrollView;
    private TextView productsCountDecrease;
    private TextView productsCountIncrease;
    private CardView productsPay;
    private CardView productsClearAll;
    public static TextView productsCount;
    private Context context;
    private ImageView productsBg;
    private ImageView productsBgColor;
    public static TextView priceToPay;
    private ScrollViewAdapterProducts scrollViewAdapterProducts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_product);

        context=this.getBaseContext();

        //
        productsScrollView= findViewById(R.id.products_scrollview);
        productsCount= findViewById(R.id.products_count);
        productsCountDecrease= findViewById(R.id.products_count_decrease);
        productsCountIncrease= findViewById(R.id.products_count_increase);
        productsBg= findViewById(R.id.products_bg);
        productsBgColor= findViewById(R.id.products_bg_color);
        priceToPay= findViewById(R.id.products_price_to_pay);
        productsClearAll=findViewById(R.id.products_clear_all);
        productsPay=findViewById(R.id.products_pay);
        //
        //productsCount.setText(String.valueOf(MainActivity.a[MainActivity.currentProductPosition]));
        scrollViewAdapterProducts = new ScrollViewAdapterProducts(MainActivity.products,context);
        productsScrollView.setAdapter(scrollViewAdapterProducts);
        productsScrollView.scrollToPosition(MainActivity.currentProductPosition);

        calculate();

        productsScrollView.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int i) {
                MainActivity.currentProductPosition = productsScrollView.getCurrentItem();
                productsCount.setText(String.valueOf(MainActivity.a[MainActivity.currentProductPosition]));
                Drawable drawable =MainActivity.drawables[MainActivity.currentProductPosition];
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Palette palette = Palette.from(bitmap).maximumColorCount(16).generate();
                productsBg.setImageDrawable(MainActivity.drawables[MainActivity.currentProductPosition]);
                productsBgColor.setBackgroundColor(palette.getVibrantColor(0));

            }
        });

        productsPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
                if(MainActivity.price==0){

                }else{

                    ProductActivity.this.startActivity(new Intent(ProductActivity.this,PayActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                }
            }
        });

       productsCountIncrease.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(MainActivity.currentProductPosition>-1){
                   productsCount.setText(String.valueOf(++MainActivity.a[MainActivity.currentProductPosition]));
               calculate();
           }
           }
       });

       productsCountDecrease.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(MainActivity.currentProductPosition>-1){
               if(MainActivity.a[MainActivity.currentProductPosition]>0)
                productsCount.setText(String.valueOf(--MainActivity.a[MainActivity.currentProductPosition]));
               calculate();
           }
           }
       });


       productsClearAll.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               productsCount.setText("0");
               MainActivity.currentProductPosition = -1;
               MainActivity.a = new int[12];
               priceToPay.setText("0.00");

           }
       });




        productsScrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.0f)
                .setMinScale(.75f)
                .setPivotX(Pivot.X.CENTER)
                .setPivotY(Pivot.Y.BOTTOM)
                .build());

    }

    public static void calculate(){
        MainActivity.price=0;

        for(int i= 0; i<12 ;i++)
           MainActivity.price += Integer.parseInt(MainActivity.products[i].getPrice()) * MainActivity.a[i];

        priceToPay.setText(String.valueOf(MainActivity.price + ".00"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        scrollViewAdapterProducts.notifyDataSetChanged();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        scrollViewAdapterProducts.notifyDataSetChanged();

    }

}
