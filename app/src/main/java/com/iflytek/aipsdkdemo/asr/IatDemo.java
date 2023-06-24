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
import com.iflytek.aipsdk.util.ErrorCode;
import com.iflytek.aipsdk.util.SpeechConstant;
import com.iflytek.aipsdk.util.SpeechError;
import com.iflytek.aipsdkdemo.R;
import com.iflytek.aipsdkdemo.util.PermissionsUtil;
import com.iflytek.util.Logs;

public class IatDemo extends Activity implements OnClickListener, PermissionsUtil.IPermissionsCallback {
    private static String TAG = "IatDemo";
    private SpeechRecognizer mIat;
    private EditText mEtResult;
    private EditText mEtUrl;
    private String str2 = "";
    private Toast mToast;

    private EditText authorEt;

    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.iatdemo);
        initLayout();
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        authorEt = (EditText) findViewById(R.id.authorEt);
        PermissionsUtil permissionsUtil = PermissionsUtil
                .with(this)
                .requestCode(1)
                .isDebug(false)//开启log
                .permissions(PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE
                        , PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE, PermissionsUtil.Permission.Microphone.RECORD_AUDIO)
                .request();

        // 测试服务器
        mEtUrl.setText("extend_params={\"params\":\"eos=3000,bos=3000\"},appid=pc20onli,url=172.31.98.80:35066,time_out=3,svc=iat,auf=audio/L16;rate=16000,aue=raw,type=1,uid=660Y5r,mi=80");
    }

    /**
     * 初始化Layout。
     */
    private void initLayout() {
        findViewById(R.id.iat_recognize).setOnClickListener(this);
        findViewById(R.id.iat_cancel).setOnClickListener(this);
        mEtResult = ((EditText) findViewById(R.id.iat_text));
        mEtUrl = (EditText) findViewById(R.id.iat_url);
        mEtUrl.setText(getResources().getString(R.string.rcg_params));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 开始听写
            case R.id.iat_recognize:
                setParams();
                int error = 0;
                {
                    //调用录音机识别
                    error = mIat.startListening(recognizerListener);
                    if (error != ErrorCode.SUCCESS) {
                        showTip("正在处理结果,请稍等");
                    } else {
                        showTip(getString(R.string.text_begin));
                    }
                }
                break;
            // 取消或者结束听写
            case R.id.iat_cancel:
                mIat.stopListening();                  //停止，获取之前的结果
//              mIat.cancel();                           //停止，不要之前的结果
                showTip("取消听写");
                break;
            default:
                break;
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][InitListener] " + "SpeechRecognizer init() code = " + code);
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
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            if (null != error) {
                showTip(error.getPlainDescription(true));
                Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onError] " + " error:" + error);
                authorEt.setText(error.getErrorCode() + "");
            }
        }

        @Override
        public void onEndOfSpeech() {
            showTip("结束说话");
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onEndOfSpeech] ");
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //调用录音机识别
//          int  err = mIat.startListening(recognizerListener);
//            if (err != ErrorCode.SUCCESS) {
//                showTip("正在处理结果,请稍等");
//            } else {
//                showTip(getString(R.string.text_begin));
//            }
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onResult] " + " results:" + results.getResultString() + " ;islast:" + isLast);
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
        String param = mEtUrl.getText().toString().trim();
        Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][setParams] " + "rb_recognize=" + param);
        mIat.setParameter(SpeechConstant.PARAM, param);
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
        Logs.v("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onPermissionsGranted] " + "--已获取录音权限---");
    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {
        Logs.v("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onPermissionsDenied] " + "--已拒绝录音权限---");
    }
}
