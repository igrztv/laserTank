package com.example.morgan.lasertang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseSocialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppCompatButton vk_button = (AppCompatButton) findViewById(R.id.login_vk);
        AppCompatButton fb_button = (AppCompatButton) findViewById(R.id.login_fb);
        AppCompatButton play_button = (AppCompatButton) findViewById(R.id.play_without_login);

        ColorStateList socialColorStateList = new ColorStateList(new int [][]{new int[0]},
                new int[]{ getResources().getColor(R.color.social_btn)});
        ColorStateList playColorStateList = new ColorStateList(new int [][]{new int[0]},
                new int[]{ getResources().getColor(R.color.play_btn)});
        vk_button.setSupportBackgroundTintList(socialColorStateList);
        fb_button.setSupportBackgroundTintList(socialColorStateList);
        play_button.setSupportBackgroundTintList(playColorStateList);

        vk_button.setOnClickListener(loginWithVk);
        fb_button.setOnClickListener(loginWithFb);
        play_button.setOnClickListener(goToPlay);

        vkRequestListener = new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                requestInProgressDialog.dismiss();
                Map<String, String> userInfo;
                try {
                    JSONArray responseArr = (JSONArray) response.json.get("response");
                    JSONObject user = responseArr.getJSONObject(0);
                    userInfo = userToHash(user);
                    //Toast.makeText(LoginActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                    saveLoginInfo("vk", userInfo);
                } catch (JSONException e) {
                    //DO NOTHING: - info is not necessary
                }
                goToPlayView();
                vkUserRequest = null;
            }
            @Override
            public void onError(VKError error) {
                requestInProgressDialog.dismiss();
                vkUserRequest = null;
                //DO NOTHING: info is not necessary
            }
        };

        vkCallback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                res.save();
                if (vkUserRequest != null) {
                    return;
                }
                vkUserRequest =  VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_max_orig"));
                requestInProgressDialog.show();
                vkUserRequest.executeWithListener(vkRequestListener);
            }

            @Override
            public void onError(VKError error) {
                showErrorText();
            }
        };

    }

    @Override
    public void processFbLoginSuccess() {
        fetchFbProfile();
    }

    @Override
    public void prepareDialog() {
        requestInProgressDialog.setIndeterminate(true);
        requestInProgressDialog.setMessage(getResources().getString(R.string.auth_in_progress_msg));
    }

    @Override
    public void showErrorText() {
        showMessage(R.string.auth_error);
    }
    View.OnClickListener loginWithVk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearSharedPreferences();
            VKSdk.login(LoginActivity.this);
        }
    };

    View.OnClickListener loginWithFb = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearSharedPreferences();
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

    private void clearSharedPreferences() {
        getSharedPreferences("login_fb", Context.MODE_PRIVATE).edit().clear().commit();
        getSharedPreferences("login_vk", Context.MODE_PRIVATE).edit().clear().commit();
    }


    private void fetchFbProfile() {
        requestInProgressDialog.show();
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        requestInProgressDialog.dismiss();
                        if (object == null) return;
                        try {
                            Map<String, String> userInfo = userToHash(object);
                            //Toast.makeText(LoginActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                            saveLoginInfo("fb", userInfo);
                            //access token is managed by fb class
                        } catch (JSONException e) {
                            //DO NOTHING: info is not  necessary
                        }
                        goToPlayView();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private HashMap<String, String> userToHash(JSONObject user) throws JSONException {
        HashMap<String, String> userInfo = new HashMap<String, String>();
        String fields[] =  {"first_name", "last_name"};
        for (String field: fields) {
            userInfo.put(field, user.getString(field));
        }
        String photo = null;
        try {//try fb
            photo = user.getJSONObject("picture").getJSONObject("data").getString("url");
        }
        catch (JSONException e) {
            //no photo;
        }
        try {//try vk
            photo = user.getString("photo_max_orig");
        }
        catch (JSONException e) {
            //no photo;
        }
        if (photo != null) {
            userInfo.put("photo", photo);
        }
        return userInfo;
    }


    private void goToPlayView() {
        Intent intent = new Intent(LoginActivity.this, InviteActivity.class);
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
}
