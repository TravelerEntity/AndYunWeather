package com.lee.andcloud.util;

import android.content.Context;
import android.location.LocationListener;
import android.os.Looper;
import android.util.Log;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;


public class Location extends Thread implements TencentLocationListener {

    private final String TAG = "Location";
    /*location实例*/
    private TencentLocation tLocation;
    private final Context context;

  

    public Location(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Log.d(TAG, "run: "+getName());
        getCurLoc();
    }

    public void getCurLoc(){
        TencentLocationRequest locationRequest = TencentLocationRequest.create();
        Log.d(TAG, "getCurLoc: "+getName());
        TencentLocationManager locationManager = TencentLocationManager.getInstance(context);
        int i =  locationManager.requestSingleFreshLocation(locationRequest,this,Looper.getMainLooper());

    }

    /*位置监听器回调*/
    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        Log.d(TAG, "onLocationChanged: "+getName());
        this.tLocation = tencentLocation;
        Log.d(TAG, "onLocationChanged:"+i);
        Log.d(TAG, "onLocationChanged: "+s);
        Log.d(TAG, "onLocationChanged: "+tencentLocation);
    }

    /*位置监听器回调*/
    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        Log.d(TAG, "onStatusUpdate: 状态码："+s);
        Log.d(TAG, "onStatusUpdate: 状态码："+i);
        Log.d(TAG, "onStatusUpdate: 状态码："+s1);
    }

}
