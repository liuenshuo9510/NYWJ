package com.nanyue.app.nywj.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nanyue.app.nywj.activity.NewsDetailActivity;
import com.nanyue.app.nywj.activity.NewsListActivity;
import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.activity.VideoList;
import com.nanyue.app.nywj.adapter.NewsListAdapter;
import com.nanyue.app.nywj.okhttp.HttpConstants;
import com.nanyue.app.nywj.okhttp.RequestCenter;
import com.nanyue.app.nywj.okhttp.bean.NewsListBean;
import com.nanyue.app.nywj.okhttp.exception.OkHttpException;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataListener;
import com.nanyue.app.nywj.okhttp.response.CommonJsonCallback;
import com.nanyue.app.nywj.view.ImageAndText;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Arrays;

import cn.bingoogolapple.bgabanner.BGABanner;


public class HomeFragment extends Fragment implements View.OnClickListener, OnRefreshListener{

    private BGABanner banner;
    private ListView listView;
    private NewsListAdapter newsListAdapter;
    private ScrollView scrollView;
    private RefreshLayout refreshLayout;
    private ImageAndText studyGround, myAction, myStory, studySuccess, teachAssist, microVideo, microTalk, more;
    private ArrayList<NewsListBean> arrayList = new ArrayList<>();
    private ArrayList<NewsListBean> bannerList = new ArrayList<>();

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
                tag = 0;
                title = "学习成才";
                break;
            case R.id.teach_assist:
                tag = 0;
                title = "教育辅导";
                break;
            case R.id.micro_video:
                Intent intent = new Intent(getActivity(), VideoList.class);
                startActivity(intent);
                break;
            case R.id.micro_talk:
                tag = 0;
                title = "微互动";
                break;
            case R.id.more:
                tag = 0;
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
        RequestCenter.newsListRequest(11, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                bannerList = (ArrayList<NewsListBean>) responseObj;
                initBanner();
            }

            @Override
            public void onFailure(Object reasonObj) {
                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 初始化轮播图
     */
    private void initBanner() {
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
        banner.setData(Arrays.asList(HttpConstants.ROOT + bannerList.get(0).getImage(),
                HttpConstants.ROOT + bannerList.get(1).getImage(),
                HttpConstants.ROOT + bannerList.get(2).getImage()),
                Arrays.asList(bannerList.get(0).getTitle(),
                        bannerList.get(1).getTitle(),
                        bannerList.get(2).getTitle()));
        banner.setDelegate(new BGABanner.Delegate<ImageView, String>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("newsId", bannerList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void getListviewData() {
        RequestCenter.homeNewsRequest(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                arrayList = (ArrayList<NewsListBean>) responseObj;
                newsListAdapter = new NewsListAdapter(getActivity(),arrayList);
                listView.setAdapter(newsListAdapter);
                setListViewHeightBasedOnChildren(listView);
            }

            @Override
            public void onFailure(Object reasonObj) {
                OkHttpException okHttpException = (OkHttpException) reasonObj;
                if (!okHttpException.getEcode().equals(CommonJsonCallback.EMPTY_ERROR)) {
                    Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_LONG).show();
                }
            }
        });
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
    }
}
