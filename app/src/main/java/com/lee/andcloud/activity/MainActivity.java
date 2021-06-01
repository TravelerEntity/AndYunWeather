package com.lee.andcloud.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.lee.andcloud.R;

public class MainActivity extends AppCompatActivity {
    Toolbar tbHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();

        setSupportActionBar(tbHead);
    }

    private void initWidget() {
        tbHead = findViewById(R.id.tb_head);
    }
}