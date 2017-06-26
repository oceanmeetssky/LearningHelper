package com.example.gcg.teacherend.AttendenceManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gcg.teacherend.CustomizedClass.ClassItem;
import com.example.gcg.teacherend.CustomizedClass.CustomizedActivity;
import com.example.gcg.teacherend.CustomizedClass.MyItemAdapter;
import com.example.gcg.teacherend.CustomizedClass.RecitingWebService;
import com.example.gcg.teacherend.CustomizedClass.StudentItem;
import com.example.gcg.teacherend.CustomizedClass.StudentItemAdapter;
import com.example.gcg.teacherend.R;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attendence_CheckAttendence extends CustomizedActivity{
    private String teacherID;
    private String selected_cno;

    private Spinner spinner_course;
    private ListView listView_attendenceTask;
    private Map<String,String> coursename_courseno;
    private Map<String,String> classname_classno;
    private List<String> courselist;
    private List<String> classlist;
    private List<ClassItem> Recordlist;
    private List<StudentItem> studentItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence__check_attendence);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        TextView title = (TextView) findViewById(R.id.textView_TitleText);
        title.setText(R.string.attendence);
        Intent intent = getIntent();
        teacherID = intent.getStringExtra("teacherID");
        spinner_course = (Spinner) findViewById(R.id.spinner_courseList);
        listView_attendenceTask = (ListView) findViewById(R.id.listview_AttendenceTaskRecord);
        addEventHandle();
        recitingService_queryCourse();
    }

    protected void addEventHandle(){
        spinner_course.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //selected_courseName = ;
                selected_cno = coursename_courseno.get(courselist.get(position));
                //Toast.makeText(getApplicationContext(),"Cno:"+selected_courseNo,Toast.LENGTH_SHORT).show();
                recitingService_QueryAttendenceTask();//查询该老师该门课的所有签到请求
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        listView_attendenceTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassItem classItem = Recordlist.get(position);
                //Toast.makeText(getApplicationContext(),classItem.getDeadline()+"\n"+classItem.getClassname(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),ShowNameList.class);
                intent.putExtra("Deadline",classItem.getDeadline());
                startActivity(intent);
            }
        });

    }


    public void updateUI(Object backtaskanswer,int ViewID) {
        super.updateUI(backtaskanswer,ViewID);
        switch (ViewID){
            case R.id.spinner_courseList:
                setDataItemForSpinner_courseList(backtaskanswer);
                break;
            case R.id.textView_Cno:
                setDataItemForListView(backtaskanswer);
                break;
            //case R.id.btnEchoMessage:
            default:
                break;
        }

    }

    protected  void recitingService_queryCourse(){//形参为要更新的UI控件的id
        RecitingWebService remote = new RecitingWebService(this,R.id.spinner_courseList);
        String methodname = "SearchLectureCourseByID";
        Map<String, String> values = new HashMap<String, String>();
        values.put("ID", teacherID);
        remote.Request(methodname,values);
    }

    protected  void recitingService_QueryAttendenceTask(){//形参为要更新的UI控件的id
        RecitingWebService remote = new RecitingWebService(this,R.id.textView_Cno);
        String methodname = "QueryAttendenceTask";
        Map<String, String> values = new HashMap<String, String>();
        values.put("Tno", teacherID);
        values.put("Cno", selected_cno);
        remote.Request(methodname,values);
    }



    protected  void setDataItemForListView(Object obj){
        if(Recordlist == null){
            Recordlist = new ArrayList<ClassItem>();//如果没有就new
        }
        else {
            Recordlist.clear();//否则就清空
        }
        Object object = obj;
        if(object == null){
            Toast.makeText(this,"没有结果",Toast.LENGTH_SHORT).show();
            return ;
        }
        else{
            SoapObject answer = (SoapObject) object;//先将xml转换成SoapObject对象
            SoapObject firstlayer = (SoapObject) answer.getProperty(0);//获取根节点，在上面的例子就是ArrayOfArrayOfString标签
            for( int i = 0;i < firstlayer.getPropertyCount();i++ ){//遍历根节点的所有下属节点，即ArrayOfString标签
                SoapObject secondlayer = (SoapObject) firstlayer.getProperty(i);//
                ClassItem classItem = new ClassItem(secondlayer.getProperty(0).toString(),secondlayer.getProperty(1).toString());
                Recordlist.add(classItem);
            }
            MyItemAdapter myItemAdapter = new MyItemAdapter(getApplicationContext(),R.layout.item_listview,Recordlist);
            //myItemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将Adapter添加到Spinner
            listView_attendenceTask.setAdapter(myItemAdapter);
        }
    }

    protected  void setDataItemForSpinner_courseList(Object obj){
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
        else{
            SoapObject answer = (SoapObject) object;//先将xml转换成SoapObject对象
            SoapObject firstlayer = (SoapObject) answer.getProperty(0);//获取根节点，在上面的例子就是ArrayOfArrayOfString标签
            for( int i = 0;i < firstlayer.getPropertyCount();i++ ){//遍历根节点的所有下属节点，即ArrayOfString标签
                SoapObject secondlayer = (SoapObject) firstlayer.getProperty(i);//
                coursename_courseno.put(secondlayer.getProperty(1).toString(),secondlayer.getProperty(0).toString());
                courselist.add(secondlayer.getProperty(1).toString());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,courselist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将Adapter添加到Spinner
            spinner_course.setAdapter(adapter);
        }
    }
}
