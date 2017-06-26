package com.example.gcg.teacherend.TaskManagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gcg.teacherend.AttendenceManagement.Attendence_CheckAttendence;
import com.example.gcg.teacherend.R;

public class TaskManagement extends AppCompatActivity implements View.OnClickListener {
    private String teacherID;
    private ImageButton releaseTask;
    private ImageButton editTask;
    private ImageButton checkTask;
    private ImageButton deleteTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);
        Intent intent = getIntent();
        teacherID = intent.getStringExtra("teacherID");
        releaseTask = (ImageButton) findViewById(R.id.imageButton_ReleaseTask);
        editTask = (ImageButton) findViewById(R.id.imageButton_EditTask);
        checkTask = (ImageButton) findViewById(R.id.imageButton_CheckTaskRecord);
        deleteTask = (ImageButton) findViewById(R.id.imageButton_DeleteTask);
        addEventHandle();
    }

    protected void addEventHandle(){
        releaseTask.setOnClickListener(this);
        editTask.setOnClickListener(this);
        checkTask.setOnClickListener(this);
        deleteTask.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageButton_ReleaseTask:
                //Toast.makeText(getApplicationContext(),"发布任务",Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getApplicationContext(),TaskManagement_ReleaseTask.class);
                intent1.putExtra("teacherID",teacherID);
                startActivity(intent1);
                break;
            case R.id.imageButton_EditTask:
                Toast.makeText(getApplicationContext(),"编辑任务",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageButton_CheckTaskRecord:
                //Toast.makeText(getApplicationContext(),"检查任务",Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(getApplicationContext(),TaskManagement_CheckTask.class);
                intent2.putExtra("teacherID",teacherID);
                startActivity(intent2);
                break;
            case R.id.imageButton_DeleteTask:
                Toast.makeText(getApplicationContext(),"删除任务",Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getApplicationContext(),"Default",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
