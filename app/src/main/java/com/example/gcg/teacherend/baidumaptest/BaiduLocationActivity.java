package com.example.gcg.teacherend.baidumaptest;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.example.gcg.teacherend.R;

public class BaiduLocationActivity extends Activity {

    private TextView label;
    private LocationService locationService;
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            Toast.makeText(getApplicationContext(), "onReceiveLocation", Toast.LENGTH_SHORT).show();

            if (bdLocation != null && bdLocation.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(512);
                sb.append("时间：" + bdLocation.getTime() + "\n");
                sb.append("定位类型：" + bdLocation.getLocType() + "\n");
                sb.append("经度：" + bdLocation.getLongitude() + "\n");
                sb.append("纬度：" + bdLocation.getLatitude() + "\n");
                sb.append("国家代码：" + bdLocation.getCountryCode() + "\n");
                sb.append("国家：" + bdLocation.getCountry() + "\n");
                sb.append("城市：" + bdLocation.getCity() + "\n");
                sb.append("区域：" + bdLocation.getDistrict() + "\n");
                sb.append("街道：" + bdLocation.getStreet() + "\n");
                sb.append("描述：" + bdLocation.getLocationDescribe() + "\n");
                StringBuffer append = sb.append("POI: ");

                if (bdLocation.getPoiList() != null && !bdLocation.getPoiList().isEmpty()) {
                    for (int i = 0; i < bdLocation.getPoiList().size(); i++) {
                        Poi poi = (Poi) bdLocation.getPoiList().get(i);
                        sb.append(poi.getName() + "; ");
                    }
                }

                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(bdLocation.getSpeed());// 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(bdLocation.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(bdLocation.getAltitude());// 单位：米
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    sb.append("\noperationers : ");
                    sb.append(bdLocation.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }

                label.setText(sb.toString());
                return;
            }

            label.setText("定位失败！");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main_2);

        label = (TextView) findViewById(R.id.label1);
        label.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void setLocationResult(String s) {
        label.setText(s);
    }

    @Override
    protected void onStop() {
        locationService.unregisterLocationListener(mListener);
        locationService.stop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        locationService = new LocationService(getApplicationContext());
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }

    public void onLocation(View view) {
        if (((Button) view).getText().toString().equals("开始定位")) {
            //init();
            locationService.start();
            ((Button) view).setText("停止定位");
        } else {
            locationService.stop();
            ((Button) view).setText("开始定位");
        }
    }
}
