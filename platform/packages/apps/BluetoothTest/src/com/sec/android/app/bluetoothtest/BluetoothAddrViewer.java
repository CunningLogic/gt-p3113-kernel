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
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothAddrViewer extends Activity implements OnClickListener {    
    private static final String TAG = "BluetoothAddrViewer";
    private static Context mContext;
    private BluetoothAdapter mAdapter;
    private TextView mTv;
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive(), action: "+action);
			
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                case BluetoothAdapter.STATE_ON:
                    Log.d(TAG, "BluetoothAdapter.STATE_ON");
                    mTv.setText(mAdapter.getAddress());
                    break;
                }
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        mContext = this;
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mReceiver, filter);

        setContentView(R.layout.bluetooth_addr_viewer);

        findViewById(R.id.ButtonExitBdAddr).setOnClickListener(this);
        mTv = (TextView) this.findViewById(R.id.TextViewBdAddr);

        mAdapter = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();

        if (mAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            mAdapter.enable();
            Toast.makeText(mContext, "Turning on...", Toast.LENGTH_LONG).show();
        } else if (mAdapter.getState() == BluetoothAdapter.STATE_ON) {
            mTv.setText(mAdapter.getAddress());
        }
    }
    
    public void onClick(View v) {
        if (v.getId() == R.id.ButtonExitBdAddr) {
            Log.d(TAG, "onClick");
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        mContext.unregisterReceiver(mReceiver);
    }
}
