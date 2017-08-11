package com.hafu.Hafu.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hafu.Hafu.HintPopupWindow;
import com.hafu.Hafu.R;
import com.hafu.Hafu.RadioButtonAdapter;

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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@ContentView(R.layout.activity_address)
public class AddressActivity extends Activity {

    @ViewInject(R.id.address_list)
    private ListView address_list;
    @ViewInject(R.id.add_address)
    private ListView add_address;
    SharedPreferences sp;
    static public HintPopupWindow hintPopupWindow;
    private JSONObject[] hafuUserProfileComments = null;


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

    private void saveExec(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("获取设置默认地址请求异常：","-->"+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("获取设置默认地址请求成功：","--->");
                String s = response.body().string();
                Message message = new Message();
                message.what = 2;
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
                int length = -1;
                String result = (String) msg.obj;
                Log.i("地址传入值：",result);
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
                        Toast.makeText(AddressActivity.this,"服务器传入地址异常,请联系Hafu小组",Toast.LENGTH_SHORT).show();
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
                    map.put("pid",String.valueOf(hafuUserProfileComments[i].get("pid")));
                    //Log.i("产生mainAddress","==>"+map.get("pid")+"=?="+sp.getString("mainAddress","")+"===>"+map.get("pid").equals(sp.getString("mainAddress","")));
                    if (map.get("pid").equals(sp.getString("mainAddress",""))) {
                        map.put("isPrimaryAddress",TRUE);
                    } else {
                        map.put("isPrimaryAddress",FALSE);
                    }
                    lists.add(map);
                }

                //Log.i("当前map值","==>"+lists.toString());
                String[] key = {"name","phone","address","pid","isPrimaryAddress"};
                int[] ids = {R.id.name,R.id.phonenumber,R.id.address,R.id.pid,R.id.isPrimaryAddress};
                hintPopupWindow = new HintPopupWindow(AddressActivity.this,lists,key,ids);
                RadioButtonAdapter radioButtonAdapter = new RadioButtonAdapter(AddressActivity.this,lists,R.layout.detail_address_item,key,ids);
                address_list.setAdapter(radioButtonAdapter);

            } else if (msg.what == 2) {
                String result = (String) msg.obj;
                //Log.i("设定默认地址返回值：","===>"+result);
                if (result.equals("TRUE")) {
                    Toast.makeText(AddressActivity.this,"设定默认地址成功",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else if (result.equals("FALSE")) {
                    Toast.makeText(AddressActivity.this,"设定默认地址失败,请重试",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddressActivity.this,"设定默认地址异常,请联系Hafu小组",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public void back(View view) {
        onBackPressed();
    }

    public void save(View view) {
        for (int i = 0; i < address_list.getCount(); i++) {
            View Child = address_list.getChildAt(i);
            RadioButton isPrimaryAddress = Child.findViewById(R.id.isPrimaryAddress);
            if (isPrimaryAddress.isChecked()) {
                String mainAddress = ((TextView) Child.findViewById(R.id.pid)).getText().toString();
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("mainAddress",mainAddress);
                editor.commit();
                List<Map<String,Object>> mapList = ((RadioButtonAdapter)address_list.getAdapter()).getMapList();
                JSONArray jsonArray = new JSONArray();
                for(i = 0; i < mapList.size(); i++) {
                    hafuUserProfileComments[i].put("name",mapList.get(i).get("name"));
                    hafuUserProfileComments[i].put("phone",mapList.get(i).get("phone"));
                    hafuUserProfileComments[i].put("address",mapList.get(i).get("address"));
                    hafuUserProfileComments[i].put("pid",Integer.valueOf(mapList.get(i).get("pid").toString()));
                    jsonArray.add(hafuUserProfileComments[i]);
                }

                Log.i("mainAddress","===>"+sp.getString("mainAddress",""));
                FormBody.Builder builder1 = new FormBody.Builder();
                FormBody formBody = builder1.add("uid", sp.getString("uid",""))
                        .add("mainAddress",mainAddress)
                        .add("newAddress",jsonArray.toJSONString()).build();


                Request.Builder builder = new Request.Builder();
                Request request = builder.url("http://" + getString(R.string.ip) + ":8080/hafu_project/set_primary_address")
                        .post(formBody)
                        .build();
                saveExec(request);
            }
        }
    }

}
