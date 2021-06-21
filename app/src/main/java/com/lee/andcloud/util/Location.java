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
    private TencentLocation location;
    private final Context context;
    private TencentLocationListener tListener;

  

    public Location(Context context,TencentLocationListener tListener) {
        this.context = context;
        this.tListener = tListener;
    }

    @Override
    public void run() {
        getCurLoc();
        Log.d(TAG, "run: 线程开启");
    }

    public TencentLocation getCurLoc(){
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setRequestLevel(3);

        TencentLocationManager locationManager = TencentLocationManager.getInstance(context);
//        int a = locationManager.requestSingleFreshLocation(null,this,Looper.getMainLooper());
        int a = locationManager.requestLocationUpdates(request,tListener);
        Log.d(TAG, "getLocation: 错误码是："+a);
        // STOPSHIP: 2021/6/17 0017 错误码是五，传入了request，喝tListernr 
//       int i =  locationManager.requestSingleFreshLocation(null,this,Looper.getMainLooper());
        return location;
    }

    /*位置监听器回调*/
    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        this.location = tencentLocation;
        Log.d(TAG, "onLocationChanged: "+location);
        Log.d(TAG, "onLocationChanged:"+i);
        Log.d(TAG, "onLocationChanged: "+s);
        Log.d(TAG, "onLocationChanged: 位置对象："+tencentLocation);
//        Log.d(TAG, "onLocationChanged: GPS强度："+tencentLocation.getGPSRssi());
    }

    /*位置监听器回调*/
    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        Log.d(TAG, "onStatusUpdate: 状态码："+s);
        Log.d(TAG, "onStatusUpdate: 状态码："+i);
        Log.d(TAG, "onStatusUpdate: 状态码："+s1);
    }



}
