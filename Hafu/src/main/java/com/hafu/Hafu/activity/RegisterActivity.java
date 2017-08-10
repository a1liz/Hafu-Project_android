package com.hafu.Hafu.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.hafu.Hafu.R;
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

@ContentView(R.layout.activity_register)
public class RegisterActivity extends Activity {
    private ImageView showCode;
    private String realCode;
    ShimmerTextView tv;
    Shimmer shimmer;

    @ViewInject(R.id.username)
    private EditText username;
    @ViewInject(R.id.password)
    private EditText password;
    @ViewInject(R.id.regphone)
    private EditText regphone;
    @ViewInject(R.id.codes)
    private EditText codes;
    private SharedPreferences sp;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                String result = (String) msg.obj;
                Log.i("结果：",result);
                if (result.equals("TRUE")) {
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        x.view().inject(this);
        tv = (ShimmerTextView) findViewById(R.id.shimmer_mainname);

        shimmer = new Shimmer();    //启动字体闪烁
        shimmer.start(tv);

        showCode = (ImageView) findViewById(R.id.showCode);
        //将验证码用图片的形式显示出来
        showCode.setImageBitmap(VerificationCode.getInstance().createBitmap());
        realCode = VerificationCode.getInstance().getCode().toLowerCase();

    }

    @Event(value = {R.id.register})
    public void register(View view) {
        if (!codes.getText().toString().toLowerCase().equals(realCode)) {
            Toast.makeText(RegisterActivity.this,"验证码错误，请重试！",Toast.LENGTH_SHORT).show();
            showCode.setImageBitmap(VerificationCode.getInstance().createBitmap());
            realCode = VerificationCode.getInstance().getCode().toLowerCase();
        } else {
            FormBody.Builder builder1 = new FormBody.Builder();
            FormBody formBody = builder1.add("id", username.getText().toString())
                    .add("pwd", password.getText().toString())
                    .add("regphone", regphone.getText().toString()).build();

            Request.Builder builder = new Request.Builder();
            Request request = builder.url("http://" + getString(R.string.ip) + ":8080/hafu_project/signup_android")
                    .post(formBody)
                    .build();
            exec(request);
        }
    }

    @Event(value = {R.id.back})
    public void exitToHome(View view) {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void exec(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("获取注册返回值异常：","-->" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                Message message = new Message();
                message.what = 1;
                message.obj = s;
                handler.sendMessage(message);
                Log.i("获取注册返回值成功：","--->" + s);
            }
        });
    }

    public void codeChange(View view) {
        showCode.setImageBitmap(VerificationCode.getInstance().createBitmap());
        realCode = VerificationCode.getInstance().getCode().toLowerCase();
    }
}
