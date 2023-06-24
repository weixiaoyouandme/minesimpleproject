package com.iflytek.aipsdkdemo.ivw;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.aipsdk.ivw.IvwAudioHelper;
import com.iflytek.aipsdk.ivw.IvwAudioInitListener;
import com.iflytek.aipsdk.ivw.IvwAudioListener;
import com.iflytek.aipsdk.util.ErrorCode;
import com.iflytek.aipsdk.util.FileUtil;
import com.iflytek.aipsdkdemo.R;
import com.iflytek.util.Logs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class IvwActivity extends Activity implements IvwAudioListener, IvwAudioInitListener, View.OnClickListener {
    private final String TAG = IvwActivity.this.getClass().getSimpleName();

    private IvwAudioHelper ivwAudioHelper = new IvwAudioHelper(this);
    private Button init;
    private Button start_record;
    private Button stop_record;
    private Button start_audio;
    private Button uninit;
    private Button audiotest;
    private TextView text;

    private EditText epath;
    private EditText et;
    private Toast mToast;

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                mToast.setText(str);
//                mToast.show();
                text.setText(str);

//                String content = str + "\n" + str + "\n" + str + "\n" + str + "\n" + str + "\n" + str + "\n" + str + "\n" + str + "\n" + str;
//                text.setText(content);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ivw_layout);
        init = (Button) findViewById(R.id.init);
        start_record = (Button) findViewById(R.id.start_record);
        stop_record = (Button) findViewById(R.id.stop_record);
        start_audio = (Button) findViewById(R.id.start_audio);
        uninit = (Button) findViewById(R.id.uninit);
        audiotest = (Button) findViewById(R.id.test);
        text = (TextView) findViewById(R.id.text);
        init.setOnClickListener(this);
        start_record.setOnClickListener(this);
        stop_record.setOnClickListener(this);
        start_audio.setOnClickListener(this);
        uninit.setOnClickListener(this);
        audiotest.setOnClickListener(this);
        et = (EditText)findViewById(R.id.et_param);
        epath = (EditText)findViewById(R.id.et_path);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }


    @Override
    public void onError(int code) {
        Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onWakeUp] " + "onError" +"code : "+code);

        if(code != 0){
//            text.setText("错误码：" + code);
            showTip("错误码：" + code);
        }

    }

    /**
     * 录音启动
     */
    @Override
    public void onRecordStart() {
        Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onRecordStart] onRecordStart");
    }

    /**
     * 录音机释放通知
     */
    @Override
    public void onRecordReleased() {
        Logs.d("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onRecordReleased] onRecordReleased");
    }

    @Override
    public void onWakeUp(String str, int code) {
        Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onWakeUp] " + "-onWakeUp---" + str + ";code : "+code);
        wirteResult(str);
//        text.setText(str + ";code : " + code);
        showTip(str + ";code : " + code);
    }


    @Override
    public void onIvwAudioInit(String str, int code) {
        Log.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onIvwAudioInit] " + "-onIvwAudioInit---" + str+";code："+code);
//        text.setText(str + ";code : " + code);
        showTip(str + ";code : " + code);
        if (code == 0) {
//            init.setEnabled(false);
        } else {
//            showTip("错误信息 ： " + str + "; code ：" + code);
//            init.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != ivwAudioHelper) {
            ivwAudioHelper.destroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.init:
                /**
                 * 只能初始化一次，初始化多次会有异常。
                 */
                String param = et.getText().toString();
                ivwAudioHelper.initIvw(param, this);
                break;
            case R.id.start_record:
                ivwAudioHelper.startRecord(this);
                break;
            case R.id.stop_record:
                ivwAudioHelper.stopRecord();
                break;
            case R.id.start_audio:
                byte[] da = FileUtil.readFile(epath.getText().toString());
                if (null != da) {
                    Log.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onClick] " + "len=:" + da.length);
                    ivwAudioHelper.startAudio(da, this);
                }else {
                    Toast.makeText(getApplication(),"检查音频文件",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.test:
                if (null != ivwAudioHelper) {
                    ivwAudioHelper.destroy();
                }
                String Dir = epath.getText().toString();
                if(Dir==null || Dir.equals("") || !(new File(Dir).isDirectory())){
                    Toast.makeText(getApplication(),"文件夹路径为空或不是文件夹路径",Toast.LENGTH_SHORT).show();
                    return;
                }
                GetFiles(Dir,".wav",true);
                for(int i = 0 ;i<lstFile.size();++i){
                    param = et.getText().toString() + ",wivw_param_value=" + lstFile.get(i);
                    ivwAudioHelper.initIvw(param, this);
                    Logs.e("--------","get(i) : " + lstFile.get(i));

                    wirteResult(lstFile.get(i));

                    byte[] data = FileUtil.readFile(lstFile.get(i));
                    if (null != data) {
                        Log.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][onClick] " + "len=:" + data.length);
                        ivwAudioHelper.startAudio(data, this );
                    }else {
                        Toast.makeText(getApplication(),"检查音频文件",Toast.LENGTH_SHORT).show();
                    }
                    if (null != ivwAudioHelper) {
                        ivwAudioHelper.destroy();
                    }
                }
                break;
            case R.id.uninit:
                ivwAudioHelper.destroy();
                break;

        }
    }


    private List<String> lstFile =new ArrayList<String>(); //结果 List

    public void GetFiles(String Path, String Extension,boolean IsIterative) //搜索目录，扩展名，是否进入子文件夹
    {
        File[] files =new File(Path).listFiles();

        for (int i =0; i < files.length; i++)
        {
            File f = files[i];
            if (f.isFile())
            {
                if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)) //判断扩展名
                    lstFile.add(f.getPath());

                if (!IsIterative)
                    break;
            }
            else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) //忽略点文件（隐藏文件/文件夹）
                GetFiles(f.getPath(), Extension, IsIterative);
        }
    }



    public void wirteResult(String str) {
        try {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/sdcard/xanh/ivwresult.txt"), true));
                writer.write(str + "\n");
                writer.close();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}