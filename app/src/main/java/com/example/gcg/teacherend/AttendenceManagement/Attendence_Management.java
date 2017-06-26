package com.example.gcg.teacherend.AttendenceManagement;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gcg.teacherend.CustomizedClass.CustomizedActivity;
import com.example.gcg.teacherend.R;

public class Attendence_Management extends CustomizedActivity {
    private String teacherID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence__management);
        Intent intent = getIntent();
        teacherID = intent.getStringExtra("teacherID");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        TextView title = (TextView) findViewById(R.id.textView_TitleText);
        title.setText(R.string.attendence);

        ImageButton button_release = (ImageButton) findViewById(R.id.button_releaseattendencetask);
        button_release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                intent = new Intent(getApplicationContext(),Attendence_Management.class);
                intent.putExtra("teacherID",teacherID);
                startActivity(intent);
                * */
                Intent intent = new Intent(getApplicationContext(),Attencence_ReleaseRequest_activity.class);
                intent.putExtra("teacherID",teacherID);
                startActivity(intent);
            }
        });
        ImageButton button_check = (ImageButton) findViewById(R.id.button_manageattendencetask);
        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Attendence_CheckAttendence.class);
                intent.putExtra("teacherID",teacherID);
                startActivity(intent);
            }
        });

    }
}
