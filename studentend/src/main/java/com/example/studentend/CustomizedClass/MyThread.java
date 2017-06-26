package com.example.studentend.CustomizedClass;

import android.os.Handler;

/**
 * Created by GCG on 2017/5/28.
 */

public class MyThread implements Runnable {
    //private CustomizedActivity activity;
    private static final int FILE_TRANS_REPORT = 4;
    private Handler handler;
    private byte[] directory_path;
    private byte[] filePath;//文件要存放再服务器的绝对路径
    private byte[] fileContent;//文件内容

    public MyThread(byte[] directory_path, byte[] filePath, byte[] fileContent,Handler handler) {
        this.directory_path = directory_path;
        //this.activity = activity;
        this.filePath = filePath;
        this.fileContent = fileContent;
        this.handler = handler;
    }

    @Override
    public void run() {
        MySocket mySocket = new MySocket(directory_path,filePath,fileContent,handler);
        mySocket.Send();//发送数据
    }
}
