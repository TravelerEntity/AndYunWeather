package com.lee.andcloud.gson;

import com.google.gson.annotations.SerializedName;

public class LifeIndex {
    @SerializedName("ultraviolet")
    public UL ul;

    public Comfort comfort;


    /*紫外线指数*/
    public class UL {
        @SerializedName("index")
        public String ulIndex;

        @SerializedName("desc")
        public String ulDesc;
    }

    /*舒适度*/
    public class Comfort {
        @SerializedName("index")
        public String comfortIndex;

        @SerializedName("desc")
        public String comfortDesc;
    }

}
