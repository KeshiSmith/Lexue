package com.zw.lexue.activitys;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zw.lexue.R;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.utils.UpdatePawAysncTask;

/**
 * 本文档为修改密码页面文档
 * Created by Keshi Smith on 2017/7/16.
 */

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_UPDATE_PASSWORD = 0X1000;//修改密码请求

    private LinearLayout llBack;
    private Button buttonConfirm;
    private EditText etPassword, etNewPassword, etRenewPassword;

    //消息传递句柄
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REQUEST_UPDATE_PASSWORD:
                    switch ((String)msg.obj){
                        case "301":
                            showMessage("用户未登录");
                            break;
                        case "302":
                            showMessage("参数不合法");
                            break;
                        case "303":
                            showMessage("用户密码错误");
                            break;
                        case "304":
                            showMessage("新旧密码相同");
                            break;
                        case "200":
                            showMessage("修改密码成功");
                            finish();
                            break;
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        intiView();//初始化布局控件
    }

    private void intiView() {
        //结束本页面功能实现
        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //获取本页面控件
        buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etNewPassword = (EditText)findViewById(R.id.etNewPassword);
        etRenewPassword = (EditText)findViewById(R.id.etRenewPassword);
        //设置监听器
        buttonConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //前台验证
        String password = etPassword.getText().toString();
        if(password.length()==0){
            showMessage("密码不能为空");
            etPassword.requestFocus();
            return;
        }
        String newPassword = etNewPassword.getText().toString();
        if(newPassword.length() == 0){
            showMessage("新密码不能为空");
            etNewPassword.requestFocus();
            return;
        }
        String renewPassword = etRenewPassword.getText().toString();
        if(renewPassword.length() == 0){
            showMessage("重复密码不能为空");
            etRenewPassword.requestFocus();
            return;
        }
        if(!newPassword.equals(renewPassword)){
            showMessage("新密码和重复密码不一致");
            etNewPassword.requestFocus();
            return;
        }
        //服务器修改密码
        UpdatePawAysncTask updatePawAysncTask = new UpdatePawAysncTask(
                handler,
                ApplicationModel.getUpdatePawAddress(),
                password,
                newPassword,
                REQUEST_UPDATE_PASSWORD);
        updatePawAysncTask.execute();
    }

    /**
     * 显示信息函数
     * @param msg 需要显示的信息
     */
    private void showMessage(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
