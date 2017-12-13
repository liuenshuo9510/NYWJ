package com.nanyue.app.nywj.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.adapter.CourseListAdapter;
import com.nanyue.app.nywj.bean.CourseListBean;
import com.nanyue.app.nywj.fragment.CourseFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jzvd.JZVideoPlayer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoList extends AppCompatActivity implements View.OnClickListener{

    private ListView listView;
    private ImageView back;
    private CourseListAdapter courseListAdapter;
    private ArrayList<String> urls = new ArrayList<String>();
    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<String> thumbs = new ArrayList<String>();
    private VideoList.MyHandler myHandler = new VideoList.MyHandler(this);

    private static class MyHandler extends Handler {

        private WeakReference<VideoList> weakReference;

        public MyHandler(VideoList courseFragment) {
            this.weakReference = new WeakReference<VideoList>(courseFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                VideoList cf = weakReference.get();
                cf.courseListAdapter = new CourseListAdapter(cf, cf.urls, cf.titles, cf.thumbs);
                cf.listView.setAdapter(cf.courseListAdapter);
            } else {
                Toast.makeText(weakReference.get(), "网络错误", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initView();
        initListView();
        getListViewData();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.course_list);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
        }
    }

    private void initListView() {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                JZVideoPlayer.onScrollReleaseAllVideos(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
    }

    private void getListViewData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://nouse.gzkuaiyi.com:9999/app/list-7")
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    String res = response.body().string();
                    Object resObject = JSONObject.parse(res);
                    Map<String, Object> resMap = (Map<String, Object>) resObject;

                    if (resMap.get("data") != null) {
                        String data = resMap.get("data").toString();
                        List<CourseListBean> list = JSONArray.parseArray(data, CourseListBean.class);
                        for (CourseListBean courseListBean : list) {
                            urls.add(courseListBean.getAttach());
                            titles.add(courseListBean.getTitle());
                            thumbs.add(courseListBean.getImage());
                        }
                        Message message = new Message();
                        message.what = 0;
                        myHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.e("e", e.toString());
                    Message message = new Message();
                    message.what = 1;
                    myHandler.sendMessage(message);
                }
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myHandler != null) {
            if (myHandler.hasMessages(0)) {
                myHandler.removeMessages(0);
            }
            if (myHandler.hasMessages(1)) {
                myHandler.removeMessages(1);
            }
        }
    }

    @Override
    public void onPause() {
        JZVideoPlayer.releaseAllVideos();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}
