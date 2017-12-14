package com.nanyue.app.nywj.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.adapter.CourseListAdapter;
import com.nanyue.app.nywj.okhttp.RequestCenter;
import com.nanyue.app.nywj.okhttp.bean.NewsListBean;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataListener;

import java.util.ArrayList;

import cn.jzvd.JZVideoPlayer;

public class CourseFragment extends Fragment {

    private ListView listView;
    private CourseListAdapter courseListAdapter;
    private ArrayList<String> urls = new ArrayList<String>();
    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<String> thumbs = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        initView(view);
        initListView();
        getListViewData();
        return view;
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.course_list);
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
        RequestCenter.newsListRequest(9, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                ArrayList<NewsListBean> arrayList = (ArrayList<NewsListBean>) responseObj;
                for (NewsListBean newsListBean : arrayList) {
                    urls.add(newsListBean.getAttach());
                    titles.add(newsListBean.getTitle());
                    thumbs.add(newsListBean.getImage());
                }
                courseListAdapter = new CourseListAdapter(getActivity(), urls, titles, thumbs);
                listView.setAdapter(courseListAdapter);
            }

            @Override
            public void onFailure(Object reasonObj) {
                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
