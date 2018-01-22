package com.nanyue.app.nywj.okhttp.listener;

import com.nanyue.app.nywj.okhttp.exception.OkHttpException;

public interface DisposeDataListener {

	/**
	 * 请求成功回调事件处理
	 */
	public void onSuccess(Object responseObj);

	/**
	 * 请求失败回调事件处理
	 */
	public void onFailure(OkHttpException reason);

}
