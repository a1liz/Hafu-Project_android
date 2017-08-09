package com.hafu.Hafu.activity;

import android.app.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.hafu.Hafu.R;
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

@ContentView(R.layout.activity_login)
public class LoginActivity extends Activity {
    ShimmerTextView tv;
    Shimmer shimmer;


    @ViewInject(R.id.username)
    private EditText username;
    @ViewInject(R.id.password)
    private EditText password;
    private SharedPreferences sp;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                String result = (String) msg.obj;
                Log.i("结果：",result);
                SharedPreferences.Editor editor = sp.edit();
                if (result.equals("FALSE")) {
                    editor.putString("isLogin","FALSE");
                } else {
                    editor.putString("uid", result);
                    editor.putString("isLogin", "TRUE");
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
            }
        }
    };
    OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        tv = findViewById(R.id.shimmer_mainname);
//
//        AssetManager mgr=getAssets();//得到AssetManager
//        Typeface tf=Typeface.createFromAsset(mgr, "fonts/bole.ttf");//根据路径得到Typeface
//        tv=findViewById(R.id.textView2);
//        tv.setTypeface(tf);//设置字体



        //以上为字体失败部分
        shimmer = new Shimmer();    //启动字体闪烁
        Log.i("--->","闪烁开始");
        shimmer.start(tv);

        x.view().inject(this);
        sp = getSharedPreferences("msg",MODE_PRIVATE);
        String user = sp.getString("username","");
        String pwd = sp.getString("password", "");
        username.setText(user);
        password.setText(pwd);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("---->","onStop");
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username",username.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.commit();
        Log.i("---->","存储成功!");
    }

    @Event(value = {R.id.login})
    public void doEvent(View view) {
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("username",username.getText().toString());
//        editor.putString("isLogin","TRUE");
//        editor.commit();

        FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1.add("id",username.getText().toString())
                .add("pwd",password.getText().toString()).build();

        Request.Builder builder = new Request.Builder();
        Request request1 = builder.url("http://10.187.133.227:8080/hafu_project/login_android")
                .post(formBody)
                .build();
        exec(request1);
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
                Log.i("异常：","-->"+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("成功：","--->");
                String s = response.body().string();
                Message message = new Message();
                message.what = 1;
                message.obj = s;
                handler.sendMessage(message);
            }
        });
    }

}
