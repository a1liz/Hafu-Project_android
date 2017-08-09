package com.hafu.Hafu.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hafu.Hafu.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by liz on 17/8/9.
 */

public class SlideShowView extends FrameLayout {
    //轮播图图片数量
    private final static int IMAGE_COUNT = 3;
    //自动轮播的时间间隔
    //private final static int TIME_INTERVAL = 5;
    //自动轮播启用开关
    private final static boolean isAutoPlay = true;
    //自定义轮播图的资源ID
    private int[] imagesResIds;
    //放轮播图片的ImageView 的list
    private List<ImageView> imageViewsList;
    //放圆点的View的list
    private List<View> dotViewsList;
    private ViewPager viewPager;
    //当前轮播页
    private int currentItem  = 0;
    //定时任务
    private ScheduledExecutorService scheduledExecutorService;
    //Handler
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }
    };
    public SlideShowView(@NonNull Context context) {
        //super(context);
        this(context,null);
    }

    public SlideShowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        //super(context, attrs);
        this(context,attrs,0);
    }
    public SlideShowView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        initUI(context);
        if(isAutoPlay){
            startPlay();
        }
    }
    /**
     * 开始轮播图切换
     */
    private void startPlay(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 3, TimeUnit.SECONDS);
    }
    /**
     * 停止轮播图切换
     */
    private void stopPlay(){
        scheduledExecutorService.shutdown();
    }
    /**
     * 初始化相关Data
     */
    private void initData(){
        imagesResIds = new int[]{R.drawable.ad1, R.drawable.ad2, R.drawable.ad3};
        imageViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<View>();
    }
    /**
     * 初始化Views等UI
     */
    private void initUI(Context context){
        LayoutInflater.from(context).inflate(R.layout.banner_view,this,true);
        for(int imageID:imagesResIds){
            ImageView view=new ImageView(context);
            view.setImageResource(imageID);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViewsList.add(view);
        }
        dotViewsList.add(findViewById(R.id.v_dot1));
        dotViewsList.add(findViewById(R.id.v_dot2));
        dotViewsList.add(findViewById(R.id.v_dot3));
        viewPager= (ViewPager) findViewById(R.id.viewpage);
        viewPager.setFocusable(true);
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.addOnPageChangeListener(new MyPageChangeListener());
        //  viewPager.setOnPageChangeListener(new MyPageChangeListener());
    }
    /**
     * 填充ViewPager的页面适配器
     * @author caizhiming
     */
    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView(imageViewsList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager)container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);
        }

        @Override
        public int getCount() {
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(ViewGroup container) {

        }

        @Override
        public void finishUpdate(ViewGroup container) {

        }
    }
    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     * @author caizhiming
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener{
        boolean isAutoPlay = false;
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            currentItem = position;
            for(int i=0;i < dotViewsList.size();i++){
                if(i ==position){
                    ((View)dotViewsList.get(position)).setBackgroundResource(R.drawable.white_dot);
                }else {
                    ((View)dotViewsList.get(i)).setBackgroundResource(R.drawable.black_dot);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }
    }
    /**
     *执行轮播图切换任务
     *@author caizhiming
     */
    private class SlideShowTask implements Runnable{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (viewPager) {
                currentItem = (currentItem+1)%imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }

    }
    /**
     * 销毁ImageView资源，回收内存
     * @author caizhiming
     */
    private void destoryBitmaps() {
        for (int i = 0; i < IMAGE_COUNT; i++) {
            ImageView imageView = imageViewsList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                //解除drawable对view的引用
                drawable.setCallback(null);
            }
        }
    }
}
