package com.example.studentend.CustomizedClass;

/**
 * Created by GCG on 2017/5/20.
 */

public class TaskRecordItem {
    private String ReleaseTime;
    private String Deadline;
    private String TaskListNo;//这个是TaskList的主码
    private String TaskNo;//这个是给用户看的第几次任务，要显示出来的，比如：第1次作业

    public String getTaskListNo() {
        return TaskListNo;
    }

    public String getReleaseTime() {
        return ReleaseTime;
    }

    public String getDeadline() {
        return Deadline;
    }

    public String getTaskNo() {
        return TaskNo;
    }

    public TaskRecordItem(String TaskListNo,String ReleaseTime,String Deadline,String TaskNo ){
        this.TaskListNo = TaskListNo;
        this.ReleaseTime = ReleaseTime;
        this.Deadline = Deadline;
        this.TaskNo = TaskNo;
    }
}
