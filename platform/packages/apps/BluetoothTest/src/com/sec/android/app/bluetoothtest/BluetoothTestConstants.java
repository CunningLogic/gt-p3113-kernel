
package com.sec.android.app.bluetoothtest;

import android.annotation.SdkConstant;
import android.annotation.SdkConstant.SdkConstantType;

/**
 * The Android Bluetooth API is not finalized, and *will* change. Use at your
 * own risk.
 *
 *
 * @hide
 */

public interface BluetoothTestConstants {

    public static final String HOSTREQ_OPERTYPE =
        "android.test.bluetooth.intent.HOSTREQ_OPERTYPE";

    public static final String HOSTREQ_BTENABLE =
        "android.test.bluetooth.intent.HOSTREQ_BTENABLE";

    public static final String HOSTREQ_BTDISABLE =
        "android.test.bluetooth.intent.HOSTREQ_BTDISABLE";

    public static final String HOSTREQ_BTSEARCH =
        "android.test.bluetooth.intent.HOSTREQ_BTSEARCH";
    
    @SdkConstant(SdkConstantType.BROADCAST_INTENT_ACTION)
    public static final String HOSTREQ_BTTESTMODE          =
        "android.test.bluetooth.intent.action.HOSTREQ_BTTESTMODE";
}
