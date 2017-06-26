package com.example.studentend.CustomizedClass;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by GCG on 2017/5/21.
 */

public class FileAccess {

    private CustomizedActivity activity;
    //private String Src_FilePath;//源文件
    //private String Dest_FilePath;//
    private File file;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(CustomizedActivity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //For API 23+ you need to request the read/write permissions even if they are already in your manifest.
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public FileAccess(CustomizedActivity activity){
        //this.Src_FilePath = filepath;
        this.activity = activity;
    }

    public byte[] ReadFile(String src_filepath){
        File src = new File(src_filepath);
        int byteread = 0;
        byte[] buffer = null;
        if (src.exists()) {
            try {
                InputStream in = new FileInputStream(src);
                int blockSize = in.available();//获取文件大小
                //FileOutputStream fout = new FileOutputStream(dest_filepath);
                buffer = new byte[blockSize];//准备缓冲区
                byteread = in.read(buffer);
                if (byteread != blockSize){
                    Toast.makeText(activity.getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                }
                in.close();//记得关闭文件流
                Toast.makeText(activity.getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return buffer;
    }

    public String CopyFile(String src_filepath,String dest_filepath){
        File src = new File(src_filepath);
        int byteread = 0;
        String response = "操作失败";
        byte[] buffer = null;
        if (src.exists()) {
            try {
                InputStream in = new FileInputStream(src);
                int blockSize = in.available();//获取文件大小
                FileOutputStream fout = new FileOutputStream(dest_filepath);
                buffer = new byte[blockSize];//准备缓冲区
                while ((byteread = in.read(buffer)) != -1) {//一口气读入整个缓冲区大小的字节数据
                    fout.write(buffer, 0, byteread);
                }
                in.close();//记得关闭文件流
                fout.close();
                response = "操作成功";
                //Toast.makeText(activity.getApplicationContext(), "复制成功", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return response;
    }
}
