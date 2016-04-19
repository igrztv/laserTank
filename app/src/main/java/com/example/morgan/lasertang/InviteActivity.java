package com.example.morgan.lasertang;

import android.content.Intent;
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
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InviteActivity extends AppCompatActivity {
    private static final int VK_WALL_PERMISSION = 8192;
    private static final String WALL_INVITATION = "Привет, я тестирую lazerTank" ;
    private static final String FB_PUBLUSH_PERMISSION = "publish_actions";
    private CallbackManager fbCallbackManager;
    private int permissionsFlag = 0;

    private static final String[] vkScope = new String[]{
            VKScope.WALL,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        Button vk_button = (Button) findViewById(R.id.invite_vk);
        Button fb_button = (Button) findViewById(R.id.invite_fb);

        vk_button.setOnClickListener(inviteWithVk);
        fb_button.setOnClickListener(inviteWithFb);

        fbCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(fbCallbackManager, fbCallback);

    }

    FacebookCallback<LoginResult> fbCallback= new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Map<String, String> userInfo = new HashMap<String, String>();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken == null) {
                showErrorToast();
                return;
            }
            publishWithFb();
        }

        @Override
        public void onCancel() {
            showErrorToast();
        }

        @Override
        public void onError(FacebookException exception) {
            showErrorToast();
        }
    };

    View.OnClickListener inviteWithVk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //check auth token
            if (VKAccessToken.currentToken() != null) {
                VKRequest request = new VKRequest("account.getAppPermissions");
                request.executeSyncWithListener(vkCheckPermissionsListener);
            }
            if (VKAccessToken.currentToken() == null || !canPostToVk()) {
                VKSdk.login(InviteActivity.this, vkScope);
            } else {
                postToVkWall();
            }
        }
    };

    private void postToVkWall() {
        VKRequest request = VKApi.wall().post(
                VKParameters.from(VKApiConst.USER_ID,
                        "-1",
                        VKApiConst.MESSAGE,
                        WALL_INVITATION));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                showSuccessToast();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(InviteActivity.this,
                        error.toString(), Toast.LENGTH_LONG).show();
                showErrorToast();
            }
        });
    }

    private boolean canPostToVk() {
        return (permissionsFlag & VK_WALL_PERMISSION) != 0;
    }

    View.OnClickListener inviteWithFb = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AccessToken fbToken = AccessToken.getCurrentAccessToken();
            if (fbToken == null || !fbToken.getPermissions().contains(FB_PUBLUSH_PERMISSION ) ) {
                LoginManager.getInstance().logInWithPublishPermissions(
                        InviteActivity.this,
                        Arrays.asList(FB_PUBLUSH_PERMISSION )
                );
            } else {
                publishWithFb();
            }

        }
    };

    private void publishWithFb() {
        JSONObject params = new JSONObject();
        try {
            params.put("message", WALL_INVITATION);
        } catch (JSONException e) {
            showErrorToast();
            return;
        }
        GraphRequest request = GraphRequest.newPostRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                params,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() == null) showSuccessToast();
                        else {
                            Toast.makeText(InviteActivity.this,
                                    response.getError().toString(), Toast.LENGTH_LONG).show();
                            showErrorToast();

                        }
                    }

                });

        request.executeAsync();
    }

    private void showErrorToast() {
        Toast.makeText(InviteActivity.this,
                "Ого, что-то пошло не так :(", Toast.LENGTH_LONG).show();
    }
    private void showSuccessToast() {
        Toast.makeText(InviteActivity.this,
                "Победа, мы на вашей стене)", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                res.save();
                postToVkWall();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(InviteActivity.this,
                        error.toString(), Toast.LENGTH_LONG).show();
                showErrorToast();
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
            fbCallbackManager.onActivityResult(requestCode,
                    resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    VKRequest.VKRequestListener vkCheckPermissionsListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            try {
                permissionsFlag = response.json.getInt("response");
            }
            catch (JSONException e) {
                //do nothing: consider we don't have  enough rights
            }
        }

        @Override
        public void onError(VKError error) {
            //do nothing: consider we don't have  enough rights
        }
    };


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
    }
}
