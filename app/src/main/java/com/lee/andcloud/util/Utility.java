package com.lee.andcloud.util;

import android.text.TextUtils;

import com.lee.andcloud.db.City;
import com.lee.andcloud.db.County;
import com.lee.andcloud.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

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
