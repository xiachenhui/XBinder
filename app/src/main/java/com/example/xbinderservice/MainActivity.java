package com.example.xbinderservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private TextView textStart;
    private TextView textClose;
    private EditText editName;
    private EditText editPwd;
    private EditText editReset;
    private Button login;
    private Button register;
    private Button reset;
    private XAidl xAidl;
    private boolean isConnect;

    private ServiceConnection conn;
    private String name;
    private String password;
    private String newPassword;
    public Looper mLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textStart = findViewById(R.id.text_start);
        textClose = findViewById(R.id.text_close);
        editName = findViewById(R.id.edit_name);
        editPwd = findViewById(R.id.edit_password);
        editReset = findViewById(R.id.edit_reset);
        login = findViewById(R.id.text_login);
        register = findViewById(R.id.text_register);
        reset = findViewById(R.id.text_reset);
        textStart.setOnClickListener(this);
        textClose.setOnClickListener(this);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        reset.setOnClickListener(this);
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                Looper.prepare();
                mLooper=Looper.myLooper();
                try {
                    Thread.sleep(10000);
                    Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "start"+mLooper.getThread().getName());
                mLooper.loop();
                Log.e(TAG, "end");
            }
        }).start();


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLooper!=null){
            Log.e(TAG, "finish");
            mLooper.quit();
        }

        if (conn!=null){
            unbindService(conn);
        }
    }

    //绑定服务
    private void bindService() {

        if (conn == null) {
            conn = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    xAidl = XAidl.Stub.asInterface(service);
                    isConnect = true;
                    Toast.makeText(MainActivity.this, "服务已开启", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    isConnect = false;
                    Toast.makeText(MainActivity.this, "服务已关闭", Toast.LENGTH_SHORT).show();
                }
            };
        }
        if (isConnect) {
            Toast.makeText(this, "服务已开启", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.xbinderservice", "com.example.xbinderservice.XAidlService"));
        bindService(intent, conn, BIND_AUTO_CREATE);

    }

    //解绑
    private void unbindService() {
        if (mLooper!=null){
            mLooper.quit();
            Log.e(TAG, "unbindService--1"+mLooper.getThread().getName());
        }else {
            Log.e(TAG, "unbindService--2"+mLooper.getThread().getName());
        }
        if (conn != null) {
            unbindService(conn);
            conn = null;
            xAidl = null;
            isConnect = false;
        } else {
            Toast.makeText(this, "服务已关闭", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_start:
                bindService();
                break;
            case R.id.text_close:
                unbindService();
                break;
            case R.id.text_login:
                toLogin();
                break;
            case R.id.text_register:
                toRegister();
                break;
            case R.id.text_reset:
                toReset();
                break;
        }
    }

    //重置密码
    private void toReset() {
        boolean info = getInfo(true);
        if (info) {
            if (xAidl != null && isConnect) {
                try {
                    int i = xAidl.resetPwd(name, password, newPassword);
                    if (i == 0) {
                        Toast.makeText(this, "密码修改失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "密码修改成功" +
                                "", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "服务未开启", Toast.LENGTH_SHORT).show();
            }
        }


    }

    //注册
    private void toRegister() {
        boolean info = getInfo(false);
        if (info) {
            if (xAidl != null && isConnect) {
                try {
                    long register = xAidl.register(name, password);
                    if (register == -1) {
                        Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "服务未开启", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //登录
    private void toLogin() {
        boolean info = getInfo(false);
        if (info) {
            if (xAidl != null && isConnect) {
                try {
                    User user = new User(name, password);
                    User login = xAidl.login(user);
                    if (login != null) {
                        Toast.makeText(this, "登录成功--" + login.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "服务未开启", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean getInfo(boolean reset) {
        name = editName.getText().toString();
        password = editPwd.getText().toString();
        newPassword = editReset.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (reset && TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "新密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

}
