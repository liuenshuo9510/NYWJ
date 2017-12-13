package com.nanyue.app.nywj.activity;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.utils.Sha1;

import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalPasswordEdit extends AppCompatActivity implements View.OnClickListener{

    private ImageView back, done;
    private EditText oldPass, newPass, correctPass;
    private SharedPreferences sharedPreferences;
    private MyHandler myHandler = new MyHandler(this);
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_password_edit);

        initView();
        initClickListener();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        done = (ImageView) findViewById(R.id.done);
        oldPass = (EditText) findViewById(R.id.old_password);
        newPass = (EditText) findViewById(R.id.new_password);
        correctPass = (EditText) findViewById(R.id.correct_password);
    }

    private void initClickListener() {
        back.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.done:
                sharedPreferences = getSharedPreferences("check", MODE_PRIVATE);
                String oldPassString = oldPass.getText().toString();
                String newPassString = newPass.getText().toString();
                String confirmPass = correctPass.getText().toString();

                if (!newPassString.equals(confirmPass)) {
                    Toast.makeText(this, "两次密码输入不一致，请重新输入", Toast.LENGTH_LONG).show();
                }
                else if (oldPassString.equals("") || newPassString.equals("") || confirmPass.equals("")){
                    Toast.makeText(this, "输入不能为空", Toast.LENGTH_LONG).show();
                }
                else if (!oldPassString.equals(sharedPreferences.getString("pass", ""))) {
                    Toast.makeText(this, "原密码不正确，请重新输入", Toast.LENGTH_LONG).show();
                }
                else if (newPassString.length() < 6 || newPassString.length() > 20) {
                    Toast.makeText(this, "新密码位数不正确，请重新输入", Toast.LENGTH_LONG).show();
                }
                else {
                    changePassword(oldPass.getText().toString(), newPass.getText().toString());
                }
                break;
        }
    }

    private void changePassword(final String oldPass, final String newPass) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sharedPreferences = getSharedPreferences("check", MODE_PRIVATE);
                    String uid = sharedPreferences.getString("uid", "");
                    String sid = sharedPreferences.getString("sid", "");
                    okHttpClient = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("uid", uid)
                            .add("password", oldPass)
                            .add("newpassword", newPass)
                            .build();
                    Request build = new Request.Builder()
                            .url("http://nouse.gzkuaiyi.com:9999/app/member/updatePassword")
                            .header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Mobile Safari/537.36")
                            .header("cookie", "JSESSIONID="+sid)
                            .post(body)
                            .build();
                    Response response = okHttpClient.newCall(build).execute();

                    String result = response.body().string();

                    Object resultObject = com.alibaba.fastjson.JSONObject.parse(result);
                    Map<String, Object> resultMap = (Map<String, Object>) resultObject;
                    String msg = resultMap.get("msg").toString();

                    if (msg.equals("修改密码成功")) {
                        sharedPreferences = getSharedPreferences("check", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("pass", newPass);
                        editor.apply();

                        Message message = new Message();
                        message.what = 0;
                        myHandler.sendMessage(message);

                        finish();
                    } else {
                        Message message = new Message();
                        message.what = 1;
                        myHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    Log.e("passwordEditError", e.toString());
                    Message message = new Message();
                    message.what = 1;
                    myHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private static class MyHandler extends Handler {
        private WeakReference<PersonalPasswordEdit> mActivity;

        public MyHandler(PersonalPasswordEdit activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(mActivity.get(), "更改密码失败", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mActivity.get(), "更改密码成功", Toast.LENGTH_LONG).show();
            }
        }
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
