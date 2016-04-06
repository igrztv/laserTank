package com.example.morgan.lasertang;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BT extends Application {

    public BluetoothAdapter bluetooth;

    @Override
    public void onCreate()
    {
        super.onCreate();
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (!bluetooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

}