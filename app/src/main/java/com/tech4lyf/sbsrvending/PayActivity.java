package com.tech4lyf.sbsrvending;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class PayActivity extends AppCompatActivity  {


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

    private UsbSerialPort usbSerialPort;
    private SerialService service;
    private boolean initialStart = true;
    private BroadcastReceiver broadcastReceiver;
    private RecyclerViewAdapterPay recyclerViewAdapterPay;
    private RecyclerView recyclerViewPay;
    private TextView payTotal;
    private ImageView payQR;
    private CardView payClearAll;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pay);
        mHandler = new MyHandler(this);

       /* setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceId = getArguments().getInt("device");
        portNum = getArguments().getInt("port");
        baudRate = getArguments().getInt("baud");
*/
        for (int i = 0; i < 12; i++) {
            if (MainActivity.a[i] != 0) {
                payProductNames[x] = MainActivity.products[i].getName();
                payProductPrice_X_Counts[x] = MainActivity.products[i].getPrice() + " x" + MainActivity.a[i];
                payProductPrices[x] = Integer.parseInt(MainActivity.products[i].getPrice()) * MainActivity.a[i] + ".00";
                x++;
            }


            recyclerViewPay = findViewById(R.id.recycler_view_pay);
            recyclerViewAdapterPay = new RecyclerViewAdapterPay(x, payProductNames, payProductPrices, payProductPrice_X_Counts);
            recyclerViewPay.setAdapter(recyclerViewAdapterPay);
            payTotal = findViewById(R.id.pay_total);
            payClearAll = findViewById(R.id.pay_clear_all);
            payEditItems = findViewById(R.id.pay_edit_items);
            payQR = findViewById(R.id.pay_upi_qr);
            payTotal.setText(MainActivity.price + ".00");

            //R11198867



            payEditItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  PayActivity.this.onBackPressed();
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(Arrays.toString(MainActivity.a).getBytes());
                    }
                    else
                        Log.d("msg","msg");

                }
            });

            payClearAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.currentProductPosition = -1;
                    MainActivity.a = new int[12];
                    Intent restartActivity = new Intent(PayActivity.this, MainActivity.class);
                    int id = 234567;
                    PendingIntent pendingIntent = PendingIntent.getActivity(PayActivity.this, id, restartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) PayActivity.this.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
                    System.exit(0);
                    //PayActivity.this.startActivity(new Intent(PayActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    // PayActivity.this.finish();
                }
            });

            upiGenerate();
        }
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
        {
            final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                    .with(this)
                    .setPayeeVpa(payeeAddress)
                    .setPayeeName(payeeName)
                    .setTransactionId(transactionId)
                    .setTransactionRefId("2019060302")
                    .setDescription("SBSR")
                    .setAmount("10.00")
                    .build();


            easyUpiPayment.setPaymentStatusListener(new PaymentStatusListener() {
                @Override
                public void onTransactionCompleted(TransactionDetails transactionDetails) {


                }

                @Override
                public void onTransactionSuccess() {

                }

                @Override
                public void onTransactionSubmitted() {

                }

                @Override
                public void onTransactionFailed() {

                }

                @Override
                public void onTransactionCancelled() {

                }

                @Override
                public void onAppNotFound() {

                }
            });

        }
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

        Uri myAction = Uri.parse(urlString);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(myAction);
        //startActivityForResult(intent, 120);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                String res = data.getStringExtra("response");
                if (res != null) {
                    if (res.contains("Status=SUCCESS")) {
                        Toast.makeText(this, "Payment successful!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Payment was not successful! Try again later", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(this, "Payment was not successful!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getUPIString(String payeeAddress, String payeeName, String trxnRefId,
                                String trxnNote, String payeeAmount, String currencyCode) {
        String UPI = "upi://pay?pa=" + payeeAddress + "&pn=" + payeeName
                + "&tr=" + trxnRefId
                + "&tn=" + trxnNote + "&am=" + payeeAmount + "&cu=" + currencyCode;
        return UPI.replace(" ", "+");
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



}
