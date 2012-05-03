
package com.sec.android.app.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.Phone;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
Add comments later
 */

public class BluetoothTestHelper extends BroadcastReceiver {
    private  Context mContext;
    private final String TAG = "BluetoothTestHelper";
    
    public Phone mPhone = null;
    private BluetoothAdapter mAdapter = null;
          
    public BluetoothTestHelper(Context context, Phone phone) {
        mContext = context;
        mPhone = phone;
        getBtRef();
        
        log("BluetoothTestHelper");
    }

    public void getBtRef() {
        mAdapter = (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter();
        if (mAdapter == null) {
            throw new RuntimeException("Platform does not support Bluetooth");
        }
    }

    public void unregisterReceiver() {
        log("unregisterReceiver");
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        log("BluetoothTestHelper - onReceive");
        
        mContext =  context;
        
        String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            
            if (state == BluetoothAdapter.STATE_ON) {
                log(" BluetoothIntent.BLUETOOTH_ENABLED_ACTION ");
                HostSendEnableAckFromApp();
                
                log("Making BT Device :: SCAN_MODE_CONNECTABLE_DISCOVERABLE");
                mAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
                Toast.makeText(context,"BT IS ENABLED", Toast.LENGTH_LONG).show();	              
            } else if (state == BluetoothAdapter.STATE_OFF) {
                log("BluetoothIntent.BLUETOOTH_DISABLED_ACTION");
                HostSendDisableAckFromApp();
                Toast.makeText(context,"BT IS DISABLED", Toast.LENGTH_LONG).show();
                
                unregisterReceiver();
            }//end cond for BT OFF
        } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
            log("BluetoothAdapter.ACTION_DISCOVERY_STARTED");
        } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            log("BluetoothAdapter.ACTION_DISCOVERY_FINISHED");
        } else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
            log("BluetoothDevice.ACTION_FOUND");
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String address = device.getAddress();
            HostSendDiscoveryAckFromApp(address) ;
            // Toast.makeText(context,"REMOTE DEVICE IS FOUND"+"[" + address + "]", Toast.LENGTH_LONG).show();
        } else {
            log("SHOULD NOT COME HERE,unknown action ");
        }
    }
    
    private synchronized void HostSendDiscoveryAckFromApp(String address) {
        log("HostSendAckFromApp for " + "[" + address + "]");
        /* Send a ACK back to the RIL code , to be implemented */
        byte[] data= new byte[2];
        int length=2;

        // forming the ACK byte array
        data[0]=0x01;  //test type search
        data[1]=0x01;  //test result

        sendAck(data,length);
    }

    private synchronized void HostSendEnableAckFromApp() {
        log("Inside --->HostSendEnableAckFromApp ");
        /* Send a ACK back to the RIL code , to be implemented */
        byte[] data= new byte[2];
        int length=2;
        // forming the ACK byte array
        data[0]=0x00;  //test type activation
        data[1]=0x01;  //test result success
        
        sendAck(data,length);
    }

    private synchronized void HostSendDisableAckFromApp() {
        log("Inside --->HostSendDisableAckFromApp ");
        /* Send a ACK back to the RIL code , to be implemented */
        byte[] data= new byte[2];
        int length=2;

        // forming the ACK byte array
        data[0]=0x02;  //test type de-activation
        data[1]=0x01;  //test result success

        sendAck(data,length);
    }

    private  void log(String msg) {
        Log.e(TAG, msg);
    }

    public void sendAck(byte data1[],int len) {
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(bos);
        
        try {
            dos.writeByte(0x0C); //main function id
            dos.writeByte(0x03); //sub function id
            dos.writeByte(len+3); // lenght of data

            for(int i=0; i < len; i++)
                dos.write(data1[i]);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (dos != null) {
            try {
                dos.close();
            } catch (IOException e) {
                // Ignore
            }
        }

        byte cmd[]=bos.toByteArray();
        
        for(int i=0;i<(len+3);i++)
            log("The cmd ack before sending to ril is ----> "+ cmd[i]);

        mPhone.invokeOemRilRequestRaw(cmd, null);
    }

    public void sendEarlyAck() {
        log("Making BT Device :: SCAN_MODE_CONNECTABLE_DISCOVERABLE");
        
        mAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
        HostSendEnableAckFromApp();
    }
}
