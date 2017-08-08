package com.hafu.Hafu.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private SharedPreferences sp;
    private int showPages;

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
                    showPages = 0;
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setVisibility(View.GONE);
                    getCartView();
                    showPages = 1;
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setVisibility(View.GONE);
                    getProfileView();
                    showPages = 2;
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
        showPages = 0;

        sp = getSharedPreferences("msg",MODE_PRIVATE);
        String user = sp.getString("username","");
        String isLogin = sp.getString("isLogin","");
        mTextMessage.setText(user+isLogin);


        // 引入底部导航栏
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     *  获取个人详情页面
     */
    private void getProfileView() {
        linearLayout.removeAllViews();
        totalView.setVisibility(View.VISIBLE);
        if (sp.getString("isLogin","").equals("TRUE")) {
            totalView = from.inflate(R.layout.main_profile,linearLayout);
        } else if (sp.getString("isLogin","").equals("FALSE")) {
            totalView = from.inflate(R.layout.waiting_for_login,linearLayout);
        } else {
            totalView.setVisibility(View.GONE);
            mTextMessage.setVisibility(View.VISIBLE);
            mTextMessage.setText("isLogin错误");
        }
    }

    private void getCartView() {
        linearLayout.removeAllViews();
        totalView.setVisibility(View.VISIBLE);
        if (sp.getString("isLogin","").equals("TRUE")) {
            totalView = from.inflate(R.layout.order_list,linearLayout);
        } else if (sp.getString("isLogin","").equals("FALSE")) {
            totalView = from.inflate(R.layout.warning_to_login,linearLayout);
        } else {
            totalView.setVisibility(View.GONE);
            mTextMessage.setVisibility(View.VISIBLE);
            mTextMessage.setText("isLogin错误");
        }
    }

    public void logout(View view) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("isLogin","FALSE");
        editor.putString("username","");
        editor.commit();
        linearLayout.removeAllViews();
        getProfileView();
    }

    public void login(View view) {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }


}
