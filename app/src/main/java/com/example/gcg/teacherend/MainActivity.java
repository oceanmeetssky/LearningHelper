package com.example.gcg.teacherend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.gcg.teacherend.AttendenceManagement.Attendence_Management;
import com.example.gcg.teacherend.TaskManagement.TaskManagement;
import com.example.gcg.teacherend.baidumaptest.BaiduLocationActivity;

public class MainActivity extends AppCompatActivity {
    private String teacherID = "00001";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.button_Attendence:
                intent = new Intent(getApplicationContext(),Attendence_Management.class);
                intent.putExtra("teacherID",teacherID);
                startActivity(intent);
                break;
            case R.id.button_Task:
                intent = new Intent(getApplicationContext(),TaskManagement.class);
                intent.putExtra("teacherID",teacherID);
                startActivity(intent);
                break;
            case R.id.button_OfficeHour:
                intent = new Intent(getApplicationContext(),relatingWebService.class);
                //intent.putExtra("teacherID",teacherID);
                startActivity(intent);
                break;
        }
    }
}
