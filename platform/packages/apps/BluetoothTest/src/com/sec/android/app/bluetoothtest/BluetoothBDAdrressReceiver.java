package com.sec.android.app.bluetoothtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class BluetoothBDAdrressReceiver extends BroadcastReceiver {    
    private static final String TAG = "BluetoothBDAdrressReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive(), action: "+action);

        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            Intent i = new Intent("com.bluetoothtestapp.BtBDstartservice.BootComplete");
            context.startService(i);
        }else if(action.equals("com.sec.android.app.bluetoothtest.BT_ID_WRITE")){
            String extra = intent.getStringExtra("MAC_DATA");
            Intent i = new Intent("com.sec.android.app.bluetoothtest.BT_ID_WRITE");
            i.putExtra("MAC_DATA",extra);
            context.startService(i);
        }else if(action.equals("com.sec.android.app.bluetoothtest.BT_ID_READ")){
            Intent i = new Intent("com.sec.android.app.bluetoothtest.BT_ID_READ");
            context.startService(i);
        }
    }
}