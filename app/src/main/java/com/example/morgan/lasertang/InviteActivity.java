package com.example.morgan.lasertang;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.facebook.AccessToken;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

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


public class InviteActivity extends BaseSocialActivity {
    private static final int VK_WALL_PERMISSION = 8192;
    private static final String FB_PUBLUSH_PERMISSION = "publish_actions";
    private int permissionsFlag = 0;

    private static final String[] vkScope = new String[]{
            VKScope.WALL,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        AppCompatButton vk_button = (AppCompatButton) findViewById(R.id.invite_vk);
        AppCompatButton fb_button = (AppCompatButton) findViewById(R.id.invite_fb);

        vk_button.setOnClickListener(inviteWithVk);
        fb_button.setOnClickListener(inviteWithFb);

        ColorStateList socialColorStateList = new ColorStateList(new int [][]{new int[0]},
                new int[]{ getResources().getColor(R.color.social_btn)});
        vk_button.setSupportBackgroundTintList(socialColorStateList);
        fb_button.setSupportBackgroundTintList(socialColorStateList);
        vkCallback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                res.save();
                postToVkWall();
            }

            @Override
            public void onError(VKError error) {
                showErrorText();
            }
        };

        vkRequestListener = new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                requestInProgressDialog.dismiss();
                showSuccessToast();
                vkUserRequest = null;
            }

            @Override
            public void onError(VKError error) {
                requestInProgressDialog.dismiss();
                showErrorText();
                vkUserRequest = null;
            }
        };
    }

    @Override
    public void processFbLoginSuccess() {
        publishWithFb();
    }

    @Override
    public void showErrorText() {
        showMessage(R.string.invite_error);
    }

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
                        getResources().getString(R.string.wall_invitation)));
        requestInProgressDialog.show();
        request.executeWithListener(vkRequestListener);
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
    @Override
    public void prepareDialog() {
        requestInProgressDialog.setIndeterminate(true);
        requestInProgressDialog.setMessage(getResources().getString(R.string.invite_in_progress_msg));
    }

    private void publishWithFb() {
        JSONObject params = new JSONObject();
        try {
            params.put("message", getResources().getString(R.string.wall_invitation));
        } catch (JSONException e) {
            showErrorText();
            return;
        }
        requestInProgressDialog.show();
        GraphRequest request = GraphRequest.newPostRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                params,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        requestInProgressDialog.dismiss();
                        if (response.getError() == null) showSuccessToast();
                        else {
                            showErrorText();
                        }
                    }

                });

        request.executeAsync();
    }

    private void showSuccessToast() {
        showMessage(R.string.invite_success);
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
