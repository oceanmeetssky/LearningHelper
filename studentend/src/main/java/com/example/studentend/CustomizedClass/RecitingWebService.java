package com.example.studentend.CustomizedClass;

/**
 * Created by GCG on 2017/5/4.
 */

public class RecitingWebService {
    private CustomizedActivity context;
    private int ViewID;
    private Object answer;//存放服务结果

    public Object getAnswer(){
        return answer;
    }

    public void setAnswer( Object answer ){
        this.answer = answer;
        context.updateUI(answer,ViewID);
    }

    public RecitingWebService(CustomizedActivity activity,int id){
        this.context = activity;//哪一个活动调用了这次远程服务
        this.ViewID = id;//这一次远程服务调用要更新的UI控件的id
    }

    public void Request(Object... params) {
        //只要在一个形参的“类型”与“参数名”之间加上三个连续的“.”（即“...”，英文里的句中省略号），就可以让它和不确定个实参相匹配
        CustomizedAsyncTask customizedAsyncTask = new CustomizedAsyncTask(this);
        customizedAsyncTask.execute(params);
        //execute(Params... params)，执行一个异步任务，需要我们在代码中调用此方法，触发异步任务的执行。
        //在这里它的实参是直接将request()接受到的不定长参数列表params传给该函数
    }

}
