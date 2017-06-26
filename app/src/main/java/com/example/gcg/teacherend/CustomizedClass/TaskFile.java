package com.example.gcg.teacherend.CustomizedClass;

/**
 * Created by GCG on 2017/5/28.
 */

public class TaskFile {

    private String Sno;//学号
    private String Sname;//姓名
    private String fileName;//文件名
    private String filePath;//文件路径

    public TaskFile(String Sno,String Sname,String fileName,String filePath){
        this.Sno = Sno;
        this.Sname = Sname;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getSname() {
        return Sname;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSno() {
        return Sno;
    }

    public String getFilePath() {
        return filePath;
    }
}
