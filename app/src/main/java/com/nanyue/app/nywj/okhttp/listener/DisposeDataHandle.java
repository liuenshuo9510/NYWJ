package com.nanyue.app.nywj.okhttp.listener;

import java.util.ArrayList;

/**
 * @author vision
 *
 */
public class DisposeDataHandle
{
	public DisposeDataListener mListener = null;
	public Class<?> mClass = null;
	public String mSource = null;
	public boolean isArray = false;

	public DisposeDataHandle(DisposeDataListener listener) {
		this.mListener = listener;
	}

	public DisposeDataHandle(DisposeDataListener listener, String filePath) {
		this.mListener = listener;
		this.mSource = filePath;
	}

	public DisposeDataHandle(DisposeDataListener listener, Class<?> clazz, boolean isArray) {
		this.mListener = listener;
		this.mClass = clazz;
		this.isArray = isArray;
	}

}