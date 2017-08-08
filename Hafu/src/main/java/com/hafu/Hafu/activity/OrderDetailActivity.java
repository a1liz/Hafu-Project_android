package com.hafu.Hafu.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hafu.Hafu.R;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailActivity extends Activity {

    @ViewInject(R.id.order_detail_list)
    private ListView order_detail_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Log.i("info","开始orderDetailActivity");
        List<Map<String,Object>> lists = new ArrayList<>();
        int[] imgIDs = {R.drawable.di7,R.drawable.di8,R.drawable.di9,R.drawable.di10,R.drawable.di11};
        String[] titles = {"薯条","香辣鸡腿堡","吉士汉堡","雪顶咖啡","老北京鸡肉卷"};
        int[] numbers = {1,2,2,1,2};
        int[] prices = {9,16,18,12,16};
        for(int i = 0; i < imgIDs.length; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("img",imgIDs[i]);
            map.put("title",titles[i]);
            map.put("number",numbers[i]);
            map.put("price",prices[i]);
            lists.add(map);
        }

        String[] key = {"img","title","number","price"};
        int[] ids = {R.id.item_img,R.id.item_title,R.id.item_number,R.id.item_price};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,lists,R.id.order_detail_list,key,ids);
        order_detail_list.setAdapter(simpleAdapter);
    }



}
