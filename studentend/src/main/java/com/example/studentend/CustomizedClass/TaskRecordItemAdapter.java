package com.example.studentend.CustomizedClass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.studentend.R;

import java.util.List;

/**
 * Created by GCG on 2017/5/21.
 */

public class TaskRecordItemAdapter extends ArrayAdapter<TaskRecordItem> {
    private int resourceID;

    public TaskRecordItemAdapter(Context context, int resource, List<TaskRecordItem> objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @NonNull
    @Override
    //getView()会在每个子项被滚动到屏幕内的时候调用
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskRecordItem taskRecordItem = getItem(position);//获取当前项的ClassItem实例
        View view;
        ViewHolder viewHolder;
        //convertView用于将之前加载的布局进行缓存
        //这样我们就可以不用每次都重新加载ListView的子项布局
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            viewHolder = new ViewHolder();
            //同理使用一个ViewHolder来保存子项内部组件的布局
            viewHolder.No = (TextView) view.findViewById(R.id.textview_listview_No);
            viewHolder.releasetime = (TextView) view.findViewById(R.id.textview_listview_releasetime);
            viewHolder.deadline = (TextView) view.findViewById(R.id.textview_listview_deadline);
            view.setTag(viewHolder);//将ViewHolder存储在View中
        }else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
            //如此，子项的布局可以直接获取
        }
        //修改子项的内部数据
        viewHolder.No.setText(taskRecordItem.getTaskNo());
        viewHolder.releasetime.setText(taskRecordItem.getReleaseTime());
        viewHolder.deadline.setText(taskRecordItem.getDeadline());

        return view;
    }

    class ViewHolder{
        TextView No;
        TextView releasetime;
        TextView deadline ;
    }
}
