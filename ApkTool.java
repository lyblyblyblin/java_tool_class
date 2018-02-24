package com.killyulong.root_y90;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.net.Uri;
import android.util.Log;

/**
 * Created by lybly on 2018/2/9.
 */

public class ApkTool {
    /* 卸载apk */
    public static void uninstallApk(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }

    /* 安装apk */
    public static void installApk(Context context, String fileName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + fileName),"application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    //获取包名
    //传入的 如/storage/emulated/0/.svgbrige/self.apk
    public static String getapkname(Context context,String path) throws Exception {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if(packageInfo != null) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            return appInfo.packageName;
        }
        return null;
    }


}
