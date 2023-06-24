package com.iflytek.aipsdkdemo.session;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.iflytek.aipsdk.session.SessionHelper;
import com.iflytek.aipsdkdemo.R;

public class TestActivity extends Activity {

    private final String TAG = TestActivity.this.getClass().getSimpleName();

    TextView tvResult;
    EditText etCsid;
    EditText etText;

    SessionHelper sessionHelper = new SessionHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_demo);
        //建议在Application中进行此操作
//        SpeechUtility.createUtility(this, null);
        tvResult = (TextView) findViewById(R.id.result);
        etCsid = (EditText) findViewById(R.id.csid);
        etText = (EditText) findViewById(R.id.text);

    }

    public void test(View v) {
        String csid = etCsid.getText().toString();
        String text = etText.getText().toString();
        String result = sessionHelper.initSession(text, csid);
        showTip(result);
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
