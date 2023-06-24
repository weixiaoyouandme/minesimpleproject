package com.iflytek.aipsdkdemo.tts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.aipsdk.common.InitListener;
import com.iflytek.aipsdk.param.MscKeys;
import com.iflytek.aipsdk.tts.SpeechSynthesizer;
import com.iflytek.aipsdk.tts.SynthesizerListener;
import com.iflytek.aipsdk.util.ErrorCode;
import com.iflytek.aipsdk.util.SpeechConstant;
import com.iflytek.aipsdk.util.SpeechError;
import com.iflytek.aipsdkdemo.R;
import com.iflytek.util.Logs;

public class TtsDemo extends Activity implements OnClickListener {
    private static String TAG = "TtsDemo";
    // 语音合成对象
    private SpeechSynthesizer mTts;
    //缓冲进度
    private int mPercentForBuffering = 0;
    //播放进度
    private float mPercentForPlaying = 0;
    private Toast mToast;
    private String params;
    private boolean online_tts;
    private String localParams;
    private String exParam;


    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ttsdemo);

        initLayout();
        // 初始化合成对
        mTts = new SpeechSynthesizer(this, mTtsInitListener);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        params = getIntent().getStringExtra("tts_url");
        exParam = getIntent().getStringExtra("exParam");
        Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onCreate] " + "-params--" + params);
    }

    /**
     * 初始化Layout
     */
    private void initLayout() {
        findViewById(R.id.tts_play).setOnClickListener(this);
        findViewById(R.id.tts_cancel).setOnClickListener(this);
        findViewById(R.id.tts_pause).setOnClickListener(this);
        findViewById(R.id.tts_resume).setOnClickListener(this);
        findViewById(R.id.tts_begin_syn).setOnClickListener(this);
        findViewById(R.id.tts_begin_play).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 开始合成
            case R.id.tts_play: {
                mPercentForBuffering = 0;
                mPercentForPlaying = 0;
                String text = ((EditText) findViewById(R.id.tts_text)).getText().toString();
                // 设置参数
                mTts.setNewParams(params);
                //false可以不被录音打断
                //mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");

                if (!TextUtils.isEmpty(exParam)) {
                    mTts.setParamEx(exParam);
                }
                int code = mTts.startSpeaking(text, mTtsListener);
                Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onClick] " + "--code--" + code);
                if (code != ErrorCode.SUCCESS) {
                    showTip("语音合成失败,错误: " + code);
                }
                break;
            }
            // 取消合成
            case R.id.tts_cancel:
                mTts.stopSpeaking();
                break;
            // 暂停播放
            case R.id.tts_pause:
                mTts.pauseSpeaking();
                break;
            // 继续播放
            case R.id.tts_resume:
                mTts.resumeSpeaking();
                break;
            case R.id.tts_begin_syn: {
                mPercentForBuffering = 0;
                mPercentForPlaying = 0;
                String text = ((EditText) findViewById(R.id.tts_text)).getText().toString();
                // 设置参数
                mTts.setNewParams(params);
                //false可以不被录音打断
                //mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");

                if (!TextUtils.isEmpty(exParam)) {
                    mTts.setParamEx(exParam);
                }
                int code = mTts.startSynthesizer(text, false, mTtsListener);
                Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onClick] " + "--code--" + code);
                if (code != ErrorCode.SUCCESS) {
                    showTip("语音合成失败,错误: " + code);
                }
                break;
            }
            case R.id.tts_begin_play:
                mTts.beginPlay();
                break;
        }
    }

    /**
     * 初始化监听�
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][InitListener] " + "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失失败,错误码：" + code);
            } else {
                showTip("初始化成功");
            }
        }
    };

    /**
     * 开始合成回调监听
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onBufferProgress] " + "sid is  " +mTts.getSessionID());
            mPercentForBuffering = percent;
            mToast.setText(String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));

            mToast.show();
        }

        @Override
        public void onSpeakProgress(float percent, int beginPos, int endPos) {
            mPercentForPlaying = percent;
            showTip(String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
            } else {
                Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onCompleted] " + "error " +error.getErrorCode());
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onBufferCompleted(String s, int length) {
            Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onBufferCompleted totalsize:] " + length);
            //计算总时长
            int rate = 16000;
            if (params.contains("auf=3")) {
                rate = 8000;
            }
            int time = length / (rate * 16 / 8);
            Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onBufferCompleted totaltime:] " + time);
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPreError(int i) {
            showTip("错误码：" + i);
        }
    };

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTts.stopSpeaking();
        // 退出时释放连接
        mTts.destroy();
    }

    @Override
    public void onBackPressed() {
        mTts.stopSpeaking();
        super.onBackPressed();
    }
}
