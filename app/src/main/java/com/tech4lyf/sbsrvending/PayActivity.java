package com.tech4lyf.sbsrvending;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tech4lyf.sbsrvending.utility.AvenuesParams;
import com.tech4lyf.sbsrvending.utility.ServiceUtility;

import static com.tech4lyf.sbsrvending.utility.Constants.accessCode;
import static com.tech4lyf.sbsrvending.utility.Constants.cancelUrl;
import static com.tech4lyf.sbsrvending.utility.Constants.currencyCode;
import static com.tech4lyf.sbsrvending.utility.Constants.merchantId;
import static com.tech4lyf.sbsrvending.utility.Constants.redirectUrl;
import static com.tech4lyf.sbsrvending.utility.Constants.rsaKeyUrl;

public class PayActivity extends AppCompatActivity {


    private RecyclerViewAdapterPay recyclerViewAdapterPay;
    private RecyclerView recyclerViewPay;
    private TextView payTotal;
    private ImageView payQR;
    private CardView payPay;
    private CardView payEditItems;
    private int x = 0;
    private String[] payProductNames = new String[12];
    private String[] payProductPrices = new String[12];
    private String[] payProductPrice_X_Counts = new String[12];

    private String orderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pay);


        for (int i = 0; i < MainActivity.a.length; i++) {
            if (MainActivity.a[i] != 0) {
                payProductNames[x] = MainActivity.products[i].getName();
                payProductPrice_X_Counts[x] = MainActivity.products[i].getPrice() + " x" + MainActivity.a[i];
                payProductPrices[x] = Integer.parseInt(MainActivity.products[i].getPrice()) * MainActivity.a[i] + ".00";
                x++;
            }
        }


        recyclerViewPay = findViewById(R.id.recycler_view_pay);
        recyclerViewAdapterPay = new RecyclerViewAdapterPay(x, payProductNames, payProductPrices, payProductPrice_X_Counts);
        recyclerViewPay.setAdapter(recyclerViewAdapterPay);
        payTotal = findViewById(R.id.pay_total);
        payPay = findViewById(R.id.pay_pay);
        payEditItems = findViewById(R.id.pay_edit_items);
        payQR = findViewById(R.id.pay_upi_qr);
        payTotal.setText(MainActivity.price + ".00");
       /* StringBuilder stringBuilder = new StringBuilder(Arrays.toString(MainActivity.a));
        stringBuilder.deleteCharAt(0);
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        String string = stringBuilder.toString().replaceAll(" ","");
        Log.d("msg", string);
        Toast.makeText(PayActivity.this,string,Toast.LENGTH_LONG).show();*/


        payEditItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayActivity.this.onBackPressed();
            }
        });


        payPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccAvenue();
            }
        });


    }

    private void ccAvenue() {
        orderId = String.valueOf(ServiceUtility.randInt(0, 9999999));
        Intent intent = new Intent(PayActivity.this, StatusActivity.class);
        intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(accessCode).toString().trim());
        intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(merchantId).toString().trim());
        intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderId).toString().trim());
        intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(currencyCode).toString().trim());
        intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull("1.00").toString().trim());
        intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(redirectUrl).toString().trim());
        intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(cancelUrl).toString().trim());
        intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(rsaKeyUrl).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_NAME, ServiceUtility.chkNull("Charli").toString().trim());
        intent.putExtra(AvenuesParams.BILLING_ADDRESS, ServiceUtility.chkNull("Room 101 ").toString().trim());
        intent.putExtra(AvenuesParams.BILLING_COUNTRY, ServiceUtility.chkNull("India").toString().trim());
        intent.putExtra(AvenuesParams.BILLING_STATE, ServiceUtility.chkNull("MH").toString().trim());
        intent.putExtra(AvenuesParams.BILLING_CITY, ServiceUtility.chkNull("Indore").toString().trim());
        intent.putExtra(AvenuesParams.BILLING_ZIP, ServiceUtility.chkNull("425001").toString().trim());
        intent.putExtra(AvenuesParams.BILLING_TEL, ServiceUtility.chkNull("9595226054").toString().trim());
        intent.putExtra(AvenuesParams.BILLING_EMAIL, ServiceUtility.chkNull("pratik.pai@avenues.info").toString().trim());
        intent.putExtra(AvenuesParams.PAYMENT_OPTION, "OPTUPI");
        PayActivity.this.startActivity(intent);
    }




}
