package com.tech4lyf.sbsrvending;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tech4lyf.sbsrvending.utility.AvenuesParams;
import com.tech4lyf.sbsrvending.utility.ServiceUtility;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Set;

import static com.tech4lyf.sbsrvending.utility.Constants.accessCode;
import static com.tech4lyf.sbsrvending.utility.Constants.cancelUrl;
import static com.tech4lyf.sbsrvending.utility.Constants.currencyCode;
import static com.tech4lyf.sbsrvending.utility.Constants.merchantId;
import static com.tech4lyf.sbsrvending.utility.Constants.redirectUrl;
import static com.tech4lyf.sbsrvending.utility.Constants.rsaKeyUrl;

public class PayActivity extends AppCompatActivity {


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private BroadcastReceiver broadcastReceiver;
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
    private UsbService usbService;
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pay);
        mHandler = new MyHandler(this);


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
        StringBuilder stringBuilder = new StringBuilder(Arrays.toString(MainActivity.a));
        stringBuilder.deleteCharAt(0);
        stringBuilder.deleteCharAt(stringBuilder.indexOf("]"));
        Log.d("msg", Arrays.toString(MainActivity.a));
        Log.d("msg", stringBuilder.toString());

        //R11198867

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
                if (usbService != null) { // if UsbService was correctly binded, Send data
                    usbService.write(stringBuilder.toString().getBytes());
                    Log.d("msg", Arrays.toString(MainActivity.a));
                } else
                    Log.d("msg", "msg");
            }

        });


    }

    private void ccAvenue(  ) {
        orderId = String.valueOf(ServiceUtility.randInt(0, 9999999));
        Intent intent = new Intent(PayActivity.this, WebViewActivity.class);
        intent.putExtra(AvenuesParams.ACCESS_CODE, accessCode);
        intent.putExtra(AvenuesParams.MERCHANT_ID, merchantId);
        intent.putExtra(AvenuesParams.ORDER_ID, orderId);
        intent.putExtra(AvenuesParams.CURRENCY, currencyCode);
        intent.putExtra(AvenuesParams.AMOUNT, "1.00");
        intent.putExtra(AvenuesParams.REDIRECT_URL, redirectUrl);
        intent.putExtra(AvenuesParams.CANCEL_URL, cancelUrl);
        intent.putExtra(AvenuesParams.RSA_KEY_URL, rsaKeyUrl);
        PayActivity.this.startActivity(intent);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void upiGenerate() {

        final String payeeAddress = "kanimozhi.johndavid@okhdfcbank";
        final String payeeName = "SBSR";
        final String transactionId = "201906035612401";
        final String transactionMessage = "SBSR";
        final String amount = "10.00";
        final String code = "INR";

        final String urlString = getUPIString(payeeAddress, payeeName, transactionId, transactionMessage, amount, code);

        BitMatrix bitMatrix = null;
        final QRCodeWriter writer = new QRCodeWriter();
        try {

            bitMatrix = writer.encode(urlString, BarcodeFormat.QR_CODE, 512, 512);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        payQR.setImageBitmap(bmp);

        Uri uri = Uri.parse(urlString);


    }

    private String getUPIString(String payeeAddress, String payeeName, String trxnRefId,
                                String trxnNote, String payeeAmount, String currencyCode) {
        String UPI = "upi://pay?pa=" + payeeAddress + "&pn=" + payeeName
                + "&tr=" + trxnRefId
                + "&tn=" + trxnNote + "&am=" + payeeAmount + "&cu=" + currencyCode;
        return UPI.replace(" ", "+");
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<PayActivity> mActivity;

        public MyHandler(PayActivity activity) {
            mActivity = new WeakReference<PayActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    // mActivity.get().display.append(data);
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


}
