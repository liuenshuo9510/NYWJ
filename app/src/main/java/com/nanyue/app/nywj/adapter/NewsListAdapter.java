package com.nanyue.app.nywj.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.okhttp.HttpConstants;
import com.nanyue.app.nywj.okhttp.bean.NewsListBean;

import java.util.ArrayList;


public class NewsListAdapter extends BaseAdapter {

    private ArrayList<NewsListBean> arrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    public NewsListAdapter(Context context, ArrayList<NewsListBean> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_newslist, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img);
        if (TextUtils.isEmpty(arrayList.get(position).getImage())) {
            Glide.with(context)
                    .load(R.drawable.banner)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(HttpConstants.ROOT + arrayList.get(position).getImage())
                    .error(R.drawable.banner)
                    .into(imageView);
        }
        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(arrayList.get(position).getTitle());

        TextView date = (TextView) convertView.findViewById(R.id.date);

        date.setText(arrayList.get(position).getPublishDate());

        return convertView;
    }
}
