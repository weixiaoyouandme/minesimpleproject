package com.iflytek.aipsdkdemo.asr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.iflytek.aipsdkdemo.R;

/**
 * Created by Administrator on 2017/3/29.
 */

public class IatLoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iat_login);
    }

    public void inTo(View view) {
        //录音识别
        Intent intent = new Intent(this, IatDemo.class);
        startActivity(intent);
    }

    public void inToAudio(View view) {
        //音频流识别
        Intent intent = new Intent(this, IatAudioDemo.class);
        startActivity(intent);
    }

    public void back(View view) {
        finish();
    }
}
