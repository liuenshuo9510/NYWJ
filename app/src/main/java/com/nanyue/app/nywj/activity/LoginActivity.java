package com.nanyue.app.nywj.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
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
import com.nanyue.app.nywj.utils.Sha1;

import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    public String error;

    private SharedPreferences sharedPreferences;
    private EditText username, password;
    private Button login;
    private OkHttpClient okHttpClient;
    private MyHandler myHandler = new MyHandler(LoginActivity.this);

    private static class MyHandler extends Handler {
        private WeakReference<LoginActivity> mActivity;

        public MyHandler(LoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(mActivity.get(), mActivity.get().error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mActivity.get(), "网络错误", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verifyStoragePermissions();

        sharedPreferences = getSharedPreferences("check", MODE_PRIVATE);
        boolean firstload = sharedPreferences.getBoolean("firstload", true);

/*        if (!firstload) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/

        initView();
        username.requestFocus();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    okHttpClient = new OkHttpClient();
                    Request build = new Request.Builder()
                            .url("http://nouse.gzkuaiyi.com:9999/app/auth?userName="+name+"&password="+pass+"&serialNo="+imei)
                            .header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Mobile Safari/537.36")
                            .build();
                    Response response = okHttpClient.newCall(build).execute();

                    String result = response.body().string();

                    //Log.e("result", result);

                    Object resultObject = JSONObject.parse(result);
                    Map<String, Object> resultMap = (Map) resultObject;

                    String data = resultMap.get("data").toString();
                    Object dataObject = JSONObject.parse(data);
                    Map<String, Object> dataMap = (Map) dataObject;

                    String status = dataMap.get("status").toString();
                    Object statusObject = JSONObject.parse(status);
                    Map<String, Object> statusMap = (Map) statusObject;

                    int i = Integer.parseInt(statusMap.get("succeed").toString());
                    //Log.e("i", i+"");
                    if (i == 0) {
                        String sessionData = dataMap.get("data").toString();
                        Object sessionDataObject = JSONObject.parse(sessionData);
                        Map<String, Object> sessionDataMap = (Map) sessionDataObject;

                        String session = sessionDataMap.get("session").toString();
                        Object sessionObject = JSONObject.parse(session);
                        Map<String, Object> sessionMap = (Map<String, Object>) sessionObject;

                        String uid = sessionMap.get("uid").toString();
                        Log.e("uid", uid);
                        String sid = sessionMap.get("sid").toString();
                        Log.e("sid", sid);
                        succeed(uid, sid, pass);
                    } else {
                        error = statusMap.get("error_desc").toString();
                        Message message = new Message();
                        message.what = 1;
                        myHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.e("logInError", e.toString());
                    Message message = new Message();
                    message.what = 0;
                    myHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private void succeed(String uid, String sid, String pass) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstload", false);
        editor.putString("uid", uid);
        editor.putString("sid", sid);
        String sha1 = Sha1.getSha1(pass);
        editor.putString("pass", sha1);
        editor.apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
        if (myHandler != null) {
            if (myHandler.hasMessages(1)) {
                myHandler.removeMessages(1);
            }
            if (myHandler.hasMessages(0)) {
                myHandler.removeMessages(0);
            }
        }
    }
}
