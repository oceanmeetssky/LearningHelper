package com.example.gcg.teacherend.TaskManagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gcg.teacherend.CustomizedClass.CustomizedActivity;
import com.example.gcg.teacherend.CustomizedClass.FileAccess;
import com.example.gcg.teacherend.CustomizedClass.MyFile;
import com.example.gcg.teacherend.CustomizedClass.MySocket;
import com.example.gcg.teacherend.CustomizedClass.RecitingWebService;
import com.example.gcg.teacherend.CustomizedClass.StudentAdapter_Re;
import com.example.gcg.teacherend.CustomizedClass.StudentItem;
import com.example.gcg.teacherend.CustomizedClass.TaskFile;
import com.example.gcg.teacherend.CustomizedClass.TaskFileAdapter;
import com.example.gcg.teacherend.R;

import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowTaskFileList extends CustomizedActivity implements View.OnClickListener{

    private static final int QUERY_TASKFILE = 0;
    private static final int QUERY_UNSUBMITTED_STUDENT_LIST = 1;
    private static final int FILE_TRANS_REPORT = 4;

    private RecyclerView recyclerView_taskfilelist;

    private String teacherID;
    private String ClassNo;
    private String TaskListNo;
    private List<TaskFile> taskFileList;
    private List<StudentItem> studentItemList;
    //注意这里存放的是没有提交作业的学生名单因此仅仅用到了TaskFile的Sno,Sname这两个字段。

    private String Path = Environment.getExternalStorageDirectory()+"/TeacherEnd/";
    //private List<MyFile> FileQueue;

    private TaskFileAdapter taskFileAdapter;
    private StudentAdapter_Re studentAdapter_re;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FILE_TRANS_REPORT:
                    //recitingService_InsertIntoAnswer();
                    //Toast.makeText(getApplicationContext(),"文件上传成功",Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(ShowTaskFileList.this)
                            .setTitle("通知")
                            .setMessage(msg.obj.toString())
                            .setPositiveButton("确定",null)
                            .show();

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task_file_list);

        recyclerView_taskfilelist = (RecyclerView) findViewById(R.id.RecyclerView_filelist);
        Button imageButton = (Button) findViewById(R.id.imageButton_download);
        imageButton.setOnClickListener(this);

        Intent intent = getIntent();
        teacherID = intent.getStringExtra("teacherID");
        ClassNo = intent.getStringExtra("ClassNo");
        TaskListNo = intent.getStringExtra("TaskListNo");

        //FileQueue = new ArrayList<MyFile>();
        FileAccess fileAccess = new FileAccess(this);
        fileAccess.verifyStoragePermissions(this);

        File file = new File(Path);//判断文件夹是否存在如果不存在就创建
        if(!file.exists()){
            file.mkdir();
        }

        recitingService_QueryTaskFileFromAnswer();

    }

    protected  void recitingService_QueryTaskFileFromAnswer(){//形参为要更新的UI控件的id
        // public string [][] QueryTaskFileFromAnswer(string TaskListNo,string ClassNo)
        RecitingWebService remote = new RecitingWebService(this,QUERY_TASKFILE);
        String methodname = "QueryTaskFileFromAnswer";
        Map<String, String> values = new HashMap<String, String>();
        //Toast.makeText(getApplicationContext(),"TaskListNo:"+TaskListNo+"\nClassNo:"+ClassNo,Toast.LENGTH_LONG).show();
        values.put("TaskListNo",TaskListNo);
        values.put("ClassNo",ClassNo);
        remote.Request(methodname,values);
    }

    protected  void recitingService_QueryTask_ForUnSubmittedStudentList(){//形参为要更新的UI控件的id
        // public string [][] QueryTaskFileFromAnswer(string TaskListNo,string ClassNo)
        RecitingWebService remote = new RecitingWebService(this,QUERY_UNSUBMITTED_STUDENT_LIST);
        String methodname = "QueryForUnSubmittedStudentList";
        Map<String, String> values = new HashMap<String, String>();
        //Toast.makeText(getApplicationContext(),"TaskListNo:"+TaskListNo+"\nClassNo:"+ClassNo,Toast.LENGTH_LONG).show();
        values.put("TaskListNo",TaskListNo);
        values.put("ClassNo",ClassNo);
        remote.Request(methodname,values);
    }

    protected  void setDataForList(Object obj,int mode){
        List list = null;
        if (mode == QUERY_TASKFILE){
            list = new ArrayList<TaskFile>();//如果没有就new
        }
        else {
            list = new ArrayList<StudentItem>();
        }
        Object object = obj;
        if(object == null){
            Toast.makeText(this,"没有结果",Toast.LENGTH_SHORT).show();
            return ;
        }
        else{
            SoapObject answer = (SoapObject) object;
            SoapObject firstlayer = (SoapObject) answer.getProperty(0);
            if (mode == QUERY_TASKFILE){
                for( int i = 0;i < firstlayer.getPropertyCount();i++ ){
                    SoapObject secondlayer = (SoapObject) firstlayer.getProperty(i);
                    TaskFile taskFile = new TaskFile(
                            secondlayer.getProperty(0).toString(),
                            secondlayer.getProperty(1).toString(),
                            secondlayer.getProperty(2).toString(),
                            secondlayer.getProperty(3).toString()
                    );
                    list.add(taskFile);
                }
                taskFileList = list;
            }else {
                for( int i = 0;i < firstlayer.getPropertyCount();i++ ){
                    SoapObject secondlayer = (SoapObject) firstlayer.getProperty(i);
                    StudentItem studentItem = new StudentItem(
                            secondlayer.getProperty(0).toString(),
                            secondlayer.getProperty(1).toString()
                    );
                    list.add(studentItem);
                }
                studentItemList = list;
            }
        }
        //Toast.makeText(getApplicationContext(),"文件列表返回"+taskFileList.size(),Toast.LENGTH_SHORT).show();
    }


    protected  void setDataItemForTaskListView(List<TaskFile>list){
        try{
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView_taskfilelist.setLayoutManager(layoutManager);
            TaskFileAdapter myItemAdapter = new TaskFileAdapter(list);
            recyclerView_taskfilelist.setAdapter(myItemAdapter);
            taskFileAdapter = myItemAdapter;
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
    protected  void setDataItemForUnfinishedStudent(List<StudentItem>list){
        try{
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView_taskfilelist.setLayoutManager(layoutManager);
            StudentAdapter_Re studentAdapter_re = new StudentAdapter_Re(list);
            recyclerView_taskfilelist.setAdapter(studentAdapter_re);
            studentAdapter_re = studentAdapter_re;
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateUI(Object backtaskanswer, int ViewID) {
        super.updateUI(backtaskanswer, ViewID);
        switch (ViewID){
            case QUERY_TASKFILE:
                setDataForList(backtaskanswer,QUERY_TASKFILE);
                setDataItemForTaskListView(taskFileList);
                //Toast.makeText(getApplicationContext(),"完成",Toast.LENGTH_SHORT).show();
                break;
            case QUERY_UNSUBMITTED_STUDENT_LIST:
                setDataForList(backtaskanswer,QUERY_UNSUBMITTED_STUDENT_LIST);
                setDataItemForUnfinishedStudent(studentItemList);
                //Toast.makeText(getApplicationContext(),"完成",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageButton_download:
                List list = taskFileAdapter.getFileIndexList();
                List<MyFile> myFileList = new ArrayList<>();
                int position;
                for(int i = 0;i < list.size();i++){
                    position = (int)list.get(i);
                    MyFile myFile = new MyFile(taskFileList.get(position).getFileName(),taskFileList.get(position).getFilePath());
                    myFileList.add(myFile);
                }
                Toast.makeText(getApplicationContext(),"Size:"+myFileList.size(),Toast.LENGTH_SHORT).show();
                for (int i = 0; i < myFileList.size();i++){
                    MyFile myFile = myFileList.get(i);
                    Toast.makeText(getApplicationContext(),myFile.getFileName() +"\n" + i +"\n"+myFile.getFilePath(),Toast.LENGTH_SHORT).show();
                    DownloadThread downloadThread = new DownloadThread(Path+myFile.getFileName(),myFile.getFilePath(),handler);
                    new Thread(downloadThread).start();//开线程启动下载
                }
                break;
            case R.id.button_submitted:
                if (taskFileList == null){
                    recitingService_QueryTaskFileFromAnswer();
                }
                else {
                    setDataItemForTaskListView(taskFileList);
                    //studentAdapter_re.setSelectedList(null);//清除学生列表
                 //   List list1 = studentAdapter_re.getSelectedList();
                  //  list1.clear();
                }
                break;
            case R.id.button_unfinished:
                if (studentItemList == null){
                    recitingService_QueryTask_ForUnSubmittedStudentList();
                }
                else {
                    setDataItemForUnfinishedStudent(studentItemList);
                  //  List list1 = taskFileAdapter.getFileIndexList();
                  //  list1.clear();
                    //taskFileAdapter.setFileIndexList(null);//清除文件列表
                }
                break;

        }
    }

    class DownloadThread implements Runnable{

        String filePath;
        String filePathInDevice;
        Handler handler;

        public DownloadThread( String filePathInDevice,String filePath,Handler handler){
            this.filePath = filePath;
            this.filePathInDevice = filePathInDevice;
            this.handler = handler;
        }

        @Override
        public void run() {
            MySocket mySocket = null;
            try {
                mySocket = new MySocket(filePathInDevice,filePath.getBytes("UTF-8"),handler);
                mySocket.ReceiveFile();//接收数据
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

}
