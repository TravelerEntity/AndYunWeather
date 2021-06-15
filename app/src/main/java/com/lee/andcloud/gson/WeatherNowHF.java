package com.lee.andcloud.gson;

import com.google.gson.annotations.SerializedName;

public class WeatherNowHF {
    public String status;

    @SerializedName("obsTime")
    public String updateTime;

    @SerializedName("temp")
    public String temperature;

    public String feelsLike;

    @SerializedName("text")
    public String weatherCondition;

    public String pressure;

    public String humidity;

    public String windDir;
    public String windScale;



}
