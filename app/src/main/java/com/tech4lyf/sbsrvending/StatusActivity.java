package com.tech4lyf.sbsrvending;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Set;

public class StatusActivity extends AppCompatActivity {

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



    private TextView transactionStatus;
    private String status;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_status);

		mHandler = new MyHandler(this);



		//Intent mainIntent = getIntent();
        transactionStatus = findViewById(R.id.transaction_status);
        StringBuilder stringBuilder = new StringBuilder(Arrays.toString(MainActivity.a));
        stringBuilder.deleteCharAt(0);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        String string = stringBuilder.toString().replaceAll(" ", "");
        Log.d("msg", string);
		if (usbService != null) {
			// if UsbService was correctly binded, Send data
			usbService.write(string.getBytes());
		}

        /*status = mainIntent.getStringExtra("transStatus");
        if(status.equals("SUCCESS")){
        	Log.d("Success",status);
			transactionStatus.setText("Transaction Successful...\n Please Collect...");

        }
        else{
        	transactionStatus.setText(status+ "\n Try Again...");
		}*/


      /*  new Thread(new Runnable() {
            @Override

                }

            }
        }).start();*/


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

	private void setFilters() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
		filter.addAction(UsbService.ACTION_NO_USB);
		filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
		filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
		filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
		registerReceiver(mUsbReceiver, filter);
	}

	/*
	 * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
	 */
	private static class MyHandler extends Handler {
		private final WeakReference<StatusActivity> mActivity;

		public MyHandler(StatusActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case UsbService.MESSAGE_FROM_SERIAL_PORT:
					String data = (String) msg.obj;
					// mActivity.get().display.append(data);
					break;
				case UsbService.CTS_CHANGE:
					Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
					break;
				case UsbService.DSR_CHANGE:
					Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
					break;
			}
		}
	}


}