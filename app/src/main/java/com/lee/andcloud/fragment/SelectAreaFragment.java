package com.lee.andcloud.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lee.andcloud.R;
import com.lee.andcloud.db.City;
import com.lee.andcloud.db.County;
import com.lee.andcloud.db.Province;
import com.lee.andcloud.util.HttpUtil;
import com.lee.andcloud.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class SelectAreaFragment extends Fragment {
    private static final String TAG = "SelectAreaFragment";
    
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView tvTitle;
    private ImageButton btBack;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    /*List的数据源*/
    private List<String> dataList = new ArrayList<>();

    /*省市区的list*/
    private List<Province> provinceList;
    private List<City> cityList;

    /*选中的省和市*/
    private Province selectedProvince;
    private City selectedCity;

    /*当前选中的级别*/
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        /*初始化各种控件*/
        View view = inflater.inflate(R.layout.select_area, container ,false);
        tvTitle = view.findViewById(R.id.tv_title);
        btBack = view.findViewById(R.id.ibt_back);
        listView = view.findViewById(R.id.lv_city);
        /*这里有个疑问，dataList是空的，想通了，空就空呗，马上就会放数据进去的*/
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        /*当点击一个列表项后触发的几个if*/
        listView.setOnItemClickListener((parent, view, position, id) -> {
            /* 通过currentLevel来了解当前选中的是省还是市，然后存入相应的变量执行查询*/
            if(currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provinceList.get(position);
                queryCities();

            } else if(currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(position);
                queryCounties();
            }
        });
        /*返回按钮的监听器*/
        btBack.setOnClickListener(v -> {
            if(currentLevel == LEVEL_COUNTY){
                queryCities();
            } else if(currentLevel == LEVEL_CITY) {
                queryProvinces();
            }
        });
        queryProvinces();
    }
    /*查找省份，优选从数据库找*/
    private void queryProvinces() {
        Log.d(TAG, "queryProvinces: ");
        /*设置界面的样式细节*/
        tvTitle.setText("中国");
        btBack.setVisibility(View.GONE);
        /*本地查询*/
        provinceList = LitePal.findAll(Province.class);
        Log.d(TAG, "queryProvinces: provinceList大小"+provinceList.size());
        /*本地查询成功*/
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName() );
            }
            adapter.notifyDataSetChanged();
            /*listView回到顶部*/
            listView.setSelection(0);
            /*设置显示级别为省*/
            currentLevel = LEVEL_PROVINCE;
        } else {
            /*网络查询*/
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    private void queryCities() {
        Log.d(TAG, "queryCities: ");
        /*设置界面的样式细节*/
        tvTitle.setText(selectedProvince.getProvinceName());
        btBack.setVisibility(View.VISIBLE);
        /*本地查询*/
        cityList = LitePal.where("provinceid = ?",
                String.valueOf(selectedProvince.getId() ) ).find(City.class);
        /*本地查询成功*/
        Log.d(TAG, "queryCities: CityList大小"+cityList.size());
        if (cityList.size() > 0 ){
            dataList.clear();
            for (City city : cityList ) {
                dataList.add(city.getCityName() );
            }
            adapter.notifyDataSetChanged();
            /*listView指针归零*/
            listView.setSelection(0);
            /*设置显示级别为省*/
            currentLevel = LEVEL_CITY;
        } else {
            /*网络查询*/
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" +provinceCode;
            queryFromServer(address,"city");
        }

    }

    private void queryCounties() {
        Log.d(TAG, "queryCounties: ");
        /*设置界面的样式细节*/
        tvTitle.setText(selectedCity.getCityName());
        btBack.setVisibility(View.VISIBLE);
        /*本地查询*/
        List<County> countyList = LitePal.where("cityid= ?",
                String.valueOf(selectedCity.getId())).find(County.class);
        /*本地查询成功*/
        if (countyList.size() > 0 ){
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName() );
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            /*网络查询*/
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }



    /*从服务查询省市区*/
    private void queryFromServer(String address, final String level) {
        Log.d(TAG, "queryFromServer: "+address+"-------"+level);

        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: 服务器查询成功");
                /*获取相应*/
                String responseText = response.body().string();
                boolean result = false;
                /*根据传入的级别来处理省、市、区数据*/
                switch (level) {
                   case "province" :
                       result = Utility.handleProvinceResponse(responseText);
                       break;
                   case "city" :
                       result = Utility.handleCityResponse(responseText, selectedProvince.getId() );
                       break;
                   case "county" :
                       result = Utility.handleCountyResponse(responseText, selectedCity.getId() );
                       break;
                   default:
                }
//                if ("province".equals(level) ){
//                    result = Utility.handleProvinceResponse(responseText);
//                } else if("city".equals(level) ) {
//                    result = Utility.handleCityResponse(responseText, selectedProvince.getId() );
//                } else if("county".equals(level )){
//                    result = Utility.handleCountyResponse(responseText, selectedCity.getId() );
//                }
               Log.d(TAG, "onResponse: result = "+result);
                /*上面处理完网络源数据后紧接着切到主线程再次查询，当然这次是本地查询*/
                if(result) {
                    getActivity().runOnUiThread( () -> {
                        closeProgressDialog();
                        switch (level) {
                            case "province" :
                                queryProvinces();
                                break;
                            case "city" :
                                queryCities();
                                break;
                            case "county" :
                                queryCounties();
                                break;
                            default:
                                break;
                        }
                    });
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: 服务器查询失败");
                /*加载失败后切回主线程弹窗提示*/
                getActivity().runOnUiThread( () -> {
                    closeProgressDialog();
                    Toast.makeText(getContext(), R.string.load_file, Toast.LENGTH_SHORT).show();
                });

            }
        } ) ;
    }

    /*关闭加载圆圈*/
    private void closeProgressDialog() {
        Log.d(TAG, "closeProgressDialog: ");
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    /*显示加载小圈*/
    private void showProgressDialog() {
        Log.d(TAG, "showProgressDialog: ");
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity() );
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
}
