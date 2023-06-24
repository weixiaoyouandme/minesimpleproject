package com.iflytek.aipsdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

import com.iflytek.util.Logs;

import java.util.HashMap;
import java.util.Map;

public abstract class MenuActivity extends Activity {
    protected ScrollView scrollView;
    protected LinearLayout linearLayout;
    protected Map<String, String> menuMap;
    protected Map<Integer, String> indexMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scrollView = new ScrollView(this);
        setContentView(scrollView);

        addLinearLayout();

        initMenu();
        initIndex();
    }

    protected OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent = new Intent();
            try {
                Class c = Class.forName(menuMap.get(indexMap.get(id)));
                intent.setClass(getApplicationContext(), c);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
//                e.printStackTrace();
                Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + "MenuActivity" + "][OnClickListener] " + " e:" + e.getMessage());
            }
        }
    };

    public abstract void initMenu();

    protected void initIndex() {
        indexMap = new HashMap<Integer, String>();
        int i = 0;
        for (Map.Entry<String, String> entry : menuMap.entrySet()) {
            // System.out.println("Key = " + entry.getKey() + ", Value = " +
            // entry.getValue());
            indexMap.put(i, entry.getKey());
            addButton(i, entry.getKey());
            i++;
        }
    }

    private void addLinearLayout() {
        linearLayout = new LinearLayout(this);
        // 注意，对于LinearLayout布局来说，设置横向还是纵向是必须的！否则就看不到效果了。
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);
    }

    protected void addButton(int id, String text) {
        Button button = new Button(this);
        button.setId(id);
        button.setText(text);
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(lp);
        button.setOnClickListener(onClickListener);
        linearLayout.addView(button);
    }
}
