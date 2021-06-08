package com.lee.andcloud.gson;

import com.google.gson.annotations.SerializedName;

public class AirQuality {
    public String pm25;

    /*aqi具体值*/
    @SerializedName("aqi")
    public AQI aqiValue;

    /*对上面的aqi值进行的描述*/
    @SerializedName("description")
    public Description aqiDescription;


    public class AQI{
        /*aqi国标*/
        @SerializedName("chn")
        public String aqiValueCH;

        /*aqi美标*/
        @SerializedName("usa")
        public String aqiValueUSA;
    }
    public class Description{
        @SerializedName("chn")
        public String aqiDscCH;

        @SerializedName("usa")
        public String aqiDscUSA;
    }
}
