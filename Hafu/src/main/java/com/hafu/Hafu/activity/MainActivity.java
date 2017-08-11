package com.hafu.Hafu.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hafu.Hafu.HafuAdapter;
import com.hafu.Hafu.ImageService;
import com.hafu.Hafu.R;
import com.hafu.Hafu.view.BasicPopupWindow;
import com.hafu.Hafu.view.CircleImageView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class MainActivity extends Activity {

    private LayoutInflater from;
    private View totalView;
    private TextView mTextMessage;
    private TextView user;
    private TextView user_phonenumber;
    private SharedPreferences sp;
    private LinearLayout linearLayout;
    private ListView order_list,store_list;
    private byte[] data;

    private CircleImageView ivHead;
    private RelativeLayout layout_choose;
    private RelativeLayout layout_photo;
    private RelativeLayout layout_close;

    private LinearLayout layout_all;
    protected int mScreenWidth;
    private BasicPopupWindow basicPopupWindow;

    /**
     * 定义三种状态
     */
    private static final int REQUESTCODE_PIC = 1;//相册
    private static final int REQUESTCODE_CAM = 2;//相机
    private static final int REQUESTCODE_CUT = 3;//图片裁剪

    private Bitmap mBitmap;
    private File mFile;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getHomePageView();
                    return true;
                case R.id.navigation_dashboard:
                    getCartView();
                    return true;
                case R.id.navigation_notifications:
                    try {
                        getProfileView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //引入各分页布局文件
        linearLayout = (LinearLayout) findViewById(R.id.topContainer);
        from = LayoutInflater.from(MainActivity.this);
        totalView = from.inflate(R.layout.main_profile,linearLayout);
        totalView.setVisibility(View.GONE);
        getHomePageView();

        mTextMessage = (TextView) findViewById(R.id.message);
        mTextMessage.setVisibility(View.GONE);

        // 获取缓存内容
        sp = getSharedPreferences("msg",MODE_PRIVATE);
        String user = sp.getString("username","");
        String isLogin = sp.getString("isLogin","");
//        mTextMessage.setText(user+isLogin);

        basicPopupWindow = new BasicPopupWindow(MainActivity.this);


        // 引入底部导航栏
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * 获取主页面
     */
    private void getHomePageView() {
        linearLayout.removeAllViews();
        totalView.setVisibility(View.VISIBLE);
        totalView = from.inflate(R.layout.home_page,linearLayout);

        List<Map<String,Object>> lists = new ArrayList<>();
        int[] imgIDs = {R.drawable.star,R.drawable.pizza,R.drawable.ha};
        String[] titles = {"星巴克","必胜客","哈根达斯"};
        float[] ratings = {4.5f,5f,4f};
        int[] eva_per_months = {39,42,68};
        int[] number_per_months = {60,79,75};
        String[] distances = {"2.43公里","700米","3.6公里"};
        int[] min_prices = {30,45,40};
        int[] extra_prices = {5,3,10};
        Boolean[] zhuans = {TRUE,Boolean.FALSE, TRUE};
        Boolean[] sus = {TRUE,Boolean.FALSE,Boolean.FALSE};
        Boolean[] piao = {TRUE,Boolean.FALSE, TRUE};
        Boolean[] joinMans = {TRUE, TRUE,Boolean.FALSE};
        int[] man_totals = {20,30,0};
        int[] man_salls = {5,8,0};
        int[] dis = {10,0,20};

        for(int i = 0; i < imgIDs.length; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("img",imgIDs[i]);
            map.put("title",titles[i]);
            map.put("rating",ratings[i]);
            map.put("eva_per_month",eva_per_months[i]);
            map.put("number_per_month",number_per_months[i]);
            map.put("distance",distances[i]);
            map.put("min_price","¥"+min_prices[i]);
            map.put("extra_price","¥"+extra_prices[i]);
            map.put("zhuan",zhuans[i]);
            map.put("su",sus[i]);
            map.put("piao",piao[i]);
            map.put("man_total",joinMans[i]);
            map.put("man","满"+man_totals[i]+"元立减"+man_salls[i]+"元");
            if(dis[i] != 0) {
                map.put("di_total",TRUE);
            } else {
                map.put("di_total",FALSE);
            }
            map.put("di","在该商家是用抵金券订餐可抵"+dis[i]+"元");

            lists.add(map);
        }

        store_list = totalView.findViewById(R.id.store_list);
        String[] key = {"img","title","rating","eva_per_month","number_per_month","distance","min_price","extra_price","zhuan","su","piao","man_total","man","di_total","di"};
        int[] ids = {R.id.item_img,R.id.item_title,R.id.item_rating,R.id.item_eva_per_month,R.id.item_number_per_month,R.id.item_distance,R.id.item_min_price,R.id.item_extra_price,R.id.item_zhuan,R.id.item_su,R.id.item_piao,R.id.item_man_total,R.id.item_man,R.id.item_di_total,R.id.item_di};
        HafuAdapter hafuAdapter = new HafuAdapter(this,lists,R.layout.store_item,key,ids);


        store_list.setAdapter(hafuAdapter);
    }

    /**
     * 获取个人详情页面
     */
    private void getProfileView() throws IOException {
        linearLayout.removeAllViews();
        totalView.setVisibility(View.VISIBLE);
        if (sp.getString("isLogin","").equals("TRUE")) {
            totalView = from.inflate(R.layout.main_profile,linearLayout);

            ivHead = (CircleImageView) totalView.findViewById(R.id.userIcon);

            layout_all = (LinearLayout) totalView.findViewById(R.id.layout_all);

            user = totalView.findViewById(R.id.user);
            user_phonenumber = totalView.findViewById(R.id.user_phonenumber);

            user.setText(sp.getString("username",""));
            user_phonenumber.setText(sp.getString("regphone",""));

            try {
                if (JSON.parseArray(sp.getString("iconStream", "")).get(0).equals("FALSE")) {
                    Log.i("从网络中获取图片====>","成功");
                    new Thread(networkTask).start();
                }
                else if (JSON.parseArray(sp.getString("iconStream", "")).get(0).equals("TRUE")) {
                    Log.i("从缓存中获取图片====>","成功");
                    JSONArray jsonArray = JSONArray.parseArray(sp.getString("iconStream", ""));
                    data = Base64.decode(jsonArray.get(1).toString(),Base64.CRLF);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  //生成位图
                    ivHead.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ivHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()){
                        case R.id.userIcon:
                            showMyDialog();
                            break;
                    }
                }
            });
        } else if (sp.getString("isLogin","").equals("FALSE")) {
            totalView = from.inflate(R.layout.waiting_for_login,linearLayout);
        } else {
            totalView = from.inflate(R.layout.waiting_for_login,linearLayout);

//            totalView.setVisibility(View.GONE);
//            mTextMessage.setVisibility(View.VISIBLE);
//            mTextMessage.setText("isLogin错误");
        }
    }

    /**
     * 获取订单界面
     */
    private void getCartView() {
        linearLayout.removeAllViews();
        totalView.setVisibility(View.VISIBLE);
        if (sp.getString("isLogin","").equals("TRUE")) {
            totalView = from.inflate(R.layout.order_list,linearLayout);
            Log.i("info","==>已登录，查看购物车");
        } else if (sp.getString("isLogin","").equals("FALSE")) {
            totalView = from.inflate(R.layout.warning_to_login,linearLayout);
            Log.i("warning","==>未登录，不可查看购物车");
            return;
        } else {
            totalView = from.inflate(R.layout.warning_to_login,linearLayout);
//            totalView.setVisibility(View.GONE);
//            mTextMessage.setVisibility(View.VISIBLE);
//            mTextMessage.setText("isLogin错误");
            Log.i("error","==>缓存isLogin部分错误");
            return;
        }

        List<Map<String,Object>> lists = new ArrayList<>();
        int[] imgIDs = {R.drawable.star,R.drawable.pizza,R.drawable.ha,R.drawable.star,R.drawable.pizza,R.drawable.ha};
        String[] titles = {"星巴克","必胜客","哈根达斯","星巴克","必胜客","哈根达斯"};
        String[] dates = {"2017-08-10 15:33","2017-07-03 04:26","2017-06-29 20:01","2017-08-10 15:33","2017-07-03 04:26","2017-06-29 20:01"};
        int[] orderStatus = {0,1,2,0,1,2};

        for(int i = 0; i < imgIDs.length; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("img",imgIDs[i]);
            map.put("title",titles[i]);
            map.put("date",dates[i]);
            if (orderStatus[i] == 0)
                map.put("status","订单已完成");
            else if (orderStatus[i] == 1)
                map.put("status","订单已取消");
            else if (orderStatus[i] == 2)
                map.put("status","订单待付款");
            map.put("good_img",imgIDs[i]);
            map.put("good_details",titles[i] + " 等共" + Integer.toString(orderStatus[i] + 1) +"件");
            map.put("good_price","¥"+Integer.toString(orderStatus[i]*10 + 11));
            lists.add(map);
        }

        order_list = totalView.findViewById(R.id.order_list);
        String[] key = {"img","title","date","status","good_img","good_details","good_price"};
        int[] ids = {R.id.item_img,R.id.item_title,R.id.item_time,R.id.item_status,R.id.item_good_img,R.id.item_good_details,R.id.item_good_price};
        SimpleAdapter simpleAdapter = new SimpleAdapter(totalView.getContext(),lists,R.layout.order_list_item,key,ids);
        order_list.setAdapter(simpleAdapter);
        order_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("info","===>onItemClick函数被触发");
                Intent intent = new Intent(MainActivity.this,OrderDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 注销动作
     * @param view
     */
    public void logout(View view) throws IOException {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("isLogin","FALSE");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(0,"FALSE");
        editor.putString("iconStream",jsonArray.toString());
        editor.commit();
        linearLayout.removeAllViews();
        getProfileView();
    }

    /**
     * 登录动作
     * @param view
     */
    public void turnToLogin(View view) {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 管理地址动作
     * @param view
     */
    public void editAddress(View view) {
        Intent intent = new Intent(MainActivity.this,AddressActivity.class);
        startActivity(intent);
    }

    public void about_us(View view) {
        basicPopupWindow.showPopupWindow(view);
    }


    // 头像选择部分
    PopupWindow avatorPop;
    private void showMyDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_show_dialog,
                null);
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
        layout_close = (RelativeLayout) view.findViewById(R.id.layout_close);

        layout_choose.setBackgroundColor(getResources().getColor(
                R.color.base_color_text_white));
        layout_photo.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.pop_bg_press));
        layout_close.setBackgroundColor(getResources().getColor(
                R.color.base_color_text_white));


        layout_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_photo.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                layout_close.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));


                openCamera();

                // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //startActivityForResult(intent,);
            }
        });

        layout_choose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                layout_photo.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_choose.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                layout_close.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                openPic();

            }
        });

        layout_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_photo.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_close.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                avatorPop.dismiss();
            }
        });



        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        avatorPop = new PopupWindow(view, mScreenWidth, 200);
        avatorPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    avatorPop.dismiss();
                    return true;
                }
                return false;
            }
        });

        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 打开相册
     */
    private void openPic() {
        Intent picIntent = new Intent(Intent.ACTION_PICK,null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(picIntent,REQUESTCODE_PIC);
    }

    /**
     * 调用相机
     */
    private void openCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!file.exists()){
                file.mkdirs();
            }
            mFile = new File(file, System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
            startActivityForResult(intent,REQUESTCODE_CAM);
        } else {
            Toast.makeText(this, "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_CAM:
                    startPhotoZoom(Uri.fromFile(mFile));
                    break;
                case REQUESTCODE_PIC:

                    if (data == null || data.getData() == null){
                        return;
                    }
                    startPhotoZoom(data.getData());

                    break;
                case REQUESTCODE_CUT:

                    if (data!= null){
                        setPicToView(data);
                    }
                    break;
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setPicToView(Intent data) {
        Bundle bundle =  data.getExtras();
        if (bundle != null){
            //这里也可以做文件上传
            mBitmap = bundle.getParcelable("data");
            ivHead.setImageBitmap(mBitmap);
        }
    }

    /**
     * 打开系统图片裁剪功能
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop",true);
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",300);
        intent.putExtra("outputY",300);
        intent.putExtra("scale",true); //黑边
        intent.putExtra("scaleUpIfNeeded",true); //黑边
        intent.putExtra("return-data",true);
        intent.putExtra("noFaceDetection",true);
        startActivityForResult(intent,REQUESTCODE_CUT);

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data1 = msg.getData();
            String val = data1.getString("value");
            Log.i("mylog", "头像获取请求结果为-->" + val);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  //生成位图
            ivHead.setImageBitmap(bitmap);
            SharedPreferences.Editor editor = sp.edit();
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(0,"TRUE");
            jsonArray.add(1, Base64.encodeToString(data,Base64.CRLF));
            Log.i("存入的图片data数组:","===>"+jsonArray.toString());
            editor.putString("iconStream",jsonArray.toString());
            editor.commit();
            // TODO
            // UI界面的更新等相关操作
        }
    };

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            // "http://" + getString(R.string.ip) + ":8080/hafu_project/images/"+sp.getString("icon","")
            try {
                data = ImageService.getImage(
                        "http://" + getString(R.string.ip) + ":8080/hafu_project/images/"+sp.getString("icon","")
                );
                 //显示图片
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "网络连接超时", Toast.LENGTH_LONG).show();  //通知用户连接超时信息
                e.printStackTrace();
            }

            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "完成头像获取");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

}
