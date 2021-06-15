package com.lee.andcloud.gson;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * 当前天气的jsonmapper，对应彩云返回值中的result对象
 */
public class WeatherNowCY {

    /*实时天气返回状态*/
    public String status;

    /*服务器响应的时间*/
    @SerializedName("server_time")
    public String responseTime;

    public String temperature;

    public String humidity;

    @SerializedName("apparent_temperature")
    public String apparentTemp;

    @SerializedName("skycon")
    public String cloudCondition;

    /*降雨量*/
    public Precipitation precipitation;

    @SerializedName("air_quality")
    public AirQuality airQuality;

}
