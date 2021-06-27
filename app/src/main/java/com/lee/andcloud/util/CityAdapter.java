package com.lee.andcloud.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lee.andcloud.R;
import com.lee.andcloud.activity.WeatherActivity;
import com.lee.andcloud.gson.City;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MViewHolder> {
    private final List<City> cityList;
    private final Context mContext;
    private WeatherActivity activity;
    private static final String TAG = "CityAdapter";

    public CityAdapter(List<City> cityList , Context mContext) {
        this.cityList = cityList;
        this.mContext = mContext;
        Log.d(TAG, "CityAdapter: "+cityList);
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MViewHolder(LayoutInflater.from(mContext).inflate(R.layout.city_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {
        holder.tvAddress.setText(cityList.get(position).address);
        holder.cd_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+cityList.get(position).location);
                String addressName = handleAddress(cityList.get(position).address);
                /*在fragment里直接拿到activity对象，然后调用方法设置天气信息*/
                WeatherActivity weatherActivity = (WeatherActivity)mContext;
                weatherActivity.setGeoLocation(cityList.get(position).location);
                weatherActivity.getDrawerLayout().closeDrawer(GravityCompat.START);
                weatherActivity.loadWeather();
                weatherActivity.setTitleCity(addressName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    static class MViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddress;
        CardView cd_city;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tv_address);
            cd_city = itemView.findViewById(R.id.cd_city);
        }
    }

    /*裁减过多的地址名*/
    public String handleAddress(String addressName){
        String[] addressArray = addressName.split(",");
        if (addressArray.length>1){
            return addressArray[0]+","+addressArray[addressArray.length-1];
        }else {
             return addressName;
        }

    }
}
