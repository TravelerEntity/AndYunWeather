package com.lee.andcloud.gson;

import java.util.List;

public class CitiesList {

    public int status;
    public List<City> result;

    @Override
    public String toString() {
        return "CitiesList{" +
                "status=" + status +
                ", result=" + result +
                '}';
    }

    public static class City {
        public LocationBean location;
        public String address;


        @Override
        public String toString() {
            return "ResultBean{" +
                    "location=" + location +
                    ", address='" + address + '\'' +
                    '}';
        }

        public static class LocationBean {
            public double lat;
            public double lng;

            @Override
            public String toString() {
                return "LocationBean{" +
                        "lat=" + lat +
                        ", lng=" + lng +
                        '}';
            }
        }
    }
}
