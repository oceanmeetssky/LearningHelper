package com.example.gcg.teacherend.CustomizedClass;

import java.io.Serializable;

/**
 * Created by GCG on 2017/5/17.
 */

public class StudentItem {
    private String Sno;
    private String Sname;

    public String getSno() {
        return Sno;
    }

    public String getSname() {
        return Sname;
    }

    public StudentItem(String sno,String sname){
        this.Sno = sno;
        this.Sname = sname;
    }

}
