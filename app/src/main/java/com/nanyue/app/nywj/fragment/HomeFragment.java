package com.nanyue.app.nywj.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.nanyue.app.nywj.activity.NewsDetailActivity;
import com.nanyue.app.nywj.activity.NewsListActivity;
import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.adapter.NewsListAdapter;
import com.nanyue.app.nywj.bean.BannerBean;
import com.nanyue.app.nywj.bean.NewsListBean;
import com.nanyue.app.nywj.view.ImageAndText;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.bgabanner.BGABanner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeFragment extends Fragment implements View.OnClickListener, OnRefreshListener{

    private BGABanner banner;
    private ListView listView;
    private NewsListAdapter newsListAdapter;
    private ScrollView scrollView;
    private RefreshLayout refreshLayout;
    private ImageAndText studyGround, myAction, myStory, studySuccess, teachAssist, microVideo, microTalk, more;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ArrayList<NewsListBean> arrayList = new ArrayList<>();
    private ArrayList<BannerBean> bannerList = new ArrayList<>();

    private MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends Handler {

        private WeakReference<HomeFragment> weakReference;

        public MyHandler (HomeFragment homeFragment) {
            weakReference = new WeakReference<HomeFragment>(homeFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HomeFragment hf = weakReference.get();
            if (msg.what == 0) {
                hf.newsListAdapter = new NewsListAdapter(hf.getActivity(), hf.arrayList);
                hf.listView.setAdapter(hf.newsListAdapter);
                hf.setListViewHeightBasedOnChildren(hf.listView);
            }
            else if (msg.what == 1) {
                hf.initBanner();
            } else {
                Toast.makeText(hf.getActivity(), "网络错误", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        initListView();
        getListviewData();
        getBanner();
        setClickListener();
        return view;
    }

    private void initView(View view) {
        banner = (BGABanner) view.findViewById(R.id.banner);
        listView = (ListView) view.findViewById(R.id.news_list);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);

        studyGround = (ImageAndText) view.findViewById(R.id.study_ground);
        myAction = (ImageAndText) view.findViewById(R.id.my_action);
        myStory = (ImageAndText) view.findViewById(R.id.my_story);
        studySuccess = (ImageAndText) view.findViewById(R.id.study_success);
        teachAssist = (ImageAndText) view.findViewById(R.id.teach_assist);
        microVideo = (ImageAndText) view.findViewById(R.id.micro_video);
        microTalk = (ImageAndText) view.findViewById(R.id.micro_talk);
        more = (ImageAndText) view.findViewById(R.id.more);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        refreshLayout.setOnRefreshListener(this);

        refreshLayout.setEnableLoadmore(false);

    }

    private void initListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                String newsId = arrayList.get(position).getId();
                intent.putExtra("newsId", newsId);
                startActivity(intent);
            }
        });
    }

    private void setClickListener() {
        studyGround.setOnClickListener(this);
        myAction.setOnClickListener(this);
        myStory.setOnClickListener(this);
        studySuccess.setOnClickListener(this);
        teachAssist.setOnClickListener(this);
        microVideo.setOnClickListener(this);
        microTalk.setOnClickListener(this);
        more.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int tag = 0;
        String title = "";
        switch (v.getId()) {
            case R.id.study_ground:
                tag = 2;
                title = "学习园地";
                break;
            case R.id.my_action:
                tag = 3;
                title = "我在行动";
                break;
            case R.id.my_story:
                tag = 4;
                title = "我的故事";
                break;
            case R.id.study_success:
                tag = 5;
                title = "学习成才";
                break;
            case R.id.teach_assist:
                tag = 6;
                title = "教育辅导";
                break;
            case R.id.micro_video:
                tag = 7;
                title = "微视频";
                break;
            case R.id.micro_talk:
                tag = 8;
                title = "微互动";
                break;
            case R.id.more:
                tag = 9;
                title = "更多";
                break;
        }
        if (tag != 0) {
            Intent intent = new Intent(getActivity(), NewsListActivity.class);
            intent.putExtra("tag", tag);
            intent.putExtra("title", title);
            startActivity(intent);
        }
    }

    private void getBanner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("http://nouse.gzkuaiyi.com:9999/app/linklist-11-3")
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    String res = response.body().string();
                    Object resObject = JSONObject.parse(res);
                    Map<String, Object> resMap = (Map) resObject;
                    if (resMap.get("data") != null) {
                        String data = resMap.get("data").toString();
                        bannerList = (ArrayList<BannerBean>) JSONArray.parseArray(data, BannerBean.class);
                        Message message = new Message();
                        message.what = 1;
                        myHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.e("e", e.toString());
                    Message message = new Message();
                    message.what = 2;
                    myHandler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 初始化轮播图
     */
    private void initBanner() {
        final String url = "http://nouse.gzkuaiyi.com:9999";
        banner.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                Glide.with(getActivity())
                        .load(model)
                        .placeholder(R.drawable.banner1)
                        .error(R.drawable.banner1)
                        .centerCrop()
                        .dontAnimate()
                        .into(itemView);
            }
        });
        banner.setData(Arrays.asList(url + bannerList.get(0).getImage(),
                url + bannerList.get(1).getImage(),
                url + bannerList.get(2).getImage()),
                Arrays.asList("", "", ""));
        banner.setDelegate(new BGABanner.Delegate<ImageView, String>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("newsId", bannerList.get(position).getThref());
                startActivity(intent);
            }
        });
    }

    private void getListviewData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("http://nouse.gzkuaiyi.com:9999/app/recommend-article?appinMenu=1")
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    String res = response.body().string();
                    Object resObject = JSONObject.parse(res);
                    Map<String, Object> resMap = (Map<String, Object>)resObject;

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
                    message.what = 2;
                    myHandler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 动态调整listView的高度，添加到布局中
     */
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);

        scrollView.post(new Runnable() {
            @Override
            public void run() {

                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        getListviewData();
        getBanner();
        refreshlayout.finishRefresh(2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myHandler != null) {
            if (myHandler.hasMessages(1)) {
                myHandler.removeMessages(1);
            }
            if (myHandler.hasMessages(0)) {
                myHandler.removeMessages(0);
            }
            if (myHandler.hasMessages(2)) {
                myHandler.removeMessages(2);
            }
        }
    }
}
