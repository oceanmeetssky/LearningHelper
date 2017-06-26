package com.example.gcg.teacherend.TaskManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gcg.teacherend.CustomizedClass.CustomizedActivity;
import com.example.gcg.teacherend.CustomizedClass.RecitingWebService;
import com.example.gcg.teacherend.CustomizedClass.TaskRecordItem;
import com.example.gcg.teacherend.CustomizedClass.TaskRecordItemAdapter;
import com.example.gcg.teacherend.R;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManagement_CheckTask extends CustomizedActivity {

    private static final int QUERY_COURSE = 0;
    private static final int QUERY_CLASS = 1;
    private static final int QUERY_TASKLIST = 2;

    private TextView label_course;
    private TextView label_class;
    private Spinner spinner_courseList;
    private Spinner spinner_classList;
    private ListView tasklistview;

    private String teacherID;
    private String selected_classNo;
    private String selected_className;
    private String selected_courseNo;
    private String selected_courseName;
    private Map<String,String> coursename_courseno;
    private Map<String,String> classname_classno;
    private List<String> courselist;
    private List<String>classlist;
    private List<TaskRecordItem> taskRecordItemList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management__check_task);

        label_course = (TextView) findViewById(R.id.textView_taskmanagement_check_course);
        label_class = (TextView) findViewById(R.id.textView_taskmanagement_check_class);
        spinner_courseList = (Spinner) findViewById(R.id.spinner_taskmanagement_check_course);
        spinner_classList = (Spinner) findViewById(R.id.spinner_taskmanagement_check_class);
        tasklistview = (ListView) findViewById(R.id.listview_taskList);

        Intent intent = getIntent();
        teacherID = intent.getStringExtra("teacherID");

        addEventHandle();

        recitingService_queryCourse();
    }

    protected  void recitingService_queryCourse(){//形参为要更新的UI控件的id
        RecitingWebService remote = new RecitingWebService(this,QUERY_COURSE);
        String methodname = "SearchLectureCourseByID";
        Map<String, String> values = new HashMap<String, String>();
        values.put("ID", teacherID);
        remote.Request(methodname,values);
    }

    protected  void recitingService_queryClasswithCno(String cname){
        RecitingWebService remote = new RecitingWebService(this,QUERY_CLASS);
        String methodname = "SearchLeadingClassByTeaIDAndCno";
        Map<String, String> values = new HashMap<String, String>();
        values.put("ID", teacherID);
        //String coursename = cname;
        String cno = coursename_courseno.get(cname);
        values.put("Cno", cno);
        remote.Request(methodname,values);
    }

    protected  void recitingService_QueryAllTaskWithTno_Cno_ClassNo(){
        // public string[][] QueryAllTaskWithTno_Cno_ClassNo(string Tno,string Cno,string ClassNo)
        //Toast.makeText(getApplicationContext(),"开始查询",Toast.LENGTH_SHORT).show();
        RecitingWebService remote = new RecitingWebService(this,QUERY_TASKLIST);
        String methodname = "QueryAllTaskWithTno_Cno_ClassNo";
        Map<String, String> values = new HashMap<String, String>();
        values.put("Tno", teacherID);
        values.put("Cno", selected_courseNo);
        values.put("ClassNo", selected_classNo);
        Toast.makeText(getApplicationContext(),"Tno:"+teacherID+"\nCno:"+selected_courseNo+"\nClassNo:"+selected_classNo,Toast.LENGTH_SHORT).show();
        remote.Request(methodname,values);
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
            spinner_courseList.setAdapter(adapter);
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
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,classlist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将Adapter添加到Spinner
            spinner_classList.setAdapter(adapter);
        }
    }

    protected  void setDataItemForTaskListView(List<TaskRecordItem> list){
        try{
            TaskRecordItemAdapter myItemAdapter = new TaskRecordItemAdapter(getApplicationContext(),R.layout.item_tasklist_listview,list);
            tasklistview.setAdapter(myItemAdapter);
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }

    }

    protected  void setDataForList(Object obj){
        List list = null;
        if(list == null){
            list = new ArrayList<TaskRecordItem>();//如果没有就new
        }
        else {
            list.clear();//否则就清空
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
                TaskRecordItem taskRecordItem = new TaskRecordItem(secondlayer.getProperty(0).toString(),secondlayer.getProperty(1).toString(),secondlayer.getProperty(2).toString(),"第"+ (i+1) + "次");
                list.add(taskRecordItem);
            }
        }
        taskRecordItemList = list;
    }

    private void addEventHandle(){
        spinner_courseList.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),"切换科目",Toast.LENGTH_SHORT).show();
                selected_courseName = courselist.get(position);
                selected_courseNo = coursename_courseno.get(selected_courseName);
                recitingService_queryClasswithCno(selected_courseName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_classList.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),"切换班级",Toast.LENGTH_SHORT).show();
                selected_className = classlist.get(position);
                selected_classNo = classname_classno.get(selected_className);
                //Toast.makeText(getApplicationContext(),"科目："+selected_courseName + "\n班级："+selected_className,Toast.LENGTH_SHORT).show();
                if (taskRecordItemList != null){
                    taskRecordItemList.clear();
                }
                recitingService_QueryAllTaskWithTno_Cno_ClassNo();
                //Toast.makeText(getApplicationContext(),"查询任务",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        tasklistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskRecordItem taskRecordItem = taskRecordItemList.get(position);
                //Toast.makeText(getApplicationContext(),classItem.getDeadline()+"\n"+classItem.getClassname(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),ShowTaskFileList.class);
                intent.putExtra("teacherID",teacherID);
                intent.putExtra("ClassNo",selected_classNo);
                intent.putExtra("TaskListNo",taskRecordItem.getTaskListNo());
                startActivity(intent);
            }
        });

    }

    @Override
    public void updateUI(Object backtaskanswer, int ViewID) {
        super.updateUI(backtaskanswer, ViewID);
        switch (ViewID){
            case QUERY_COURSE:
                setDataItemForSpinner_course(backtaskanswer);
                break;
            case QUERY_CLASS:
                setDataItemForSpinner_class(backtaskanswer);
                break;
            case QUERY_TASKLIST:
                //Toast.makeText(getApplicationContext(),"结果返回",Toast.LENGTH_SHORT).show();
                setDataForList(backtaskanswer);
                setDataItemForTaskListView(taskRecordItemList);
                break;
        }
    }
}
