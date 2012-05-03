package com.sec.android.app.bluetoothtest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.IOException;

public class BluetoothRfTest extends Activity implements OnClickListener {    

    private static final String TAG = "BluetoothRfTest";
    private static final boolean DBG = false;

    private BluetoothAdapter mAdapter;
    private Context mContext;
    private IntentFilter mIntentFilter;
    private static boolean mFlagBluetoothTestModeService;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DBG) Log.d(TAG, "onReceive(), action: "+action);
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(mContext, "BT is turned on", Toast.LENGTH_SHORT).show();
                    onEnterBtRfTest();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Toast.makeText(mContext, "Turning BT on, Please wait...", Toast.LENGTH_SHORT).show();
                break;
                }
            }

            if (action.equals("com.android.samsungtest.BluetoothRfTestOff")) {
                Toast.makeText(mContext, "Exit Bluetooth RF test mode", Toast.LENGTH_SHORT).show();

                if (mAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    mAdapter.disable();
                }
                finish();
            }
        }
    };
        
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_rf_test);
        if (DBG) Log.d(TAG, "OnCreate()");

        findViewById(R.id.ButtonExitBtRfTest).setOnClickListener(this);
        
        mContext = this;
        
        mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mReceiver, mIntentFilter);

        final IntentFilter mIntentFilter2 = new IntentFilter();      
        mIntentFilter2.addAction("com.android.samsungtest.BluetoothRfTestOff");
        mContext.registerReceiver(mReceiver, mIntentFilter2);
        
        mAdapter = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();

        if (mAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            mAdapter.enable();
        } else if (mAdapter.getState() == BluetoothAdapter.STATE_ON) {
            onEnterBtRfTest();
        }
    }

    private void onEnterBtRfTest() {
        if (DBG) Log.d(TAG, "Enter Bluetooth RF test mode");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        mAdapter.setDiscoverableTimeout(0);
        SystemProperties.set("ctl.start", "bt_dut_cmd");
        Toast.makeText(mContext, "Enter Bluetooth RF test mode", Toast.LENGTH_SHORT).show();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.ButtonExitBtRfTest) {
            if (mAdapter.getState() == BluetoothAdapter.STATE_ON) {
                mAdapter.disable();
            }
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }
}
