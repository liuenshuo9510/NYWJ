package com.nanyue.app.nywj.okhttp.exception;

public class OkHttpException extends Exception {
	private static final long serialVersionUID = 1L;

	public static final String NETWORK_ERROR = "网络错误"; // the network relative error
	public static final String JSON_ERROR = "解析失败"; // the JSON relative error
	public static final String EMPTY_ERROR = "无数据"; // the unknow error

	private String error_message;
	private String error_detail;

	public OkHttpException() {
		error_message = "";
		error_detail = "";
	}

	public OkHttpException(String error_message, String error_detail) {
		this.error_message = error_message;
		this.error_detail = error_detail;
	}


	public String getError_message() {
		return error_message;
	}

	public String getError_detail() {
		return error_detail;
	}

	public void setError_detail(String error_detail) {
		this.error_detail = error_detail;
	}

	public void setError_message(String error_message) {
		this.error_message = error_message;
	}
}