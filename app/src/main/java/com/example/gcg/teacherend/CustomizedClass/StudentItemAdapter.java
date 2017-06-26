package com.example.gcg.teacherend.CustomizedClass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gcg.teacherend.R;

import java.util.List;

/**
 * Created by GCG on 2017/5/17.
 */

public class StudentItemAdapter extends ArrayAdapter<StudentItem> {
    private int resourceID;

    public StudentItemAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @NonNull
    @Override
    public View getView ( int position, View convertView, ViewGroup parent){
        StudentItem studentItem =  getItem(position);//获取当前项的ClassItem实例
        View view;
        ViewHolder viewHolder;
        //convertView用于将之前加载的布局进行缓存
        //这样我们就可以不用每次都重新加载ListView的子项布局
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
            viewHolder = new ViewHolder();
            //同理使用一个ViewHolder来保存子项内部组件的布局
            viewHolder.Sno = (TextView) view.findViewById(R.id.textview_listview_sno);
            viewHolder.Sname = (TextView) view.findViewById(R.id.textview_listview_sname);
            view.setTag(viewHolder);//将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
            //如此，子项的布局可以直接获取
        }
        //修改子项的内部数据
        viewHolder.Sno.setText(studentItem.getSno());
        viewHolder.Sname.setText(studentItem.getSname());
        return view;
    }
    class ViewHolder{
        TextView Sno ;
        TextView Sname;
    }
}
