package com.iflytek.aipsdkdemo.nlp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.aipsdk.nlp.INlpListener;
import com.iflytek.aipsdk.nlp.NlpHelper;
import com.iflytek.aipsdkdemo.R;


public class NlpDemo extends Activity {


    private TextView mShowMsgTextView;


    private EditText et;

    private NlpHelper nlpHelper;

    private String params;
    private String text;
    private String nlp_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.nlp_demo);
//        //建议在Application中进行此操作
//        SpeechUtility.createUtility(this, null);
        initUi();
        nlpHelper = new NlpHelper();
        SharedPreferences sharedPreferences = getSharedPreferences("nlp_url", Activity.MODE_PRIVATE);
        params = sharedPreferences.getString("nlp_url", getResources().getString(R.string.nlp_params));
//        params = sharedPreferences.getString("nlp_url", "");
        params = params.trim();
    }

    private void initUi() {
        mShowMsgTextView = (TextView) findViewById(R.id.showMsg);
        et = (EditText) findViewById(R.id.nlpeditText);
    }

    public void start(View view) {
        if (null != nlpHelper) {
            nlpHelper.getResult(params, et.getText().toString().trim(), iNlpListener);
        }

    }

    INlpListener iNlpListener = new INlpListener() {
        @Override
        public void onResult(final String s, final int i) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mShowMsgTextView.setText(s);
                    Toast.makeText(getApplicationContext(), "code:" + i, Toast.LENGTH_SHORT).show();
                }
            });

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}