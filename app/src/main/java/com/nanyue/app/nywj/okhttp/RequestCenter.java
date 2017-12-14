package com.nanyue.app.nywj.okhttp;

import com.nanyue.app.nywj.okhttp.bean.LogInBean;
import com.nanyue.app.nywj.okhttp.bean.NewsDetailBean;
import com.nanyue.app.nywj.okhttp.bean.NewsListBean;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataHandle;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataListener;
import com.nanyue.app.nywj.okhttp.request.CommonRequest;
import com.nanyue.app.nywj.okhttp.request.RequestParams;
import com.nanyue.app.nywj.okhttp.response.CommonJsonCallback;

public class RequestCenter {

    public static void getRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz, boolean isArray) {
        CommonOkHttpClient.get(CommonRequest.createGetRequest(url, params), new DisposeDataHandle(listener, clazz, isArray));
    }

    public static void postRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz, boolean isArray) {
        CommonOkHttpClient.get(CommonRequest.createPostRequest(url, params), new DisposeDataHandle(listener, clazz, isArray));
    }

    public static void logInRequest(String name, String pass, String imei, DisposeDataListener disposeDataListener) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("userName", name);
        requestParams.put("password", pass);
        requestParams.put("serialNo", imei);
        getRequest(HttpConstants.LOGIN, requestParams, disposeDataListener, LogInBean.class, false);
    }

    public static void newsListRequest(int id, DisposeDataListener disposeDataListener) {
        getRequest(HttpConstants.NEWS_LIST + id, null, disposeDataListener, NewsListBean.class, true);
    }

    public static void newsDetailRequest(String id, DisposeDataListener disposeDataListener) {
        getRequest(HttpConstants.NEWS_DETAIL + id, null, disposeDataListener, NewsDetailBean.class, false);
    }

    public static void homeNewsRequest(DisposeDataListener disposeDataListener) {
        getRequest(HttpConstants.HOME_NEWS, null, disposeDataListener, NewsListBean.class, true);
    }

}
