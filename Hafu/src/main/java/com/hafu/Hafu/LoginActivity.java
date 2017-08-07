package com.hafu.Hafu;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;


public class LoginActivity extends Activity {
    ShimmerTextView tv;
    Shimmer shimmer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tv = (ShimmerTextView) findViewById(R.id.shimmer_mainname);
//
//        AssetManager mgr=getAssets();//得到AssetManager
//        Typeface tf=Typeface.createFromAsset(mgr, "fonts/bole.ttf");//根据路径得到Typeface
//        tv=findViewById(R.id.textView2);
//        tv.setTypeface(tf);//设置字体



        //以上为字体失败部分
        shimmer = new Shimmer();    //启动字体闪烁
        shimmer.start(tv);

    }


}
