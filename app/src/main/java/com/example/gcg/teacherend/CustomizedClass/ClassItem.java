package com.example.gcg.teacherend.CustomizedClass;

/**
 * Created by GCG on 2017/5/17.
 */

public class ClassItem {
    public String Deadline;
    private String Classname;

    public String getDeadline() {
        return Deadline;
    }

    public String getClassname() {
        return Classname;
    }

    public ClassItem(String Deadline,String Classname){
        this.Deadline = Deadline;
        this.Classname = Classname;
    }

}
