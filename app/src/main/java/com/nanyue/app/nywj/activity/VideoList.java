package com.nanyue.app.nywj.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.adapter.CourseListAdapter;
import com.nanyue.app.nywj.okhttp.RequestCenter;
import com.nanyue.app.nywj.okhttp.bean.NewsListBean;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataListener;

import java.util.ArrayList;

import cn.jzvd.JZVideoPlayer;

public class VideoList extends AppCompatActivity implements View.OnClickListener{

    private ListView listView;
    private ImageView back;
    private CourseListAdapter courseListAdapter;
    private ArrayList<String> urls = new ArrayList<String>();
    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<String> thumbs = new ArrayList<String>();

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
        RequestCenter.newsListRequest(7, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                ArrayList<NewsListBean> arrayList = (ArrayList<NewsListBean>) responseObj;
                for (NewsListBean newsListBean : arrayList) {
                    urls.add(newsListBean.getAttach());
                    titles.add(newsListBean.getTitle());
                    thumbs.add(newsListBean.getImage());
                }
                courseListAdapter = new CourseListAdapter(VideoList.this, urls, titles, thumbs);
                listView.setAdapter(courseListAdapter);
            }

            @Override
            public void onFailure(Object reasonObj) {
                Toast.makeText(VideoList.this, "网络错误", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
