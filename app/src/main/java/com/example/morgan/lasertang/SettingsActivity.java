package com.example.morgan.lasertang;

import android.app.ListActivity;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsActivity extends SideActivity implements View.OnClickListener {

    String LOG = "SETTINGS_ACTIVITY_LOG";

    BTDevicesReceiver newBTdevice;
    TextView tankName;

    public static SettingsListAdapter adapter;

    final static String[] motor_item = new String[] {
            "В бой!", "Прокачать в магазине", "Использование сенсоров", "Органы управления", "Похвастаться", "меню"
    };
    final static String[] motor_comment = new String[] {
            "", "", "Управление с помощью гироскопа", "Руль слева, башня справа", "+10 секунд ускорения", "меню"
    };

    static Boolean[] motor_cB = new Boolean[] {false, false, true, true, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        drawer.addView(contentView, 0);

        Intent calledIntent = getIntent();

        tankName = (TextView) findViewById(R.id.tankName);
        tankName.setText(calledIntent.getStringExtra("NAME"));

        adapter = new SettingsListAdapter(this, motor_item, motor_comment, motor_cB);

        ListView listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(adapter);

        newBTdevice = new BTDevicesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTService.tankDisconnected);
        registerReceiver(newBTdevice, intentFilter);

        tankName.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(LOG, adapter.getItem(position));
                switch(position){
                    case 0:
                        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivityForResult(mainIntent, 1);
                        break;
                    case 1:
                        Intent storeIntent = new Intent(SettingsActivity.this, StoreActivity.class);
                        startActivityForResult(storeIntent, 1);
                        break;
                    case 4:
                        Intent inviteIntent = new Intent(SettingsActivity.this, InviteActivity.class);
                        startActivityForResult(inviteIntent, 1);
                        break;
                    case 5:
                        Intent menuIntent = new Intent(SettingsActivity.this, SideActivity.class);
                        startActivityForResult(menuIntent, 1);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.d(LOG, "onClick");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(newBTdevice);
    }

    private class BTDevicesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            if(action.equals(BTService.tankDisconnected)) {
                Toast.makeText(arg0, "Sorry lost connection", Toast.LENGTH_SHORT).show();
                SettingsActivity.this.finish();
            }
        }
    }
}
