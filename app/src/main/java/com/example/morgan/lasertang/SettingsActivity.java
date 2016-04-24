package com.example.morgan.lasertang;

import android.app.ListActivity;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    String LOG = "SETTINGS_ACTIVITY_LOG";

    BTDevicesReceiver newBTdevice;
    Button goToStore;
    TextView tankName;

    public static CustomListAdapter adapter;

    final static String[] motor_item = new String[] {
            "Прокачать в магазине", "Использование сенсоров", "Органы управления", "Похвастаться"
    };
    final static String[] motor_comment = new String[] {
            "", "Управление с помощью гироскопа", "Руль слева, башня справа", ""
    };
    static Integer[] motor_img={
            R.drawable.tank,
            R.drawable.tank,
            R.drawable.tank,
            R.drawable.tank
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent calledIntent = getIntent();

        tankName = (TextView) findViewById(R.id.tankName);
        tankName.setText(calledIntent.getStringExtra("NAME"));

        goToStore = (Button) findViewById(R.id.button);
        goToStore.setOnClickListener(this);

        adapter = new CustomListAdapter(this, motor_item, motor_comment, motor_img);

        ListView listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(adapter);

        newBTdevice = new BTDevicesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTService.tankDisconnected);
        registerReceiver(newBTdevice, intentFilter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(LOG, adapter.getItem(position));
                switch(position){
                    case 0:
                        Intent intent = new Intent(SettingsActivity.this, StoreActivity.class);
                        startActivityForResult(intent, 1);
                        break;

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SettingsActivity.this, StoreActivity.class);
        startActivityForResult(intent, 1);
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
                Toast.makeText(arg0, "Sorry lost connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
