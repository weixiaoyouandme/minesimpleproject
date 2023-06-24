package com.iflytek.aipsdkdemo;

import android.app.Application;
import android.util.Log;

import com.iflytek.aipsdk.common.SpeechUtility;
import com.iflytek.crashcollect.CrashCollector;
import com.iflytek.util.Logs;

public class App extends Application {
    private static final String TAG = "App";

    private final String BASE_PATH = "/sdcard/test/";
    /**
     * 是否授权成功
     */
    public static boolean isAuthor = true;

    /**
     * 本地翻译是否初始化
     */
    public static boolean isLocalInit = false;

    /**
     * 本地翻译是否初始化
     */
    public static boolean isOcrInit = false;

    @Override
    public void onCreate() {
        super.onCreate();
        //ca_path不传则走默认证书
        String params = null;
//        params = "ca_path=ca.jet,res=0";
//        params = "ca_path=/sdcard/ca.crt,res=1";
        SpeechUtility.createUtility(App.this, params);
        initCrashCollect();
        Logs.setSaveFlag(false, BASE_PATH);
        Logs.setPerfFlag(false);
        Logs.setPrintFlag(true);
    }

    //设置崩溃收集
    private void initCrashCollect() {
        //日志开关
        CrashCollector.setDebugable(true);
        //初始化崩溃sdk
        CrashCollector.init(this, "58787f17");
        CrashCollector.setWorkDir(BASE_PATH);
    }
}
