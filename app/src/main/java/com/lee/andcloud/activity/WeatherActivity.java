package com.lee.andcloud.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.lee.andcloud.R;
import com.lee.andcloud.gson.AirQuality;
import com.lee.andcloud.gson.DailyWeather;
import com.lee.andcloud.gson.Precipitation;
import com.lee.andcloud.gson.WeatherNowCY;
import com.lee.andcloud.gson.WeatherNowHF;
import com.lee.andcloud.util.HttpUtil;
import com.lee.andcloud.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private static final String HFTOKEN = "efa4346f71d446c9a35dd3224249ae47";
    private static final String TAG = "WeatherActivity";
    private static final String LOC_TEST = "106.3944,30.2752";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        /*加载天气数据*/
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
     * 从服务器获取天气预报信息
     */
    private void requestForecastHF(){
        String forecastUrl = "https://devapi.qweather.com/v7/weather/7d?location="+LOC_TEST+"&key="+HFTOKEN;
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
        showTomorrowWeather(weatherList.get(0));
        /*天气TextView 的id*/
        int[] widgetIds = {
                R.id.tv_forecast_date2,R.id.tv_forecast_highest2,R.id.tv_forecast_lowest2,
                R.id.tv_forecast_date3,R.id.tv_forecast_highest3,R.id.tv_forecast_lowest3,
                R.id.tv_forecast_date4,R.id.tv_forecast_highest4,R.id.tv_forecast_lowest4,
        };

        for (int idIndict = 0,dateIndic=1; idIndict < widgetIds.length; dateIndic++) {
            /*获取一天天气实例*/
            DailyWeather dailyWeather =  weatherList.get(dateIndic );

            /*绑定一天的天气控件*/
            TextView forecastData = findViewById( widgetIds[idIndict++] );
            TextView forecastHighest = findViewById(widgetIds[idIndict++] );
            TextView forecastLowest = findViewById(widgetIds[idIndict++] );

            forecastData.setText("日期");

            forecastHighest.setText( new StringBuilder(dailyWeather.tempMax+"°") );
            forecastLowest.setText(new StringBuilder(dailyWeather.tempMin+"°") );
            Log.d(TAG, "showForecastHF: "+dailyWeather.tempMax);
            Log.d(TAG, "showForecastHF: "+dailyWeather.tempMin);
                
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
        String avgTemp = (String.valueOf(Math.round( (tMax+tMin) /2.0) ) )+"°";
        tvTomorrow.setText(avgTemp);
    }

    private void showWeatherInfoHF(WeatherNowHF weatherNowHf) {
        TextView tvCurTemp = findViewById(R.id.tv_cur_temp);
        TextView tvHumidity = findViewById(R.id.tv_bottom_humidity);
        TextView tvAppearTemp = findViewById(R.id.tv_bottom_appear_temp);
        TextView tvWindScale = findViewById(R.id.tv_bottom_aqi);
        TextView tvPressure = findViewById(R.id.tv_bottom_pressure);
        TextView tvWindDir = findViewById(R.id.tv_bottom_aqi_idict);


        tvWindDir.setText(weatherNowHf.windDir);
        tvCurTemp.setText(weatherNowHf.temperature);
        tvWindScale.setText(new StringBuilder( weatherNowHf.windScale+"级" ) );
        tvHumidity.setText(new StringBuilder( weatherNowHf.humidity + "%" ) );
        tvAppearTemp.setText(new StringBuilder( weatherNowHf.feelsLike + "°" ) );
        tvPressure.setText(new StringBuilder( weatherNowHf.pressure+"hPa" ) );
    }
}