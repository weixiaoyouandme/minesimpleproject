package com.iflytek.aipsdkdemo.personal;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.iflytek.aipsdk.common.SpeechUtility;
import com.iflytek.aipsdk.personal.IPersResListener;
import com.iflytek.aipsdk.personal.PersRes;
import com.iflytek.aipsdkdemo.R;
import com.iflytek.aipsdkdemo.util.TestUtil;
import com.iflytek.util.Logs;

import java.io.IOException;

import static com.iflytek.aipsdkdemo.R.id.result;

public class TestActivity extends Activity {

    private final String TAG = TestActivity.this.getClass().getSimpleName();

    PersRes persRes = new PersRes();

    TextView tvResult;
    EditText etParams;
    EditText etFile;
//    private final String BASE_PARAMS = ",url=172.31.3.84:1035";
    private final String BASE_PARAMS = ",url=192.168.65.38:1029";
    private String PR_LOGIN_PARAMS = "appid=pc20onli,uid=123yy66" + BASE_PARAMS;
    private String PR_UPLOAD_PARAMS = "res_type=0,subcmd=upload,appid=pc20onli" + BASE_PARAMS;
    private String PR_DOWNLOAD_PARAMS = "res_type=0,subcmd=download,appid=pc20onli" + BASE_PARAMS;
    private String PR_UPLOAD_FILE = "/sdcard/test.txt";

    private final String BASE_PARAMS_Ex = ",url=192.168.77.83:8074";
    private String PR_UPLOAD_PARAMS_Ex = "res_type=0,res_id=androidtest,engine_name=iat,subcmd=upload,appid=pc20onli,uid=selfparam~ab_asr" + BASE_PARAMS_Ex;
    private String PR_DOWNLOAD_PARAMS_Ex = "res_type=0,res_id=androidtest,subcmd=download,appid=pc20onli,uid=selfparam~ab_asr" + BASE_PARAMS_Ex;

    private final String[] K = {"login", "upload", "download", "uploadEx", "downloadEx"};
    private final String[] V = {"00", "01", "02","03", "04"};
    private String type = V[0];
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    IPersResListener listener = new IPersResListener() {
        @Override
        public void onLoginResult(String s, int i) {
            Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onLoginResult] " + "###onLoginResult" + s + "###错误码：" + i);
            showTip("###onLoginResult" + s + "###错误码：" + i);
        }

        @Override
        public void onUploadResult(String s, int i) {
            Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onUploadResult] " + "###onUploadResult" + s + "###错误码：" + i);
            showTip("###onUploadResult" + s + "###错误码：" + i);
        }

        @Override
        public void onDownloadResult(byte[] bytes, int i) {
            Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onDownloadResult] " + "###onDownloadResult 错误码：" + i);
            showTip("###onDownloadResult 错误码：" + i);
            String filePath = "/sdcard/test/" + System.currentTimeMillis() + ".txt";
            if (null != bytes) {
                TestUtil.byte2File(bytes, filePath);
                showTip(filePath);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_demo);
        SpeechUtility.createUtility(this, null);
        tvResult = (TextView) findViewById(result);
        etParams = (EditText) findViewById(R.id.params);
        etFile = (EditText) findViewById(R.id.file);
        spinner = (Spinner) findViewById(R.id.spinner01);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, K);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = V[position];
                select();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etParams.setText(PR_LOGIN_PARAMS);
        etFile.setText(PR_UPLOAD_FILE);
        tvResult.setText("测试upload和download前需要保证login成功,测试uploadEx和downloadEx不需要login");

    }

    private void select(){
        String params = "";
        int v = Integer.parseInt(type);
        switch (v) {
            case 0:
                params = PR_LOGIN_PARAMS;
                break;
            case 1:
                params = PR_UPLOAD_PARAMS;
                break;
            case 2:
                params = PR_DOWNLOAD_PARAMS;
                break;
            case 3:
                params = PR_UPLOAD_PARAMS_Ex;
                break;
            case 4:
                params = PR_DOWNLOAD_PARAMS_Ex;
                break;

        }
        etParams.setText(params);
    }

    public void test(View v) {
        int val = Integer.parseInt(type);
        switch (val) {
            case 0:
                testLogin();
                break;
            case 1:
                testUpload();
                break;
            case 2:
                testDownload();
                break;
            case 3:
                testUploadEx();
                break;
            case 4:
                testDownloadEx();
                break;
        }

    }

    private void testLogin() {
//        if (!checkEditText(etParams)) {
//            showTip("请检查参数");
//            return;
//        }
        String params = etParams.getText().toString();

        persRes.login(params, null, listener);

    }

    private void testUpload() {
        if (!checkEditText(etParams)) {
            showTip("请检查参数");
            return;
        }
        if (!checkEditText(etFile)) {
            showTip("图片为空");
            return;
        }
        String params = etParams.getText().toString();
        String filePath = etFile.getText().toString();
        byte[] data = null;
        try {
            data = TestUtil.file2Byte(filePath);
        } catch (IOException e) {
//                e.printStackTrace();
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][testUpload] " + " e：" + e.getMessage());
        }
        persRes.upload(params, data, null, listener);
    }

    private void testDownload() {
        if (!checkEditText(etParams)) {
            showTip("请检查参数");
            return;
        }
        String params = etParams.getText().toString();
        persRes.download(params, null, listener);
    }

    private void testUploadEx() {
        if (!checkEditText(etParams)) {
            showTip("请检查参数");
            return;
        }
        if (!checkEditText(etFile)) {
            showTip("图片为空");
            return;
        }
        String params = etParams.getText().toString();
        String filePath = etFile.getText().toString();
        byte[] data = null;
        try {
            data = TestUtil.file2Byte(filePath);
        } catch (IOException e) {
//                e.printStackTrace();
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][testUploadEx] " + " e：" + e.getMessage());
        }
        persRes.uploadEx(params, data, null, listener);
    }

    private void testDownloadEx() {
        if (!checkEditText(etParams)) {
            showTip("请检查参数");
            return;
        }
        String params = etParams.getText().toString();
        persRes.downloadEx(params, null, listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResult.setText(str);
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
