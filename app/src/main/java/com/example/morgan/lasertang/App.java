package com.example.morgan.lasertang;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;

import java.util.Map;

/**
 * Created by nastya on 17.04.16.
 */
public class App extends Application {

    private VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                VKAccessToken.removeTokenAtKey(getApplicationContext(), "login_vk_token");
                SharedPreferences loginSettings = getSharedPreferences("login_vk", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginSettings.edit();
                editor.clear();
                editor.commit();
            }
        }
    };

    private AccessTokenTracker fbAccessTokenTracker;

    @Override
    public void onCreate() {
        VKSdk.initialize(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    SharedPreferences loginSettings = getSharedPreferences("login_fb", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginSettings.edit();
                    editor.clear();
                    editor.commit();
                }
            }
        };
        super.onCreate();
    }


}