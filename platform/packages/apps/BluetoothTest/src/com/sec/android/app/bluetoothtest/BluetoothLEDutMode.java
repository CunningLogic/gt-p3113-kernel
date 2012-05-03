package com.sec.android.app.bluetoothtest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import java.lang.Process;
import java.lang.Runtime;
import java.io.DataOutputStream;
import java.io.IOException;




public class BluetoothLEDutMode  extends Activity implements OnClickListener{
    private static final String TAG = "BluetoothLEDutMode";
    private WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, TAG);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_le_dut_mode);
        View buttonon = this.findViewById(R.id.EnableLEMode);
        buttonon.setOnClickListener(this);
        View buttonoff = this.findViewById(R.id.DisableLEMode);
        buttonoff.setOnClickListener(this);
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.EnableLEMode:
                Log.i(TAG,"BluetoothLEDutMode on");
                EnableBluetoothLEDUTMode();
                break;
            case R.id.DisableLEMode:
                Log.i(TAG,"BluetoothLEDutMode off");
                DisableBluetoothLEDUTMode();
                break;
        }
    }

    public void EnableBluetoothLEDUTMode() {
         mWakeLock.acquire();

        SystemProperties.set("ctl.start", "LE_dut_cmd");
        SystemProperties.set("ctl.start", "hciattach_le");
    }
    public void DisableBluetoothLEDUTMode() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
//        SystemProperties.set("ctl.stop", "LE_dut_cmd");
//        SystemProperties.set("ctl.stop", "hciattach_le");
    }
}
