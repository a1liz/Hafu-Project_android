package com.hafu.Hafu.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hafu.Hafu.R;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {

    private LayoutInflater from;
    private View totalView;
    private TextView mTextMessage;
    private SharedPreferences sp;
    private LinearLayout linearLayout;
    private ListView order_list;

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

        // 获取缓存内容
        sp = getSharedPreferences("msg",MODE_PRIVATE);
        String user = sp.getString("username","");
        String isLogin = sp.getString("isLogin","");
        mTextMessage.setText(user+isLogin);


        // 引入底部导航栏
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    /**
     * 获取个人详情页面
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

    /**
     * 获取订单界面
     */
    private void getCartView() {
        linearLayout.removeAllViews();
        totalView.setVisibility(View.VISIBLE);
        if (sp.getString("isLogin","").equals("TRUE")) {
            totalView = from.inflate(R.layout.order_list,linearLayout);
            Log.i("info","==>已登录，查看购物车");
        } else if (sp.getString("isLogin","").equals("FALSE")) {
            totalView = from.inflate(R.layout.warning_to_login,linearLayout);
            Log.i("warning","==>未登录，不可查看购物车");
            return;
        } else {
            totalView.setVisibility(View.GONE);
            mTextMessage.setVisibility(View.VISIBLE);
            mTextMessage.setText("isLogin错误");
            Log.i("error","==>缓存isLogin部分错误");
            return;
        }

        List<Map<String,Object>> lists = new ArrayList<>();
        int[] imgIDs = {R.drawable.star,R.drawable.pizza,R.drawable.ha,R.drawable.star,R.drawable.pizza,R.drawable.ha};
        String[] titles = {"星巴克","必胜客","哈根达斯","星巴克","必胜客","哈根达斯"};
        String[] dates = {"2017-08-10 15:33","2017-07-03 04:26","2017-06-29 20:01","2017-08-10 15:33","2017-07-03 04:26","2017-06-29 20:01"};
        int[] orderStatus = {0,1,2,0,1,2};

        for(int i = 0; i < imgIDs.length; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("img",imgIDs[i]);
            map.put("title",titles[i]);
            map.put("date",dates[i]);
            if (orderStatus[i] == 0)
                map.put("status","订单已完成");
            else if (orderStatus[i] == 1)
                map.put("status","订单已取消");
            else if (orderStatus[i] == 2)
                map.put("status","订单待付款");
            map.put("good_img",imgIDs[i]);
            map.put("good_details",titles[i] + " 等共" + Integer.toString(orderStatus[i] + 1) +"件");
            map.put("good_price","¥"+Integer.toString(orderStatus[i]*10 + 11));
            lists.add(map);
        }

        order_list = totalView.findViewById(R.id.order_list);
        String[] key = {"img","title","date","status","good_img","good_details","good_price"};
        int[] ids = {R.id.item_img,R.id.item_title,R.id.item_time,R.id.item_status,R.id.item_good_img,R.id.item_good_details,R.id.item_good_price};
        SimpleAdapter simpleAdapter = new SimpleAdapter(totalView.getContext(),lists,R.layout.order_list_item,key,ids);
        order_list.setAdapter(simpleAdapter);

    }

    /**
     * 注销动作
     * @param view
     */
    public void logout(View view) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("isLogin","FALSE");
        editor.putString("username","");
        editor.commit();
        linearLayout.removeAllViews();
        getProfileView();
    }

    /**
     * 登录动作
     * @param view
     */
    public void login(View view) {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }


}
