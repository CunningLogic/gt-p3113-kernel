package com.sec.android.app.bluetoothtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.provider.Telephony.Intents.SECRET_CODE_ACTION;

public class BluetoothTestBroadcastReceiver extends BroadcastReceiver {    
    private static final String TAG = "BluetoothTestBroadcastReceiver";
    
    public BluetoothTestBroadcastReceiver () {
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive(), action: "+action);
        if (action.equals(SECRET_CODE_ACTION)) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            String host = intent.getData().getHost();
            if (host.equals("232337")) {
                i.setClass(context, BluetoothAddrViewer.class);
            } else if (host.equals("232331")) {
                i.setClass(context, BluetoothRfTest.class);
            } else if (host.equals("533881")||host.equals("533880")) {
                i.setClass(context, BluetoothLEDutMode.class);
            }
                
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
