package com.lee.andcloud.util;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.lee.andcloud.db.City;
import com.lee.andcloud.db.County;
import com.lee.andcloud.db.Province;
import com.lee.andcloud.gson.DailyWeather;
import com.lee.andcloud.gson.WeatherNowCY;
import com.lee.andcloud.gson.WeatherNowHF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utility {
    private static final String TAG = "Utility";

    
    
    public static WeatherNowHF handleWeatherResponseHF(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject resultOfWeatherNowHF = jsonObject.getJSONObject("now");
            WeatherNowHF weatherNowHF = new Gson().fromJson(resultOfWeatherNowHF.toString(),WeatherNowHF.class);
            /*保存响应代码*/
            weatherNowHF.status = jsonObject.getString("code");
            /*JSON转GSON转WeatherNow类对象*/
            return weatherNowHF;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /** 彩云天气源
     * 解析传入的json格式的字符串
     * @param response 服务器响应的当前天气字符串，json格式
     * @return 当前天气的json对象
     */
    public static WeatherNowCY handleWeatherResponseCY(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject resultOfWeatherNow = jsonObject.getJSONObject("result").getJSONObject("realtime");
            WeatherNowCY weatherNowCY = new Gson().fromJson(resultOfWeatherNow.toString(), WeatherNowCY.class);
            /*保存服务器响应时间*/
            weatherNowCY.responseTime = jsonObject.getString("server_time");
            return weatherNowCY;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<DailyWeather> handleWeatherForecastHF(String responseText) {
        try{
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(responseText);
            JSONArray weatherJSONArray = jsonObject.getJSONArray("daily");

            Log.d(TAG, "handleWeatherForecastHF: "+weatherJSONArray.get(0));
            List<DailyWeather> weatherList =gson.fromJson(weatherJSONArray.toString(),new TypeToken<List<DailyWeather>>(){}.getType());

            return weatherList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    /*
    处理服务器相应回来省份字符串，这里的三个方法分别用来处理省、市、县的数据
    接收到服务器的数据后，装配成对象然后调用父类的save()方法，放进数据库里
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)) {
            try{
                /*构建json array*/
                JSONArray allProvinces = new JSONArray(response);
                /*遍历json array*/
                for (int i = 0; i < allProvinces.length(); i++ ){
                    JSONObject provincesJSONObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provincesJSONObject.getString("name"));
                    province.setProvinceCode(provincesJSONObject.getInt("id"));
                    /*这个方法是继承父类的，将返回的对象加进数据库中*/
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }

    /*
   处理服务器相应回来市级字符串
    */
    public static boolean handleCityResponse(String response, int provinceId){
        if(!TextUtils.isEmpty(response)) {
            try{
                /*构建json array*/
                JSONArray allCities = new JSONArray(response);
                /*遍历json array*/
                for (int i = 0; i < allCities.length(); i++ ){
                    JSONObject citiesJSONObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(citiesJSONObject.getString("name"));
                    city.setCityCode(citiesJSONObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    /*这个方法是继承父类的，将返回的对象加进数据库中*/
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }

    /*
   处理服务器相应回来县、区字符串
    */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)) {
            try{
                /*构建json array*/
                JSONArray allCounties = new JSONArray(response);
                /*遍历json array*/
                for (int i = 0; i < allCounties.length(); i++ ){
                    JSONObject countiesJSONObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countiesJSONObject.getString("name"));
                    county.setWeatherId(countiesJSONObject.getString("weather_id"));
                    county.setCityId(cityId);
                    /*这个方法是继承父类的，将返回的对象加进数据库中*/
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }



}
