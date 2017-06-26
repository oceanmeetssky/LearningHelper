package com.example.studentend.baidumaptest;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by Jiang Pei on 2016/5/16.
 */
public class LocationService {
    private LocationClient client = null;
    private LocationClientOption mOption, diyOption;
    private Object objLock = new Object();

    public LocationService(Context locationContext) {
        synchronized (objLock) {
            if (client == null) {
                client = new LocationClient(locationContext);
                client.setLocOption(getDefaultLocationClientOption());
            }
        }
    }

    public boolean registerListener(BDLocationListener listener) {
        boolean isSuccess = false;

        if (listener != null) {
            client.registerLocationListener(listener);
            isSuccess = true;
        }

        return isSuccess;
    }

    public void unregisterLocationListener(BDLocationListener listener) {
        if (listener != null)
            client.unRegisterLocationListener(listener);
    }

    public boolean setLocationOption(LocationClientOption option) {
        boolean isSuccess = false;

        if (option != null) {
            if (client.isStarted())
                client.stop();

            diyOption = option;
            client.setLocOption(option);
            isSuccess = true;
        }

        return isSuccess;
    }

    public LocationClientOption getOption() {
        return diyOption;
    }

    public void start() {
        synchronized (objLock) {
            if (client != null && !client.isStarted())
                client.start();
        }
    }

    public void stop() {
        synchronized (objLock) {
            if (client != null && client.isStarted())//保证了不会重复关闭
                client.stop();
        }
    }

    public LocationClientOption getDefaultLocationClientOption() {
        if (mOption == null) {
            mOption = new LocationClientOption();

            mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            mOption.setCoorType("bd0911");
            /*setCoorType：  设置返回值坐标类型  
                bd09ll  表示百度经纬度坐标，
                gcj02   表示经过国测局加密的坐标，
                wgs84   表示gps获取的坐标。
            * */
            mOption.setScanSpan(3000);
            mOption.setIsNeedAddress(true);
            mOption.setIsNeedLocationDescribe(true);
            mOption.setNeedDeviceDirect(false);
            mOption.setLocationNotify(false);
            mOption.setIgnoreKillProcess(true);
            mOption.setIsNeedLocationPoiList(true);
            mOption.SetIgnoreCacheException(false);
        }
        return mOption;
    }
}
