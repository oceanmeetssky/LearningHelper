package com.example.gcg.teacherend.AttendenceManagement;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gcg.teacherend.CustomizedClass.CustomizedActivity;
import com.example.gcg.teacherend.CustomizedClass.RecitingWebService;
import com.example.gcg.teacherend.CustomizedClass.StudentItem;
import com.example.gcg.teacherend.CustomizedClass.StudentItemAdapter;
import com.example.gcg.teacherend.R;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowNameList extends CustomizedActivity {

    private String Deadline;
    private List<StudentItem> studentItemList;
    private ListView listView_studentlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_name_list);
        listView_studentlist = (ListView) findViewById(R.id.listview_attendence_shownamelist);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        TextView title = (TextView) findViewById(R.id.textView_TitleText);
        title.setText(R.string.attendence);
        Intent intent = getIntent();
        Deadline = intent.getStringExtra("Deadline");
        recitingService_QueryAttendence_ForNameList();
    }

    @Override
    public void updateUI(Object backtaskanswer, int ViewID) {
        super.updateUI(backtaskanswer, ViewID);
        switch (ViewID){
            case 1:
                setDataItemForList(backtaskanswer);
                break;
            default:
                Toast.makeText(getApplicationContext(),"Switch默认结果",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    protected  void recitingService_QueryAttendence_ForNameList(){
        RecitingWebService remote = new RecitingWebService(this,1);
        String methodname = "QueryAttendence_ForNameList";
        Map<String, String> values = new HashMap<String, String>();
        values.put("Deadline", Deadline);
        remote.Request(methodname,values);
    }

    protected  void setDataItemForList(Object obj){
        if(studentItemList == null){
            studentItemList = new ArrayList<StudentItem>();//如果没有就new
        }
        else {
            studentItemList.clear();//否则就清空
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
                StudentItem studentItem = new StudentItem(secondlayer.getProperty(0).toString(),secondlayer.getProperty(1).toString());
                studentItemList.add(studentItem);
            }
            StudentItemAdapter myItemAdapter = new StudentItemAdapter(getApplicationContext(),R.layout.item_student,studentItemList);
            //myItemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将Adapter添加到Spinner
            //listView_attendenceTask.setAdapter(myItemAdapter);
            listView_studentlist.setAdapter(myItemAdapter);
        }
    }
}
