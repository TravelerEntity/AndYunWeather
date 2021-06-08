package com.lee.andcloud.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lee.andcloud.R;

public class MainActivity extends AppCompatActivity {
    private Toolbar tbHead;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
        /*用toolbar替代actionbar*/
        setSupportActionBar(tbHead);
        loadNavBtn();
    }

    private void loadNavBtn() {
        /*获取actionbar实例，不为空才会进行后面的设置*/
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_location_city_24);
        }
    }

    /**
     * 加载toolbar上的选项时系统调用
     * @param menu 要加载到这个menu上
     * @return true or false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*加载菜单布局*/
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    /**
     * toolbar上按钮被点击时调用
     * @param item 被点击的按钮
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId() ){
            case R.id.it_more:
                Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
                intent.putExtra("weather_id","10101000");
                startActivity(intent);
                Toast.makeText(this, "点击了更多按钮", Toast.LENGTH_SHORT).show();
                break;
            /*toolbar上的导航按钮被点击时，最左边按钮*/
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    /**
     * 初始化界面的View
     */
    private void initWidget() {
        tbHead = findViewById(R.id.tb_head);
        drawerLayout = findViewById(R.id.drawer_layout);
    }
}