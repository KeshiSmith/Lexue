package com.zw.lexue.activitys;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zw.lexue.R;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.utils.RegisterAysncTask;
import com.zw.lexue.widgets.EmailAutoCompleteTextView;

/**
 * 本文档为登陆注册功能实现的Java程序，实现将表单数据上传至Web服务器，验证登陆，注册账号等功能。
 * 同时，此程序也完成自动登陆检测相关的小功能。
 * Created by Keshi Smith on 2017/6/3.
 */

public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_REGISTER = 0X1000;  //注册请求

    private EditText editTextNickname,editTextPassword;
    private EmailAutoCompleteTextView emailTextView;
    private int accountType = ApplicationModel.TYPE_STUDENT_NOT_VERIFY;

    //消息传递，用于处理注册结果。
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case REQUEST_REGISTER:
                    switch ((String)msg.obj){
                        case "failed":
                            showMessage("服务器连接失败，请稍后再试");
                            break;
                        case "error":
                            showMessage("连接失败，请检查网络设置");
                            break;
                        case "200":
                            showMessage("注册成功");
                            finish();
                            break;
                        case "301":
                            showMessage("用户名或密码或昵称格式不正确");
                            break;
                        case "303":
                            showMessage("用户名或邮箱已存在");
                            break;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        intiView();//初始化布局控件
    }

    /**
     * 初始化布局控件
     * 功能1：登陆/注册表单数据上传至服务器，获取结果跳转到主页面。
     * 功能2：登陆/注册页面间进行切换跳转。
     * 功能3：通过检测邮箱与密码长度，控制确认按钮是否可用。
     */
    private void intiView(){
        //结束本页面功能实现
        final LinearLayout buttonBack=(LinearLayout) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new LinearLayout.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();//结束本页面，返回上一页面
            }
        });

        //昵称编辑框，邮箱编辑框，密码编辑框
        editTextNickname = (EditText)findViewById(R.id.editTextNickname);
        emailTextView = (EmailAutoCompleteTextView) findViewById(R.id.emailTextViewRegister);
        editTextPassword =(EditText)findViewById(R.id.editTextPasswordRegister);

        //账户类型单选按钮,其中：
        //1:学生 2:老师 3:管理员 11:学生邮箱未验证 22:老师邮箱未验证
        //因为为注册界面，所以只存在类型 11：学生邮箱未验证 22:老师邮箱未验证。
        RadioGroup typeOfAccount = (RadioGroup) findViewById(R.id.radioGroupType);
        typeOfAccount.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                switch(checkedId)
                {
                    case R.id.radioButtonStudents:
                        accountType = ApplicationModel.TYPE_STUDENT_NOT_VERIFY;
                        break;
                    case R.id.radioButtonTeachers:
                        accountType = ApplicationModel.TYPE_TEACHER_NOT_VERIFY;
                        break;
                }
            }
        });

        //注册账号功能实现
        final Button buttonConfirm=(Button)findViewById(R.id.buttonRegister);
        buttonConfirm.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                String url = ApplicationModel.getRegisterAddress();
                String name = editTextNickname.getText().toString();
                String count = emailTextView.getText().toString();
                String paw = editTextPassword.getText().toString();
                int type = accountType;
                onHttpRegister(url, name, count, paw, type); //注册
            }
        });

        //昵称长度检测
        editTextNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int lengthNickName = editable.length();
                int lengthEmail = emailTextView.length();
                int lengthPassword = editTextPassword.length();

                if(lengthNickName>0 && lengthEmail >0 && lengthPassword>0){
                    buttonConfirm.setEnabled(true);
                }
                else {
                    buttonConfirm.setEnabled(false);
                }
            }
        });

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
                int lengthNickName = editTextNickname.length();
                int lengthEmail = editable.length();
                int lengthPassword = editTextPassword.length();

                if(lengthNickName>0 && lengthEmail >0 && lengthPassword>0){
                    buttonConfirm.setEnabled(true);
                }
                else {
                    buttonConfirm.setEnabled(false);
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
                int lengthNickName = editTextNickname.length();
                int lengthEmail = emailTextView.length();
                int lengthPassword = editable.length();

                if(lengthNickName>0 && lengthEmail >0 && lengthPassword>0){
                    buttonConfirm.setEnabled(true);
                }
                else {
                    buttonConfirm.setEnabled(false);
                }
            }
        });
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
     * @return 表格信息的正误。
     */
    private boolean accoutVerify(String name, String count, String paw)
    {
        if(name.matches("^\\d.*"))
        {
            showMessage("昵称开头不能为数字");
            return false;
        }
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
     * 开启一个异步任务，执行连接网络，注册新账号的动作。
     */
    private void onHttpRegister(String url, String name, String count, String paw, int type){
        if(accoutVerify(name, count, paw))
        {
            RegisterAysncTask registerAysncTask = new RegisterAysncTask(
                    handler, url, name, count, paw, type, REQUEST_REGISTER);
            registerAysncTask.execute();
        }
    }
}
