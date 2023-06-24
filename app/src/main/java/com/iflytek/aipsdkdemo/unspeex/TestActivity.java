package com.iflytek.aipsdkdemo.unspeex;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.aipsdk.audio.AudioHelper;
import com.iflytek.aipsdk.audio.AudioListener;
import com.iflytek.aipsdkdemo.R;
import com.iflytek.util.Logs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestActivity extends Activity {

    private final String TAG = TestActivity.this.getClass().getSimpleName();

    private EditText etRecParams;
    private EditText path_et;
    private CheckBox cbSave;
    private LinearLayout linearLayout;
    private TextView text;
    private boolean isStart = false;

    /**
     * 将压缩后的音频解压，参数为：aue=speex-wb-decode
     * <p/>
     * 将raw音频的音频压缩，参数为：aue=speex-wb
     */
    private String RECODE_PARAMS = "aue=speex-wb";
    AudioHelper audioHelper = new AudioHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unspeex_demo);
        etRecParams = (EditText) findViewById(R.id.rec_params);
        cbSave = (CheckBox) findViewById(R.id.cb_save);
        etRecParams.setText(RECODE_PARAMS);
        linearLayout = (LinearLayout) findViewById(R.id.path);
        linearLayout.setVisibility(View.VISIBLE);
        path_et = (EditText) findViewById(R.id.path_et);
        path_et.setText("/sdcard/aip.pcm");
        text = (TextView) findViewById(R.id.text);
        text.setVisibility(View.VISIBLE);
    }

    public void test(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start:
                testStart();
        }

    }


    private void testStart() {
        audioHelper.init(etRecParams.getText().toString().trim(), listener);
        isStart = true;
        readFile();
    }


    public static boolean checkEditText(EditText et) {
        String value = et.getText().toString();
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        return true;
    }

//    private void readFile() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                byte[] temps = null;
//                try {
//                    /**
//                     * 获取待转化音频流，实际项目中可以用hanler异步调用
//                     */
//                    temps = getBytes(path_et.getText().toString().trim());
//                } catch (IOException e1) {
//                    Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][readFile] " + "e=" + e1);
//                }
//                if (temps == null) {
//                    Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][readFile] " + "读取文件失败");
//                    return;
//                }
//                /***
//                 * 开始会话，音频流开始转化
//                 */
//                int error = audioHelper.startUnSpeex(temps);
//                /***
//                 * 结束会话，关闭
//                 */
//                audioHelper.stopRecord();
//            }
//        }).start();
//
//
//    }

    private void readFile() {
        try {
            FileInputStream in = new FileInputStream(path_et.getText().toString().trim());
            int left = in.available();
            int perSize = 190320;      //每次块大小
            if(left < perSize){
                perSize = left;
            }

            byte[] temp = new byte[perSize];
            int size = 0;
            while ((size = in.read(temp))!=-1 && isStart==true) {
                //写入音频流，调用该接口，一般都是耗时操作，为防止ANR，建议app开启线程调用或者其他异步方式调用
                int error = audioHelper.startUnSpeex(temp);
                left -= size;
                if(left <= 0)
                    break;
                if(left < perSize){
                    perSize = left;
                    temp = new byte[perSize];
                }
                if (error != 0) {
                    /**
                     * writeAudio返回为非零，表示尚未开始会话，或者提前结束会话
                     */
                    break;
                }
                // Thread.sleep(40);
            }

            in.close();
            audioHelper.stopRecord();
            //写入音频结束后，一定要调用识别停止接口,获取最终的结果，且防止连接超时报错
            isStart = false;
        } catch (Exception e) {
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][readFile] " + "e=" + e);
        }
    }



    AudioListener listener = new AudioListener() {
        @Override
        public void onRecordBuffer(byte[] bytes, int i) {

            if (bytes != null) {
                Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onRecordBuffer] " + "bytes.length====" + bytes.length);
                if (cbSave.isChecked()) {
                    if (null != bytes && bytes.length > 0) {
                        writeData(bytes, "/sdcard/test/aip_decode.pcm");
                    }

                }
            }
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onRecordBuffer] " +  "###eps：" + i);
//            Toast.makeText(TestActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int i) {
            isStart = false;
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onError] " +  "###错误码：" + i);
//            Toast.makeText(TestActivity.this, "###错误码：" + i, Toast.LENGTH_SHORT).show();

        }
    };

    /**
     * 写文件
     *
     * @param content
     * @param localPath
     */
    void writeData(byte[] content, String localPath) {
        try {
            File file = new File(localPath);
//            if (file.exists()) {
//                file.delete();
//            }
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(content);
            fos.close();
        } catch (Exception e) {
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][writeData] " +  "e=" + e);
        }

    }

    /**
     * 读文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public byte[] getBytes(String filePath) throws IOException {
        File file = new File(filePath);

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][getBytes] " + "file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);

        byte[] buffer = new byte[(int) fileSize];

        int offset = 0;

        int numRead = 0;

        while (offset < buffer.length

                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {

            offset += numRead;

        }
        // 确保所有数据均被读取

        if (offset != buffer.length) {

            throw new IOException("Could not completely read file "
                    + file.getName());

        }
        fi.close();

        return buffer;
    }

}
