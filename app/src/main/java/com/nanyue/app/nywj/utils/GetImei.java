package com.nanyue.app.nywj.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GetImei {

    public static String getImei(Context context) {
        if (Build.VERSION.SDK_INT < 21) {
            return getImeiFor4(context);
        } else if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) {
            return getImeiFor5(context);
        } else {
            return getImeiFor6(context);
        }
    }

    /**
     * 4.0 获取IMEI 或者Meid
     */
    private static String getImeiFor4(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }


    /**
     * 5.0 获取IMEI
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static String getImeiFor5(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        Map<String, String> map = new HashMap<String, String>();
        Class<?> clazz;
        Method method;

        try {
            clazz = Class.forName("android.os.SystemProperties");
            method = clazz.getMethod("get", String.class, String.class);

            String gsm = (String) method.invoke(null, "ril.gsm.imei", "");
            String meid = (String) method.invoke(null, "ril.cdma.meid", "");

            map.put("meid", meid);
            if (!TextUtils.isEmpty(gsm)) {
                //the value of gsm like:xxxxxx,xxxxxx
                String imeiArray[] = gsm.split(",");
                if (imeiArray.length > 0) {
                    map.put("imei1", imeiArray[0]);

                    if (imeiArray.length > 1) {
                        map.put("imei2", imeiArray[1]);
                    }
                }
            } else {
                map.put("imei1", mTelephonyManager.getDeviceId());
            }
        } catch (Exception e) {
            Log.e("IMEIError", e.toString());
        }
        for (String key : map.keySet()) {
            if (map.get(key).length() == 15) {
                return map.get(key);
            }
        }

        //return mTelephonyManager.getDeviceId();
        return null;
    }

    /**
     * 6.0 获取IMEI
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static String getImeiFor6(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        String imei1 = mTelephonyManager.getDeviceId(0);
        String imei2 = mTelephonyManager.getDeviceId(1);
        if (imei1.length() == 14) {
            return imei2;
        } else if (imei2.length() == 14){
            return imei1;
        } else {
            return imei1;
        }
    }
}
