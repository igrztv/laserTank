package com.example.morgan.lasertang;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class BTService extends Service {

    private static final int REQUEST_ENABLE_BT = 1;
    // Tank Commands
    private static final byte CMD_SHOOT = 0x01;
    private static final byte CMD_MOVE = 0x02;

    BluetoothSocket clientSocket;
    private BroadcastReceiver mReceiver = null;
    BluetoothAdapter bluetooth;

    ArrayAdapter<String> itemsAdapter;

    public BTService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.d(LOG_TAG, "onBind");
//        return null;

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    final String LOG_TAG = "myLogs";

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Log.d(LOG_TAG, "onDestroy");
    }

    void someTask() {

        Log.d(LOG_TAG, "SomeTask Function");

        bluetooth = BluetoothAdapter.getDefaultAdapter();

        Log.d(LOG_TAG, "bluetooth.isEnabled Function");

//        if (!bluetooth.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Log.d(LOG_TAG, "startService Function");
//            startService(enableBtIntent);
//        }

        Log.d(LOG_TAG, "bluetooth.enable()");
        bluetooth.enable();
        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        Log.d(LOG_TAG, "showBluetoothDevices Function");
        showBluetoothDevices();

//        // Register the BroadcastReceiver
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        Log.d(LOG_TAG, "TRY connection BLOCK");

        try {
            BluetoothDevice device = bluetooth.getRemoteDevice("20:15:02:11:26:45");
            Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            clientSocket = (BluetoothSocket) m.invoke(device, 1);
            clientSocket.connect();
        } catch (IOException |
                SecurityException |
                IllegalArgumentException |
                NoSuchMethodException |
                InvocationTargetException |
                IllegalAccessException e) {
            Log.d("BLUETOOTH", e.getMessage());
        }
    }

    private void showBluetoothDevices() {

        if (!bluetooth.isEnabled()) {
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                itemsAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        boolean discovering = bluetooth.startDiscovery();
        if (!discovering) {
            //Toast.makeText(getApplicationContext(), "CANNOT DISCOVER", Toast.LENGTH_LONG).show();
        } else {
            // Create a BroadcastReceiver for ACTION_FOUND
            mReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    // When discovery finds a device
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        // Add the name and address to an array adapter to show in a ListView
                        itemsAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                }
            };
        }
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // запишем в лог значения requestCode и resultCode
//        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);
//        // если пришло ОК
//        if (resultCode == RESULT_OK) {
//            // show list of BT devices
//            showBluetoothDevices();
//        } else {
//            Toast.makeText(this, "Wrong result", Toast.LENGTH_SHORT).show();
//        }
//    }

    private boolean sendCMD(byte cmd) {
        byte[] buf = {cmd};
        return sendCMD(buf);
    }

    private boolean sendCMD(byte[] cmd) {
        try {
            OutputStream outStream = clientSocket.getOutputStream();
            outStream.write(cmd);
            return true;
        } catch (IOException e) {
            Log.d("BLUETOOTH", e.getMessage());
        }
        return false;
    }
}
