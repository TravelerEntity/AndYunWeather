package com.lee.andcloud.activity;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.lee.andcloud.R;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Temp extends AppCompatActivity implements TencentLocationListener {
    private static final String TAG = "Temp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_temp);

        requirePermission();
//        requirePermission();


        /*获取位置管理器*/
        TencentLocationManager locationManager = TencentLocationManager.getInstance(this);
        /*定位请求器*/
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setRequestLevel(1).setAllowGPS(true).setInterval(5000);
        Log.d(TAG, "onCreate: "+request.getQQ());
        /*发起定位*/

        locationManager.requestLocationUpdates(request,this);

        Log.d(TAG, "onCreate: 跑起来了");
//        locationManager.removeUpdates(this);


//        locationManager.requestSingleFreshLocation(null,this,Looper.getMainLooper());
    }

    /*位置监听器回调*/
    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        Log.d(TAG, "onLocationChanged: 位置对象："+tencentLocation);

    }
    /*位置监听器回调*/
    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        Log.d(TAG, "onStatusUpdate: 状态码："+s);
        Log.d(TAG, "onStatusUpdate: 状态码："+i);
        Log.d(TAG, "onStatusUpdate: 状态码："+s1);
    }

    @AfterPermissionGranted(1)
    private void requirePermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
//                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        };
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            Log.d(TAG, "requirePermission: 需要权限");
            EasyPermissions.requestPermissions(this, "需要权限", 1, permissions);
            for (int i = 0; i < 10; i++) {
                requirePermission();
            }
        }
    }
}