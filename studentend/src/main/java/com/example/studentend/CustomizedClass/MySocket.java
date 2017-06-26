package com.example.studentend.CustomizedClass;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by GCG on 2017/5/27.
 */

public class MySocket{
    private static final int FILE_TRANS_REPORT = 4;
    private Socket clientSocket;
    private String receiveMessage;
    private String IP_Address = "120.25.74.174";
    //private String IP_Address = "10.210.153.193";
    //private CustomizedActivity activity;
    private Handler handler;
    private int Port = 5010;
    private byte[] directory_path;
    private byte[] filePath;//文件要存放再服务器的绝对路径
    private byte[] fileContent;//文件内容



    //学生端用这个构造函数
    public MySocket(byte[] directory_path, byte[] filePath, byte[] fileContent,Handler handler) {
        //this.activity = activity;
        this.handler = handler;
        this.directory_path = directory_path;
        this.filePath = filePath;
        this.fileContent = fileContent;
        try {
            // 客户端 Socket 可以通过指定 IP 地址或域名两种方式来连接服务器端,实际最终都是通过 IP 地址来连接服务器
            // 新建一个Socket，指定其IP地址及端口号
            clientSocket = new Socket(IP_Address, Port);
            // 客户端socket在接收数据时，有两种超时：1. 连接服务器超时，即连接超时；2. 连接服务器成功后，接收服务器数据超时，即接收超时
            // 设置 socket 读取数据流的超时时间
            clientSocket.setSoTimeout(5000);
            // 发送数据包，默认为 false，即客户端发送数据采用 Nagle 算法；
            // 但是对于实时交互性高的程序，建议其改为 true，即关闭 Nagle 算法，客户端每发送一次数据，无论数据包大小都会将这些数据发送出去
            clientSocket.setTcpNoDelay(true);
            // 设置客户端 socket 关闭时，close() 方法起作用时延迟 30 秒关闭，如果 30 秒内尽量将未发送的数据包发送出去
            clientSocket.setSoLinger(true, 30);
            // 设置输出流的发送缓冲区大小，默认是4KB，即4096字节
            clientSocket.setSendBufferSize(4096);
            // 设置输入流的接收缓冲区大小，默认是4KB，即4096字节
            clientSocket.setReceiveBufferSize(4096);
            // 作用：每隔一段时间检查服务器是否处于活动状态，如果服务器端长时间没响应，自动关闭客户端socket
            // 防止服务器端无效时，客户端长时间处于连接状态
            clientSocket.setKeepAlive(true);
            // 代表可以立即向服务器端发送单字节数据
            clientSocket.setOOBInline(true);
            // 数据不经过输出缓冲区，立即发送
            clientSocket.sendUrgentData(0x44);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //new Thread(this).start();//启动线程进行文件上传工作
    }

    private void ReturnMessage(String msg){
        //activity.updateUI(msg,FILE_TRANS_REPORT);
        //Message message = handler.obtainMessage();
        Message message = new Message();
        message.what = FILE_TRANS_REPORT;//消息标识符，用于对面区分是具体什么消息，好做相应的分类处理
        message.obj = msg;//消息本体，Object类型
        handler.sendMessage(message);//子线程向主线程发送消息
    }

    public void Send() {
        //先传送文件绝对路径
        // 客户端向服务器端发送数据，获取客户端向服务器端输出流
        OutputStream osSend = null;
        DataOutputStream dataOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        InputStream inReceive = null;
        DataInputStream dataInputStream = null;
        try {
            /*
            int acknowledge = dataInputStream.read();//等待服务器回复
            if(acknowledge == 0){
                ReturnMessage("Socket传输失败");
                return;
                //关于return 和 finally的执行问题：
                //当try和catch块中有return时,先会执行return后面的表达式,然后执行finally块，最后跳出。
            }
            */
            osSend = clientSocket.getOutputStream();
            dataOutputStream = new DataOutputStream(osSend);
            bufferedOutputStream = new BufferedOutputStream(dataOutputStream);
            inReceive = clientSocket.getInputStream();
            dataInputStream = new DataInputStream(inReceive);
            // 向服务器端写数据，写入一个缓冲区
            // 注：此处字符串最后必须包含“\r\n\r\n”，告诉服务器HTTP头已经结束，可以处理数据，否则会造成下面的读取数据出现阻塞
            // 在write() 方法中可以定义规则，与后台匹配来识别相应的功能，例如登录Login() 方法，可以写为write("Login|LeoZheng,0603 \r\n\r\n"),供后台识别;
            bufferedOutputStream.write(directory_path);//向服务器传送文件存储路径
            // 发送缓冲区中数据 - 前面说调用 flush() 无效，可能是调用的方法不对吧！
            bufferedOutputStream.flush();
            int acknowledge = dataInputStream.read();//等待服务器回复
            if(acknowledge == 0){
                ReturnMessage("文件夹路径传输失败");
                return;
                //关于return 和 finally的执行问题：
                //当try和catch块中有return时,先会执行return后面的表达式,然后执行finally块，最后跳出。
            }

            bufferedOutputStream.write(filePath);//向服务器传送文件存储路径
            bufferedOutputStream.flush();
            acknowledge = dataInputStream.read();//等待服务器回复
            if(acknowledge == 0){
                ReturnMessage("文件名传输失败");
                return;
            }
            //等待服务器随便回传一个字节，以便确认服务器已收到文件存储路径，并已完成文件创建/删除的相关工作
            bufferedOutputStream.write(fileContent);//接着传送文件本体
            bufferedOutputStream.flush();
            acknowledge = dataInputStream.read();//等待服务器返回
            if(acknowledge == 0){
                ReturnMessage("文件内容传输失败");
                return;
            }
            ReturnMessage("文件上传成功!");

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (bufferedOutputStream != null){
                    bufferedOutputStream.close();
                }
                if (dataOutputStream != null){
                    dataOutputStream.close();
                }
                if (osSend != null){
                    osSend.close();
                }
                if (dataInputStream != null){
                    dataInputStream.close();
                }
                if (inReceive != null){
                    inReceive.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ReturnMessage("文件已上传!");//为什么这个消息可以放出去，但是外面try里面的却不行？
        }

    }
}
