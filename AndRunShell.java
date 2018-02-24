package com.linyb;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by lybly on 2018/2/24.
 */

public class AndRunShell {

    private final static String Test_su = "test su";

    public static DataInputStream suTerminal(String command) {

        Process process = null;

        try {
            process = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }

//执行到这，Superuser会跳出来，选择是否允许获取最高权限
        OutputStream outstream = process.getOutputStream();
        DataOutputStream DOPS = new DataOutputStream(outstream);
        InputStream instream = process.getInputStream();
        DataInputStream DIPS = new DataInputStream(instream);
        String temp = command + "\n";
//加回车
        try {
            DOPS.writeBytes(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
//执行
        try {
            DOPS.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
//刷新，确保都发送到outputstream
        try {
            DOPS.writeBytes("exit\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
//退出
        try {
            DOPS.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return DIPS;
    }

    public static boolean isRooted() {
//检测是否ROOT过
        DataInputStream stream;
        boolean flag = false;
        try {
            stream = suTerminal("ls /");
//目录哪都行，不一定要需要ROOT权限的
            if (stream.readLine() != null) flag = true;
//根据是否有返回来判断是否有root权限
        } catch (Exception e1) {
// TODO Auto-generated catch block
            e1.printStackTrace();

        }

        return flag;
    }


    //如shell一样执行命令
    public static void nosuTerminal(final Context context, final String shellstr) {
        //Thread为子线程，进行耗时操作
        final Activity activityincome = (Activity) context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Activity activity = (Activity) context;
                try {
                    //可以直接使用 su -c来执行内容
                    //Process process = Runtime.getRuntime().exec("su -c am start com.android.browser/.BrowserPreferencesPage");
                    Process process = Runtime.getRuntime().exec(shellstr);

                    if (process.waitFor() == 0) {
                        activityincome.finish();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "失败，可能设备不支持", Toast.LENGTH_SHORT).show();
                            activityincome.finish();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}

