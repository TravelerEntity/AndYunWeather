package com.lee.andcloud.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.lee.andcloud.R;
import com.lee.andcloud.gson.AirQuality;
import com.lee.andcloud.gson.DailyWeather;
import com.lee.andcloud.gson.Precipitation;
import com.lee.andcloud.gson.WeatherNowCY;
import com.lee.andcloud.gson.WeatherNowHF;
import com.lee.andcloud.util.HttpUtil;
import com.lee.andcloud.util.Location;
import com.lee.andcloud.util.Utility;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class WeatherActivity extends AppCompatActivity implements TencentLocationListener{
    private static final String HFTOKEN = "efa4346f71d446c9a35dd3224249ae47";
    private static final String TAG = "WeatherActivity";
    private static final String LOC_TEST = "106.3944,30.2752";
    /*location实例*/
    private TencentLocation location;
    TencentLocationManager locationManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*加载toolbar布局*/
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        requirePermission();

        getLocation();

//        setToolbar();
    }

    /*开子线程获取定位*/
    private void getLocation() {
        new Thread("定位线程") {
            @Override
            public void run() {
                Log.d(TAG, "run: " + getName());
                TencentLocationRequest request = TencentLocationRequest.create();
                request.setRequestLevel(3);

                locationManager = TencentLocationManager.getInstance(WeatherActivity.this);
                int a = locationManager.requestLocationUpdates(request, WeatherActivity.this, Looper.getMainLooper());
            }
        }.start();

    }
    /*加载天气数据*/
    private void loadWeather() {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = shp.getString("weatherHF",null);
        if(weatherString != null) {
            /*有缓存时就直接从本地加载*/
//            WeatherNowHF weatherNowHf = Utility.handleWeatherResponseHF(weatherString);
            requestWeatherHF("23");
            requestForecastHF();
        } else {
            String weatherId = getIntent().getStringExtra("weather_id");
            requestWeatherHF(weatherId);
            requestForecastHF();
        }
    }

    /*设置toolbar的样式*/
    private void setToolbar() {
        /*绑定toolbar*/
        Toolbar tbHead = findViewById(R.id.tb_head_city);
        /*将title设置为定位到的结果*/
        tbHead.setTitle(location.getTown());
        setSupportActionBar(tbHead);

        TextView mTitle = findViewById(R.id.tv_head_city);
        mTitle.setText(tbHead.getTitle());
        /*这个为了title居中*/
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }



    /*位置监听器回调*/
    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        locationManager.removeUpdates(this);
        this.location = tencentLocation;
        Log.d(TAG, "onLocationChanged: "+location);
        setToolbar();
        loadWeather();

    }
    /*位置监听器回调*/
    @Override
    public void onStatusUpdate(String s, int i, String s1) {
    }

    /**
     * 字符串转星期
     * @param date string格式的字符串
     * @return 当前的星期
     * @throws ParseException
     */
    public static String getWeek(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(Objects.requireNonNull(sdf.parse(date)));
        }catch (ParseException e){
            Log.e(TAG, "发生了日期转换异常，传入的date："+date, e);
        }
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }
    
    /*请求和风天气数据*/
    private void requestWeatherHF(String weatherId) {
//        String weatherUrl = "https://devapi.qweather.com/v7/weather/now?key="+HFTOKEN+"&location="+location.getLatitude()+","+location.getLongitude();
        String weatherUrl = "https://devapi.qweather.com/v7/weather/now?key="+HFTOKEN+"&location="+location.getLongitude()+","+location.getLatitude();
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "从服务器获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d(TAG, "翻译翻译，什么叫现在的天气怎么样: "+responseText);
                final WeatherNowHF weatherNowHF = Utility.handleWeatherResponseHF(responseText);
                /*把数据保存进SharePreferences*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*判断是否正常返回了天气数据*/
                        if( weatherNowHF != null && "200".equals(weatherNowHF.status) ){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            /*保存进preferences里面*/
                            editor.putString("weatherHF",responseText);
                            editor.apply();
                            showWeatherInfoHF(weatherNowHF);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * 从服务器获取未来4天气预报信息
     */
    private void requestForecastHF(){
        String forecastUrl = "https://devapi.qweather.com/v7/weather/7d?location="+location.getLongitude()+","+location.getLatitude()+"&key="+HFTOKEN;
        Log.d(TAG, "requestForecastHF: "+forecastUrl);
        HttpUtil.sendOkHttpRequest(forecastUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "从服务器获取天气预报信息失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, "翻译翻译，什么叫未来几天的天气情是是是是是搜索况: "+responseText);
                Log.d(TAG, "onResponse: 哇哇哇哇哇哇哇哇哇哇哇哇哇哇哇哇哇哇哇");

                final List<DailyWeather> weatherList = Utility.handleWeatherForecastHF(responseText);
                Log.d(TAG, "onResponse: "+ (weatherList != null ? weatherList.toString() : null));

                /*把数据保存进SharePreferences*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*判断是否正常返回了天气数据*/
                        if(true){
//                            SharedPreferences.Editor editor = PreferenceManager
//                                    .getDefaultSharedPreferences(MainActivity.this)
//                                    .edit();
//                            /*保存进preferences里面*/
//                            editor.putString("weather",responseText);
//                            editor.apply();
                            showForecastHF(weatherList);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气预报信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showForecastHF(List<DailyWeather> weatherList) {
        /*显示明天的天气*/
        showTomorrowWeather(weatherList.get(1));
        /*天气TextView 的id*/
        int[] widgetIds = {
                R.id.tv_forecast_date2,R.id.tv_forecast_highest2,R.id.tv_forecast_lowest2,R.id.iv_forecast_date2,
                R.id.tv_forecast_date3,R.id.tv_forecast_highest3,R.id.tv_forecast_lowest3,R.id.iv_forecast_date3,
                R.id.tv_forecast_date4,R.id.tv_forecast_highest4,R.id.tv_forecast_lowest4,R.id.iv_forecast_date4,
        };
        /*idIndex：界面上天气预报textview的id索引
          dateIndex：日期索引，用来指示改显示哪天的数据*/
        for (int idIndex = 0,dateIndex=2; idIndex < widgetIds.length; dateIndex++) {
            /*获取一天天气实例*/
            DailyWeather dailyWeather =  weatherList.get(dateIndex );

            /*绑定一天的天气控件*/
            TextView forecastData = findViewById( widgetIds[idIndex++] );
            TextView forecastHighest = findViewById(widgetIds[idIndex++] );
            TextView forecastLowest = findViewById(widgetIds[idIndex++] );
            ImageView forecastIcon = findViewById(widgetIds[idIndex++] );

            forecastData.setText(getWeek(dailyWeather.forecastDate));
            forecastHighest.setText( new StringBuilder(dailyWeather.tempMax+"°") );
            forecastLowest.setText(new StringBuilder(dailyWeather.tempMin+"°") );
            forecastIcon.setImageResource(getIconIdByNum(dailyWeather.weatherIconId) );
            Log.d(TAG, "showForecastHF: 天气对象"+dailyWeather);
        }


    }

    /**
     * 在界面显示明天的平均气温
     * @param tomorrow 天气对象实例
     */
    private void showTomorrowWeather(DailyWeather tomorrow) {
        int tMax = Integer.parseInt(tomorrow.tempMax);
        int tMin = Integer.parseInt(tomorrow.tempMin);

        TextView tvTomorrow = findViewById(R.id.tv_tomorrow_temp);
        ImageView ivTomorrowIcon = findViewById(R.id.iv_tmr_temp);

        String avgTemp = (String.valueOf(Math.round( (tMax+tMin) /2.0) ) )+"°";
        tvTomorrow.setText(avgTemp);
        ivTomorrowIcon.setImageResource(getIconIdByNum(tomorrow.weatherIconId));
    }

    /**
     * 把实时天气信息显示到界面
     * @param weatherNowHf 实时天气对象
     */
    private void showWeatherInfoHF(WeatherNowHF weatherNowHf) {
        TextView tvUpdateTime = findViewById(R.id.tv_update_time);
        TextView tvCurTemp = findViewById(R.id.tv_cur_temp);
        TextView tvHumidity = findViewById(R.id.tv_bottom_humidity);
        TextView tvAppearTemp = findViewById(R.id.tv_bottom_appear_temp);
        TextView tvWindScale = findViewById(R.id.tv_bottom_aqi);
        TextView tvPressure = findViewById(R.id.tv_bottom_pressure);
        TextView tvWindDir = findViewById(R.id.tv_bottom_aqi_idict);
        ImageView ivCurTemp = findViewById(R.id.iv_cur_weather_icon);

        tvUpdateTime.setText("现在");
        tvWindDir.setText(weatherNowHf.windDir);
        tvCurTemp.setText(weatherNowHf.temperature);
        tvWindScale.setText(new StringBuilder( weatherNowHf.windScale+"级" ) );
        tvHumidity.setText(new StringBuilder( weatherNowHf.humidity + "%" ) );
        tvAppearTemp.setText(new StringBuilder( weatherNowHf.feelsLike + "°" ) );
        tvPressure.setText(new StringBuilder( weatherNowHf.pressure+"hPa" ) );

        ivCurTemp.setImageResource( getIconIdByNum(weatherNowHf.icon) );
    }

    /**
     * 通过拼接icon码从而获取本地icon的资源名
     * @param icon
     * @return
     */
    private int getIconIdByNum(String icon){
        StringBuilder iconId = new StringBuilder("p"+icon);
        return this.getResources().
                getIdentifier(iconId.toString(),"drawable",this.getPackageName());

    }

    /**
     * 请求权限
     */
    @AfterPermissionGranted(1)
    private void requirePermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        };
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            Log.d(TAG, "requirePermission: 需要权限");
            EasyPermissions.requestPermissions(this, "需要权限", 1, permissions);
        }
    }

    /**
     * 授权后的回调
     * @param requestCode 请求码
     * @param permissions 请求的权限
     * @param grantResults 通过的权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: 请求码："+requestCode);

        for (String permission : permissions) {
            Log.d(TAG, "onRequestPermissionsResult: 请求的权限："+permission);
        }
        for (int grantResult : grantResults) {
            Log.d(TAG, "onRequestPermissionsResult: 通过的权限的结果"+grantResult);
        }

        if (requestCode == 1){
            getLocation();
        }

    }
}