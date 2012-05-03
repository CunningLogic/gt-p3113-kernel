package com.sec.android.app.bluetoothtest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.os.SystemProperties;

/**
 * Add comments later
 */

public class BluetoothBDTestService extends Service {
    public BluetoothTestHelper mHelper = null;
    private static final String TAG = "BluetoothBDTestService";
    private static Context mContext;
    private String bd_address_path = null; /* "/efs/bluetooth/bt_addr" */
    private static IntentFilter mIntentFilter;
    private static final int ADDRESS_LENGTH = 17;
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (intent == null) {
            // Nothing to process, stop.
            Log.d(TAG, "onStart - intent is null.");
            stopSelf();
            return;
        }

        String action = intent.getAction();
        Log.e(TAG, "onStart(), action: " + action);

        bd_address_path = SystemProperties.get("ro.bt.bdaddr_path");
        Log.e(TAG, "bd_address_path: " + bd_address_path);
		
        if (action.equals("com.bluetoothtestapp.BtBDstartservice.BootComplete")) {
            File f = new File(bd_address_path);
            if(bd_address_path != null && f.exists() && f.length()>0){
                Log.e(TAG, "already exist!( "+bd_address_path+" )"+", file length: "+f.length());
                return;
            }
            else{
                if(f.length()==0){
                    Log.e(TAG, "file length is 0, this file will be removed and make new random address!");
                    f.delete();
                }
                createBDAddrFileUpdateAddr(generateRandomAdrress(),false);
            }
        } else if (action.equals("com.sec.android.app.bluetoothtest.BT_ID_WRITE")) {
            boolean mWriteSuccess = false;
            String extra = intent.getStringExtra("MAC_DATA");
            File f = new File(bd_address_path);

            Log.e(TAG, "Get extra, Write address: " + extra.toUpperCase());

            if(f.exists()){
                f.delete();
                Log.e(TAG, "The previous file is deleted, and BT address will update in new file.");
            }
            mWriteSuccess = createBDAddrFileUpdateAddr(extra,mWriteSuccess);
            sendIntentToFactoryApp(mWriteSuccess);
        } else if (action.equals("com.sec.android.app.bluetoothtest.BT_ID_READ")) {
            boolean mReadSuccess = false;
            String address = null;
            BufferedReader br = null;
            File f = null;

            try {
                f = new File(bd_address_path);
                if (!f.exists()) {
                    mReadSuccess = false;
                } else {
                    FileInputStream fstream = new FileInputStream(bd_address_path);
                    DataInputStream in = new DataInputStream(fstream);
                    br = new BufferedReader(new InputStreamReader(in));
                    address = br.readLine();

                    if (address != null && checkBluetoothAddress(address))
                        mReadSuccess = true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sendIntentToRIL(address, mReadSuccess);

        } else {
            Log.e(TAG, " **** not expecting this intent *** ");
        }
    }

    // API to send response to Factory app
    void sendIntentToFactoryApp(boolean status) {
        Log.e(TAG, "sendIntentToFactoryApp(), status: "+status);
        Intent mFactIntent = new Intent(
                "com.sec.android.app.bluetoothtest.BT_ID_RESPONSE");
        if (status == true)
            mFactIntent.putExtra("S_DATA", "OK");
        else
            mFactIntent.putExtra("S_DATA", "NG");
        this.sendBroadcast(mFactIntent);
    }

    // API to send response to RIL
    void sendIntentToRIL(String address, boolean status) {
        Log.e(TAG, "sendIntentToRIL(), status: "+status);
        Intent mRILIntent = new Intent(
                "com.sec.android.app.bluetoothtest.BT_ID_RESPONSE");
        if (status == true)
            mRILIntent.putExtra("S_DATA", address);
        else
            mRILIntent.putExtra("S_DATA", "NG");
        this.sendBroadcast(mRILIntent);
    }

    // API to create directory and file and update BD address
    boolean createBDAddrFileUpdateAddr(String address , boolean mWriteSuccess){
        if(!checkBluetoothAddress(address))
            return false;

        //String bd_folder = bd_address_path.substring(0, bd_address_path.lastIndexOf("/",0)-1);
        //Log.d(TAG, "get folder path :" + bd_folder);

        try {
            File f = new File("/efs/bluetooth");
            // if directory doesn't exists create the directory
            if (!f.exists()) {
                boolean status = f.mkdirs();
                // set permission to 755
                f.setReadable(true, false);
                f.setWritable(true, true);
                f.setExecutable(true, false);
                Log.d(TAG, "directory creation status :" + status);
            }

            File f1 = new File(bd_address_path);
            // if file doesn't exists create the file
            if (!f1.exists()) {
                boolean status = f1.createNewFile();
                // set permission to 644
                f1.setReadable(true, false);
                Log.d(TAG, "file creation status :" + status);
            }

            File f2 = new File("/efs/imei/bt.txt");
            //for upgrade.
            if (f2.exists()) {
                String prevAddress = getPrevAddress();
                if (prevAddress != null && checkBluetoothAddress(prevAddress))
                    address = prevAddress;

                Log.e(TAG, "*** get & remove previous address file ***");
            }			

            Writer output = new BufferedWriter(new FileWriter(bd_address_path));
            mWriteSuccess = false;
            if (address != null) {
                output.write(address);
                output.flush();
                SystemProperties
                        .set("persist.service.bt.bt_macaddr", address);
                mWriteSuccess = true;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mWriteSuccess;
    }

    //API to generate random address
    String generateRandomAdrress(){
        Log.d(TAG, "generateRandomAdrress()");
        byte b[] = new byte[12];
        int i = 0;
        Integer j;
        StringBuffer rand_data = new StringBuffer();
        StringBuffer address = null;

        Random ran = new Random();
        ran.nextBytes(b);

        for (i = 0; i < 6; i++) {
            // if byte value is between 0 to 15 increase it to avoid
            // length problem.
            if (0 <= b[i] && b[i] <= 15) {
                b[i] += 16;
            }
            j = b[i] & 0xFF;

            //The first byte must have a even value.
            //So, I added the safe code.
            if(i==0)
            {
                j = 0x22;
                Log.e(TAG,"The first byte must have a even value, first byte value: 0x"+Integer.toHexString(j));
            }
			
            System.out.println("next value :" + i + ":"
                    + rand_data.append(Integer.toHexString(j)));
        }

        address = addressFormatter(rand_data);	
        Log.e(TAG,"Address is :"+address.toString().toUpperCase());
		
        return address.toString().toUpperCase();
    }

    StringBuffer addressFormatter(StringBuffer address){
        for (int k = 2; k < 16; k = k + 3) {
            address.insert(k, ":");
        }
        return address;
    }

    String getPrevAddress(){
        Log.e(TAG, "*** get previous address, for os upgrade **");
        String prevAddress = null;
        StringBuffer address = null;
        BufferedReader br = null;
        int i;

        try {
            FileInputStream fstream = new FileInputStream("/efs/imei/bt.txt");
            DataInputStream in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            prevAddress = br.readLine();

            if (prevAddress == null)
                return null;

            //remove bt_macaddr:
            address = addressFormatter(new StringBuffer(prevAddress.substring(11,23)));
            return address.toString().toUpperCase();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
	
    //API to validate BD address
    public static boolean checkBluetoothAddress(String address) {
        if (address == null || address.length() != ADDRESS_LENGTH) {
            return false;
        }
        for (int i = 0; i < ADDRESS_LENGTH; i++) {
            char c = address.charAt(i);
            switch (i % 3) {
                case 0:
                case 1:
                    if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F')) {
                        // hex character, OK
                        break;
                    }
                    return false;
                case 2:
                    if (c == ':') {
                        break;  // OK
                    }
                    return false;
            }
        }
        return true;
    }
}
