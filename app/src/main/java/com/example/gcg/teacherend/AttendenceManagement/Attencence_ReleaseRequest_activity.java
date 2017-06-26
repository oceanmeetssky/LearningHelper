package com.example.gcg.teacherend.AttendenceManagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.example.gcg.teacherend.CustomizedClass.CustomizedActivity;
import com.example.gcg.teacherend.CustomizedClass.RecitingWebService;
import com.example.gcg.teacherend.R;
import com.example.gcg.teacherend.baidumaptest.LocationService;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attencence_ReleaseRequest_activity extends CustomizedActivity implements View.OnClickListener {
    private static final String TAG = "AttencenceRequest_activ";

    private String teacherID;
    private Spinner spinner_course;
    private Spinner spinner_class;
    private Spinner spinner_duration;
    private ImageButton button_publish;
    private Map<String,String> coursename_courseno;
    private Map<String,String> classname_classno;
    private List<String>courselist;
    private List<String>classlist;
    private List<String>timelist;

    private String selected_classNo;
    private String selected_className;
    private String selected_courseNo;
    private String selected_courseName;
    private String selected_duration;
    private String longitude;
    private String latitude;


    //目前我们似乎暂时无法将AsyncTask所做的任务分割到一个类里面，因为会有一个无法及时更新UI的问题
    //比如说
    //好像做到了。。。。
    //final String WEB_SERVICE_URL = "http://120.25.74.174/gcgWebSite/Service.asmx";
    //final String Namespace = "http://tempuri.org/";//命名空间,在发布的WebService文件里面有，由于我们没有改，默认是这个

    private LocationService locationService;
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            longitude = bdLocation.getLongitude()+"";
            latitude = bdLocation.getLatitude()+"";
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attencencerequest_activity);

        spinner_course = (Spinner) findViewById(R.id.spinner_CourseItem);
        spinner_class = (Spinner) findViewById(R.id.spinner_CLassItem);
        spinner_duration = (Spinner) findViewById(R.id.spinner_timeset);
        button_publish = (ImageButton) findViewById(R.id.button_publish_attendence_request);

        timelist = new ArrayList<String>();
        for(int i = 1;i <= 5;i++){
            timelist.add("" + (i * 3) + "分钟" );
        }

        // adpater对象  
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,timelist);
        spinner_duration.setAdapter(arrayAdapter);

        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        teacherID = intent.getStringExtra("teacherID");
        if(actionBar != null){
            actionBar.hide();
        }
        TextView title = (TextView) findViewById(R.id.textView_TitleText);
        title.setText(R.string.attendence);
        //开始给课程以及班级两个下拉菜单填充数据，调用服务查询远程数据库
        teacherID = "00001";
        recitingService_queryCourse();

        recitingService_queryClass();//注意这一步疑似多余！！！！
        addEventHandle();
        Log.d(TAG, "onCreate: ");

    }
    protected void addEventHandle(){
        //spinner_course = (Spinner) findViewById(R.id.spinner_CourseItem);
        spinner_course.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_courseName = courselist.get(position);
                selected_courseNo = coursename_courseno.get(selected_courseName);
                //Toast.makeText(getApplicationContext(),"Cno:"+selected_courseNo,Toast.LENGTH_SHORT).show();
                recitingService_queryClasswithCno(selected_courseName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_class.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_className = classlist.get(position);
                selected_classNo = classname_classno.get(selected_className);
                //Toast.makeText(getApplicationContext(),"ClassNo:"+selected_classNo,Toast.LENGTH_SHORT).show();
                //recitingService_queryClasswithCno(cname);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_duration.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_duration = timelist.get(position);
                //Toast.makeText(getApplicationContext(),"Duration:"+selected_duration,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button_publish.setOnClickListener(this);
    }

    protected  void recitingService_insertAttendenceTask(){//形参为要更新的UI控件的id
        RecitingWebService remote = new RecitingWebService(this,0);//传入标题栏的控件id，没有人会去改这个的
        String methodname = "InsertIntoAttendenceTask";
        Map<String, String> values = new HashMap<String, String>();
        //public String InsertIntoAttendenceTask(string Cno,string ClassNo,string Duration)
        values.put("Cno", selected_courseNo);
        values.put("ClassNo", selected_classNo);
        values.put("Duration", selected_duration);
        remote.Request(methodname,values);
    }

    @Override
    protected void onStart() {
        super.onStart();

        locationService = new LocationService(getApplicationContext());
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }

    protected  void recitingService_insertTeacherLocation(){//形参为要更新的UI控件的id
        RecitingWebService remote = new RecitingWebService(this,1);//传入标题栏的控件id，没有人会去改这个的
        String methodname = "InsertIntoTeacherLocation";

        Map<String, String> values = new HashMap<String, String>();
        values.put("Tno", teacherID);
        values.put("Longitude", longitude);
        values.put("Latitude", latitude);
        remote.Request(methodname,values);
        Toast.makeText(Attencence_ReleaseRequest_activity.this,"经度"+longitude,Toast.LENGTH_LONG).show();
        Toast.makeText(Attencence_ReleaseRequest_activity.this,"纬度"+latitude,Toast.LENGTH_LONG).show();
        locationService.stop();
    }

    protected  void recitingService_queryCourse(){//形参为要更新的UI控件的id
        RecitingWebService remote = new RecitingWebService(this,R.id.spinner_CourseItem);
        String methodname = "SearchLectureCourseByID";
        Map<String, String> values = new HashMap<String, String>();
        values.put("ID", teacherID);
        remote.Request(methodname,values);
    }

    protected  void recitingService_queryClass(){
        RecitingWebService remote = new RecitingWebService(this,R.id.spinner_CLassItem);
        String methodname = "SearchLeadingClassByID";
        Map<String, String> values = new HashMap<String, String>();
        values.put("ID", teacherID);
        remote.Request(methodname,values);
    }

    protected  void recitingService_queryClasswithCno(String c){
        RecitingWebService remote = new RecitingWebService(this,R.id.spinner_CLassItem);
        String methodname = "SearchLeadingClassByTeaIDAndCno";
        Map<String, String> values = new HashMap<String, String>();
        values.put("ID", teacherID);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_CourseItem);
        //String coursename = spinner.getSelectedItem().toString();
        String coursename = c;
        String cno = coursename_courseno.get(coursename);
        values.put("Cno", cno);
        remote.Request(methodname,values);
    }

    @Override
    public void updateUI(Object backtaskanswer,int ViewID) {
        super.updateUI(backtaskanswer,ViewID);
        //这个函数必定是被一个AsyncTask异步任务调用
        //同时形参会返回异步任务的后台运算结果，比如远程连接查询数据库后的结果
        switch (ViewID){
            case 0:
                break;
            case 1:
                SoapObject receiver = (SoapObject) backtaskanswer;
                //注意这里由于xml <string xmlns="http://tempuri.org/">操作成功！</string>
                //仅有一层标签包围我们需要的信息，所以只需要一次SoapObject receiver = (SoapObject) backtaskanswer;
                //相比较下面，由于我们需要的的0001和高等数学都被三个标签包围，所以需要三次SoapObject拆分
                // <ArrayOfArrayOfString xmlns="http://tempuri.org/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                // <ArrayOfString>
                // <string>0001</string>
                // <string>高等数学</string>
                // </ArrayOfString>

                // <ArrayOfString>
                // <string>0002</string>
                // <string>离散数学</string>
                // </ArrayOfString>
                // </ArrayOfArrayOfString>
                new AlertDialog.Builder(Attencence_ReleaseRequest_activity.this)
                        .setTitle("通知")
                        .setMessage(receiver.getProperty(0).toString())
                        .setPositiveButton("确定",null)
                        .show();
                break;//没有人会更新标题栏的，所以直接break
            case R.id.spinner_CourseItem:
                setDataItemForSpinner_course(backtaskanswer);break;
            case R.id.spinner_CLassItem:
                setDataItemForSpinner_class(backtaskanswer);break;
            default:
                break;
        }

    }

    protected  void setDataItemForSpinner_course(Object obj){
        coursename_courseno = new HashMap<String, String>();
        if(courselist == null){
            courselist = new ArrayList<String>();//如果没有就new
        }
        else {
            courselist.clear();//否则就清空
        }
        Object object = obj;
        if(object == null){
            Toast.makeText(this,"没有结果",Toast.LENGTH_SHORT).show();
            return ;
        }
        else{//如果有结果的话，应该会返回一个二维数组，第一列是Cno，第二列是CName
            /*xml其实是一个分层的树结构，比如
            <ArrayOfArrayOfString xmlns="http://tempuri.org/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <ArrayOfString>
                    <string>0001</string>
                    <string>高等数学</string>
                </ArrayOfString>

                <ArrayOfString>
                    <string>0002</string>
                    <string>离散数学</string>
                </ArrayOfString>
            </ArrayOfArrayOfString>
            这里的xml根节点有两个一级子节点，每个一级子结点又有两个二级子节点
            */
            SoapObject answer = (SoapObject) object;//先将xml转换成SoapObject对象
            SoapObject firstlayer = (SoapObject) answer.getProperty(0);//获取根节点，在上面的例子就是ArrayOfArrayOfString标签
            for( int i = 0;i < firstlayer.getPropertyCount();i++ ){//遍历根节点的所有下属节点，即ArrayOfString标签
                SoapObject secondlayer = (SoapObject) firstlayer.getProperty(i);//
                coursename_courseno.put(secondlayer.getProperty(1).toString(),secondlayer.getProperty(0).toString());
                courselist.add(secondlayer.getProperty(1).toString());
                Log.d(TAG, secondlayer.getProperty(1).toString());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,courselist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将Adapter添加到Spinner
            spinner_course.setAdapter(adapter);
        }
    }

    protected  void setDataItemForSpinner_class(Object obj){
        classname_classno = new HashMap<String, String>();
        if(classlist == null){
            classlist = new ArrayList<String>();
        }
        else {
            classlist.clear();
        }
        Object object = obj;
        if(object == null){
            Toast.makeText(this,"没有结果",Toast.LENGTH_SHORT).show();
            return ;
        }
        else{//如果有结果的话，应该会返回一个二维数组，第一列是Cno，第二列是CName
            SoapObject answer = (SoapObject) object;//先将xml转换成SoapObject对象
            SoapObject firstlayer = (SoapObject) answer.getProperty(0);//获取根节点，在上面的例子就是ArrayOfArrayOfString标签
            for( int i = 0;i < firstlayer.getPropertyCount();i++ ){//遍历根节点的所有下属节点，即ArrayOfString标签
                SoapObject secondlayer = (SoapObject) firstlayer.getProperty(i);//
                classname_classno.put(secondlayer.getProperty(1).toString(),secondlayer.getProperty(0).toString());
                classlist.add(secondlayer.getProperty(1).toString());
                //Log.d(TAG, secondlayer.getProperty(1).toString());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,classlist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将Adapter添加到Spinner
            spinner_class.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        //一旦点击发布按钮，就弹出一个窗口，提示是否确认
        // 确认后，调用服务往数据库插入一条记录到AttendenceTask表，截止日期用服务器的时间计算
        // 若取消，则返回当前活动
        switch (v.getId()){
            case R.id.button_publish_attendence_request:
                Toast.makeText(getApplicationContext(),"是否发布",Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(Attencence_ReleaseRequest_activity.this)
                        .setTitle("发布签到任务")
                        .setMessage("签到任务\n学科："+selected_courseName+"\n班级："+selected_className+"\n持续时间："+selected_duration)
                        //.setMultiChoiceItems(new String[]{"选项1","选项2","选项3","选项4"},null,null)
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface v, int arg1){
                                //Toast.makeText(getApplicationContext(),"确认",Toast.LENGTH_SHORT).show();
                                recitingService_insertAttendenceTask();
                                recitingService_insertTeacherLocation();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
                break;
        }

    }
}
