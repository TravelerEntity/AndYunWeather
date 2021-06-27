package com.lee.andcloud.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.andcloud.R;
import com.lee.andcloud.gson.AirQuality;
import com.lee.andcloud.gson.DailyWeather;
import com.lee.andcloud.gson.Precipitation;
import com.lee.andcloud.gson.WeatherNowCY;
import com.lee.andcloud.gson.WeatherNowHF;
import com.lee.andcloud.util.HttpUtil;
import com.lee.andcloud.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String CAIYUNTOKEN = "VU1I3Ri5V2DoJgD7";
    private static final String HFTOKEN = "efa4346f71d446c9a35dd3224249ae47";
    private static final String TAG = "MainActivity";
    private String loc = "106.3944,30.2752";

    /*顶部toolbar*/
    private Toolbar tbHead;


    /*页面布局控件*/
    private DrawerLayout drawerLayout;
    private NestedScrollView nsvWeather;
    private LinearLayout llForecast;

    /*页面的文本控件*/
    private TextView tvTitleCity;
    private TextView tvTitleUpdateTime;
    private TextView tvDegree;
    private TextView tvWeatherInfo;
    private TextView tvPM25;
    private TextView tvAQI;
    private TextView tvComfort;
    private TextView tvCarWashing;
    private TextView tvSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        /*初始化页面控件*/
        initWidget();

        /*用toolbar替代actionbar*/
        setSupportActionBar(tbHead);

        loadNavBtn();

        /*加载天气数据*/
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = shp.getString("weatherHF",null);
        if(weatherString != null) {
            /*有缓存时就直接从本地加载*/
//            WeatherNowHF weatherNowHf = Utility.handleWeatherResponseHF(weatherString);
            requestWeatherHF("23");
            requestWeatherCY("@");
            requestForecastHF();
        } else {
            String weatherId = getIntent().getStringExtra("weather_id");
            requestWeatherHF(weatherId);
            requestWeatherCY("@");
            requestForecastHF();
        }

    }

    /*请求和风天气数据*/
    private void requestWeatherHF(String weatherId) {
        String weatherUrl = "https://devapi.qweather.com/v7/weather/now?key="+HFTOKEN+"&location=106.3944,30.2752";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "从服务器获取天气信息失败", Toast.LENGTH_SHORT).show();
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
                                    .getDefaultSharedPreferences(MainActivity.this)
                                    .edit();
                            /*保存进preferences里面*/
                            editor.putString("weatherHF",responseText);
                            editor.apply();
                            showWeatherInfoHF(weatherNowHF);
                        } else {
                            Toast.makeText(MainActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * 从服务器获取天气预报信息
     */
    private void requestForecastHF(){
        String forecastUrl = "https://devapi.qweather.com/v7/weather/3d?location="+loc+"&key="+HFTOKEN;
        HttpUtil.sendOkHttpRequest(forecastUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "从服务器获取天气预报信息失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, "翻译翻译，什么叫未来几天的天气情况: "+responseText);

                final List<DailyWeather> weatherList = Utility.handleWeatherForecastHF(responseText);
                Log.d(TAG, "onResponse: "+ weatherList.toString());

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
//                            showWeatherInfoCY(weatherNowCY);
                        } else {
                            Toast.makeText(MainActivity.this, "获取天气预报信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /*请求彩云天气数据*/
    private void requestWeatherCY(String weatherId) {
        String weatherUrl = "https://api.caiyunapp.com/v2.5/"+CAIYUNTOKEN+"/106.3944,30.2752/realtime.json";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "从服务器获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = Objects.requireNonNull(response.body()).string();
                Log.d(TAG, "翻译翻译，什么叫: "+responseText);
                final WeatherNowCY weatherNowCY = Utility.handleWeatherResponseCY(responseText);
                Log.d(TAG, "onResponse: "+ weatherNowCY.toString());
                /*把数据保存进SharePreferences*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*判断是否正常返回了天气数据*/
                        if("ok".equals(weatherNowCY.status)){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(MainActivity.this)
                                    .edit();
                            /*保存进preferences里面*/
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfoCY(weatherNowCY);
                        } else {
                            Toast.makeText(MainActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    private void showWeatherInfoHF(WeatherNowHF weatherNowHf) {
      String temperature = weatherNowHf.temperature;
      String feelsLike = weatherNowHf.feelsLike;
      String humidity =  weatherNowHf.humidity;
      String updateTime = weatherNowHf.updateTime;
      String weatherCon =  weatherNowHf.weatherCondition;

//        Log.d(TAG, "showWeatherInfo: "+temperature);
//        Log.d(TAG, "showWeatherInfo: "+humidity);
//        Log.d(TAG, "showWeatherInfo: "+feelsLike);

      tvDegree.setText(temperature);
      tvWeatherInfo.setText(weatherCon);
    }

    private void showWeatherInfoCY(WeatherNowCY weatherNowCY) {
        AirQuality airQuality =  weatherNowCY.airQuality;
        Precipitation precipitation = weatherNowCY.precipitation;

//        Log.d(TAG, "showWeatherInfoCY: "+weatherNowCY.responseTime);

        tvAQI.setText(airQuality.aqiValue.aqiValueCH);
        tvPM25.setText(airQuality.pm25);
    }

    private void loadNavBtn() {
        /*获取actionbar实例，不为空才会进行后面的设置*/
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_location_city_24);
        }
    }

    /**
     * toolbar上按钮被点击时调用
     * @param item 被点击的按钮
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId() ){
//            case R.id.it_more:
//                Toast.makeText(this, "点击了更多按钮", Toast.LENGTH_SHORT).show();
//                break;
            /*toolbar上的导航按钮被点击时，最左边按钮*/
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    /**
     * 初始化界面的View
     */
    private void initWidget() {
        tbHead = findViewById(R.id.tb_head);
        drawerLayout = findViewById(R.id.drawer_layout);
//        nsvWeather = findViewById(R.id.nsv_weather_layout);
//        llForecast = findViewById(R.id.ll_forecast);
//
//        tvTitleCity = findViewById(R.id.tv_title_city);
//        tvTitleUpdateTime = findViewById(R.id.tv_title_update_time);
//        tvDegree = findViewById(R.id.tv_degree);
//        tvWeatherInfo = findViewById(R.id.tv_weather_info);
//        tvPM25 = findViewById(R.id.tv_pm25);
//        tvAQI = findViewById(R.id.tv_aqi);
//        tvComfort  = findViewById(R.id.tv_comfort);
//        tvCarWashing = findViewById(R.id.tv_car_washing);
//        tvSport = findViewById(R.id.tv_sport);
    }
}