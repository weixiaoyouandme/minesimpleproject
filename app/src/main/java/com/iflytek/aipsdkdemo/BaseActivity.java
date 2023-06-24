package com.iflytek.aipsdkdemo;

import android.app.Activity;
import android.os.Bundle;

import com.iflytek.aipsdkdemo.util.PermissionsUtil;
import com.iflytek.util.Logs;

public class BaseActivity extends Activity implements PermissionsUtil.IPermissionsCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionsUtil permissionsUtil = PermissionsUtil
                .with(this)
                .requestCode(1)
                .isDebug(false)//开启log
                .permissions(PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE
                        , PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE
//                        , PermissionsUtil.Permission.Microphone.RECORD_AUDIO
                )
                .request();
    }

    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
//        Toast.makeText(this, "已获取权限", Toast.LENGTH_SHORT).show();
        Logs.v("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + "BaseActivity" + "][onPermissionsGranted] " + "--已获取录音权限---");
    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {
//        Toast.makeText(this, "已拒绝权限", Toast.LENGTH_SHORT).show();
        Logs.v("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + "BaseActivity" + "][onPermissionsDenied] " + "--已拒绝录音权限---");
    }

}
