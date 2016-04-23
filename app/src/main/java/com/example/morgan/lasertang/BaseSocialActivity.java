package com.example.morgan.lasertang;

import android.app.ProgressDialog;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKRequest;

public abstract class BaseSocialActivity extends AppCompatActivity {
    protected CallbackManager fbCallbackManager;
    protected ProgressDialog requestInProgressDialog;
    protected VKRequest vkUserRequest;
    protected VKRequest.VKRequestListener vkRequestListener;
    protected static final String LOGIN_SHARED_PREFERENCES = "login_";
    protected VKCallback<VKAccessToken> vkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fbCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(fbCallbackManager, fbCallback);

        requestInProgressDialog = new ProgressDialog(this,
                R.style.AppThemeProcessDialog);
        prepareDialog();

    }

    FacebookCallback<LoginResult> fbCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken == null) {
                showErrorText();
                return;
            }
            processFbLoginSuccess();
        }

        @Override
        public void onCancel() {
            showErrorText();
        }

        @Override
        public void onError(FacebookException exception) {
            showErrorText();
        }
    };

    public abstract void showErrorText();

    public abstract void processFbLoginSuccess();

    public abstract void prepareDialog();

    protected void showMessage(int messageId) {
        Toast.makeText(this,
                getResources().getString(messageId), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (vkUserRequest != null) {
            outState.putLong("vk_request", vkUserRequest.registerObject());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        long requestId = savedInstanceState.getLong("request");
        vkUserRequest = VKRequest.getRegisteredRequest(requestId);
        if (vkUserRequest != null) {
            vkUserRequest.unregisterObject();
            vkUserRequest.setRequestListener(vkRequestListener);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, vkCallback)) {
            fbCallbackManager.onActivityResult(requestCode,
                    resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vkUserRequest != null) {
            vkUserRequest.cancel();
        }
    }
}
