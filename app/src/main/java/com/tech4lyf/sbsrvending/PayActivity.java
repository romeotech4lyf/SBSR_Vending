package com.tech4lyf.sbsrvending;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

public class PayActivity extends AppCompatActivity implements SerialListener, ServiceConnection {


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
    private int deviceId, portNum, baudRate;
    private String newline = "\r\n";

    private TextView receiveText;

    private UsbSerialPort usbSerialPort;
    private SerialService service;
    private boolean initialStart = true;
    private Connected connected = Connected.False;
    private BroadcastReceiver broadcastReceiver;
    private ControlLines controlLines;
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

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Constants.INTENT_ACTION_GRANT_USB)) {
                        Boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                        connect(granted);
                    }
                }
            };

            payEditItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  PayActivity.this.onBackPressed();
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        usbService.write(Arrays.toString(MainActivity.a).getBytes());
                    }

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

    @Override
    public void onSerialConnect() {
        status("connected");
        connected = Connected.True;
        controlLines.start();
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if (initialStart) {
            initialStart = false;
            PayActivity.this.runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }

    private void connect() {
        connect(null);
    }

    private void connect(Boolean permissionGranted) {
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) PayActivity.this.getSystemService(Context.USB_SERVICE);
        for (UsbDevice v : usbManager.getDeviceList().values())
            if (v.getDeviceId() == deviceId)
                device = v;
        if (device == null) {
            status("connection failed: device not found");
            return;
        }
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if (driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device);
        }
        if (driver == null) {
            status("connection failed: no driver for device");
            return;
        }
        if (driver.getPorts().size() < portNum) {
            status("connection failed: not enough ports at device");
            return;
        }
        usbSerialPort = driver.getPorts().get(portNum);
        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if (usbConnection == null && permissionGranted == null && !usbManager.hasPermission(driver.getDevice())) {
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(PayActivity.this, 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), 0);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return;
        }
        if (usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
                status("connection failed: permission denied");
            else
                status("connection failed: open failed");
            return;
        }

        connected = Connected.Pending;
        try {
            usbSerialPort.open(usbConnection);
            usbSerialPort.setParameters(baudRate, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            SerialSocket socket = new SerialSocket(PayActivity.this.getApplicationContext(), usbConnection, usbSerialPort);
            service.connect(socket);
            // usb connect is not asynchronous. connect-success and connect-error are returned immediately from socket.connect
            // for consistency to bluetooth/bluetooth-LE app use same SerialListener and SerialService classes
            onSerialConnect();
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        controlLines.stop();
        service.disconnect();
        usbSerialPort = null;
    }

    private void send(String str) {
        if (connected != Connected.True) {
            Toast.makeText(PayActivity.this, "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn);
            byte[] data = Arrays.toString(MainActivity.a).getBytes();
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(byte[] data) {
        receiveText.append(new String(data));
    }

    void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    private enum Connected {False, Pending, True}

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

    class ControlLines {
        private static final int refreshInterval = 200; // msec

        private Handler mainLooper;
        private Runnable runnable;

        ControlLines(View view) {
            mainLooper = new Handler(Looper.getMainLooper());
            runnable = this::run; // w/o explicit Runnable, a new lambda would be created on each postDelayed, which would not be found again by removeCallbacks


        }

        private void toggle(View v) {
            ToggleButton btn = (ToggleButton) v;
            if (connected != Connected.True) {
                btn.setChecked(!btn.isChecked());
                Toast.makeText(PayActivity.this, "not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            String ctrl = "";

        }

        private void run() {
            if (connected != Connected.True)
                return;
            try {
                EnumSet<UsbSerialPort.ControlLine> controlLines = usbSerialPort.getControlLines();

                mainLooper.postDelayed(runnable, refreshInterval);
            } catch (IOException e) {
                status("getControlLines() failed: " + e.getMessage() + " -> stopped control line refresh");
            }
        }

        void start() {
            if (connected != Connected.True)
                return;
            try {
                EnumSet<UsbSerialPort.ControlLine> controlLines = usbSerialPort.getSupportedControlLines();

                run();
            } catch (IOException e) {
                Toast.makeText(PayActivity.this, "getSupportedControlLines() failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        void stop() {
            mainLooper.removeCallbacks(runnable);

        }
    }

}
