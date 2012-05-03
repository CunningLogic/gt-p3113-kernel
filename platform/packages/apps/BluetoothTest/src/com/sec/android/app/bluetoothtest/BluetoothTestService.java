
package com.sec.android.app.bluetoothtest;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

/**
Add comments later
 */

public class BluetoothTestService extends Service  {
    public BluetoothTestHelper mHelper = null;
    private static final String TAG = "BluetoothTestService";
    private static Context mContext;
    private static IntentFilter mIntentFilter;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.e(TAG, "onCreate()");

        Phone phone = PhoneFactory.getDefaultPhone();
        Log.e(TAG, "PhoneFactory.getDefaultPhone()  is  " + phone);

        if (phone == null) {
            Log.e(TAG, "BT TEST Application is not able to get the phone instance");
        }//end of phone==null check if cond.

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);

        mContext = getApplicationContext();	                	
        Log.e(TAG, "app context is " + mContext );	
		
        mHelper = new BluetoothTestHelper(mContext, phone);

        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onStart()");

        mContext.registerReceiver(mHelper, mIntentFilter);

        // super.onStart(intent, startId);
    }
}
