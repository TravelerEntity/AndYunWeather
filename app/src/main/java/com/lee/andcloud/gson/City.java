package com.lee.andcloud.gson;

import android.location.Location;

public class City {
    public String address;
    public Geo location;

    public City(String address,String geo) {
        String[] geoArray = geo.split(",");
        this.address = address;
        this.location = new Geo();
        this.location.setGeoLocation(geoArray[1],geoArray[0]);
    }

    public static class Geo {
        public String lat;
        public String lng;

        @Override
        public String toString() {
            return lng+","+lat;
        }
        public void setGeoLocation(double lat,double lng){
            this.lat = lat+"";
            this.lng = lng+"";
        }
        public void setGeoLocation(String lat,String lng){
            this.lat = lat;
            this.lng = lng;
        }
    }

    @Override
    public String toString() {
        return "City{" +
                "address='" + address + '\'' +
                ", location=" + location +
                '}';
    }


}
