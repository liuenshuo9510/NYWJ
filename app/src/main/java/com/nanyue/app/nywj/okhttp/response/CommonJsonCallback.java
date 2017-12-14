package com.nanyue.app.nywj.okhttp.response;

import android.os.Handler;
import android.os.Looper;


import com.alibaba.fastjson.JSONObject;
import com.nanyue.app.nywj.okhttp.exception.OkHttpException;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataHandle;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataListener;
import com.nanyue.app.nywj.okhttp.listener.DisposeHandleCookieListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * @author vision
 * @function 专门处理JSON的回调
 */
public class CommonJsonCallback implements Callback {

    /**
     * the logic layer exception, may alter in different app
     */
    private final String EMPTY_MSG = "";
    private final String COOKIE_STORE = "Set-Cookie"; // decide the server it
    // can has the value of
    // set-cookie2

    /**
     * the java layer exception, do not same to the logic error
     */
    public static final String NETWORK_ERROR = "网络错误"; // the network relative error
    public static final String JSON_ERROR = "解析失败"; // the JSON relative error
    public static final String EMPTY_ERROR = "无数据"; // the unknow error

    /**
     * 将其它线程的数据转发到UI线程
     */
    private Handler mDeliveryHandler;
    private DisposeDataListener mListener;
    private Class<?> mClass;
    private boolean isArray;

    public CommonJsonCallback(DisposeDataHandle handle) {
        this.mListener = handle.mListener;
        this.mClass = handle.mClass;
        this.isArray = handle.isArray;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(final Call call, final IOException ioexception) {
        /**
         * 此时还在非UI线程，因此要转发
         */
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, ioexception));
            }
        });
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        final String result = response.body().string();
        final ArrayList<String> cookieLists = handleCookie(response.headers());
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
                /**
                 * handle the cookie
                 */
                if (mListener instanceof DisposeHandleCookieListener) {
                    ((DisposeHandleCookieListener) mListener).onCookie(cookieLists);
                }
            }
        });
    }

    private void handleResponse(String responseObj) {
        if (responseObj == null || responseObj.trim().equals("")) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }

        Object resObject = JSONObject.parse(responseObj);
        Map<String, Object> resMap = (Map)resObject;

        if (resMap.get("data") == null) {
            mListener.onFailure(new OkHttpException(EMPTY_ERROR, EMPTY_MSG));
            return;
        }

        String data = resMap.get("data").toString();
        try {
            if (!isArray) {
                mListener.onSuccess(JSONObject.parseObject(data, mClass));
            } else {
                mListener.onSuccess(JSONObject.parseArray(data, mClass));
            }

        } catch (Exception e) {
            mListener.onFailure(new OkHttpException(JSON_ERROR, e.toString()));
        }
    }

    private ArrayList<String> handleCookie(Headers headers) {
        ArrayList<String> tempList = new ArrayList<String>();
        for (int i = 0; i < headers.size(); i++) {
            if (headers.name(i).equalsIgnoreCase(COOKIE_STORE)) {
                tempList.add(headers.value(i));
            }
        }
        return tempList;
    }
}