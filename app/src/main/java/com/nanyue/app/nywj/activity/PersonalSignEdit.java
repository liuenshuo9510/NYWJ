package com.nanyue.app.nywj.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.EasyEditSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nanyue.app.nywj.R;

import okhttp3.OkHttpClient;

public class PersonalSignEdit extends AppCompatActivity implements View.OnClickListener{
    private ImageView back, done;
    private EditText sign;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_sign_edit);

        initView();
        initClickListener();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        done = (ImageView) findViewById(R.id.done);
        sign = (EditText) findViewById(R.id.sign);
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
                String signString = sign.getText().toString();
                if (signString.equals("")){
                    Toast.makeText(this, "输入不能为空", Toast.LENGTH_LONG).show();
                }
                else if (signString.length() > 20) {
                    Toast.makeText(this, "输入长度不能超过20", Toast.LENGTH_LONG).show();
                }
                else {
                    sharedPreferences = getSharedPreferences("personal", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("sign", sign.getText().toString());
                    editor.apply();
                    finish();
                }
                break;
        }
    }
}
