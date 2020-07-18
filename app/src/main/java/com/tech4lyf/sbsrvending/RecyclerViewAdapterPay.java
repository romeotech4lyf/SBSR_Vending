package com.tech4lyf.sbsrvending;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

public class RecyclerViewAdapterPay extends RecyclerView.Adapter<RecyclerViewAdapterPay.ViewHolder> {

    public RecyclerViewAdapterPay(int x, String[] payProductNames, String[] payProductPrices, String[] payProductPrice_X_Counts) {
        this.x=x;
        this.payProductNames = payProductNames;
        this.payProductPrices = payProductPrices;
        this.payProductPrice_X_Counts = payProductPrice_X_Counts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(MainActivity.context).inflate(R.layout.list_item_pay,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.payName.setText(payProductNames[position]);
        holder.payPrice.setText(payProductPrices[position]);
        holder.payPriceXCount.setText(payProductPrice_X_Counts[position]);

    }
    private  int x =0;
    private String[] payProductNames;
    private String[] payProductPrices;
    private String[] payProductPrice_X_Counts;

    @Override
    public int getItemCount() {
        return x;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView payName;
        TextView payPrice;
        TextView payPriceXCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            payName=itemView.findViewById(R.id.list_item_pay_name);
            payPrice=itemView.findViewById(R.id.list_item_pay_price);
            payPriceXCount=itemView.findViewById(R.id.list_item_pay_price_x_count);
        }
    }
}
