package com.tech4lyf.sbsrvending;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.tech4lyf.sbsrvending.ProductActivity.productsCount;

public class ScrollViewAdapterProducts extends RecyclerView.Adapter<ScrollViewAdapterProducts.ViewHolder> {
    private Product[] products;
    private Context context;

    public ScrollViewAdapterProducts(Product[] products,Context context){
        this.products=products;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScrollViewAdapterProducts.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_products,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        Product product = products[position];
        holder.productName.setText(product.getName());
        holder.productPrice.setText(product.getPrice());
        if(product.getId()!=null)
            holder.productImage.setImageDrawable(MainActivity.drawables[position]);
        holder.products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++MainActivity.a[position];
                productsCount.setText(String.valueOf(MainActivity.a[MainActivity.currentProductPosition]));


            }
        });

    }

    @Override
    public int getItemCount() {
        return products.length;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        CardView products;
        TextView productName;
        TextView productPrice;
        ImageView productImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            products = itemView.findViewById(R.id.list_item_products);
            productName = itemView.findViewById(R.id.list_item_product_names);
            productPrice = itemView.findViewById(R.id.list_item_product_prices);
            productImage = itemView.findViewById(R.id.list_item_product_images);
        }
    }
}
