package com.lee.andcloud.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 未来的天气情况
 */
public class DailyWeather {
    @SerializedName("fxDate")
    public String forecastDate;

    @SerializedName("iconDay")
    public String weatherIconId;

    @SerializedName("textDay")
    public String weatherDescDaylight;

    public String tempMax;

    public String tempMin;

    public String uvIndex;



    @Override
    public String toString() {
        return "DailyWeather{" +
                "forecastDate='" + forecastDate + '\'' +
                ", weatherIconId='" + weatherIconId + '\'' +
                ", weatherDescDaylight='" + weatherDescDaylight + '\'' +
                ", tempMax='" + tempMax + '\'' +
                ", tempMin='" + tempMin + '\'' +
                ", uvIndex='" + uvIndex + '\'' +
                '}';
    }
}
