package com.example.gcg.teacherend.CustomizedClass;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gcg.teacherend.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GCG on 2017/5/29.
 */

public class StudentAdapter_Re extends RecyclerView.Adapter<StudentAdapter_Re.ViewHolder>{
    private List<StudentItem> studentItemList;

    //private List<StudentItem> selectedList;
    public void setSelectedList(List<Object> list) {
        this.selectedList = list;
    }

    private List<Object> selectedList;//记录taskFileList中哪几项被选中了

    public List<Object> getSelectedList() {
        return selectedList;
    }


    public StudentAdapter_Re(List<StudentItem> studentItems){
        this.studentItemList = studentItems;
        selectedList = new ArrayList<Object>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建ViewHolder实例并注册事件
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.studentitem_re,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        //为外层布局注册事件
        holder.StudentItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                StudentItem studentItem = studentItemList.get(position);
                Toast.makeText(v.getContext(),"你点击了这个子项"+studentItem.getSname(),Toast.LENGTH_SHORT).show();
            }
        });
        //为子项控件注册事件
        holder.checkBox_chooseFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = holder.getAdapterPosition();
                StudentItem studentItem = studentItemList.get(position);
                if (isChecked){
                    selectedList.add(position);
                }
                else {
                    //得想办法将这一项移除，可惜不是Map
                    for(int i = 0;i < selectedList.size();i++ ){
                        if ((int)selectedList.get(i) == position){
                            selectedList.remove(i);
                        }
                    }
                    // fileList.remove(myFile);
                }
            }
        });
        //当点击没有注册事件的子项部分时就会相应整体子项事件
        return holder;
    }

    @Override
    public void onBindViewHolder(StudentAdapter_Re.ViewHolder holder, int position) {
        //为子项数据赋值，当子项被滚入屏幕时执行
        StudentItem studentItem = studentItemList.get(position);
        holder.textView_sno.setText(studentItem.getSno());
        holder.textView_sname.setText(studentItem.getSname());
    }

    @Override
    public int getItemCount() {
        return studentItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View StudentItemView;//保存子项最外层布局的实例
        TextView textView_sno;
        TextView textView_sname;
        CheckBox checkBox_chooseFile;

        public ViewHolder(View itemView) {
            super(itemView);
            StudentItemView = itemView;
            textView_sno = (TextView) itemView.findViewById(R.id.textview_sno_student);
            textView_sname = (TextView) itemView.findViewById(R.id.textView_sname_student);
            checkBox_chooseFile = (CheckBox) itemView.findViewById(R.id.checkBox_chooseFile_student);
        }
    }
}
