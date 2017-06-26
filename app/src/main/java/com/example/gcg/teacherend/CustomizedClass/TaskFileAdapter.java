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
 * Created by GCG on 2017/5/28.
 */

public class TaskFileAdapter  extends RecyclerView.Adapter<TaskFileAdapter.ViewHolder>{
    private List<TaskFile> taskFileList;

    public void setFileIndexList(List<Object> fileList) {
        this.fileindexList = fileList;
    }

    private List<Object> fileindexList;//记录taskFileList中哪几项被选中了

    public List<Object> getFileIndexList() {
        return fileindexList;
    }

    public TaskFileAdapter(List<TaskFile> taskFileList){
        this.taskFileList = taskFileList;
        fileindexList = new ArrayList<Object>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建ViewHolder实例并注册事件
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fileitem,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        //为外层布局注册事件
        holder.TaskFileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                TaskFile taskFile = taskFileList.get(position);
                Toast.makeText(v.getContext(),"你点击了这个子项"+taskFile.getSname(),Toast.LENGTH_SHORT).show();
            }
        });
        //为子项控件注册事件
        holder.checkBox_chooseFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = holder.getAdapterPosition();
                TaskFile taskFile = taskFileList.get(position);
                MyFile myFile = new MyFile(taskFile.getFileName(),taskFile.getFilePath());
                if (isChecked){
                    fileindexList.add(position);
                }
                else {
                    //得想办法将这一项移除，可惜不是Map
                    for(int i = 0;i < fileindexList.size();i++ ){
                        if ((int)fileindexList.get(i) == position){
                            fileindexList.remove(i);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        //为子项数据赋值，当子项被滚入屏幕时执行
        TaskFile taskFile = taskFileList.get(position);
        holder.textView_sno.setText(taskFile.getSno());
        holder.textView_sname.setText(taskFile.getSname());
        holder.textView_filename.setText(taskFile.getFileName());
    }

    @Override
    public int getItemCount() {
        return taskFileList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View TaskFileView;//保存子项最外层布局的实例
        TextView textView_sno;
        TextView textView_sname;
        TextView textView_filename;
        CheckBox checkBox_chooseFile;

        public ViewHolder(View itemView) {
            super(itemView);
            TaskFileView = itemView;
            textView_sno = (TextView) itemView.findViewById(R.id.textview_sno);
            textView_sname = (TextView) itemView.findViewById(R.id.textView_sname);
            textView_filename = (TextView) itemView.findViewById(R.id.textView_filename);
            checkBox_chooseFile = (CheckBox) itemView.findViewById(R.id.checkBox_chooseFile);
        }
    }
}
