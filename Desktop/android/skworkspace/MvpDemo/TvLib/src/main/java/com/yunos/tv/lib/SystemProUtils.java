package com.yunos.tv.lib;

import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SystemProUtils {
    static int mFocusMode = 2;
    public static boolean isQiyi = false;

    public SystemProUtils() {
    }

    public static void setGlobalFocusMode(int mode) {
        mFocusMode = mode;
    }

    public static int getGlobalFocusMode() {
        return mFocusMode;
    }

    public static String getSystemVersion() {
        String version = VERSION.RELEASE;
        String[] versionArray = version.split("-");
        return versionArray[0];
    }

    public static String getUUID() {
        try {
            Class e = Class.forName("com.yunos.baseservice.clouduuid.CloudUUID");
            Method m = e.getMethod("getCloudUUID", new Class[0]);
            String result = (String)m.invoke((Object)null, new Object[0]);
            return !TextUtils.isEmpty(result) && !"false".equalsIgnoreCase(result)?result:"unknow_tv_imei";
        } catch (ClassNotFoundException var3) {
            return "unknow_tv_uuid";
        } catch (NoSuchMethodException var4) {
            return "unknow_tv_uuid";
        } catch (Exception var5) {
            var5.printStackTrace();
            return "unknow_tv_uuid";
        }
    }

    public static String getDomain() {
        String prefix = "http://";
        String posfix = "/";
        String default_domain = "http://api.yunos.wasu.tv/";

        try {
            Class e = Class.forName("android.os.SystemProperties");
            Method m = e.getMethod("get", new Class[]{String.class, String.class});
            String result = (String)m.invoke((Object)null, new Object[]{"ro.yunos.domain.aliyingshi", "falsenull"});
            if("falsenull".equals(result)) {
                Log.w("System", "domain yingshi  is falsenull, return default");
                return default_domain;
            } else {
                return !TextUtils.isEmpty(result)?"http://" + result.trim().replaceAll("/", "") + "/":default_domain;
            }
        } catch (Exception var6) {
            Log.w("System", "getDomain: error");
            return default_domain;
        }
    }

    public static String getDomainMTOP() {
        String prefix = "http://";
        String posfix = "/rest/api3.do?";
        String default_domainmtop = "http://m.yunos.wasu.tv/rest/api3.do?";

        try {
            Class e = Class.forName("android.os.SystemProperties");
            Method m = e.getMethod("get", new Class[]{String.class, String.class});
            String result = (String)m.invoke((Object)null, new Object[]{"ro.yunos.domain.aliyingshi.mtop", "falsenull"});
            if("falsenull".equals(result)) {
                Log.w("System", "getDomainMTOP falsenull,return default");
                return default_domainmtop;
            } else {
                return !TextUtils.isEmpty(result)?"http://" + result.trim().replaceAll("/", "") + "/rest/api3.do?":default_domainmtop;
            }
        } catch (Exception var6) {
            Log.w("System", "getDomainMTOP: error");
            return default_domainmtop;
        }
    }

    public static String getLicense() {
        String liscence = "1";

        try {
            Class e = Class.forName("android.os.SystemProperties");
            Method m = e.getMethod("get", new Class[]{String.class, String.class});
            String result = (String)m.invoke((Object)null, new Object[]{"ro.yunos.domain.license", "falsenull"});
            if("falsenull".equals(result)) {
                Log.w("System", "domain yingshi mtop is unknow!!!");
                return liscence;
            } else {
                return TextUtils.isEmpty(result)?liscence:result.trim();
            }
        } catch (Exception var4) {
            Log.w("System", "getLicense: error");
            return liscence;
        }
    }

    public static String getLogoPath() {
        try {
            Class e = Class.forName("android.os.SystemProperties");
            Method m = e.getMethod("get", new Class[]{String.class, String.class});
            String result = (String)m.invoke((Object)null, new Object[]{"ro.yunos.domain.license.logo", "falsenull"});
            if("falsenull".equals(result)) {
                Log.w("System", "domain yingshi logo path is unknow!!!");
                return null;
            } else {
                return result.trim();
            }
        } catch (Exception var3) {
            Log.w("System", "getLogoPath: error");
            return null;
        }
    }

    public static String getContents() {
        String defaultContent = "0,3,4,5";

        try {
            Class e = Class.forName("android.os.SystemProperties");
            Method m = e.getMethod("get", new Class[]{String.class, String.class});
            String result = (String)m.invoke((Object)null, new Object[]{"ro.yunos.domain.aliyingshi.cts", "falsenull"});
            if("falsenull".equals(result)) {
                Log.w("System", "domain yingshi contents is unknow!!!");
                return defaultContent;
            } else {
                return TextUtils.isEmpty(result)?defaultContent:result;
            }
        } catch (Exception var4) {
            Log.w("System", "getContents: error");
            return defaultContent;
        }
    }

    public static String getManufacture() {
        if(isQiyi) {
            return "ali_haiertv";
        } else {
            try {
                Class e = Class.forName("android.os.SystemProperties");
                Method m = e.getMethod("get", new Class[]{String.class, String.class});
                String result = (String)m.invoke((Object)null, new Object[]{"ro.product.manufacturer", "falsenull"});
                if("falsenull".equals(result)) {
                    Log.w("System", "manufactuer is unknow!!!");
                    return "unknow_tv_manufactuer";
                } else {
                    return TextUtils.isEmpty(result)?"":result;
                }
            } catch (Exception var3) {
                Log.w("System", "getManufacture: error");
                return "";
            }
        }
    }

    public static String getChip() {
        if(isQiyi) {
            return "amlogic_t866";
        } else {
            try {
                Class e = Class.forName("android.os.SystemProperties");
                Method m = e.getMethod("get", new Class[]{String.class, String.class});
                String result = (String)m.invoke((Object)null, new Object[]{"ro.yunos.product.chip", "falsenull"});
                if("falsenull".equals(result)) {
                    Log.w("System", "chip is unknow!!!");
                    return "unknow_tv_chip";
                } else {
                    return TextUtils.isEmpty(result)?"":result;
                }
            } catch (Exception var3) {
                Log.w("System", "getChip: error");
                return "";
            }
        }
    }

    public static String getMediaParams() {
        String strProp = getSystemProperties("ro.media.ability");
        if(strProp != null && !TextUtils.isEmpty(strProp)) {
            Log.d("media", "=====strProp====" + strProp);
            return strProp;
        } else {
            Log.w("media", "=====strProp null====");
            return "";
        }
    }

    public static String getDeviceName() {
        try {
            Class e = Class.forName("android.os.SystemProperties");
            Method m = e.getMethod("get", new Class[]{String.class, String.class});
            String result = (String)m.invoke((Object)null, new Object[]{"ro.product.model", "falsenull"});
            return "falsenull".equals(result)?null:result;
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static String getSystemProperties(String key) {
        String value = null;
        Class cls = null;

        try {
            cls = Class.forName("android.os.SystemProperties");
            Method e = cls.getMethod("get", new Class[]{String.class});
            Object object = cls.newInstance();
            value = (String)e.invoke(object, new Object[]{key});
        } catch (SecurityException var5) {
            var5.printStackTrace();
        } catch (NoSuchMethodException var6) {
            var6.printStackTrace();
        } catch (IllegalAccessException var7) {
            var7.printStackTrace();
        } catch (InstantiationException var8) {
            var8.printStackTrace();
        } catch (IllegalArgumentException var9) {
            var9.printStackTrace();
        } catch (InvocationTargetException var10) {
            var10.printStackTrace();
        } catch (ClassNotFoundException var11) {
            var11.printStackTrace();
        }

        return value;
    }
}
