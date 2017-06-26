package com.example.studentend.CustomizedClass;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by GCG on 2017/5/5.
 */

public class CustomizedAsyncTask extends AsyncTask {
    private static final String TAG = "CustomizedAsyncTaskTag";
    private RecitingWebService remote;
    final String WEB_SERVICE_URL = "http://120.25.74.174/gcgWebSite/Service.asmx";
    //服务器链接，如果有端口的话还要注明端口，但是我们的服务器没有端口限制，所以不用写
    //我们这个项目的所有服务方法都在一个WebServices里面，所以上面的URL可以写死
    final String Namespace = "http://tempuri.org/";//命名空间,在发布的WebService文件里面有，由于我们没有改，默认是这个

    public CustomizedAsyncTask(RecitingWebService recitingWebService){
        this.remote = recitingWebService;
    }

    protected Object doInBackground(Object... params) {
        //后台执行，比较耗时的操作都可以放在这里。注意这里不能直接操作UI。
        //此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间。
        //在执行过程中可以调用publicProgress(Progress…)来更新任务的进度。
        //注意doInBackground的返回类型必须与AsyncTask声明的后台计算结果类型一致
                /*if (params != null && params.length == 2) {
                    return CallWebService((String) params[0], (Map<String, String>) params[1]);
                } else if (params != null && params.length == 1) {
                    return CallWebService((String) params[0], null);
                } else {
                    return null;
                }*/
        // 第一个参数是调用的服务方法名
        // 第二个参数是服务方法参数
        return CallWebService((String) params[0],(Map<String, Object>) params[1]);
    }

    protected void onPostExecute(Object result) {
        //相当于Handler处理UI的方式，在这里面可以使用在doInBackground得到的结果处理操作UI。
        //此方法在主线程执行，任务执行的结果doInBackground的返回值作为此方法的参数result返回
        if (result != null) {
            remote.setAnswer(result);
        }
        else {
            //Toast.makeText(context,"result == null",Toast.LENGTH_SHORT).show();
            Log.d("null","result == null");
        }
    }
    public Object CallWebService(String MethodName, Map<String, Object> MethodParams) {
        // 1、创建SoapObject对象，并指定webservice的命名空间和调用的方法名
        SoapObject request = new SoapObject(Namespace, MethodName);
        // 2、设置调用方法的参数值，如果没有参数，可以省略，
        if (MethodParams != null) {
            Iterator iterator = MethodParams.entrySet().iterator();//迭代器
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();//迭代获取Map里面的每一个key-value实例，同时获取key和value,从而不用查询Map两遍
                //注意不是所有的参数都是String，所以需要做判断，我们设定如果是以BYTE[]开头的参数就转换成byte[]
                //同时我们约定以'-'作为类型参数名分隔符。
                //String str[] =
                String parameterName = (String) entry.getKey();
                if(parameterName.indexOf("BYTE[]") != -1){//参数是byte[]类型的
                    String str [] = parameterName.split("-");
                    request.addProperty(str[1], (byte[]) entry.getValue());
                }
                else {
                    request.addProperty((String) entry.getKey(), (String) entry.getValue());
                }
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
        envelope.dotNet = true; //指定webservice的语言类型（java，PHP，dotNet）
        HttpTransportSE ht = new HttpTransportSE(WEB_SERVICE_URL);//创建HttpTransportSE对象 传入webservice服务器地址
        // 使用call方法调用WebService方法
        try {
            ht.call(null, envelope);//Call方法的第一个参数一般为null，第2个参数就是前面创建的SoapSerializationEnvelope对象。
            //ht知道WebService所在服务器的IP地址以及虚拟列表下服务文件所在地址
            //envelop包含一个SoapObject对象，该对象有Webservices的命名空间以及要调用的方法以及该方法的参数
            //于是就可以调用了
        } catch (HttpResponseException e) {
            //Toast.makeText(context, "HttpResponseException:\n" + e.toString(), Toast.LENGTH_SHORT);

        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(context, "IOException:\n" + e.toString(), Toast.LENGTH_SHORT);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            //Toast.makeText(context, "XmlPullParserException:\n" + e.toString(), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(context, "Exception:\n" + e.toString(), Toast.LENGTH_SHORT);
        }
            /*
            final SoapPrimitive result = (SoapPrimitive) envelope.getResponse();//注意getResponse只能返回一条信息，有待测试！
            if (result != null) {
                Log.d("----收到的回复----", result.toString());
                return result.toString();
            }
            */
        //获取WebServices服务结果的方式还有如下这种：
        return envelope.bodyIn;//获取到全部结果，可能是一维数组，也可能是二维数组，甚至可能仅仅是一个字符串

    }
}
