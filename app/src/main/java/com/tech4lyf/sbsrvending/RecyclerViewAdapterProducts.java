package com.tech4lyf.sbsrvending;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterProducts extends RecyclerView.Adapter<RecyclerViewAdapterProducts.ViewHolder> {

    private ArrayList <Product> products;
    private Context context;


    public RecyclerViewAdapterProducts(ArrayList products,Context context){
        this.products = products;
        this.context=context;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_products,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,final int position) {
//        final Product product = products.get(position);
        holder.products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.currentProductPosition =position;
                ++MainActivity.a[position];
                context.startActivity(new Intent(context,ProductActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


            }
        });

    }

    @Override
    public int getItemCount() {
        return 12;
    }

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
