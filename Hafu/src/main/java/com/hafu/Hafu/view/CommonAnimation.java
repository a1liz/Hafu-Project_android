package com.hafu.Hafu.view;

/**
 * Created by apple on 17/8/10.
 */
//这个类我是单纯的想让about us 的动画显示出来
import android.support.v7.app.AppCompatActivity;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;

        import com.hafu.Hafu.R;
        import com.romainpiel.shimmer.Shimmer;
        import com.romainpiel.shimmer.ShimmerTextView;

public class CommonAnimation extends AppCompatActivity {
    ShimmerTextView tv,tv2;
    Shimmer shimmer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        tv = (ShimmerTextView) findViewById(R.id.shimmer_mainname1);
        tv2=(ShimmerTextView)findViewById(R.id.shimmer_mainname2);
        shimmer = new Shimmer();    //启动字体闪烁
        shimmer.start(tv);
        shimmer.start(tv2);
    }
}


