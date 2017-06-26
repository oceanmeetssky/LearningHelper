package com.example.gcg.teacherend.TaskManagement;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gcg.teacherend.AttendenceManagement.Attencence_ReleaseRequest_activity;
import com.example.gcg.teacherend.CustomizedClass.CustomizedActivity;
import com.example.gcg.teacherend.CustomizedClass.RecitingWebService;
import com.example.gcg.teacherend.CustomizedClass.Task;
import com.example.gcg.teacherend.R;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TaskManagement_ReleaseTask extends CustomizedActivity implements View.OnClickListener{
    private String teacherID = "00001";
    private String TaskListNo;
    private Spinner spinner_course;
    private Spinner spinner_class;
    private Button button_settime;
    private Button button_sendTask;
    private ImageButton imageButton_addTask;
    private EditText task_title;
    private EditText task_detail;
    private Map<String,String> coursename_courseno;
    private Map<String,String> classname_classno;
    private List<String> courselist;
    private List<String>classlist;
    private String selected_classNo;
    private String selected_courseNo;
    private List<Task> taskList;
    private List<String> selectedClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management__release_task);
        recitingService_queryCourse();
        spinner_course = (Spinner) findViewById(R.id.spinner_taskrelease_course);
        spinner_class = (Spinner) findViewById(R.id.spinner_taskrelease_class);
        button_settime = (Button) findViewById(R.id.button_setTime);
        button_sendTask = (Button) findViewById(R.id.button_sendTask);
        task_title = (EditText) findViewById(R.id.editText_taskrelease_title);
        task_detail = (EditText) findViewById(R.id.editText_taskrelease_detail);
        imageButton_addTask = (ImageButton) findViewById(R.id.imageButton_addTask);
        taskList = new ArrayList<Task>();
        selectedClass = new ArrayList<String>();
        addEventHandle();
    }

    protected void addEventHandle(){
        //editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);  
        task_title.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        task_title.setSingleLine(false);
        task_title.setHorizontallyScrolling(false);
        task_detail.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        task_detail.setSingleLine(false);
        task_detail.setHorizontallyScrolling(false);
        imageButton_addTask.setOnClickListener(this);
        button_settime.setOnClickListener(this);
        button_sendTask.setOnClickListener(this);
        spinner_course.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_courseNo = coursename_courseno.get(courselist.get(position));
                recitingService_queryClasswithCno(selected_courseNo);//班级的spinner的数据集是根据课程的spinner项来决定的
                //但是这里由于每一次点击都要调用服务器，有点效率低下
                //如果可以用一个Map存放几个Map就好了，每次都先检查是不是已经下载到了本地，如果是的话就直接用不要再联网
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_class.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_classNo = classname_classno.get(classlist.get(position));
                selectedClass.add(selected_classNo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void updateUI(Object backtaskanswer, int ViewID) {
        super.updateUI(backtaskanswer, ViewID);
        switch (ViewID){
            case R.id.spinner_taskrelease_course:
                setDataItemForSpinner_course(backtaskanswer);break;
            case R.id.spinner_taskrelease_class:
                setDataItemForSpinner_class(backtaskanswer);break;
            case 1://调用远程服务往TaskList插入完成
                if (backtaskanswer == null){
                    Toast.makeText(TaskManagement_ReleaseTask.this,"异常错误，TaskList插入失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                SoapObject receiver = (SoapObject) backtaskanswer;
                TaskListNo = receiver.getProperty(0).toString();

//                new AlertDialog.Builder(TaskManagement_ReleaseTask.this)
//                        .setTitle("通知")
//                        .setMessage(receiver.getProperty(0).toString())
//                        .setPositiveButton("确定",null)
//                        .show();

                //在这里循环调用recitingService_InsertTaskScope
                for (int i = 0;i < classlist.size();i++){
                    String classno = classname_classno.get(classlist.get(i));
                    recitingService_InsertTaskScope(classno);
                }

                String questionID = "";
                for(int i = 0;i < taskList.size();i++){
                    questionID = TaskListNo + i;
                    recitingService_InsertQuestionList(questionID,taskList.get(i).getTitle(),taskList.get(i).getDetail());
                }

                break;
            case 2:
                if (backtaskanswer == null){
                    Toast.makeText(TaskManagement_ReleaseTask.this,"异常错误，TaskList插入失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                SoapObject receiver2 = (SoapObject) backtaskanswer;

                new AlertDialog.Builder(TaskManagement_ReleaseTask.this)
                        .setTitle("通知")
                        .setMessage(receiver2.getProperty(0).toString())
                        .setPositiveButton("确定",null)
                        .show();
                break;
            case 3:
                if (backtaskanswer == null){
                    Toast.makeText(TaskManagement_ReleaseTask.this,"异常错误，TaskList插入失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                SoapObject receiver3 = (SoapObject) backtaskanswer;

//                new AlertDialog.Builder(TaskManagement_ReleaseTask.this)
//                        .setTitle("通知")
//                        .setMessage(receiver3.getProperty(0).toString())
//                        .setPositiveButton("确定",null)
//                        .show();
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
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,classlist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将Adapter添加到Spinner
            spinner_class.setAdapter(adapter);
        }
    }

    protected  void recitingService_queryCourse(){//形参为要更新的UI控件的id
        RecitingWebService remote = new RecitingWebService(this,R.id.spinner_taskrelease_course);
        String methodname = "SearchLectureCourseByID";
        Map<String, String> values = new HashMap<String, String>();
        values.put("ID", teacherID);
        remote.Request(methodname,values);
    }

    protected  void recitingService_queryClasswithCno(String cno){
        RecitingWebService remote = new RecitingWebService(this,R.id.spinner_taskrelease_class);
        String methodname = "SearchLeadingClassByTeaIDAndCno";
        Map<String, String> values = new HashMap<String, String>();
        values.put("ID", teacherID);
        values.put("Cno", cno);
        remote.Request(methodname,values);
    }



    protected void recitingService_InsertIntoTaskList(){
        RecitingWebService remote = new RecitingWebService(this,1);
        String methodname = "InsertIntoTaskList";
        //  public String InsertIntoTaskList(string Tno, string Cno, string Deadline)
        //String methodname = "HelloWorld";
        //HelloWorld
        Map<String, String> values = new HashMap<String, String>();
        values.put("Tno", teacherID);
        values.put("Cno", selected_courseNo);
        values.put("Deadline",button_settime.getText().toString());
        remote.Request(methodname,values);
    }

    protected void recitingService_InsertTaskScope(String ClassNo){
        RecitingWebService remote = new RecitingWebService(this,2);
        String methodname = "InsertTaskScope";
        // public string InsertTaskScope(string TaskListNo,string ClassNo)
        Map<String, String> values = new HashMap<String, String>();
        values.put("TaskListNo", TaskListNo);
        values.put("ClassNo", ClassNo);
        remote.Request(methodname,values);
    }

    protected void recitingService_InsertQuestionList(String QuestionID,String Title,String Detail){
        RecitingWebService remote = new RecitingWebService(this,3);
        String methodname = "InsertQuestionList";
        //public string InsertQuestionList(string TaskListNo, string QuestionID,string Title,string Detail)
        Map<String, String> values = new HashMap<String, String>();
        values.put("TaskListNo", TaskListNo);
        values.put("QuestionID", QuestionID);
        values.put("Title", Title);
        values.put("Detail", Detail);
        remote.Request(methodname,values);
    }

    @Override
    public void onClick(View v) {
        Task task = null;
        switch (v.getId()){
            case R.id.textView_taskrelease_deadline:
                break;
            case R.id.button_setTime:
                Calendar c = Calendar.getInstance();
                ////注意下面的DatePickerDialog的第一个参数千万不能用getApplicationContext,需要直接用对应活动的this指针，否则什么都没有
                new DatePickerDialog(TaskManagement_ReleaseTask.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker dp, int year, int month, int day) {
                                button_settime.setText(year + "-" + (month+1) + "-" + day);
                                //通过Calendar获取的mouth是从0开始的，0代表1月，所以要加一
                            }
                        },
                        c.get(Calendar.YEAR),//DatePickerDialog的初始年为当前年
                        c.get(Calendar.MONTH),//DatePickerDialog的初始月份为当前月
                        c.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.imageButton_addTask:
                if(task_title.getText().length() == 0){
                    Toast.makeText(getApplicationContext(),"题目不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                task = new Task(task_title.getText().toString(),task_detail.getText().toString());
                taskList.add(task);//保存已经输入的题目
                task_title.setText("");//重置输入区域，为下一次输入做准备
                task_detail.setText("");
                break;
            case R.id.button_sendTask:
                if(task_title.getText().length() != 0){
                    task = new Task(task_title.getText().toString(),task_detail.getText().toString());
                    taskList.add(task);
                }
                if(taskList.size() == 0){
                    Toast.makeText(TaskManagement_ReleaseTask.this,"没有任务",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(TaskManagement_ReleaseTask.this,"num = "+ taskList.size(),Toast.LENGTH_SHORT).show();
                    recitingService_InsertIntoTaskList();//先往TaskList里面插入一条记录，形成任务记录号
                    task_title.setText("");//重置输入区域，为下一次输入做准备
                    task_detail.setText("");
                }
                break;

        }
    }
}
