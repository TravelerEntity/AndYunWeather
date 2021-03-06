package com.lee.andcloud.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lee.andcloud.R;
import com.lee.andcloud.activity.WeatherActivity;
import com.lee.andcloud.gson.City;
import com.lee.andcloud.util.CityAdapter;
import com.lee.andcloud.util.HttpUtil;
import com.lee.andcloud.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class SelectAreaFragment extends Fragment {
    private static final String TAG = "SelectAreaFragment";
    private RecyclerView recyclerView;
    private EditText etCityQuery;
    private List<City> cityList;
    private View view;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_area, container, false);
        Log.d(TAG, "onCreateView:");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");

        this.view = view;
        progressBar = view.findViewById(R.id.progressBar);
        etCityQuery = view.findViewById(R.id.et_search_city);
        recyclerView = view.findViewById(R.id.recycler_view_cities);
        Button btSendQuery = view.findViewById(R.id.bt_search_city);
        initCityList();
        etCityQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d(TAG, "onTextChanged: ????????????"+s+"???start???"+start+"???before???"+before+"???count??????"+count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                ProgressBar progressBar = view.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "afterTextChanged: "+s);
                Log.d(TAG, "afterTextChanged: ###############");
                queryCity(s.toString());
            }
        });

        btSendQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = etCityQuery.getText().toString();
                if ("".equals(keyword)){return;}
            }
        });

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    private void queryCity(String words) {
        if (words.equals("")){return;}
        String cityUrl = "https://apis.map.qq.com/ws/district/v1/search?key=XWPBZ-FY66F-QQ5J6-JOMZT-GMGAF-Z2FBI&keyword="+words;
        HttpUtil.sendOkHttpRequest(cityUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(SelectAreaFragment.this.getContext(), "???????????????", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                cityList = Utility.handleCitiesListResponse(responseText) ;

                /*???????????????????????????*/
                if(cityList==null){return;}
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView = view.findViewById(R.id.recycler_view_cities);
                        recyclerView.setLayoutManager(new LinearLayoutManager(SelectAreaFragment.this.getContext())); //?????????????????????
                        recyclerView.setAdapter(new CityAdapter(cityList,SelectAreaFragment.this.getContext()));    //??????Adapt
                        progressBar.setVisibility(View.GONE);
                        closeKeyboard(getActivity());
                    }
                });
            }
        });
    }

    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm =  (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private void initCityList(){
        String[] cityName ={"??????","??????","??????","??????","??????"};
        String[] cityGeo ={"116.41,39.91","121.48,31.22","104.04,30.40","113.00,28.21","107.50,31.22"};
        cityList = new ArrayList<>();
        for (int i = 0; i < cityGeo.length; i++) {
            cityList.add(new City(cityName[i],cityGeo[i]));
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectAreaFragment.this.getContext())); //?????????????????????
        recyclerView.setAdapter(new CityAdapter(cityList,SelectAreaFragment.this.getContext()));    //??????Adapt
        closeKeyboard(getActivity() );
    }

}
