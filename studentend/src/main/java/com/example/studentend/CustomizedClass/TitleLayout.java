package com.example.studentend.CustomizedClass;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.studentend.R;


/**
 * Created by GCG on 2017/5/1.
 */

public class TitleLayout extends LinearLayout {
    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title_layout,this);//使用布局文件对这个控件进行样式设计
        ImageButton button = (ImageButton) findViewById(R.id.button_TitleBack);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();//销毁当前活动，返回上一次活动，即上一级界面
            }
        });
    }

}
