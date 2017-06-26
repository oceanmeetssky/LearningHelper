package com.example.gcg.teacherend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gcg.teacherend.CustomizedClass.CustomizedActivity;

public class Login extends CustomizedActivity implements View.OnClickListener {

    private String teacherID;
    private EditText editText_ID;
    private EditText editText_Pass;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText_ID = (EditText) findViewById(R.id.editText_ID);
        editText_Pass = (EditText) findViewById(R.id.editText_Pass);
        button = (Button) findViewById(R.id.button_login_enter);
        button.setOnClickListener(this);
        editText_ID.setText("00001");
        editText_Pass.setText("123456");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login_enter:
                teacherID = editText_ID.getText().toString();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("teacherID",teacherID);
                startActivity(intent);
                break;
        }
    }
}
