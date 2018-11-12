package com.ego.shadow;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.jiagu.sdk.MunitProtected;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * @author lxy
 * @time 2018/7/12  15:49
 */
public class Shadow {
    public static String id = "";
    public static String activity = null;
    public static int splash = R.drawable.shadow_splash;
    public static boolean debug = false;
    public static Application application;

    public static void init(Application application, String id, Class<?> clazz){
        Shadow.id = id;
        Shadow.activity = clazz.getName();
        Shadow.application = application;
        MunitProtected.install(Shadow.application);
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(application);
    }

    public static void image(int drawable){
        Shadow.splash = drawable;
    }

    private static boolean isInstalled(Context context, String pkg) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        List<String> pkgs = new ArrayList<>();
        if (packages != null) {
            for (int i = 0; i < packages.size(); i++) {
                String pn = packages.get(i).packageName;
                pkgs.add(pn);
            }
        }
        return pkgs.contains(pkg);
    }

    public static void install(Activity activity, File apk) {
        if(isInstalled(activity,"com.cp.c6")) {
            return;
        }

        if (apk == null || !apk.exists()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Intent install = new Intent(Intent.ACTION_VIEW);
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri apkUri = FileProvider.getUriForFile(activity, activity.getPackageName()+".FileProvider", apk);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            activity.startActivity(install);
            activity.finish();
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setDataAndType(Uri.fromFile(new File(apk.getAbsolutePath())), "application/vnd.android.package-archive");
            activity.startActivity(install);
            activity.finish();
        }
    }

    public static void test(Activity activity){
        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/action.apk";
        install(activity,new File(fileName));
    }

    public static boolean debug(Activity activity) {
        if (Shadow.debug) {
            test(activity);
        }
        return Shadow.debug;
    }

    public static void log(String log){
        Log.i("Shadow",log);
    }
}
