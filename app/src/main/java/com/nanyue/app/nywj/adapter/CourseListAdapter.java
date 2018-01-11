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
import com.nanyue.app.nywj.okhttp.HttpConstants;

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
                HttpConstants.ROOT + videoUrls.get(position),
                JZVideoPlayer.SCREEN_WINDOW_LIST,
                videoTitles.get(position));
        Glide.with(convertView.getContext())
                .load(HttpConstants.ROOT + videoThumbs.get(position))
                .error(R.drawable.banner2)
                .centerCrop()
                .into(viewHolder.jzVideoPlayer.thumbImageView);
        viewHolder.jzVideoPlayer.positionInList = position;
        return convertView;
    }

    private class ViewHolder {
        JZVideoPlayerStandard jzVideoPlayer;
    }
}
