package com.hafu.Hafu.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hafu.Hafu.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

public class RegisterActivity extends AppCompatActivity {
    ShimmerTextView tv;
    Shimmer shimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        tv = (ShimmerTextView) findViewById(R.id.shimmer_mainname);

        shimmer = new Shimmer();    //启动字体闪烁
        shimmer.start(tv);
    }

}
