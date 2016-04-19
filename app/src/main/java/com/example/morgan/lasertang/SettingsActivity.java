package com.example.morgan.lasertang;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

public class SettingsActivity extends AppCompatActivity {

    String LOG = "SETTINGS_ACTIVITY_LOG";

    BTDevicesReceiver newBTdevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        newBTdevice = new BTDevicesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTService.searchStarted);
        intentFilter.addAction(BTService.newDevice);
        intentFilter.addAction(BTService.searchStopped);
        intentFilter.addAction(BTService.tankConnected);
        intentFilter.addAction(BTService.tankDisconnected);
        registerReceiver(newBTdevice, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG, "onResume");
    }

    private class BTDevicesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            if(action.equals(BTService.tankDisconnected)) {
                Log.d(LOG, "Sorry lost connection");
            }
        }
    }
}
