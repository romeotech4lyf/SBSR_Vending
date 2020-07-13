package com.tech4lyf.sbsrvending;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.tech4lyf.sbsrvending.ProductActivity.productsCount;

public class ScrollViewAdapterProducts extends RecyclerView.Adapter<ScrollViewAdapterProducts.ViewHolder> {
    private ArrayList<Product> products;
    private int[] a;

    public ScrollViewAdapterProducts(ArrayList<Product> products){
        this.products=products;
        this.a = new int[12];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScrollViewAdapterProducts.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_products,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        //Product product = products.get(position);
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

        //return products.size();

    return 12;}


    class ViewHolder extends RecyclerView.ViewHolder {

        CardView products;
        TextView productName;
        TextView productPrice;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            products = itemView.findViewById(R.id.list_item_products);
            productName = itemView.findViewById(R.id.list_item_product_names);
            productPrice = itemView.findViewById(R.id.list_item_product_prices);
        }
    }
}
