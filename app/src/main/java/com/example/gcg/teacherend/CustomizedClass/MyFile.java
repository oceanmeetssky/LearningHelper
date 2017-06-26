package com.example.gcg.teacherend.CustomizedClass;

/**
 * Created by GCG on 2017/5/29.
 */

public class MyFile {

    private String fileName;
    private String filePath;

    public MyFile(String fileName,String filePath){
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }
    public String getFilePath() {
        return filePath;
    }

}
