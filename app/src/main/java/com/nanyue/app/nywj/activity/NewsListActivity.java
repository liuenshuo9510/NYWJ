package com.nanyue.app.nywj.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.adapter.NewsListAdapter;
import com.nanyue.app.nywj.bean.NewsListBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsListActivity extends AppCompatActivity implements View.OnClickListener, OnLoadmoreListener, OnRefreshListener{

    private ListView listView;
    private ImageView back;
    private TextView textView;
    private RefreshLayout refreshLayout;
    private NewsListAdapter newsListAdapter;
    private ArrayList<NewsListBean> arrayList = new ArrayList<>();
    private OkHttpClient okHttpClient;
    private int tag;
    private String title;
    private MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<NewsListActivity> mActivity;

        public MyHandler(NewsListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            NewsListActivity ac = mActivity.get();
            if (msg.what == 0) {
                ac.newsListAdapter = new NewsListAdapter(ac, ac.arrayList);
                ac.listView.setAdapter(ac.newsListAdapter);
            } else {
                Toast.makeText(ac, "网络错误", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newslist);

        Intent intent = getIntent();
        tag = intent.getIntExtra("tag", 0);
        title = intent.getStringExtra("title");

        initView();
        initListView();
        getListViewData();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.news_list);
        back = (ImageView) findViewById(R.id.back);
        textView = (TextView) findViewById(R.id.title);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

        textView.setText(title);

        back.setOnClickListener(this);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
    }

    private void initListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsListBean newsListBean = arrayList.get(position);
                String newsId = newsListBean.getId();
                Intent intent = new Intent(NewsListActivity.this, NewsDetailActivity.class);
                intent.putExtra("newsId", newsId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private void getListViewData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://nouse.gzkuaiyi.com:9999/app/list-" + tag)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    String res = response.body().string();
                    Object resObject = JSONObject.parseObject(res);
                    Map<String, Object> resMap = (Map<String, Object>) resObject;
                    if (resMap.get("data") != null) {
                        String data = resMap.get("data").toString();
                        arrayList = (ArrayList<NewsListBean>) JSONArray.parseArray(data, NewsListBean.class);
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
    public void onRefresh(RefreshLayout refreshlayout) {
        getListViewData();
        refreshlayout.finishRefresh(2000);
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        newsListAdapter.notifyDataSetChanged();
        refreshlayout.finishLoadmore();
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
