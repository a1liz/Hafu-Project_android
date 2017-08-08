package com.hafu.Hafu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;


public class MainActivity extends Activity {

    private LayoutInflater from;
    private View totalView;
    private TextView mTextMessage;
    private Drawable drawable;
    private Button edit_address,about_us,remained_money,coupons,points,contact_us;

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
                    mTextMessage.setVisibility(View.VISIBLE);
                    mTextMessage.setText(R.string.title_dashboard);
                    totalView.setVisibility(View.GONE);
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

        setLoggedInProfileView();

    }

    /**
     *  获取个人详情页面
     */
    private void getProfileView() {
        totalView.setVisibility(View.VISIBLE);

    }

    public void logout(View view) {
        linearLayout.removeAllViews();
        totalView = from.inflate(R.layout.waiting_for_login,linearLayout);
        setLoggedOutProfileView();


    }

    public void login(View view) {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    /**
     *  设置已登录状态下的Profile页面
     *
     */
    private void setLoggedInProfileView() {
        edit_address = totalView.findViewById(R.id.edit_address);
        about_us = totalView.findViewById(R.id.about_us);
        remained_money = totalView.findViewById(R.id.remained_money);
        coupons = totalView.findViewById(R.id.coupons);
        points = totalView.findViewById(R.id.points);

        // 为remianed_money按钮设置下侧图标
        drawable = getResources().getDrawable(R.drawable.remain_money,null);
        drawable.setBounds(0,0,70,50);
        remained_money.setCompoundDrawables(null,null,null,drawable);

        // 为coupons按钮设置下侧图标
        drawable = getResources().getDrawable(R.drawable.coupons,null);
        drawable.setBounds(0,0,70,50);
        coupons.setCompoundDrawables(null,null,null,drawable);

        // 为points按钮设置下侧图标
        drawable = getResources().getDrawable(R.drawable.points,null);
        drawable.setBounds(0,0,70,50);
        points.setCompoundDrawables(null,null,null,drawable);

        // 为about_us按钮设置左侧图标
        drawable = getResources().getDrawable(R.drawable.info,null);
        drawable.setBounds(0,0,40,40);
        about_us.setCompoundDrawables(drawable,null,null,null);
        about_us.setCompoundDrawablePadding(20);
        about_us.setPadding(20,0,0,0);

        // 为edit_addresss按钮设置左侧图标
        drawable = getResources().getDrawable(R.drawable.pin,null);
        drawable.setBounds(0,0,40,40);
        edit_address.setCompoundDrawables(drawable,null,null,null);
        edit_address.setCompoundDrawablePadding(20);
        edit_address.setPadding(20,0,0,0);
    }

    /**
     *  设置未登录状态下的Profile页面
     *
     */
    private void setLoggedOutProfileView() {
        about_us = totalView.findViewById(R.id.about_us);
        remained_money = totalView.findViewById(R.id.remained_money);
        coupons = totalView.findViewById(R.id.coupons);
        points = totalView.findViewById(R.id.points);
        contact_us = totalView.findViewById(R.id.contact_us);

        // 为remianed_money按钮设置下侧图标
        drawable = getResources().getDrawable(R.drawable.remain_money,null);
        drawable.setBounds(0,0,70,50);
        remained_money.setCompoundDrawables(null,null,null,drawable);

        // 为coupons按钮设置下侧图标
        drawable = getResources().getDrawable(R.drawable.coupons,null);
        drawable.setBounds(0,0,70,50);
        coupons.setCompoundDrawables(null,null,null,drawable);

        // 为points按钮设置下侧图标
        drawable = getResources().getDrawable(R.drawable.points,null);
        drawable.setBounds(0,0,70,50);
        points.setCompoundDrawables(null,null,null,drawable);

        // 为about_us按钮设置左侧图标
        drawable = getResources().getDrawable(R.drawable.info,null);
        drawable.setBounds(0,0,40,40);
        about_us.setCompoundDrawables(drawable,null,null,null);
        about_us.setCompoundDrawablePadding(20);
        about_us.setPadding(20,0,0,0);

        // 为contact_us按钮设置左侧图标
        drawable = getResources().getDrawable(R.drawable.contact,null);
        drawable.setBounds(0,0,40,40);
        contact_us.setCompoundDrawables(drawable,null,null,null);
        contact_us.setCompoundDrawablePadding(20);
        contact_us.setPadding(20,0,0,0);
    }
}
