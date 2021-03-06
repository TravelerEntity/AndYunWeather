package com.lee.andcloud.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lee.andcloud.gson.City;
import com.lee.andcloud.gson.DailyWeather;
import com.lee.andcloud.gson.WeatherNowHF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static List<DailyWeather> handleWeatherForecastHF(String responseText) {
        try{
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(responseText);
            JSONArray weatherJSONArray = jsonObject.getJSONArray("daily");

            Log.d(TAG, "handleWeatherForecastHF: "+weatherJSONArray.get(0));
            return gson.fromJson(weatherJSONArray.toString(),new TypeToken<List<DailyWeather>>(){}.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static List<City> handleCitiesListResponse(String responseText) {
        try{
            Gson gson = new Gson();
            JSONObject jsonObjectResponse = new JSONObject(responseText);
            /*因为是二维数组，所以需要get两次array*/
            JSONArray jsonArrayResult = jsonObjectResponse.getJSONArray("result");
            /*转一维数组*/
            JSONArray jsonArrayResult1 = jsonArrayResult.getJSONArray(0);
            /*获取单个城市*/
//            JSONObject jsonObjectCity = jsonArrayResult1.getJSONObject(0);
            return gson.fromJson(jsonArrayResult1.toString(),new TypeToken<List<City>>(){}.getType());
//            return new Gson().fromJson(jsonArrayResult1.toString(),City[].class);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
