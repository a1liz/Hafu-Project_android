package com.hafu.Hafu.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.hafu.Hafu.R;
import com.hafu.Hafu.view.VerificationCode;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

public class RegisterActivity extends AppCompatActivity {
    private ImageView showCode;
    private String realCode;
    ShimmerTextView tv;
    Shimmer shimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        tv = (ShimmerTextView) findViewById(R.id.shimmer_mainname);

        shimmer = new Shimmer();    //启动字体闪烁
        shimmer.start(tv);




        showCode = (ImageView) findViewById(R.id.iv_showCode);
        //将验证码用图片的形式显示出来
        showCode.setImageBitmap(VerificationCode.getInstance().createBitmap());
        realCode = VerificationCode.getInstance().getCode().toLowerCase();


    }

}
