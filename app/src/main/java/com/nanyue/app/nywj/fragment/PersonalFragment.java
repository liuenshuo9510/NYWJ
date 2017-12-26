package com.nanyue.app.nywj.fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.nanyue.app.nywj.App;
import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.activity.MainActivity;
import com.nanyue.app.nywj.activity.PersonalFeedback;
import com.nanyue.app.nywj.activity.PersonalNameEdit;
import com.nanyue.app.nywj.activity.PersonalPasswordEdit;
import com.nanyue.app.nywj.activity.PersonalSignEdit;
import com.nanyue.app.nywj.okhttp.HttpConstants;
import com.nanyue.app.nywj.okhttp.RequestCenter;
import com.nanyue.app.nywj.okhttp.bean.PersonalInfoBean;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataListener;
import com.nanyue.app.nywj.utils.GetPathByUri;
import com.nanyue.app.nywj.view.CircleImageView;

import java.io.File;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.data;

public class PersonalFragment extends Fragment implements View.OnClickListener{

    private RelativeLayout changePassword, nickname, briefIntroduction, problemFeedback;
    private TextView briefIntroductionView, nicknameView;
    private SharedPreferences sharedPreferences;
    private CircleImageView circleImageView;
    private Uri uri;

    public final static int ALBUM_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        initView(view);
        initClickListener();
        getData();
        return view;
    }

    @Override
    public void onStart() {
        sharedPreferences = getActivity().getSharedPreferences("personal", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        if (!name.equals("")) {
            nicknameView.setText(name);
        }
        String sign = sharedPreferences.getString("sign", "");
        if (!sign.equals("")) {
            briefIntroductionView.setText(sign);
        }
        super.onStart();
    }

    private void initView(View view) {
        changePassword = (RelativeLayout) view.findViewById(R.id.change_password);
        nickname = (RelativeLayout) view.findViewById(R.id.nickname);
        briefIntroduction = (RelativeLayout) view.findViewById(R.id.brief_introduction);
        problemFeedback = (RelativeLayout) view.findViewById(R.id.problem_feedback);

        briefIntroductionView = (TextView) view.findViewById(R.id.brief_introduction_view);
        nicknameView = (TextView) view.findViewById(R.id.nickname_view);

        circleImageView = (CircleImageView) view.findViewById(R.id.personal_picture);
    }

    private void initClickListener() {
        changePassword.setOnClickListener(this);
        nickname.setOnClickListener(this);
        briefIntroduction.setOnClickListener(this);
        problemFeedback.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_password:
                Intent intent1 = new Intent(getActivity(), PersonalPasswordEdit.class);
                getActivity().startActivity(intent1);
                break;
            case R.id.nickname:
                Intent intent2 = new Intent(getActivity(), PersonalNameEdit.class);
                getActivity().startActivity(intent2);
                break;
            case R.id.brief_introduction:
                Intent intent3 = new Intent(getActivity(), PersonalSignEdit.class);
                getActivity().startActivity(intent3);
                break;
            case R.id.problem_feedback:
                Intent intent4 = new Intent(getActivity(), PersonalFeedback.class);
                getActivity().startActivity(intent4);
                break;
            case R.id.personal_picture:
                if (Build.VERSION.SDK_INT  > 19) {
                    Intent intent5 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent5.setType("image/*");
                    startActivityForResult(intent5, ALBUM_REQUEST_CODE);
                } else {
                    Intent intent5 = new Intent(Intent.ACTION_GET_CONTENT);
                    intent5.setType("image/*");
                    startActivityForResult(intent5, ALBUM_REQUEST_CODE);
                }
        }
    }

    public void getData() {
        sharedPreferences = getActivity().getSharedPreferences("check", Context.MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", "");
        RequestCenter.personalInfoRequest(uid, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                PersonalInfoBean personalInfoBean = (PersonalInfoBean) responseObj;
                String name = personalInfoBean.getUser().getNickname();
                nicknameView.setText(name);
                sharedPreferences = getActivity().getSharedPreferences("personal", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", name);
                editor.apply();

                String img = personalInfoBean.getUser().getHead();
                Glide.with(getActivity()).load(HttpConstants.ROOT + img).fitCenter().into(circleImageView);
            }

            @Override
            public void onFailure(Object reasonObj) {
                Toast.makeText(getActivity(), "加载个人信息失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PersonalFragment.ALBUM_REQUEST_CODE) {
            try {
                uri = data.getData();
                File file = new File(getPath(uri));
                sharedPreferences = getActivity().getSharedPreferences("check", Context.MODE_PRIVATE);
                String uid = sharedPreferences.getString("uid", "");
                String sid = sharedPreferences.getString("sid", "");
                postPicture(file, uid, sid);
            } catch (Exception e) {
                Log.e("eee", e.toString());
            }
        }
    }

    private String getPath(Uri uri) {
        String path = "";
        if (Build.VERSION.SDK_INT  >= 19) {
            path = GetPathByUri.getPath(getActivity(),uri);
        } else {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
                cursor.close();
            }
        }
        return path;
    }

    private void postPicture(final File file, final String uid, final String sid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = getFileRequest(HttpConstants.ROOT_URL + "/member/updateHead", file, uid, sid);
                    Response response = okHttpClient.newCall(request).execute();
                    String res = response.body().string();
                    Object resObject = JSONObject.parse(res);
                    Map<String, Object> resMap = (Map<String, Object>) resObject;

                    if (resMap.get("msg").toString().equals("修改个人头像成功")) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(getActivity()).load(uri).fitCenter().into(circleImageView);
                                Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("eee", e.toString());
                }
            }
        }).start();
    }

    public Request getFileRequest(String url, File file, String uid, String sid){
        MultipartBody.Builder builder=  new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("uid", uid);
        builder.addPart( Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"1.png\""), RequestBody.create(MediaType.parse("image/png"),file));
        RequestBody body = builder.build();
        return new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Mobile Safari/537.36")
                .header("cookie", "JSESSIONID="+sid)
                .post(body)
                .build();

    }

}