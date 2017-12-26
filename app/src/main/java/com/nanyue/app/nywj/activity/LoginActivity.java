package com.nanyue.app.nywj.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.okhttp.RequestCenter;
import com.nanyue.app.nywj.okhttp.bean.LogInBean;
import com.nanyue.app.nywj.okhttp.exception.OkHttpException;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataListener;
import com.nanyue.app.nywj.okhttp.response.CommonJsonCallback;

import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences sharedPreferences;
    private EditText username, password;
    private Button login;
    private boolean firstLoad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("check", MODE_PRIVATE);
        firstLoad = sharedPreferences.getBoolean("firstLoad", true);

        if (!firstLoad) {
            String name, pass, imei;
            name = sharedPreferences.getString("name", "");
            pass = sharedPreferences.getString("pass", "");
            imei = sharedPreferences.getString("imei", "");
            logIn(name, pass, imei);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            verifyStoragePermissions();
            initView();
            username.requestFocus();
        }
    }

    private void initView() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            String imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();

            if (username.getText().toString().equals("") || username.getText().toString().equals("")) {
                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();
            } else {
                String name = username.getText().toString();
                String pass = password.getText().toString();
                logIn(name, pass, imei);
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "获取本机识别码失败，请手动更改权限", Toast.LENGTH_LONG).show();
        }
    }

    private void logIn(final String name, final String pass, final String imei) {
        RequestCenter.logInRequest(name, pass, imei, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                LogInBean logInBean = (LogInBean) responseObj;
                if (logInBean.getStatus().getError_code() != 0) {
                    String error = logInBean.getStatus().getError_desc();
                    if (firstLoad) {
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", name);
                    editor.putString("pass", pass);
                    editor.putString("imei", imei);
                    editor.putString("uid", logInBean.getData().getSession().getUid());
                    editor.putString("sid", logInBean.getData().getSession().getSid());
                    editor.putBoolean("firstLoad", false);
                    editor.apply();
                    if (firstLoad) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Object reasonObj) {
                OkHttpException okHttpException = (OkHttpException) reasonObj;
                if (!okHttpException.getEcode().equals(CommonJsonCallback.EMPTY_ERROR)) {
                    Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void verifyStoragePermissions() {
        String[] permissions = new String[]{
                "android.permission.READ_PHONE_STATE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.MOUNT_UNMOUNT_FILESYSTEMS"
        };
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, permissions, new PermissionsResultAction() {
            @Override
            public void onGranted() {}

            @Override
            public void onDenied(String permission) {}
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
