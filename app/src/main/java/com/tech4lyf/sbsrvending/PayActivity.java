 package com.tech4lyf.sbsrvending;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

 public class PayActivity extends AppCompatActivity {
    private RecyclerViewAdapterPay recyclerViewAdapterPay;
    private RecyclerView recyclerViewPay;
    private TextView payTotal;
    private CardView payClearAll;
    private CardView payEditItems;
    private int x =0;
    private String[] payProductNames = new String[12];
    private String[] payProductPrices = new String[12];
    private String[] payProductPrice_X_Counts = new String[12];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pay);
        for(int i= 0;i<12;i++){
            if(MainActivity.a[i]!=0){
                 payProductNames[x] = MainActivity.products[i].getName();
                 payProductPrice_X_Counts[x] =MainActivity.products[i].getPrice()+" x"+MainActivity.a[i];
                 payProductPrices[x] = String.valueOf(Integer.parseInt(MainActivity.products[i].getPrice())*MainActivity.a[i]) + ".00";
                 x++;
            }
        recyclerViewPay = findViewById(R.id.recycler_view_pay);
        recyclerViewAdapterPay = new RecyclerViewAdapterPay(x,payProductNames,payProductPrices,payProductPrice_X_Counts);
        recyclerViewPay.setAdapter(recyclerViewAdapterPay);
        payTotal=findViewById(R.id.pay_total);
        payClearAll=findViewById(R.id.pay_clear_all);
        payEditItems=findViewById(R.id.pay_edit_items);
        payTotal.setText(String.valueOf(MainActivity.price+".00"));

        payEditItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayActivity.this.onBackPressed();
            }
        });

            payClearAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.currentProductPosition = -1;
                    MainActivity.a = new int[12];
                     Intent restartActivity = new Intent(PayActivity.this,MainActivity.class);
                     int id = 234567;
                    PendingIntent pendingIntent = PendingIntent.getActivity(PayActivity.this,id,restartActivity,PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) PayActivity.this.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC,System.currentTimeMillis()+100,pendingIntent);
                    System.exit(0);
                    //PayActivity.this.startActivity(new Intent(PayActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                   // PayActivity.this.finish();
                }
            });

    }
}
 }
