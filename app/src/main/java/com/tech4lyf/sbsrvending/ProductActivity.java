package com.tech4lyf.sbsrvending;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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
    public static TextView productsCount;
    private Context context;
    private ImageView productsBg;
    private ImageView productsBgColor;

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

        //
        productsCount.setText(String.valueOf(MainActivity.a[MainActivity.currentProductPosition]));
        ScrollViewAdapterProducts scrollViewAdapterProducts;
        scrollViewAdapterProducts = new ScrollViewAdapterProducts(MainActivity.products,context);
        productsScrollView.setAdapter(scrollViewAdapterProducts);
        productsScrollView.scrollToPosition(MainActivity.currentProductPosition);

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
                .setMaxScale(1.0f)
                .setMinScale(.75f)
                .setPivotX(Pivot.X.CENTER)
                .setPivotY(Pivot.Y.BOTTOM)
                .build());

    }
}
