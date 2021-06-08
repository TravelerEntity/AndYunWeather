package com.lee.andcloud.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

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
import com.lee.andcloud.gson.LifeIndex;
import com.lee.andcloud.gson.Precipitation;
import com.lee.andcloud.gson.WeatherNow;
import com.lee.andcloud.util.HttpUtil;
import com.lee.andcloud.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WeatherActivity extends AppCompatActivity {
    private static final String caiyunToken = "VU1I3Ri5V2DoJgD7";
    private static final String TAG = "WeatherActivity";
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
        setContentView(R.layout.activity_weather);

        /*初始化页面控件*/
        initWidget();

        /*用toolbar替代actionbar*/
        setSupportActionBar(tbHead);

        loadNavBtn();

        /*加载天气数据*/
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = shp.getString("weather",null);
        if(weatherString != null) {
            /*有缓存时就直接从本地加载*/
            WeatherNow  weatherNow = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weatherNow);
        } else {
            String weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
        }

    }

    private void requestWeather(String weatherId) {
        String weatherUrl = "https://api.caiyunapp.com/v2.5/VU1I3Ri5V2DoJgD7/106.3944,30.2752/realtime.json";
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
                Log.d(TAG, "翻译翻译，什么叫: "+responseText);
                final WeatherNow weatherNow = Utility.handleWeatherResponse(responseText);
                Log.d(TAG, "onResponse: "+weatherNow.toString());
                /*把数据保存进SharePreferences*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*判断是否正常返回了天气数据*/
                        if( weatherNow != null && "ok".equals(weatherNow.status) ){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            /*保存进preferences里面*/
                            editor.putString("weather",responseText);
                            editor.apply();
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(WeatherNow weatherNow) {
        String temperature = weatherNow.temperature;
        String apparentTemp = weatherNow.apparentTemp;
        String humidity = weatherNow.humidity = weatherNow.humidity;
        String cloudCon = weatherNow.cloudCondition;
        AirQuality airQuality =  weatherNow.airQuality;
        Precipitation precipitation = weatherNow.precipitation;
        LifeIndex lifeIndex = weatherNow.lifeIndex;

        /*对温度进行格式化*/
        String tempString =String.valueOf( Math.round( Double.valueOf( temperature) ) ) ;

        tvDegree.setText(tempString+"°");
        tvAQI.setText(airQuality.aqiValue.aqiValueCH);
        tvPM25.setText(airQuality.pm25);
        tvWeatherInfo.setText(cloudCon);
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
     * 加载toolbar上的选项时系统调用
     * @param menu 要加载到这个menu上
     * @return 是否成功
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*加载菜单布局*/
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    /**
     * toolbar上按钮被点击时调用
     * @param item 被点击的按钮
     * @return 是否成功
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId() ){
            case R.id.it_more:
                Toast.makeText(this, "点击了更多按钮", Toast.LENGTH_SHORT).show();
                break;
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
        nsvWeather = findViewById(R.id.nsv_weather_layout);
        llForecast = findViewById(R.id.ll_forecast);

        tvTitleCity = findViewById(R.id.tv_title_city);
        tvTitleUpdateTime = findViewById(R.id.tv_title_update_time);
        tvDegree = findViewById(R.id.tv_degree);
        tvWeatherInfo = findViewById(R.id.tv_weather_info);
        tvPM25 = findViewById(R.id.tv_pm25);
        tvAQI = findViewById(R.id.tv_aqi);
        tvComfort  = findViewById(R.id.tv_comfort);
        tvCarWashing = findViewById(R.id.tv_car_washing);
        tvSport = findViewById(R.id.tv_sport);
    }
}