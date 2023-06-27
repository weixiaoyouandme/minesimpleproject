package com.iflytek.aipsdkdemo.asr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.aipsdk.asr.RecognizerListener;
import com.iflytek.aipsdk.asr.RecognizerResult;
import com.iflytek.aipsdk.asr.SpeechRecognizer;
import com.iflytek.aipsdk.common.InitListener;
import com.iflytek.aipsdk.param.MscKeys;
import com.iflytek.aipsdk.util.ErrorCode;
import com.iflytek.aipsdk.util.SpeechConstant;
import com.iflytek.aipsdk.util.SpeechError;
import com.iflytek.aipsdkdemo.R;
import com.iflytek.aipsdkdemo.util.PermissionsUtil;
import com.iflytek.util.Logs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class IatAudioDemo extends Activity implements OnClickListener, PermissionsUtil.IPermissionsCallback {
    private static String TAG = "IatAudioDemo";
    private SpeechRecognizer mIat;
    private EditText mEtResult;
    private EditText mEtUrl;
    private EditText mEtPcm;
    private String str2 = "";
    private Toast mToast;
    private boolean isStop = false;
    private EditText authorEt;

    BufferedInputStream in = null;

    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.iat_audio_demo);
        initLayout();
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        authorEt = (EditText) findViewById(R.id.authorEt);
        PermissionsUtil permissionsUtil = PermissionsUtil
                .with(this)
                .requestCode(1)
                .isDebug(false)//开启log
                .permissions(PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE
                        , PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                .request();
    }

    /**
     * 初始化Layout。
     */
    private void initLayout() {
        findViewById(R.id.iat_cancel).setOnClickListener(this);
        findViewById(R.id.iat_file).setOnClickListener(this);
        mEtUrl = (EditText) findViewById(R.id.iat_url);
        mEtResult = ((EditText) findViewById(R.id.iat_text));
        mEtPcm = ((EditText) findViewById(R.id.iat_pcm));
        mEtUrl.setText(getResources().getString(R.string.rcg_params));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 开始听写
            case R.id.iat_file:
                setParams();
                int error = 0;
                String path = mEtPcm.getText().toString();
                File file = new File(path);
                Boolean isDire = false;
                if (!file.exists()) {
                    showTip("PCM文件" + path + "不存在");
                    return;
                } else {
                    isDire = file.isDirectory();
                    if (isDire) {
                        showTip("这是个文件夹路径，请输入用于识别的音频文件！");
                        return;
                    }
                }
                mIat.setParameter(SpeechConstant.AUDIO_SOURCE, Integer.toString(MscKeys.VALUE_AUDIO_EXTERNALFILE));
                isStop = false;
                error = mIat.startListening(recognizerListener);
                if (error != 0) {
                    showTip("开始错误！");
                } else {
                    readFile();
                }
                break;
            // 取消或者结束听写
            case R.id.iat_cancel:
                isStop = true;
                showTip("取消听写");
                break;
            default:
                break;
        }
    }

    private void readFile() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int errAuw = 0;
                    in = new BufferedInputStream(new FileInputStream(mEtPcm.getText().toString().trim()));
                    byte[] temp = new byte[2560];
                    int size = 0;
                    while ((size = in.read(temp)) != -1 && isStop == false) {
                        //写入音频流，调用该接口，一般都是耗时操作，为防止ANR，建议app开启线程调用或者其他异步方式调用
                        if (isStop) {
                            break;
                        }
                        errAuw = mIat.writeAudio(temp, 0, size);
                        if (errAuw != 0) {
                            /**
                             * writeAudio返回为非零，表示尚未开始会话，或者提前结束会话
                             */
                            break;
                        }
                        Thread.sleep(80);
                    }
                    in.close();
                    //写入音频结束后，一定要调用识别停止接口,获取最终的结果，且防止连接超时报错
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             mIat.stopListening();                     //停止，获取之前的结果
//                           mIat.cancel();                            //停止，不要之前的结果
                        }
                    });
                    showTip("结束说话");
                } catch (Exception e) {
                    Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][readFile] " + "e=" + e);
                }
            }
        }).start();
    }

    /**
     * 初始化监听器，此处需要注意
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][InitListener] " + "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code);
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener recognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onBeginOfSpeech] ");
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onError] ");
            if (null != error) {
                showTip(error.getPlainDescription(true));
                Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onError] " + " error:" + error);
                authorEt.setText(error.getErrorCode() + "");
            }

        }

        @Override
        public void onEndOfSpeech() {
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onEndOfSpeech] ");
            showTip("结束说话");
//            isStop = true;
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onResult] " + " results:" + results.getResultString() + ";isLast:" + isLast);
            if (results.getResultString() != "" || results.getResultString() != null) {
                mEtResult.setText(null);// 清空显示内容
                mEtResult.append(results.getResultString() + "\n" + str2);
                mEtResult.setSelection(mEtResult.length());
                str2 = results.getResultString();
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

        @Override
        public void onWakeUp(final String str, final int code) {

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

    private boolean setParams() {
        String params = mEtUrl.getText().toString().trim();
        Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][setParams] " + "rb_recognize=" + params);
        mIat.setParameter(SpeechConstant.PARAM, params);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mIat.destroy();
    }

    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
        Logs.v("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onPermissionsGranted] ");
    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {
        Logs.v("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onPermissionsDenied] ");
    }
}
