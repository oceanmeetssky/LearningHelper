package com.example.studentend.CustomizedClass;

/**
 * Created by GCG on 2017/5/20.
 */

public class TaskRecord_QuestionItem {
    private String Title;
    private String Detail;

    public String getTitle() {
        return Title;
    }

    public String getDetail() {
        return Detail;
    }

    public TaskRecord_QuestionItem(String Title, String Detail){
        this.Title = Title;
        this.Detail = Detail;
    }
}
