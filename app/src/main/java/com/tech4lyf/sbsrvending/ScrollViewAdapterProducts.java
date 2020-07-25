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

import static com.tech4lyf.sbsrvending.ProductActivity.calculate;
import static com.tech4lyf.sbsrvending.ProductActivity.productsCount;

public class ScrollViewAdapterProducts extends RecyclerView.Adapter<ScrollViewAdapterProducts.ViewHolder> {
    private ArrayList<Product> products;
    private Context context;

    public ScrollViewAdapterProducts(ArrayList<Product> products,Context context){
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
        Product product = products.get(position);
        if(product!=null) {
            holder.productName.setText(product.getName());
            holder.productPrice.setText(product.getPrice());
            holder.productSelected.setText(String.valueOf(MainActivity.a.get(position)));
            if (product.getId() != null)
                holder.productImage.setImageDrawable(MainActivity.drawables.get(position));
            holder.products.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.a.set(position,MainActivity.a.get(position)+1);
                    productsCount.setText(String.valueOf(MainActivity.a.get(MainActivity.currentProductPosition)));
                    holder.productSelected.setText(String.valueOf(MainActivity.a.get(position)));
                    calculate();

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        CardView products;
        TextView productName;
        TextView productPrice;
        TextView productSelected;
        ImageView productImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            products = itemView.findViewById(R.id.list_item_products);
            productName = itemView.findViewById(R.id.list_item_product_names);
            productPrice = itemView.findViewById(R.id.list_item_product_prices);
            productImage = itemView.findViewById(R.id.list_item_product_images);
            productSelected = itemView.findViewById(R.id.list_item_product_selected);

        }
    }
}
