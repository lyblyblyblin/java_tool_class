package com.killyulong.root_y90;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by lybly on 2018/2/8.
 * 增删改查文件管理
 */

public class FileStorageHelper {
    private static final String SEPARATOR = File.separator;//路径分隔符

//    **
//    * 复制res/raw中的文件到指定目录
//    * @param context 上下文
//    * @param id 资源ID
//    * @param fileName 文件名
//    * @param storagePath 目标文件夹的路径
//    */

	/*
	demo
	FileStorageHelper.copyFilesFromRaw(this,R.raw.doc_test,"doc_test",path + "/" + "mufeng");
    上面代码是将raw中的doc_test复制到/mufeng下
    */
    public static void copyFilesFromRaw(Context context, int id, String fileName, String storagePath){
        InputStream inputStream=context.getResources().openRawResource(id);
        File file = new File(storagePath);
        if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
            file.mkdirs();
        }
        readInputStream(storagePath + SEPARATOR + fileName, inputStream);
    }

    /**
     * 读取输入流中的数据写入输出流
     *
     * @param storagePath 目标文件路径
     * @param inputStream 输入流
     */
    public static void readInputStream(String storagePath, InputStream inputStream) {
        File file = new File(storagePath);
        try {
            if (!file.exists()) {
                // 1.建立通道对象
                FileOutputStream fos = new FileOutputStream(file);
                // 2.定义存储空间
                byte[] buffer = new byte[inputStream.available()];
                // 3.开始读文件
                int lenght = 0;
                while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                    // 将Buffer中的数据写到outputStream对象中
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();// 刷新缓冲区
                // 4.关闭流
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //删除一个文件 (不是文件夹)
    public static boolean delAFile(String delAFilePath) {
        //判断文件存不存在 不存在写入日志
        //有无斜杠判断
        //删除失败与文件不存在的日志情况
        //这里写了两次日志
        if (!delAFilePath.endsWith(SEPARATOR))
        {
            File file = new File(delAFilePath);
            if (file.delete())
                return true;
        }else {
            //写入日志
            String msm="delAFile: "+delAFilePath+"  no a file,is dir";
            Log.d(TAG, msm);

        }
        //写入日志
        String msm="delAFile: "+delAFilePath+"  del false";
        Log.d(TAG, msm);
        return false;
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    //更改
    ///sdcard/aaaaaa是不合格的
    ///sdcard/aaaaaa/才行
    //写入日志
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = FileStorageHelper.delAFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = FileStorageHelper.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！" + dir );
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }


    //改地方 调用自己的删除
    //遍历删除文件夹下包含指定字符的文件
    //传入  文件路径
    //文件名
    //从那开始判断
    public static void travDeleteFilesLikeName(String dirPath,String likeName,int startNum){
        File file = new File(dirPath);
        if(file.isFile()){
            //是文件
            String temp = file.getName().substring(0,file.getName().lastIndexOf("."));
            if(temp.indexOf(likeName) == startNum){
                file.delete();
            }
        } else {
            //是目录
            File[] files = file.listFiles();
            for(int i = 0; i < files.length; i++){
                travDeleteFilesLikeName(files[i].toString(), likeName,startNum);
            }
        }
    }
    //不遍历删除文件夹下包含指定字符的文件
    //传入  文件路径
    //文件名
    //从那开始判断
    public static void deleteFilesLikeName(String dirPath,String likeName,int startNum){
        File file = new File(dirPath);
        if(file.isFile()){
            //是文件
            String temp = file.getName().substring(0,file.getName().lastIndexOf("."));
            if(temp.indexOf(likeName) == startNum){
                file.delete();
            }
        } else {
            //是目录
            File[] files = file.listFiles();
            for(int i = 0; i < files.length; i++){

                if(files[i].toString().indexOf(likeName) == (startNum+dirPath.length()))
                {
                    deleteDirectory(files[i].toString());
                    Log.d(TAG, files[i].toString());
                }
            }
        }
    }


    /**
     * 获取内置SD卡路径
     * @return
     */
    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取外置SD卡路径
     * @return  应该就一条记录或空
     */
    public static List<String> getExtSDCardPath()
    {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard"))
                {
                    String [] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory())
                    {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

    //返回文件夹里面的文件(不包括文件夹)
    public static List<File> showDirectory(String inputDdir) {
        String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+inputDdir;
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("目录：" + dir + "不正确");
            return null;
        }
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        List<File> returnFiles=new ArrayList<File>();
        int n=0;
        for(int i=0;i<files.length;i++)
        {
            if(!files[i].isDirectory())
            {
                returnFiles.add(files[i]);
                //Log.d(TAG, "showDirectory: "+files[i].toString());
                n++;
            }

        }
        return returnFiles;
    }
}

