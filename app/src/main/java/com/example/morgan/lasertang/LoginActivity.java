package com.example.morgan.lasertang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String LOGIN_SHARED_PREFERENCES = "login_";
    private VKRequest vkUserRequest;
    private CallbackManager fbCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button vk_button = (Button) findViewById(R.id.login_vk);
        Button fb_button = (Button) findViewById(R.id.login_fb);
        Button play_button = (Button) findViewById(R.id.play_without_login);

        vk_button.setOnClickListener(loginWithVk);
        fb_button.setOnClickListener(loginWithFb);
        play_button.setOnClickListener(goToPlay);

        fbCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(fbCallbackManager, fbCallback);

    }

    FacebookCallback<LoginResult> fbCallback= new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Map<String, String> userInfo = new HashMap<String, String>();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken == null) {
                processOauthError();
                return;
            }
            fetchFbProfile();
            goToPlayView();
        }

        @Override
        public void onCancel() {
            processOauthError();
        }

        @Override
        public void onError(FacebookException exception) {
            processOauthError();
        }
    };

    View.OnClickListener loginWithVk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VKSdk.login(LoginActivity.this);
        }
    };

    View.OnClickListener loginWithFb = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            LoginManager.getInstance().logInWithReadPermissions(
                LoginActivity.this,
                Arrays.asList("public_profile")
            );

        }
    };

    View.OnClickListener goToPlay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goToPlayView();
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                res.save();
                if (vkUserRequest != null) {
                    return;
                }
                vkUserRequest =  VKApi.users().get();
                vkUserRequest.executeWithListener(userInfoRequestListener);
            }

            @Override
            public void onError(VKError error) {
                processOauthError();
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            fbCallbackManager.onActivityResult(requestCode,
                    resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void fetchFbProfile() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (object == null) return;
                        try {
                            Map<String, String> userInfo = userToHash(object);
                            saveLoginInfo("fb", userInfo);
                            //access token is managed by fb class
                        } catch (JSONException e) {
                            //DO NOTHING: info is not  necessary
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private HashMap<String, String> userToHash(JSONObject user) throws JSONException {
        HashMap<String, String> userInfo = new HashMap<String, String>();
        String fields[] =  {"first_name", "last_name"};
        for (String field: fields) {
            userInfo.put(field, user.getString(field));
        }
        return userInfo;
    }

    VKRequest.VKRequestListener userInfoRequestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            Map<String, String> userInfo;
            try {
                JSONArray responseArr = (JSONArray) response.json.get("response");
                JSONObject user = (JSONObject) responseArr.getJSONObject(0);
                userInfo = userToHash(user);
                saveLoginInfo("vk", userInfo);
            }
            catch (JSONException e) {
                //DO NOTHING: - info is not necessary
            }
            goToPlayView();
        }

        @Override
        public void onError(VKError error) {
            //DO NOTHING: info is not necessary
        }
    };

    private void processOauthError() {
        Toast.makeText(LoginActivity.this,
            "Упс, авторизация не прошла :(", Toast.LENGTH_LONG).show();
    }

    private void goToPlayView() {
        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void saveLoginInfo(String authMethod, Map<String, String> loginDict) {
        SharedPreferences loginSettings = getSharedPreferences(LOGIN_SHARED_PREFERENCES + authMethod, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginSettings.edit();
        for (Map.Entry<String, String> entry : loginDict.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.commit();
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
            vkUserRequest.setRequestListener(userInfoRequestListener);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vkUserRequest != null) {
            vkUserRequest.cancel();
        }
    }
}
