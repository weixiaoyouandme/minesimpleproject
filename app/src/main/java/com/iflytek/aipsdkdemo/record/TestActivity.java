package com.iflytek.aipsdkdemo.record;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.iflytek.aipsdk.audio.AudioHelper;
import com.iflytek.aipsdk.audio.AudioListener;
import com.iflytek.aipsdkdemo.R;
import com.iflytek.util.Logs;

import java.io.File;
import java.io.FileOutputStream;

public class TestActivity extends Activity {

    private final String TAG = TestActivity.this.getClass().getSimpleName();
    private int choose;//0 stop  1 start
    private Button restop;
    private String pcmPath;
    private Boolean isStop;
    private TextView tvResult;
    private TextView tvMsg;
    private EditText etRecParams;
    private CheckBox cbSave;
    private String RECODE_PARAMS = "aue=raw,vad_res=EVAD_MID_LSTM_FF_zx_FBN40_chen_qrwang2_7f4a_20200227_ent.bin,res=0";
    //private String RECODE_PARAMS = "aue=raw,vad_res=meta_vad_16k.jet,res=0";
//    private String RECODE_PARAMS = "aue=speex-wb,vad_res=meta_vad_16k.jet,res=0";
//    private String RECODE_PARAMS = "aue=speex-wb,vad_res=/sdcard/meta_vad_16k.jet,res=1";

    AudioHelper audioHelper = new AudioHelper(this);

    void writeData(byte[] content, String localPath) {
        try {
            File file = new File(localPath);
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(content);
            fos.close();
        } catch (Exception e) {
//                e.printStackTrace();
            Logs.v("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][writeData] " + "e=" + e);
        }

    }

    AudioListener listener = new AudioListener() {
        @Override
        public void onRecordBuffer(byte[] bytes, int i) {
            Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onRecordBuffer] " + "###eps：" + i);
//            showTip("###eps：" + i);

            if (cbSave.isChecked()) {
                if (null != bytes && bytes.length > 0) {
                    writeData(bytes, pcmPath);
                }
            }
        }

        @Override
        public void onError(int i) {
            Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onError] " + "###错误码：" + i);
            showTip("###错误码：" + i);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_demo);
        tvResult = (TextView) findViewById(R.id.result);
        tvMsg = (TextView) findViewById(R.id.tips);
        etRecParams = (EditText) findViewById(R.id.rec_params);
        cbSave = (CheckBox) findViewById(R.id.cb_save);
//        isStop = false;
        choose = 1;//0 stop  1 start
        etRecParams.setText(RECODE_PARAMS);
        restop = (Button) findViewById(R.id.btn_restop);
    }

    public void test(View v) {
        int id = v.getId();
        switch (id) {
//            case R.id.btn_start:
//                testStart();
//                break;
//            case R.id.btn_stop:
//                testStop();
//                break;
//            case R.id.btn_test:
//                isStop = false;
//                recodeTest();
//                break;
            case R.id.btn_restop:
                restop();
                break;
        }

    }

    private void restop() {
        //  choose = 1;//0 stop  1 start
        if (choose == 1) {//开始录音
            testStart();
            choose = 0;
            restop.setText("停止录音");
        } else {//停止录音
            testStop();
            choose = 1;
            restop.setText("开始录音");
        }
    }

    private void recodeTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    String param = etRecParams.getText().toString();
                    int ret = audioHelper.startRecord(param, listener);
                    Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][testStart] " + "###startRecord ret:" + ret);
                    try {
                        Thread.sleep(100);
                        audioHelper.stopRecord();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

    }

    private void testStart() {
        pcmPath = "/sdcard/test/" + System.currentTimeMillis() + ".pcm";
        showTip("###pcmPath：" + pcmPath);
        String param = etRecParams.getText().toString();
        int ret = audioHelper.startRecord(param, listener);
        Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][testStart] " + "###startRecord ret:" + ret);
    }

    private void testStop() {
        isStop = true;
        audioHelper.stopRecord();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioHelper.stopRecord();
    }

    protected void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResult.setText(str);
            }
        });
    }

    protected void showMsg(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMsg.setText(str);
            }
        });
    }

    public static boolean checkEditText(EditText et) {
        String value = et.getText().toString();
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        return true;
    }


}
