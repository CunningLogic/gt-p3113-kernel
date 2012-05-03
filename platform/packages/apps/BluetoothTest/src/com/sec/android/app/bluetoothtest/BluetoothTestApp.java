
package com.sec.android.app.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

/*
BluetoothTestApp is a Broadcast receiver only to receive Intents of type "android.test.bluetooth.intent.action.HOSTREQ_BTTESTMODE"
The operation of this intent action could be

BluetoothTestConstants.HOSTREQ_BTENABLE
BluetoothTestConstants.HOSTREQ_BTDISABLE
BluetoothTestConstants.HOSTREQ_BTSEARCH

When we get a ENABLE action , a Service(BluetoothTestService) is started and will be running till we get a DISABLE action.
As part of this new Service , we dynamically register additional Bluetooth Intents ("BluetoothIntent.xxxxxx") with a new Broadcast receiver
(BluetoothTestHelper)to get additional info about the responses that come from the BT stack. These additional intents are processed in
BluetoothTestHelper onReceive from where we send the ACKs to the client of this application.
*/

public class BluetoothTestApp extends BroadcastReceiver {

    private static final String TAG = "BluetoothTestApp";
    private static BluetoothAdapter mAdapter = null;
    private static Context mContext;
    private static Intent mIntent;
    private static boolean mIsStarted = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG,"onReceive(), action: "+action);

        mContext =  context.getApplicationContext();
        Log.e(TAG,"app context is " + mContext);

        mAdapter = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();
        if (mAdapter == null) {
            throw new RuntimeException("Platform does not support Bluetooth");
        }

        if (action.equals(BluetoothTestConstants.HOSTREQ_BTTESTMODE)) {
            String operation = intent.getStringExtra(BluetoothTestConstants.HOSTREQ_OPERTYPE);
            Log.e(TAG,"operation is " + operation);
            if (operation.equals(BluetoothTestConstants.HOSTREQ_BTENABLE)) {
                mIntent= new Intent("com.bluetoothtestapp.btstartservice");
                mContext.startService(mIntent);
                mIsStarted = true;
                HostRequestBTON(mContext);
            } else if (operation.equals(BluetoothTestConstants.HOSTREQ_BTDISABLE)) {
                if (mIsStarted) {
                HostRequestBTOFF(mContext);
                mContext.stopService(mIntent);
                    mIsStarted = false;
                }
            } else if (operation.equals(BluetoothTestConstants.HOSTREQ_BTSEARCH)) {
                if (mIsStarted) {
                HostRequestBTSearch(mContext);
                }
            } else {
                Log.e(TAG,"SHOULD NOT COME HERE ");
            }
        }// end of ril intent check condition.
    }//End of onReceive
    
    private synchronized void HostRequestBTON(Context context) {
        Log.e(TAG,"HostRequestBTON()");
        Toast.makeText(context,"REQUEST TO MAKE BT ON", Toast.LENGTH_LONG).show();

        /* mAdapter = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();
        if (mAdapter == null) {
            throw new RuntimeException("Platform does not support Bluetooth");
        } */

        if (mAdapter.isEnabled()) {
            Log.e(TAG,"BT is already enabled");
            Toast.makeText(context,"BT IS ALREADY ON ", Toast.LENGTH_LONG).show();

            Phone phone = PhoneFactory.getDefaultPhone();
            Log.e(TAG,"PhoneFactory.getDefaultPhone()  is  " + phone );

            BluetoothTestHelper earlyAck = new BluetoothTestHelper(context, phone);
            earlyAck.sendEarlyAck();
        } else {
            Log.e(TAG,"Enabling BT ");
            mAdapter.enable();
        }
    }

    private synchronized void HostRequestBTOFF(Context context) {
        Log.e(TAG,"HostRequestBTOFF()");
        Toast.makeText(context,"REQUEST TO MAKE BT OFF", Toast.LENGTH_LONG).show();

        Log.e(TAG,"Disabling BT ");
        mAdapter.disable();
    }

    private synchronized void HostRequestBTSearch(Context context) {
        Log.e(TAG,"HostRequestBTSearch()");
        Toast.makeText(context,"REQUEST TO PERFORM BT SEARCH", Toast.LENGTH_LONG).show();

        mAdapter.startDiscovery();
    }
}
