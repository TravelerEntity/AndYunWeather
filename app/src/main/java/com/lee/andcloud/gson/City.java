package com.lee.andcloud.gson;

public class City {
    public String address;
    public Geo location;

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
    }

    @Override
    public String toString() {
        return "City{" +
                "address='" + address + '\'' +
                ", location=" + location +
                '}';
    }


}
