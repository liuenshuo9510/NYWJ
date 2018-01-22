package com.nanyue.app.nywj.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.okhttp.RequestCenter;
import com.nanyue.app.nywj.okhttp.bean.NewsDetailBean;
import com.nanyue.app.nywj.okhttp.exception.OkHttpException;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataListener;
import com.nanyue.app.nywj.okhttp.response.CommonJsonCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private WebView webView;
    private ImageView back;
    private String newsId;
    private String content;
    private static String title;
    private static String author;
    private static String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsdetail);

        Intent intent = getIntent();
        if (intent.getStringExtra("newsId") != null) {
            newsId = intent.getStringExtra("newsId");
        }

        initView();
        initWebView();
        getData();
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.webview);
        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(this);
    }

    private void initWebView() {

        //设置不用系统浏览器打开,直接显示在当前Webview
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
    }

    private void getData() {
        RequestCenter.newsDetailRequest(newsId, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                NewsDetailBean newsDetailBean = (NewsDetailBean) responseObj;
                content = newsDetailBean.getContent();
                title = newsDetailBean.getTitle();
                author = newsDetailBean.getAuthor();
                date = newsDetailBean.getPublishDate();
                String con = getNewContent(content);
                webView.loadDataWithBaseURL(null, con, "text/html", "utf-8", null);
            }

            @Override
            public void onFailure(OkHttpException reasonObj) {
                if (reasonObj.getError_message().equals(OkHttpException.NETWORK_ERROR)) {
                    Toast.makeText(NewsDetailActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(NewsDetailActivity.this, "获取文章详情失败", Toast.LENGTH_LONG).show();
                    Log.e(reasonObj.getError_message(), reasonObj.getError_detail());
                }
            }
        });
    }

    public static String getNewContent(String htmltext){
        try {
            Document doc = Jsoup.parse(htmltext);
            Elements elements = doc.getElementsByTag("img");
            for (Element element : elements) {
                element.attr("style","width: 100%; height: auto;");
                element.parent().attr("style", "");
            }

            Element element = doc.body();
            element.prepend("<font size=\"5\">" + title + "</font>" +
                    "<p>" + date + "&nbsp;&nbsp;" + author + "</p>");
            return doc.toString();
        } catch (Exception e) {
            Log.e("e", e.toString());
            return htmltext;
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }


}
