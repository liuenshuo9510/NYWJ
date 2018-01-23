package com.nanyue.app.nywj.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.nanyue.app.nywj.MyLog;
import com.nanyue.app.nywj.activity.MainActivity;
import com.nanyue.app.nywj.okhttp.CommonOkHttpClient;
import com.nanyue.app.nywj.okhttp.HttpConstants;
import com.nanyue.app.nywj.okhttp.RequestCenter;
import com.nanyue.app.nywj.okhttp.bean.NewsListBean;
import com.nanyue.app.nywj.okhttp.exception.OkHttpException;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataHandle;
import com.nanyue.app.nywj.okhttp.listener.DisposeDataListener;
import com.nanyue.app.nywj.okhttp.listener.DisposeDownloadListener;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Request;


public class UpdateApk {

    private static String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/NYWJXX.apk";

    public static void checkUpdate(final Context context) {
        RequestCenter.newAppRequest("9f70618e27604ea397a344018815fe71", new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                ArrayList<NewsListBean> list = (ArrayList<NewsListBean>) responseObj;
                NewsListBean newApp = list.get(0);
                int code = Integer.parseInt(newApp.getTitle());
                String apkSrc = newApp.getAttach();
                checkInfo(context, code, apkSrc);
            }

            @Override
            public void onFailure(OkHttpException reason) {
                MyLog.e(reason.getError_message(), reason.getError_detail());
                Toast.makeText(context, "版本检查失败，请查看网络", Toast.LENGTH_LONG).show();
            }
        });
    }

    private static void checkInfo(final Context context, int code, final String apkSrc) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            if (code > versionCode) {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("版本升级")
                        .setMessage("有新的更新可用，是否立即下载")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getApk(context, apkSrc);
                                Toast.makeText(context, "后台正在下载", Toast.LENGTH_LONG).show();
                            }
                        })
                        .create();
                alertDialog.show();
            } else {
                Toast.makeText(context, "当前版本为最新版", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            MyLog.e("checkUpdate", e.toString());
        }
    }

    private static void getApk(final Context context, String apkSrc) {
        CommonOkHttpClient.downloadFile(new Request.Builder()
                .url(HttpConstants.ROOT + apkSrc)
                .build(), new DisposeDataHandle(new DisposeDownloadListener() {
            @Override
            public void onProgress(int progrss) {

            }

            @Override
            public void onSuccess(Object responseObj) {
                //Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_LONG).show();
                installApk(context);
            }

            @Override
            public void onFailure(OkHttpException reason) {
                MyLog.e(reason.getError_message(), reason.getError_detail());
                Toast.makeText(context, "下载失败，请手动更新", Toast.LENGTH_LONG).show();
            }
        }, FILE_PATH));
    }

    private static void installApk(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, "com.nanyue.app.nywj.fileProvider", new File(FILE_PATH));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(new File(FILE_PATH));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            MyLog.e("installApk", e.toString());
        }
    }
}
