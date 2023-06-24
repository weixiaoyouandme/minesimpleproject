package com.iflytek.aipsdkdemo;

import android.os.Bundle;

import java.util.LinkedHashMap;

public class MainActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initMenu() {
        menuMap = new LinkedHashMap<String, String>();
        menuMap.put("音频解压Demo",
                "com.iflytek.aipsdkdemo.unspeex.TestActivity");
        menuMap.put("录音Demo",
                "com.iflytek.aipsdkdemo.record.TestActivity");
        menuMap.put("日志关联Demo",
                "com.iflytek.aipsdkdemo.session.TestActivity");
        menuMap.put("个性化Demo",
                "com.iflytek.aipsdkdemo.personal.TestActivity");
        menuMap.put("语音识别Demo",
                "com.iflytek.aipsdkdemo.asr.IatLoginActivity");
        menuMap.put("语音合成Demo",
                "com.iflytek.aipsdkdemo.tts.TtsLoginActivity");
        menuMap.put("语义理解Demo",
                "com.iflytek.aipsdkdemo.nlp.NlpLoginActivity");
        menuMap.put("翻译Demo",
                "com.iflytek.aipsdkdemo.translate.TestActivity");
        menuMap.put("唤醒 Demo",
                "com.iflytek.aipsdkdemo.ivw.IvwActivity");
    }

}
