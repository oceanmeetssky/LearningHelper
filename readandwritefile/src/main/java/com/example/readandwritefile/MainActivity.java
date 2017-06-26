package com.example.readandwritefile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    String filepath;
    File file;
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
    public static void verifyStoragePermissions(Activity activity) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        verifyStoragePermissions(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //Action属性代表系统要执行的动作 ACTION_GET_CONTENT：让用户在运行的程序中选择数据
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //Category属性用于指定动作Action被执行的环境,CATEGORY_OPENABLE：增加一个可打开的分类
                //通常和ACTION_GET_CONTENT组合使用
                startActivityForResult(intent, 1);
            }
        });
        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File src = new File(filepath);
                int index = filepath.lastIndexOf('/');//复制后的文件仍在同一个文件夹
                int byteread = 0;
                int bytesum = 0;
                String newpath = filepath.substring(0, index) + "/newfile1.txt";
                if (src.exists()) {
                    try {
                        InputStream in = new FileInputStream(src);
                        int blockSize = in.available();//获取文件大小
                        textView.setText(textView.getText()+"文件大小："+ blockSize);
                        FileOutputStream fout = new FileOutputStream(newpath);
                        byte[] buffer = new byte[blockSize];
                        while ((byteread = in.read(buffer)) != -1) {
                            bytesum += byteread; //字节数 文件大小
                            System.out.println(bytesum);
                            fout.write(buffer, 0, byteread);
                        }
                        in.close();
                        fout.close();
                        Toast.makeText(getApplicationContext(), "复制成功", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        file = null;
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String string = uri.toString();

            String a[];
            //判断文件是否在sd卡中
            //Environment.getExternalStorageDirectory()获取SD卡存储根目录
            if (string.indexOf(String.valueOf(Environment.getExternalStorageDirectory())) != -1) {
                //对Uri进行切割
                a = string.split(String.valueOf(Environment.getExternalStorageDirectory()));
                //获取到file
                //file = new File(Environment.getExternalStorageDirectory(), a[1]);
                textView.setText(Environment.getExternalStorageDirectory() + a[1]);
            } else if (string.indexOf(String.valueOf(Environment.getDataDirectory())) != -1) { //判断文件是否在手机内存中
                //对Uri进行切割
                a = string.split(String.valueOf(Environment.getDataDirectory()));
                //获取到file
                //file = new File(Environment.getDataDirectory(), a[1]);
                textView.setText(Environment.getDataDirectory() + a[1]);
            } else {
                //出现其他没有考虑到的情况
                Toast.makeText(this, "文件路径解析失败！", Toast.LENGTH_SHORT).show();
            }
        }
        filepath = textView.getText().toString();
        return;
    }

}
