package com.iflytek.aipsdkdemo.translate;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.iflytek.aipsdk.translate.ITranslateListener;
import com.iflytek.aipsdk.translate.Translate;
import com.iflytek.aipsdkdemo.R;
import com.iflytek.util.Logs;

public class TestActivity extends Activity {

    private final String TAG = TestActivity.this.getClass().getSimpleName();

    ClipboardManager clipboard;

    TextView tvResult;
    EditText etAddr;
    EditText etText;
    private final String SRV_ADDR = "type=encn,uid=660Y5r,url=10.1.138.15:5074/itr/,appid=pc20onli";
    private final String PARAMS = "uid=660Y5r,type=%s,url=%s,appid=pc20onli";
    private final String[] K = {"汉译维", "维译汉"};
    private final String[] V = {"cnug", "ugcn"};
    private String type = V[0];
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private Translate translate;
    ITranslateListener translateListener = new ITranslateListener() {
        @Override
        public void onResult(String s, int i) {
            Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onResult] " + "###" + s + "###" + i);
            if (i == 0) {
                showTip(s);
            } else {
                showTip("错误码：" + i);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_demo);
//        //建议在Application中进行此操作
//        SpeechUtility.createUtility(this, null);
        tvResult = (TextView) findViewById(R.id.result);
        etAddr = (EditText) findViewById(R.id.addr);
        etText = (EditText) findViewById(R.id.text);
        spinner = (Spinner) findViewById(R.id.spinner01);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, K);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = V[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etAddr.setText(SRV_ADDR);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        etText.setText("你好");

    }

    public void copy(View view) {
        ClipData myClip;
        String text = tvResult.getText().toString();
        myClip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(myClip);
    }

    public void test(View v) {
        if (!checkEditText(etAddr)) {
            showTip("请检查参数");
            return;
        }
        if (!checkEditText(etText)) {
            showTip("翻译文本为空");
            return;
        }
        String addr = etAddr.getText().toString();
        String text = etText.getText().toString();
        String params = addr;
//        String params = String.format(PARAMS, "cnug", "172.31.3.84:1028");
        translate = new Translate();
        translate.translate(params, text, translateListener);
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
