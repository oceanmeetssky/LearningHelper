package com.example.studentend.TaskManagement;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentend.CustomizedClass.CustomizedActivity;
import com.example.studentend.CustomizedClass.RecitingWebService;
import com.example.studentend.CustomizedClass.TaskRecordItem;
import com.example.studentend.CustomizedClass.TaskRecordItemAdapter;
import com.example.studentend.R;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManagement extends CustomizedActivity {

    private static final int SEARCH_COURSE = 0;
    private static final int SEARCH_ALLTASKS = 1;
    private static final int SEARCH_UNFINISHED_TASKS = 2;

    private boolean SearchAllRecord = true;//是不是显示所有记录？
    private boolean isDirty_taskRecordItemList = true;//因为采用了缓冲，所以要判断，当前List的数据是否还有效。
    private boolean isDirty_taskRecordItemList_unfinished = true;//因为采用了缓冲，所以要判断，当前List的数据是否还有效。

    private String studentID;
    private String selected_ClassNo;
    private Map<String,String> cname_classno;
    private List<String> cnamelist;//存放spinner的数据集课程名集合
    private List<TaskRecordItem> taskRecordItemList;
    private List<TaskRecordItem> taskRecordItemList_unfinished;
    private Spinner spinner_coursename_classno;
    private ListView TaskListView;
    private Button button_allTask;
    private Button button_unfinishedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        TextView title = (TextView) findViewById(R.id.textView_TitleText);
        title.setText(R.string.Task);
        spinner_coursename_classno = (Spinner) findViewById(R.id.spinner_Task_CourseName_ClassNo);
        button_allTask = (Button) findViewById(R.id.button_Task_All_Record);
        button_unfinishedTask = (Button) findViewById(R.id.button_Task_Unfinished_Record);
        TaskListView = (ListView) findViewById(R.id.ListView_TaskList);
        Intent intent = getIntent();
        studentID = intent.getStringExtra("studentID");
        addEventHandle();
        recitingService_SearchLearningCourseByID();
    }

    protected void addEventHandle(){
        button_allTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskRecordItemList != null && isDirty_taskRecordItemList == false){
                    setDataItemForTaskListView(taskRecordItemList);
                }
                else {
                    recitingService_QuertTaskListWithClassNo();
                }
                SearchAllRecord = true;//是显示所有记录
            }
        });
        button_unfinishedTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskRecordItemList_unfinished != null && isDirty_taskRecordItemList_unfinished == false){
                    setDataItemForTaskListView(taskRecordItemList_unfinished);
                }
                else {
                    recitingService_QuertTaskListForUnfinishedTask();
                }
                SearchAllRecord = false;//只显示未完成记录
                //recitingService_QuertTaskListForUnfinishedTask();

            }
        });
        spinner_coursename_classno.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_ClassNo = cname_classno.get(cnamelist.get(position));
                Toast.makeText(TaskManagement.this,"ClassNo:"+selected_ClassNo,Toast.LENGTH_SHORT).show();
                //每一次切换课程，当前LIst里面存放的任务记录就都无效了，需要重新查询
                isDirty_taskRecordItemList = true;
                isDirty_taskRecordItemList_unfinished = true;
                if (SearchAllRecord){
                    recitingService_QuertTaskListWithClassNo();
                }
                else {
                    recitingService_QuertTaskListForUnfinishedTask();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        TaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskRecordItem taskRecordItem = taskRecordItemList.get(position);
                //Toast.makeText(getApplicationContext(),classItem.getDeadline()+"\n"+classItem.getClassname(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),ShowQuestionDetailList.class);
                intent.putExtra("studentID",studentID);
                intent.putExtra("TaskListNo",taskRecordItem.getTaskListNo());
                startActivity(intent);
            }
        });
    }

    @Override
    public void updateUI(Object backtaskanswer, int ViewID) {
        super.updateUI(backtaskanswer, ViewID);
        switch (ViewID){
            case SEARCH_COURSE:
                setDataItemForSpinner_course(backtaskanswer);
                break;
            case SEARCH_ALLTASKS:
                setDataForList(backtaskanswer,SEARCH_ALLTASKS);
                setDataItemForTaskListView(taskRecordItemList);
                ///setDataItemForTaskListView(backtaskanswer);
                break;
            case SEARCH_UNFINISHED_TASKS:
                setDataForList(backtaskanswer,SEARCH_UNFINISHED_TASKS);
                setDataItemForTaskListView(taskRecordItemList_unfinished);
                break;
        }
    }

    protected  void recitingService_SearchLearningCourseByID(){//形参为要更新的UI控件的id
        RecitingWebService remote = new RecitingWebService(this,SEARCH_COURSE);
        String methodname = "SearchLearningCourseByID";
        Map<String, String> values = new HashMap<String, String>();
        values.put("Sno", studentID);
        remote.Request(methodname,values);
    }

    protected  void recitingService_QuertTaskListWithClassNo(){
        RecitingWebService remote = new RecitingWebService(this,SEARCH_ALLTASKS);
        String methodname = "QuertTaskListWithClassNo";
        Map<String, String> values = new HashMap<String, String>();
        values.put("ClassNo", selected_ClassNo);
        remote.Request(methodname,values);
    }

    protected  void recitingService_QuertTaskListForUnfinishedTask(){
        RecitingWebService remote = new RecitingWebService(this,SEARCH_UNFINISHED_TASKS);
        String methodname = "QuertTaskListForUnfinishedTask";
        Map<String, String> values = new HashMap<String, String>();
        values.put("Sno", studentID);
        values.put("ClassNo", selected_ClassNo);
        remote.Request(methodname,values);
    }

    protected  void setDataItemForSpinner_course(Object obj){
        cname_classno = new HashMap<String, String>();
        if(cnamelist == null){
            cnamelist = new ArrayList<String>();//如果没有就new
        }
        else {
            cnamelist.clear();//否则就清空
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
                cname_classno.put(secondlayer.getProperty(0).toString(),secondlayer.getProperty(1).toString());
                cnamelist.add(secondlayer.getProperty(0).toString());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,cnamelist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_coursename_classno.setAdapter(adapter);
        }
    }

    //protected  void setDataItemForTaskListView(Object obj){//这段代码的冗余度太高了
    protected  void setDataItemForTaskListView(List<TaskRecordItem> list){
        TaskRecordItemAdapter myItemAdapter = new TaskRecordItemAdapter(getApplicationContext(),R.layout.item_listview,list);
        TaskListView.setAdapter(myItemAdapter);
    }
    //这个函数是为List  taskRecordItemList\taskRecordItemList_unfinished填充数据的
    protected  void setDataForList(Object obj,int mode){
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
                TaskRecordItem taskRecordItem = new TaskRecordItem(secondlayer.getProperty(0).toString(),secondlayer.getProperty(1).toString(),secondlayer.getProperty(2).toString(),"第"+i + "次");
                list.add(taskRecordItem);
            }
        }
        if (mode == SEARCH_ALLTASKS){
            taskRecordItemList = list ;
            isDirty_taskRecordItemList = false;//刚刚更新的数据，已经干净了
        }
        else if (mode == SEARCH_UNFINISHED_TASKS){
            taskRecordItemList_unfinished = list ;
            isDirty_taskRecordItemList_unfinished = false;
        }
    }

}
