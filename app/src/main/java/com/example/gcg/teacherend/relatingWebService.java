package com.example.gcg.teacherend;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class relatingWebService extends Activity {
    TextView tvMessage;
    final String METHOD_HELLO_WORLD = "HelloWorld";//WebServices的方法名
    final String METHOD_ECHO_MESSAGE = "EchoMessage";

    //服务器链接，如果有端口的话还要注明端口，但是我们的服务器没有端口限制，所以不用写
    final String WEB_SERVICE_URL = "http://120.25.74.174/gcgWebSite/Service.asmx";
    final String Namespace = "http://tempuri.org/";//命名空间,在发布的WebService文件里面有，由于我们没有改，默认是这个

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatingwebservicelayout);
        initBtn();// 初始化按钮
        initTv();
    }

    private void initTv() {
        tvMessage = (TextView) this.findViewById(R.id.tvMessage);
    }

    private void initBtn() {
        View btnHelloWorld = this.findViewById(R.id.btnHelloWorld);
        btnHelloWorld.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Map<String, String> values = new HashMap<String, String>();
                values.put("msg", "这是Android手机发出的信息");
                Request(METHOD_HELLO_WORLD);
            }
        });

        View btnEchoMessage = this.findViewById(R.id.btnEchoMessage);
        btnEchoMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> values = new HashMap<String, String>();
                values.put("msg", "这是Android手机发出的信息");
                Request(METHOD_ECHO_MESSAGE, values);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * 执行异步任务
     *
     * @param params 方法名+参数列表（哈希表形式）
     */
    public void Request(Object... params) {
        //只要在一个形参的“类型”与“参数名”之间加上三个连续的“.”（即“...”，英文里的句中省略号），就可以让它和不确定个实参相匹配
        new AsyncTask<Object, Object, String>() {
            //public abstract class AsyncTask<Params, Progress, Result> { 
            //三种泛型参数类型分别代表“启动任务执行的输入参数”、“后台任务执行的进度”、“后台计算结果的类型”
            //在特定场合下，并不是所有类型都被使用，如果没有被使用，可以用Java.lang.Void类型代替。
            @Override
            protected String doInBackground(Object... params) {
                //后台执行，比较耗时的操作都可以放在这里。注意这里不能直接操作UI。
                //此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间。
                //在执行过程中可以调用publicProgress(Progress…)来更新任务的进度。
                //注意doInBackground的返回类型必须与AsyncTask声明的后台计算结果类型一致
                if (params != null && params.length == 2) {
                    return CallWebService((String) params[0], (Map<String, String>) params[1]);
                } else if (params != null && params.length == 1) {
                    return CallWebService((String) params[0], null);
                } else {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                //相当于Handler处理UI的方式，在这里面可以使用在doInBackground得到的结果处理操作UI。
                //此方法在主线程执行，任务执行的结果doInBackground的返回值作为此方法的参数result返回
                if (result != null) {
                    tvMessage.setText("服务器回复的信息 : " + result);
                }
            }
        }.execute(params);
        //execute(Params... params)，执行一个异步任务，需要我们在代码中调用此方法，触发异步任务的执行。
        //在这里它的实参是直接将request()接受到的不定长参数列表params传给该函数
    }
    /*
一个异步任务的执行一般包括以下几个步骤：
1.execute(Params... params)，执行一个异步任务，需要我们在代码中调用此方法，触发异步任务的执行。
2.onPreExecute()，在execute(Params... params)被调用后立即执行，一般用来在执行后台任务前对UI做一些标记。
3.doInBackground(Params... params)，在onPreExecute()完成后立即执行，用于执行较为费时的操作，此方法将接收输入参数和返回计算结果。在执行过程中可以调用publishProgress(Progress... values)来更新进度信息。
4.onProgressUpdate(Progress... values)，在调用publishProgress(Progress... values)时，此方法被执行，直接将进度信息更新到UI组件上。
5.onPostExecute(Result result)，当后台操作结束时，此方法将会被调用，计算结果将做为参数传递到此方法中，直接将结果显示到UI组件上。
在使用的时候，有几点需要格外注意：
1.异步任务的实例必须在UI线程中创建。
2.execute(Params... params)方法必须在UI线程中调用。
3.不要手动调用onPreExecute()，doInBackground(Params... params)，onProgressUpdate(Progress... values)，onPostExecute(Result result)这几个方法。
4.不能在doInBackground(Params... params)中更改UI组件的信息。
5.一个任务实例只能执行一次，如果执行第二次将会抛出异常。
因此，我们上述代码的异步任务执行情况是由主线程调用request()，处在主线程的request()实例化AsyncTask对象并确定后台任务的返回值为String
接着这个对象执行execute()开始异步任务处理,由于没有重写onPreExecute,所以经过一个类似空方法后直接进入doInBackground
并将处理结果给onPostExecute来更新UI，注意不能在doInBackground不是在UI线程做的，但是onPostExecute是由主线程调用的，所以他可以更新UI
可以这样理解：AscycTask对象会创建后台线程执行异步任务，并把操作结果通知UI线程。

Android.os.AsyncTask类：
* android的类AsyncTask对线程间通讯进行了包装，提供了简易的编程方式来使后台线程和UI线程进行通讯：后台线程执行异步任务，并把操作结果通知UI线程。
* AsyncTask是抽象类.AsyncTask定义了三种泛型类型 Params，Progress和Result。
* Params 启动任务执行的输入参数，比如HTTP请求的URL。这是doInBackground接受的参数
* Progress 后台任务执行的百分比。为显示进度的参数
* Result 后台执行任务最终返回的结果，比如String,Integer等。为doInBackground返回类型和onPostExecute传入的参数类型。
* doInBackground方法和onPostExecute的参数必须对应
* AsyncTask的执行分为四个步骤，每一步都对应一个回调方法，开发者需要实现这些方法。
* 1) 继承AsyncTask
* 2) 实现AsyncTask中定义的下面一个或几个方法
* onPreExecute(), 该方法将在执行实际的后台操作前被 UI线程 调用。可以在该方法中做一些准备工作，如在界面上显示一个进度条，或者一些控件的实例化，这个方法可以不用实现。
* doInBackground(Params...), 将在onPreExecute方法执行后马上执行，该方法运行在 后台线程 中。这里将主要负责执行那些很耗时的后台处理工作。可以调用 publishProgress方法来更新实时的任务进度。该方法是抽象方法，子类必须实现。
* onProgressUpdate(Progress...),在publishProgress方法被调用后，UI线程 将调用这个方法从而在界面上展示任务的进展情况，例如通过一个进度条进行展示。
* onPostExecute(Result), 在doInBackground 执行完成后，onPostExecute 方法将被 UI线程 调用，后台的计算结果将通过该方法传递到 UI线程，并且在界面上展示给用户.
* onCancelled(),在用户取消线程操作的时候调用。在主线程中调用onCancelled()的时候调用。

    * */

    /**
     * 调用WebService
     *
     * @return WebService的返回值
     */
    public String CallWebService(String MethodName, Map<String, String> Params) {
        // 1、创建SoapObject对象，并指定webservice的命名空间和调用的方法名
        SoapObject request = new SoapObject(Namespace, MethodName);
        // 2、设置调用方法的参数值，如果没有参数，可以省略，
        if (Params != null) {
            Iterator iter = Params.entrySet().iterator();//迭代器
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();//迭代获取Map里面的每一个key-value实例，同时获取key和value,从而不用查询Map两遍
                request.addProperty((String) entry.getKey(), (String) entry.getValue());
                //设置调用方法的参数值，如果没有参数，可以省略
                //要注意，addProperty方法的第1个参数虽然表示调用方法的参数名，但该参数值并不一定与服务端的WebService类中的方法参数名一致，只要设置参数的顺序和类型一致即可。
                //在这里我们调用的是WebServices的EchoMessage方法，该方法需要接受一个String类型的形参，虽然我们用了一个Map但是这个Map实际上仅有一对key-value实例
                //所以还是仅仅传入了一个参数，符合WebServices上提供的方法所规定的参数数目和类型
            }
        }
        // 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        //创建SoapSerializationEnvelope对象时需要通过SoapSerializationEnvelope类的构造方法设置SOAP协议的版本号。
        //该版本号需要根据服务端WebService的版本号设置。
        envelope.bodyOut = request;//bodyOut即为先前的SoapObject
        // c#写的应用程序必须加上这句
        envelope.dotNet = true; //指定webservice的类型的（java，PHP，dotNet）
        HttpTransportSE ht = new HttpTransportSE(WEB_SERVICE_URL);//创建HttpTransportSE对象 传入webservice服务器地址
        // 使用call方法调用WebService方法
        try {
            ht.call(null, envelope);//Call方法的第一个参数一般为null，第2个参数就是前面创建的SoapSerializationEnvelope对象。
            //ht知道WebService所在服务器的IP地址以及虚拟列表下服务文件所在地址
            //envelop包含一个SoapObject对象，该对象有Webservices的命名空间以及要调用的方法以及该方法的参数
            //于是就可以调用了
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            final SoapPrimitive result = (SoapPrimitive) envelope.getResponse();//注意getResponse只能返回一条信息，有待测试！
            if (result != null) {
                Log.d("----收到的回复----", result.toString());
                return result.toString();
            }
            /*获取WebServices服务结果的方式还有如下这种：
             ht.call(null, envelope);  
             SoapObject result = (SoapObject) envelope.bodyIn;  
             访问SoapSerializationEnvelope对象的bodyIn属性，该属性返回一个SoapObject对象，该对象就代表了Web Service的所有返回消息
             解析该SoapObject对象，即可获取调用web Service 的返回值
             detail = (SoapObject) result.getProperty("getWeatherbyCityNameResult");   
             System.out.println("detail"+ detail); 
            * */

        } catch (SoapFault e) {
            Log.e("----发生错误---", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


}
