package com.hafu.Hafu.activity;

import android.app.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.hafu.Hafu.R;
import com.hafu.Hafu.domain.HafuUserComment;
import com.hafu.Hafu.view.VerificationCode;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.Boolean.TRUE;

@ContentView(R.layout.activity_login)
public class LoginActivity extends Activity {
    ShimmerTextView tv;
    Shimmer shimmer;


    @ViewInject(R.id.showCode)
    private ImageView showCode;
    @ViewInject(R.id.codes)
    private EditText codes;
    @ViewInject(R.id.username)
    private EditText username;
    @ViewInject(R.id.password)
    private EditText password;
    @ViewInject(R.id.remember)
    private CheckBox remeber;
    private String realCode;
    private SharedPreferences sp;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                String result = (String) msg.obj;
                Log.i("登录返回值结果：",result);
                try {
                    JSONArray jsonArray = JSON.parseArray(result);
                    SharedPreferences.Editor editor = sp.edit();
                    Log.i("jsonArray的值:",jsonArray.get(0).toString());
                    if (jsonArray.get(0).toString().equals("FALSE")) {
                        editor.putString("isLogin","FALSE");
                    } else if (jsonArray.get(0).toString().equals("TRUE")) {
                        HafuUserComment hafuUserComment = JSON.parseObject(jsonArray.get(1).toString(),HafuUserComment.class);
                        editor.putString("uid", hafuUserComment.getUid().toString());
                        editor.putString("regtime",hafuUserComment.getRegtime().toString());
                        editor.putString("regphone",hafuUserComment.getRegphone().toString());
                        editor.putString("icon",hafuUserComment.getIcon());
                        editor.putString("mainAddress",Integer.toString(hafuUserComment.getMainAddress()));
                        editor.putString("isLogin", "TRUE");
                    } else {
                        Log.i("异常:--->","登录返回值不为TRUE或FALSE");
                    }
                    editor.commit();

                    String isLogin = sp.getString("isLogin","");
                    Log.i("isLogin = ",isLogin);
                    if (isLogin.equals("TRUE")) {
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    } else if (isLogin.equals("FALSE")) {
                        Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("ERROR ====>","缓存中isLogin字段错误");
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this,"服务器错误，请联系hafu小组",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }
    };
    OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        x.view().inject(this);
        Log.i("---->","onCreate");
        tv = findViewById(R.id.shimmer_mainname);
        shimmer = new Shimmer();    //启动字体闪烁
        shimmer.start(tv);

        sp = getSharedPreferences("msg",MODE_PRIVATE);

        String user = sp.getString("username","");
        String pwd = sp.getString("password", "");
        String tmp = sp.getString("isRemember","");
        if (tmp.equals("TRUE"))
            remeber.setChecked(TRUE);
        username.setText(user);
        password.setText(pwd);
        //将验证码用图片的形式显示出来
        showCode.setImageBitmap(VerificationCode.getInstance().createBitmap());
        realCode = VerificationCode.getInstance().getCode().toLowerCase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("---->","onStop");
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username",username.getText().toString());
        editor.putString("isRemember",remeber.isChecked() ? "TRUE" : "FALSE");
        if (remeber.isChecked()) {
            editor.putString("password", password.getText().toString());
            editor.putString("isRemember","TRUE");
        } else {
            editor.putString("password", "");
            editor.putString("isRemember","FALSE");
        }
        editor.commit();
        Log.i("---->","存储成功!");
    }

    @Event(value = {R.id.login})
    public void login(View view) {
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("username",username.getText().toString());
//        editor.putString("isLogin","TRUE");
//        editor.commit();

        if (!codes.getText().toString().toLowerCase().equals(realCode)) {
            Toast.makeText(LoginActivity.this,"验证码错误，请重试！",Toast.LENGTH_SHORT).show();
            showCode.setImageBitmap(VerificationCode.getInstance().createBitmap());
            realCode = VerificationCode.getInstance().getCode().toLowerCase();
        } else {
            FormBody.Builder builder1 = new FormBody.Builder();
            FormBody formBody = builder1.add("id", username.getText().toString())
                    .add("pwd", password.getText().toString()).build();

            Request.Builder builder = new Request.Builder();
            Request request = builder.url("http://" + getString(R.string.ip) + ":8080/hafu_project/login_android")
                    .post(formBody)
                    .build();
            exec(request);
        }
    }

    @Event(value = {R.id.back})
    public void exitToHome(View view) {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void exec(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("获取用户id请求异常：","-->"+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("获取用户id请求成功：","--->");
                String s = response.body().string();
                Message message = new Message();
                message.what = 1;
                message.obj = s;
                handler.sendMessage(message);
            }
        });
    }

    @Event(value = {R.id.signup})
    public void signup(View view) {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    public void codeChange(View view) {
        showCode.setImageBitmap(VerificationCode.getInstance().createBitmap());
        realCode = VerificationCode.getInstance().getCode().toLowerCase();
    }
}
