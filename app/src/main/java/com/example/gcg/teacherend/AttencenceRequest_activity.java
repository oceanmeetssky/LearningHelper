package com.example.gcg.teacherend;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.gcg.teacherend.CustomizedClass.RecitingWebService;
import org.ksoap2.serialization.SoapObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttencenceRequest_activity extends CustomizedActivity {
    private static final String TAG = "AttencenceRequest_activ";

    private String teacherID;
    private Spinner spinner_course;
    private Spinner spinner_class;
    private Map<String,String> coursename_courseno;
    private Map<String,String> classno_classname;
    private List<String>courselist;
    private List<String>classlist;

    //目前我们似乎暂时无法将AsyncTask所做的任务分割到一个类里面，因为会有一个无法及时更新UI的问题
    //比如说
    final String WEB_SERVICE_URL = "http://120.25.74.174/gcgWebSite/Service.asmx";
    final String Namespace = "http://tempuri.org/";//命名空间,在发布的WebService文件里面有，由于我们没有改，默认是这个



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attencencerequest_activity);

        spinner_course = (Spinner) findViewById(R.id.spinner_CourseItem);
        spinner_class = (Spinner) findViewById(R.id.spinner_CLassItem);
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

        recitingService_queryClass();
        addEventHandle();
        Log.d(TAG, "onCreate: ");

    }
    protected void addEventHandle(){
        //spinner_course = (Spinner) findViewById(R.id.spinner_CourseItem);
        spinner_course.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cname = courselist.get(position);
                Toast.makeText(getApplicationContext(),"Cname:"+courselist.get(position),Toast.LENGTH_SHORT).show();
                recitingService_queryClasswithCno(cname);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
            case R.id.spinner_CourseItem:
                setDataItemForSpinner_course(backtaskanswer);break;
            case R.id.spinner_CLassItem:
                setDataItemForSpinner_class(backtaskanswer);break;
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
        classno_classname = new HashMap<String, String>();
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
                classno_classname.put(secondlayer.getProperty(0).toString(),secondlayer.getProperty(1).toString());
                classlist.add(secondlayer.getProperty(1).toString());
                Log.d(TAG, secondlayer.getProperty(1).toString());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,classlist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将Adapter添加到Spinner
            spinner_class.setAdapter(adapter);
        }
    }

}
