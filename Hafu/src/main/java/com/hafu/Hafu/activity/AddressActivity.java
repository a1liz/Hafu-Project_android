package com.hafu.Hafu.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hafu.Hafu.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ContentView(R.layout.activity_address)
public class AddressActivity extends Activity {

    @ViewInject(R.id.address_list)
    private ListView address_list;
    @ViewInject(R.id.add_address)
    private ListView add_address;
    SharedPreferences sp;

    OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        sp = getSharedPreferences("msg",MODE_PRIVATE);

        getAddressList();
    }

    private void getAddressList() {
        FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1.add("uid", sp.getString("uid","")).build();

        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://" + getString(R.string.ip) + ":8080/hafu_project/get_address")
                .post(formBody)
                .build();
        exec(request);

    }

    @Event(value = {R.id.add_address})
    public void setAddress_list(View view) {

    }

    private void exec(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("获取地址列表请求异常：","-->"+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.i("获取地址列表请求成功：","--->");
                String s = response.body().string();
                Message message = new Message();
                message.what = 1;
                message.obj = s;
                handler.sendMessage(message);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                JSONObject[] hafuUserProfileComments = null;
                int length = -1;
                String result = (String) msg.obj;
                //Log.i("地址传入值：",result);
                try {
                    JSONArray jsonArray = JSONArray.parseArray(result);
                    if (jsonArray.get(0).toString().equals("TRUE")) {
                        length = jsonArray.size() - 1;
                        hafuUserProfileComments = new JSONObject[length];
                        for(int i = 1; i <= length; i++) {
                            hafuUserProfileComments[i - 1] = JSON.parseObject(jsonArray.get(i).toString());
                        }

                    } else if (jsonArray.get(0).toString().equals("FALSE")) {
                        length = 0;
                    } else {
                        Log.i("服务器传入地址异常,","需查看错误代码");
                        Toast.makeText(AddressActivity.this,"服务器传入地址异常,需查看错误代码",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (length<0)
                    return;

                List<Map<String,Object>> lists = new ArrayList<>();

                for(int i = 0; i < length; i++) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("name",hafuUserProfileComments[i].get("name"));
                    map.put("phone",hafuUserProfileComments[i].get("phone"));
                    map.put("address",hafuUserProfileComments[i].get("address"));
                    map.put("pid",hafuUserProfileComments[i].get("pid"));

                    lists.add(map);
                }

                String[] key = {"name","phone","address","pid"};
                int[] ids = {R.id.name,R.id.phonenuber,R.id.address,R.id.pid};
                SimpleAdapter hafuAdapter = new SimpleAdapter(AddressActivity.this,lists,R.layout.detail_address_item,key,ids);

                address_list.setAdapter(hafuAdapter);
                address_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                });
            }
        }
    };

    public void back(View view) {
        onBackPressed();
    }

}
