package com.example.studentend.TaskManagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentend.CustomizedClass.CustomizedActivity;
import com.example.studentend.CustomizedClass.FileAccess;
import com.example.studentend.CustomizedClass.MyThread;
import com.example.studentend.CustomizedClass.RecitingWebService;
import com.example.studentend.CustomizedClass.TaskQuestionDetailItemAdapter;
import com.example.studentend.CustomizedClass.TaskRecord_QuestionItem;
import com.example.studentend.R;

import org.ksoap2.serialization.SoapObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowQuestionDetailList extends CustomizedActivity implements View.OnClickListener{

    private static final int ACCESS_FILE = 0;
    private static final int QUERY_TASK_DETAIL = 1;
    private static final int UPLOAD_ANSWER = 2;
    private static final int GET_PATH = 3;
    private static final int FILE_TRANS_REPORT = 4;
    private static final int INSERT_ANSWER = 5;
    //D:\Data\Teacher\teacherid\courseID\classid\studentid
    private String studentID;
    private String TaskListNo;
    private String Filepath;
    private String FileName;
    private String FileStoragePath;
    private byte[] FileContent;
    private List<TaskRecord_QuestionItem> questionItemList;
    private ListView listView_questionlist;
    private Button button_commitAnswer;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FILE_TRANS_REPORT:
                    recitingService_InsertIntoAnswer();
                    //Toast.makeText(getApplicationContext(),"文件上传成功",Toast.LENGTH_SHORT).show();
                    /*
                    new AlertDialog.Builder(ShowQuestionDetailList.this)
                            .setTitle("通知")
                            .setMessage(msg.obj.toString())
                            .setPositiveButton("确定",null)
                            .show();
                            */
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.studentend.R.layout.activity_show_taskdetail_list);
        listView_questionlist = (ListView) findViewById(com.example.studentend.R.id.listview_attendence_shownamelist);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        button_commitAnswer = (Button) findViewById(R.id.imageButton_Task_CommitAnswer);
        button_commitAnswer.setOnClickListener(this);
        TextView title = (TextView) findViewById(com.example.studentend.R.id.textView_TitleText);
        title.setText(com.example.studentend.R.string.Task_QuestionList);
        Intent intent = getIntent();
        studentID = intent.getStringExtra("studentID");
        TaskListNo = intent.getStringExtra("TaskListNo");
        recitingService_QuertTaskDetailWithTaskListNo();
    }

    @Override
    public void updateUI(Object backtaskanswer, int ViewID) {
        super.updateUI(backtaskanswer, ViewID);
        switch (ViewID){
            case QUERY_TASK_DETAIL:
                setDataItemForList(backtaskanswer);
                break;
            case INSERT_ANSWER://反正都是一样的，显示一个弹窗结果而已，就共用出口吧。
            case UPLOAD_ANSWER:
                SoapObject receiver1 = (SoapObject) backtaskanswer;
                new AlertDialog.Builder(ShowQuestionDetailList.this)
                        .setTitle("通知")
                        .setMessage(receiver1.getProperty(0).toString())
                        .setPositiveButton("确定",null)
                        .show();
                break;
            case GET_PATH:
                SoapObject receiver2 = (SoapObject) backtaskanswer;
                //为什么明明返回的是绝对路径，但是却只有文件夹路径？？？
                String directory_path = receiver2.getProperty(0).toString();
                FileStoragePath = directory_path + FileName;

                try {
                    byte [] directory_path_arr = directory_path.getBytes("UTF-8");
                    byte [] filePath_arr = FileStoragePath.getBytes("UTF-8");
                    //MySocket mySocket = new MySocket(filePath_arr,FileContent,ShowQuestionDetailList.this);
                    //new Thread(new MySocket(filePath_arr,FileContent,ShowQuestionDetailList.this));
                    MyThread myThread = new MyThread(directory_path_arr,filePath_arr,FileContent,handler);
                    new Thread(myThread).start();
                    //new Thread(mySocket).start();//启动线程开始上传
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case FILE_TRANS_REPORT:
                new AlertDialog.Builder(ShowQuestionDetailList.this)
                        .setTitle("通知")
                        .setMessage(backtaskanswer.toString())
                        .setPositiveButton("确定",null)
                        .show();
                break;

            default:
                Toast.makeText(getApplicationContext(),"Switch默认结果",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    protected  void recitingService_QuertTaskDetailWithTaskListNo(){
        RecitingWebService remote = new RecitingWebService(this,QUERY_TASK_DETAIL);
        String methodname = "QuertTaskDetailWithTaskListNo";
        Map<String, String> values = new HashMap<String, String>();
        values.put("TaskListNo", TaskListNo);
        remote.Request(methodname,values);
    }

    protected  void recitingService_GetFileStoragePath(){
        RecitingWebService remote = new RecitingWebService(this,GET_PATH);
        String methodname = "GetFileStoragePath";
        Map<String, Object> values = new HashMap<String, Object>();
        //计算出文件名
        int index = Filepath.lastIndexOf('/');
        FileName = Filepath.substring(index + 1,Filepath.length());
        //public string GetFileStoragePath(string TaskListNo,string Sno,string filename)
        values.put("TaskListNo", TaskListNo);
        values.put("Sno", studentID);
        values.put("FileName",FileName);
        remote.Request(methodname,values);
    }

    protected  void recitingService_InsertIntoAnswer(){
        // public String InsertIntoAnswer(string TaskListNo,string Sno,string FName,string FilePath)
        RecitingWebService remote = new RecitingWebService(this,INSERT_ANSWER);
        String methodname = "InsertIntoAnswer";
        Map<String, String> values = new HashMap<String, String>();
        values.put("TaskListNo", TaskListNo);
        values.put("Sno", studentID);
        values.put("FName",FileName);
        values.put("FilePath",FileStoragePath);
        remote.Request(methodname,values);
    }

    protected  void setDataItemForList(Object obj){
        if(questionItemList == null){
            questionItemList = new ArrayList<TaskRecord_QuestionItem>();//如果没有就new
        }
        else {
            questionItemList.clear();//否则就清空
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
                TaskRecord_QuestionItem taskRecord_questionItem = new TaskRecord_QuestionItem(secondlayer.getProperty(0).toString(),secondlayer.getProperty(1).toString());
                questionItemList.add(taskRecord_questionItem);
            }
            TaskQuestionDetailItemAdapter myItemAdapter = new TaskQuestionDetailItemAdapter(getApplicationContext(), com.example.studentend.R.layout.item_questionitem,questionItemList);
            //myItemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将Adapter添加到Spinner
            //listView_attendenceTask.setAdapter(myItemAdapter);
            listView_questionlist.setAdapter(myItemAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageButton_Task_CommitAnswer:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //Action属性代表系统要执行的动作 ACTION_GET_CONTENT：让用户在运行的程序中选择数据
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //Category属性用于指定动作Action被执行的环境,CATEGORY_OPENABLE：增加一个可打开的分类
                //通常和ACTION_GET_CONTENT组合使用
                startActivityForResult(intent, ACCESS_FILE);
        }

    }
    //调用系统自带资源管理器并选择文件后将回调这个函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String string = uri.toString();
            //Toast.makeText(ShowQuestionDetailList.this, string,Toast.LENGTH_SHORT).show();

            String a[];
            //判断文件是否在sd卡中
            //Environment.getExternalStorageDirectory()获取SD卡存储根目录
            if (string.indexOf(String.valueOf(Environment.getExternalStorageDirectory())) != -1) {
                //对Uri进行切割
                a = string.split(String.valueOf(Environment.getExternalStorageDirectory()));
                //获取到file
                //file = new File(Environment.getExternalStorageDirectory(), a[1]);
                //textView.setText(Environment.getExternalStorageDirectory() + a[1]);
                Filepath = Environment.getExternalStorageDirectory() + a[1];
            } else if (string.indexOf(String.valueOf(Environment.getDataDirectory())) != -1) { //判断文件是否在手机内存中
                //对Uri进行切割
                a = string.split(String.valueOf(Environment.getDataDirectory()));
                //获取到file
                //file = new File(Environment.getDataDirectory(), a[1]);
                //textView.setText(Environment.getDataDirectory() + a[1]);
                Filepath = Environment.getDataDirectory() + a[1];
            } else {
                //出现其他没有考虑到的情况
                Toast.makeText(this, "文件路径解析失败！", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(ShowQuestionDetailList.this, Filepath,Toast.LENGTH_SHORT).show();
            FileAccess fileAccess = new FileAccess(this);
            fileAccess.verifyStoragePermissions(this);//检查权限
            FileContent = fileAccess.ReadFile(Filepath);//以byte[]获取选择的文件

            recitingService_GetFileStoragePath();//上传文件
        }
        return;
    }

}
