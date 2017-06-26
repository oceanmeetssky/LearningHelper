package com.example.studentend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.example.studentend.TaskManagement.TaskManagement;
import com.example.studentend.baidumaptest.LocationService;
//import com.example.gcg.teacherend.baidumaptest.LocationService;

import com.example.studentend.CustomizedClass.CustomizedActivity;
import com.example.studentend.CustomizedClass.RecitingWebService;
import com.example.studentend.baidumaptest.LocationService;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends CustomizedActivity implements View.OnClickListener {

    private String studentID = "00000001";
    private String Cno_waitForSign;
    private String longitude;
    private String latitude;
    private String deadline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private LocationService locationService;
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            longitude = bdLocation.getLongitude()+"";
            latitude = bdLocation.getLatitude()+"";
            Toast.makeText(getApplicationContext(),"定位成功",Toast.LENGTH_SHORT).show();
        }
    };

    protected  void recitingService_queryForAttendenceRequest(){
        RecitingWebService remote = new RecitingWebService(this,1);
        String methodname = "QueryForAttendenceRequest";
        Map<String, String> values = new HashMap<String, String>();
        Toast.makeText(getApplicationContext(),"签到查询服务调用",Toast.LENGTH_SHORT).show();
        values.put("Sno", studentID);
        remote.Request(methodname,values);
        //Toast.makeText(getApplicationContext(),"结束",Toast.LENGTH_SHORT).show();
    }

    protected  void recitingService_TryToSignUp(){
        RecitingWebService remote = new RecitingWebService(this,2);
        String methodname = "TryToSignUp";
        Map<String, String> values = new HashMap<String, String>();
        Toast.makeText(getApplicationContext(),"服务调用",Toast.LENGTH_SHORT).show();
        values.put("Sno", studentID);
        values.put("Cno", Cno_waitForSign);
        values.put("Longitude", longitude);
        values.put("Latitude", latitude);
        values.put("DeadLine", deadline);
        Toast.makeText(MainActivity.this,"经度"+longitude,Toast.LENGTH_LONG).show();
        Toast.makeText(MainActivity.this,"纬度"+latitude,Toast.LENGTH_LONG).show();
        remote.Request(methodname,values);
        //Toast.makeText(getApplicationContext(),"结束",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(longitude == null && latitude == null){
            locationService = new LocationService(getApplicationContext());
            locationService.registerListener(mListener);
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
            //locationService.start();//开始定位
        }
    }

    @Override
    public void updateUI(Object backtaskanswer,int ViewID) {
        super.updateUI(backtaskanswer,ViewID);

        //这个函数必定是被一个AsyncTask异步任务调用
        //同时形参会返回异步任务的后台运算结果，比如远程连接查询数据库后的结果
        switch (ViewID){
            case 1:
                //Toast.makeText(getApplicationContext(),"签到查询结果返回",Toast.LENGTH_SHORT).show();
                Map<String,String> map = getMapOfCnameAndCnoAndDeadLine(backtaskanswer);
                if(map.size() == 0){
                    //Toast.makeText(getApplicationContext(),"没有签到请求",Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("通知")
                            .setMessage("暂时没有签到请求哦~")
                            .setPositiveButton("确认",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface v, int arg1){
                                    //Toast.makeText(getApplicationContext(),"确认",Toast.LENGTH_SHORT).show();
                                    locationService.stop();//停止定位服务
                                }
                            })
                            .show();
                    return;
                }
                String str = "";
                Iterator iter = map.entrySet().iterator();//迭代器
                while (iter.hasNext()){
                    Map.Entry entry = (Map.Entry) iter.next();
                    str += "\n学科："+ entry.getKey();//注意我们的key才是CName
                    Cno_waitForSign = entry.getValue().toString();//保存待签到的科目号
                }
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("通知")
                        .setMessage("老师喊你签到啦~"+ str)
                        .setPositiveButton("签到",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface v, int arg1){
                                //Toast.makeText(getApplicationContext(),"确认",Toast.LENGTH_SHORT).show();
                                recitingService_TryToSignUp();
                                locationService.stop();
                            }
                        })
                        .show();
                break;//没有人会更新标题栏的，所以直接break
            case 2:
                SoapObject receiver = (SoapObject) backtaskanswer;
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("通知")
                        .setMessage(receiver.getProperty(0).toString())
                        .setPositiveButton("确定",null)
                .show();
                locationService.stop();
                break;//没有人会更新标题栏的，所以直接break
            default:
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //locationService.stop();//为什么出现弹窗就关闭，因为弹窗有可能是弹出来的签到请求，此时已经有坐标了
    }

    protected  Map getMapOfCnameAndCnoAndDeadLine(Object obj){
        Object object = obj;
        Map<String,String> cname_cno = null;
        if(object == null){
            Toast.makeText(this,"没有结果",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"有结果",Toast.LENGTH_SHORT).show();
            cname_cno = new HashMap<>();
            SoapObject answer = (SoapObject) object;//先将xml转换成SoapObject对象
            SoapObject firstlayer = (SoapObject) answer.getProperty(0);//获取根节点，在上面的例子就是ArrayOfArrayOfString标签
            for( int i = 0;i < firstlayer.getPropertyCount();i++ ){//遍历根节点的所有下属节点，即ArrayOfString标签
                SoapObject secondlayer = (SoapObject) firstlayer.getProperty(i);//
                cname_cno.put(secondlayer.getProperty(1).toString(),secondlayer.getProperty(0).toString());
                deadline = secondlayer.getProperty(2).toString();
                Toast.makeText(this,secondlayer.getProperty(1).toString(),Toast.LENGTH_SHORT).show();
            }
        }
        return cname_cno;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_Attendence:
                locationService.start();
                recitingService_queryForAttendenceRequest();
                break;
            case R.id.button_Task:
                Intent intent = new Intent(getApplicationContext(),TaskManagement.class);
                intent.putExtra("studentID",studentID);
                startActivity(intent);
                break;
        }

    }
}
