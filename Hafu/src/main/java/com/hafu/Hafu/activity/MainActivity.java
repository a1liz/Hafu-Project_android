package com.hafu.Hafu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hafu.Hafu.R;


public class MainActivity extends Activity {

    private LayoutInflater from;
    private View totalView;
    private TextView mTextMessage;


    private LinearLayout linearLayout;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setVisibility(View.VISIBLE);
                    mTextMessage.setText(R.string.title_home);
                    totalView.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setVisibility(View.GONE);
                    getCartView();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setVisibility(View.GONE);
                    getProfileView();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //引入各分页布局文件
        linearLayout = (LinearLayout) findViewById(R.id.topContainer);
        from = LayoutInflater.from(MainActivity.this);
        totalView = from.inflate(R.layout.main_profile,linearLayout);
        totalView.setVisibility(View.GONE);

        mTextMessage = (TextView) findViewById(R.id.message);

        // 引入底部导航栏
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     *  获取个人详情页面
     */
    private void getProfileView() {
        linearLayout.removeAllViews();
        totalView = from.inflate(R.layout.main_profile,linearLayout);
        totalView.setVisibility(View.VISIBLE);

    }

    private void getCartView() {
        linearLayout.removeAllViews();
        totalView = from.inflate(R.layout.warning_to_login,linearLayout);
        totalView.setVisibility(View.VISIBLE);
    }

    public void logout(View view) {
        linearLayout.removeAllViews();
        totalView = from.inflate(R.layout.waiting_for_login,linearLayout);
    }

    public void login(View view) {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
