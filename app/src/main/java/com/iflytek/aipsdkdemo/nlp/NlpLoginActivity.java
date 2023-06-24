package com.iflytek.aipsdkdemo.nlp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.aipsdkdemo.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NlpLoginActivity extends Activity implements OnClickListener {
    private Toast mToast;
    private String address;
    private Intent intent;
    private EditText edit_server_address;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.nlp_login);

        findViewById(R.id.btn_login).setOnClickListener(NlpLoginActivity.this);
        // findViewById(R.id.btn_ifr).setOnClickListener(TtsLoginActivity.this);
        mToast = Toast.makeText(NlpLoginActivity.this, "", Toast.LENGTH_SHORT);
        edit_server_address = (EditText) findViewById(R.id.edit_server_address);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("nlp_url", Context.MODE_PRIVATE);
        String nlp_url = sharedPreferences.getString("nlp_url", getResources().getString(R.string.https_nlp_params));
//        String nlp_url = sharedPreferences.getString("nlp_url", "");
        edit_server_address.setText(nlp_url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                // 过滤掉不合法的用户名
                address = ((EditText) findViewById(R.id.edit_server_address))
                        .getText().toString();
//                if (TextUtils.isEmpty(address)) {
//                    showTip("服务地址不能为空");
//                    return;
//                } else {
//                    Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
//                    Matcher m = p.matcher(address);
//                    if (m.find()) {
//                        showTip("不支持中文字符");
//                        return;
//                    } else if (address.contains(" ")) {
//                        showTip("不能包含空格");
//                        return;
//                    } else if (!address.matches("^[a-zA-Z0-9][a-zA-Z0-9_.@:/]{5,29}")) {
//                        //showTip("6-30个字母、数字或下划线、点、@的组合，以字母开头");
//                        //return;
//                    }
//                }
                SharedPreferences sharedPreferences = getSharedPreferences("nlp_url", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                editor.putString("nlp_url", address.trim());
                editor.commit();//提交修改

                intent = new Intent(NlpLoginActivity.this, NlpDemo.class);
                intent.putExtra("nlp_url", address);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
}
