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
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.nanyue.app.nywj.R;
import com.nanyue.app.nywj.adapter.NewsListAdapter;
import com.nanyue.app.nywj.bean.NewsDetailBean;
import com.nanyue.app.nywj.fragment.HomeFragment;

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
    private MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends Handler {

        private WeakReference<NewsDetailActivity> weakReference;

        public MyHandler (NewsDetailActivity newsDetailActivity) {
            weakReference = new WeakReference<NewsDetailActivity>(newsDetailActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NewsDetailActivity hf = weakReference.get();
            if (msg.what == 0) {
                String con = getNewContent(hf.content);
                hf.webView.loadDataWithBaseURL(null, con, "text/html", "utf-8", null);
            } else {
                Toast.makeText(hf, "网络错误", Toast.LENGTH_LONG).show();
            }
        }
    }

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

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://nouse.gzkuaiyi.com:9999/app/view-" + newsId)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    String res = response.body().string();
                    Object resObject = JSONObject.parse(res);
                    Map<String, Object> map = (Map) resObject;

                    if (map.get("data") != null) {
                        String data = map.get("data").toString();
                        NewsDetailBean newsDetailBean = JSONObject.parseObject(data, NewsDetailBean.class);
                        content = newsDetailBean.getContent();
                        title = newsDetailBean.getTitle();
                        author = newsDetailBean.getAuthor();
                        date = newsDetailBean.getPublishDate();
                        Message message = new Message();
                        message.what = 0;
                        myHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = new Message();
                    message.what = 1;
                    myHandler.sendMessage(message);
                }
            }
        }).start();
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
        if (myHandler != null) {
            if (myHandler.hasMessages(1)) {
                myHandler.removeMessages(1);
            }
            if (myHandler.hasMessages(0)) {
                myHandler.removeMessages(0);
            }
        }
        super.onDestroy();
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
}
