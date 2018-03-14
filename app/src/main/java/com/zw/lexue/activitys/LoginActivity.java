package com.zw.lexue.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zw.lexue.R;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.utils.LoginAysncTask;
import com.zw.lexue.widgets.EmailAutoCompleteTextView;

/**
 * 本文档为登陆注册功能实现的Java程序，实现将表单数据上传至Web服务器，验证登陆，注册账号等功能。
 * 同时，此程序也完成自动登陆检测相关的小功能。
 * Created by Keshi Smith on 2017/6/3.
 */

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 0X1000;    //登陆请求

    private boolean mIsExit = false;                    //双击退出页面标志

    private EmailAutoCompleteTextView emailTextView;    //邮箱编辑控件
    private EditText editTextPassword;                  //密码编辑控件
    private String email = "";                          //账户邮箱
    private String password = "";                       //账户密码

    // 主界面Intent的对象，用于登陆成功后跳转到主界面。
    private Intent mainActivityIntent;

    //消息传递，用于处理登陆结果。
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                //登陆请求
                case REQUEST_LOGIN:
                    switch ((String)msg.obj){
                        case "failed":
                            showMessage("服务器连接失败，请稍后再试");
                            break;
                        case "error":
                            showMessage("连接失败，请检查网络设置");
                            break;
                        case "200":
                            //登陆新账号删除数据
                            if(email != ApplicationModel.getCount()){
                                ApplicationModel.clearApplicationData(LoginActivity.this);
                            }
                            //存储账号密码
                            ApplicationModel.setCount(email);
                            ApplicationModel.setPaw(password);
                            ApplicationModel.saveApplicationData(LoginActivity.this);
                            //启动跳转至主页面
                            showMessage("登陆成功");
                            startActivity(mainActivityIntent);
                            finish();
                            break;
                        case "301":
                            showMessage("用户名或密码格式不正确");
                            break;
                        case "302":
                            showMessage("用户名或密码错误");
                            break;
                        case "303":
                            showMessage("验证码输入不正确");
                            break;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intiView();//初始化布局控件
    }

    /**
     * 初始化布局控件
     * 功能1：登陆/注册表单数据上传至服务器，获取结果跳转到主页面。
     * 功能2：登陆/注册页面间进行切换跳转。
     * 功能3：通过检测邮箱与密码长度，控制确认按钮是否可用。
     */
    private void intiView() {
        //设置跳转到注册页面
        final Intent registerActivityIntent = new Intent(this, RegisterActivity.class);
        Button buttonToRegister = (Button) findViewById(R.id.buttonToRegister);
        buttonToRegister.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(registerActivityIntent);//启动跳转至主页面
            }
        });

        //初始化设置Intent
        mainActivityIntent = new Intent(this, MainActivity.class);
        final Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                String url = ApplicationModel.getLoginAddress();
                email = emailTextView.getText().toString();
                password = editTextPassword.getText().toString();
                onHttpLogin(url, email, password);//登陆
            }
        });

        //检测邮箱和密码长度，确定登陆/注册按钮是否可用，并设置初值
        emailTextView = (EmailAutoCompleteTextView) findViewById(R.id.emailTextView);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        //获取上次账户邮箱，若存在则设置初值
        String count = ApplicationModel.getCount();
        if(count!=null){
            emailTextView.setText(count);
        }

        //邮箱长度检测
        emailTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length1 = editable.length();
                int length2 = editTextPassword.getText().length();

                if (length1 <= 0) {
                    buttonLogin.setEnabled(false);
                } else if (length2 > 0) {
                    buttonLogin.setEnabled(true);
                }
            }
        });

        //密码长度检测
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length1 = editable.length();
                int length2 = emailTextView.getText().length();

                if (length1 <= 0) {
                    buttonLogin.setEnabled(false);
                } else if (length2 > 0) {
                    buttonLogin.setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //连续两次按下返回键退出程序
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit == true) {
                finish();//结束当前Activity
                System.exit(0);//结束当前应用程序
            } else {
                Toast.makeText(this, R.string.exit_confirm, Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示信息函数
     * @param msg
     */
    private void showMessage(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 验证表格信息填写是否有误，若正确则返回值true，否则返回值为false。
     * @param count 账户
     * @param paw   密码
     * @return 表格信息的正误。
     */
    private boolean accoutVerify(String count, String paw)
    {
        if(!EmailAutoCompleteTextView.isLegalAddress(count))
        {
            showMessage("邮箱地址不合法");
            return false;
        }
        if(paw.length() < 6)
        {
            showMessage("密码长度不能低于6位");
            return false;
        }
        return true;
    }

    /**
     * 在线注册，连接服务器，注册新账号。
     */
    private void onHttpLogin(String url, String count, String paw){
        if(accoutVerify(count, paw))
        {
            LoginAysncTask registerAysncTask = new LoginAysncTask(handler, url, count, paw, REQUEST_LOGIN);
            registerAysncTask.execute();
        }
    }
}
