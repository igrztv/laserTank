package com.example.morgan.lasertang;

import android.app.ListActivity;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class SettingsActivity extends ListActivity {

    String LOG = "SETTINGS_ACTIVITY_LOG";

    BTDevicesReceiver newBTdevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_settings);

        // 1. pass context and data to the custom adapter
        ListAdapter adapter = new ListAdapter(this, generateData());
        // 3. setListAdapter
        //listView.setAdapter(adapter); if extending Activity
        setListAdapter(adapter);

        newBTdevice = new BTDevicesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTService.searchStarted);
        intentFilter.addAction(BTService.newDevice);
        intentFilter.addAction(BTService.searchStopped);
        intentFilter.addAction(BTService.tankConnected);
        intentFilter.addAction(BTService.tankDisconnected);
        registerReceiver(newBTdevice, intentFilter);
    }

    private ArrayList<Model> generateData(){
        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model("Group Title"));
        models.add(new Model(R.drawable.amperka, "Menu Item 1", "1"));
        models.add(new Model(R.drawable.amperka, "Menu Item 2", "2"));
        models.add(new Model(R.drawable.amperka, "Menu Item 3", "12"));

        return models;
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
