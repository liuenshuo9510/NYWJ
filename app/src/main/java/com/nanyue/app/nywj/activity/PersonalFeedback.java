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
import android.widget.Toast;

import com.nanyue.app.nywj.R;

import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalFeedback extends AppCompatActivity implements View.OnClickListener{
    private ImageView back, done;
    private EditText question;
    private SharedPreferences sharedPreferences;
    private MyHandler myHandler = new MyHandler(this);
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_feedback);

        initView();
        initClickListener();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        done = (ImageView) findViewById(R.id.done);
        question = (EditText) findViewById(R.id.question);
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
                if (question.getText().toString().equals("")){
                    Toast.makeText(this, "输入不能为空", Toast.LENGTH_LONG).show();
                } else {
                    changePassword(question.getText().toString());
                }
                break;
        }
    }

    private void changePassword(final String question) {
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
                            .add("feedback", question)
                            .build();
                    Request build = new Request.Builder()
                            .url("http://nouse.gzkuaiyi.com:9999/app/member/updateFeedback")
                            .header("cookie", "JSESSIONID="+sid)
                            .header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Mobile Safari/537.36")
                            .post(body)
                            .build();
                    Response response = okHttpClient.newCall(build).execute();

                    String result = response.body().string();
                    Object resultObject = com.alibaba.fastjson.JSONObject.parse(result);
                    Map<String, Object> resultMap = (Map<String, Object>) resultObject;
                    String msg = resultMap.get("msg").toString();

                    if (msg.equals("修改个人反馈成功")) {
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
        private WeakReference<PersonalFeedback> mActivity;

        public MyHandler(PersonalFeedback activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(mActivity.get(), "提交失败", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mActivity.get(), "提交成功", Toast.LENGTH_LONG).show();
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
