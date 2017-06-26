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

public class TaskQuestionDetailItemAdapter extends ArrayAdapter<TaskRecord_QuestionItem> {
    private int resourceID;

    public TaskQuestionDetailItemAdapter(Context context, int resource, List<TaskRecord_QuestionItem> objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @NonNull
    @Override
    //getView()会在每个子项被滚动到屏幕内的时候调用
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskRecord_QuestionItem taskRecord_questionItem = getItem(position);//获取当前项的ClassItem实例
        View view;
        TaskQuestionDetailItemAdapter.ViewHolder viewHolder;
        //convertView用于将之前加载的布局进行缓存
        //这样我们就可以不用每次都重新加载ListView的子项布局
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            viewHolder = new TaskQuestionDetailItemAdapter.ViewHolder();
            //同理使用一个ViewHolder来保存子项内部组件的布局
            viewHolder.Title = (TextView) view.findViewById(R.id.textview_listview_question_title);
            viewHolder.Detail = (TextView) view.findViewById(R.id.textview_listview_question_detail);

            view.setTag(viewHolder);//将ViewHolder存储在View中
        }else {
            view = convertView;
            viewHolder = (TaskQuestionDetailItemAdapter.ViewHolder)view.getTag();
            //如此，子项的布局可以直接获取
        }
        //修改子项的内部数据
        viewHolder.Title.setText(taskRecord_questionItem.getTitle());
        viewHolder.Detail.setText(taskRecord_questionItem.getDetail());

        return view;
    }

    class ViewHolder{
        TextView Title;
        TextView Detail;
    }
}
