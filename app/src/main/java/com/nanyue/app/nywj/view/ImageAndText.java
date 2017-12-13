package com.nanyue.app.nywj.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nanyue.app.nywj.R;

public class ImageAndText extends FrameLayout{

    private ImageView imageView;
    private TextView textView;
    private String title;
    private int imgResource;

    public ImageAndText(Context context) {
        this(context, null);
    }

    public ImageAndText(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ImageAndText(Context context, AttributeSet attributeSet, int def) {
        super(context, attributeSet, def);
        initTypedArray(context, attributeSet);
        initView(context);
    }

    private void initTypedArray(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ImageAndText);
        title = typedArray.getString(R.styleable.ImageAndText_title);
        imgResource = typedArray.getResourceId(R.styleable.ImageAndText_img_src, 0);
        typedArray.recycle();
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.viewgroup_homefragment, this);
        imageView = (ImageView) view.findViewById(R.id.img);
        textView = (TextView) view.findViewById(R.id.text);

        textView.setText(title);
        imageView.setImageResource(imgResource);
    }
}
