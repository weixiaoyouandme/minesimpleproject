package com.iflytek.aipsdkdemo.tts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.aipsdkdemo.R;

public class TtsLoginActivity extends Activity implements OnClickListener {
    private Toast mToast;
    private String address;
    private Intent intent;
    private EditText edit_server_address,exParam;
    private String tts_url;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tts_login_tts);
        findViewById(R.id.btn_login).setOnClickListener(TtsLoginActivity.this);
        mToast = Toast.makeText(TtsLoginActivity.this, "", Toast.LENGTH_SHORT);
        edit_server_address = (EditText) findViewById(R.id.edit_server_address);
        exParam= (EditText) findViewById(R.id.exParam);
        tts_url = getResources().getString(R.string.tts_params);
        edit_server_address.setText(tts_url);

        // 测试服务器
        tts_url = "vid=62420,auf=4,aue=raw,svc=tts,type=1,uid=660Y5r,appid=57567804,url=https://1.192.127.106:8405";
        edit_server_address.setText(tts_url);
        exParam.setText("spd=50");
    }

    @Override
    protected void onResume() {
        super.onResume();
        edit_server_address.setText(tts_url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                // 过滤掉不合法的用户名
                address = ((EditText) findViewById(R.id.edit_server_address))
                        .getText().toString();
                intent = new Intent(TtsLoginActivity.this, TtsDemo.class);
                intent.putExtra("tts_url", edit_server_address.getText().toString().trim());
                intent.putExtra("exParam", exParam.getText().toString().trim());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void back(View view) {
        finish();
    }
}
