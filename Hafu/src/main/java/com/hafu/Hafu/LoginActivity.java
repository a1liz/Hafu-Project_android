package com.hafu.Hafu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_login)
public class LoginActivity extends Activity {

    @ViewInject(R.id.username)
    private EditText username;
    @ViewInject(R.id.password)
    private EditText password;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
