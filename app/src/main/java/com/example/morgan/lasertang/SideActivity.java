package com.example.morgan.lasertang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class SideActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawer;
    NavigationView navigationView;

    String LOG = "MENU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                Log.d(LOG, "onDrawerClosed");
                // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                Log.d(LOG, "onDrawerOpened");
                drawer.bringToFront();
                drawer.requestLayout();

                navigationView.bringToFront();
                navigationView.requestLayout();
                HashMap<String, String> userInfo = getUserInfo();

                if (userInfo != null) {
                    TextView username = (TextView) findViewById(R.id.sideUsername);
                    username.setText(String.format("%s %s", userInfo.get("first_name"), userInfo.get("last_name")));
                    if (!userInfo.get("photo").equals("")) {
                        (new ImageCache()).initCacheDir(SideActivity.this);
                        //Toast.makeText(this, userInfo.get("photo"), Toast.LENGTH_SHORT).show();
                        new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                                .execute(userInfo.get("photo"));
                    }
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.side, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_game) {
            Log.d(LOG, "nav_game");
            Intent intent = new Intent(SideActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_connection) {
            Log.d(LOG, "nav_connection");
        } else if (id == R.id.nav_store) {
            Log.d(LOG, "nav_store");
            Intent intent = new Intent(SideActivity.this, StoreActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search) {
            Log.d(LOG, "nav_search");
            Intent intent = new Intent(SideActivity.this, SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_login) {
            Log.d(LOG, "nav_login");
            Intent intent = new Intent(SideActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_invite) {
            Log.d(LOG, "nav_invite");
            Intent intent = new Intent(SideActivity.this, InviteActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_reference) {
            Log.d(LOG, "nav_reference");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean hasLoginInfo(SharedPreferences loginSettings) {
        return loginSettings.getString("first_name", null) != null;
    }

    public HashMap<String, String> getUserInfo() {
        SharedPreferences loginSettings = getSharedPreferences("login_fb", Context.MODE_PRIVATE);
        if (loginSettings == null || !(hasLoginInfo(loginSettings))) {
            loginSettings = getSharedPreferences("login_vk", Context.MODE_PRIVATE);
        }
        if (loginSettings == null) {
            return null;
        }
        HashMap<String, String> userInfo = new HashMap<String, String>();
        String fields[] =  {"first_name", "last_name", "photo"};
        for (String field: fields) {
            userInfo.put(field, loginSettings.getString(field, ""));
        }
        return userInfo;
    }
}
