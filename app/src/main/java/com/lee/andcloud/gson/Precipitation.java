package com.lee.andcloud.gson;

import com.google.gson.annotations.SerializedName;

public class Precipitation {
    public Local local;
    public Nearest nearest;


    /*本地降雨情况*/
    public class Local{
        /*降雨强度*/
        public String intensity;
    }
    /*最近的降雨情况*/
    public class Nearest{
        public String distance;
        public String intensity;
    }
}
