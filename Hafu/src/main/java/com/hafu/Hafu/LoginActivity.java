package com.hafu.Hafu;

import android.app.Activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_login)
public class LoginActivity extends Activity {
    ShimmerTextView tv;
    Shimmer shimmer;


    @ViewInject(R.id.username)
    private EditText username;
    @ViewInject(R.id.password)
    private EditText password;
    private SharedPreferences sp;

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


    }


}
