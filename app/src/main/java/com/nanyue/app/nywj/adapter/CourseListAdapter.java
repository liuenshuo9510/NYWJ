package com.nanyue.app.nywj.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nanyue.app.nywj.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.*;

public class CourseListAdapter extends BaseAdapter {

    public static final String TAG = "JiaoZiVideoPlayer";

    private Context context;

    private ArrayList<String> videoUrls;
    private ArrayList<String> videoTitles;
    private ArrayList<String> videoThumbs;

    public CourseListAdapter(Context context, ArrayList<String> videoUrls, ArrayList<String> videoTitles, ArrayList<String> videoThumbs) {
        this.context = context;
        this.videoUrls = videoUrls;
        this.videoTitles = videoTitles;
        this.videoThumbs = videoThumbs;
    }

    @Override
    public int getCount() {
        return videoUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.listview_courselist, null);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.jzVideoPlayer = (JZVideoPlayerStandard) convertView.findViewById(R.id.videoplayer);
        viewHolder.jzVideoPlayer.setUp(
                "http://nouse.gzkuaiyi.com:9999" + videoUrls.get(position),
                //"http://jzvd.nathen.cn/6ea7357bc3fa4658b29b7933ba575008/fbbba953374248eb913cb1408dc61d85-5287d2089db37e62345123a1be272f8b.mp4",
                JZVideoPlayer.SCREEN_WINDOW_LIST,
                videoTitles.get(position));
        Glide.with(convertView.getContext())
                .load("http://nouse.gzkuaiyi.com:9999" + videoThumbs.get(position))
                .into(viewHolder.jzVideoPlayer.thumbImageView);
        viewHolder.jzVideoPlayer.positionInList = position;
        return convertView;
    }

    private class ViewHolder {
        JZVideoPlayerStandard jzVideoPlayer;
    }
}
