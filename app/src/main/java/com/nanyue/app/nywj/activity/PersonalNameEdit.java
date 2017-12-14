package com.nanyue.app.nywj.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.okhttp.HttpConstants;


import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalNameEdit extends AppCompatActivity implements View.OnClickListener{
    private ImageView back, done;
    private EditText name;
    private SharedPreferences sharedPreferences;

    private OkHttpClient okHttpClient;
    private MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<PersonalNameEdit> mActivity;

        public MyHandler(PersonalNameEdit activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(mActivity.get(), "更改昵称失败", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mActivity.get(), "更改昵称成功", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_name_edit);

        initView();
        initClickListener();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        done = (ImageView) findViewById(R.id.done);
        name = (EditText) findViewById(R.id.name);
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
                String nameString = name.getText().toString();
                if (nameString.equals("")){
                    Toast.makeText(this, "输入不能为空", Toast.LENGTH_LONG).show();
                }
                else if (nameString.length() > 8) {
                    Toast.makeText(this, "输入长度不能超过8", Toast.LENGTH_LONG).show();
                }
                else {
                    changeName(nameString);
                }
                break;
        }
    }

    private void changeName(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sharedPreferences = getSharedPreferences("check", MODE_PRIVATE);
                    String uid = sharedPreferences.getString("uid", "");
                    String sid = sharedPreferences.getString("sid", "");
                    //Log.e("uid", uid);
                    Log.e("sid", sid);
                    okHttpClient = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("uid", uid)
                            .add("nickname", name)
                            .build();
                    Request build = new Request.Builder()
                            .url(HttpConstants.ROOT_URL + "/member/updateProfile")
                            .header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Mobile Safari/537.36")
                            .header("cookie", "JSESSIONID="+sid)
                            .post(body)
                            .build();
                    Response response = okHttpClient.newCall(build).execute();

                    String result = response.body().string();
                    Log.e("result", result);

                    Object resultObject = com.alibaba.fastjson.JSONObject.parse(result);
                    Map<String, Object> resultMap = (Map<String, Object>) resultObject;
                    String msg = resultMap.get("msg").toString();

                    if (msg.equals("修改个人昵称成功")) {
                        sharedPreferences = getSharedPreferences("personal", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", name);
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
                    Log.e("passwordNameError", e.toString());
                    Message message = new Message();
                    message.what = 1;
                    myHandler.sendMessage(message);
                }
            }
        }).start();
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
