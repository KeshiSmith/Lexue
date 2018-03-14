package com.zw.lexue.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zw.lexue.R;
import com.zw.lexue.model.ApplicationModel;

/**
 * 本文档为设置页面的Java文档，用于修改应用内相关设置。
 * Created by Keshi Smith on 2017/7/16.
 */

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout llBack, llPassword, llBanMessage, llBanWelcomePage, llAbout;
    private TextView tvEmail;
    private CheckBox cbBanMessage, cbBanWelcomePage;
    private Button buttonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        intiView();//初始化布局控件
        intiData();//初始化控件数据
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
        //设置本页面控件监听
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        llPassword = (LinearLayout) findViewById(R.id.llPassword);
        llPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, PasswordActivity.class);
                startActivity(intent);
            }
        });
        llBanMessage = (LinearLayout) findViewById(R.id.llBanMessage);
        cbBanMessage = (CheckBox) findViewById(R.id.cbBanMessage);
        llBanMessage.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean currentBanMessage = cbBanMessage.isChecked();
                cbBanMessage.setChecked(!currentBanMessage);
                ApplicationModel.setMessage(!currentBanMessage);
            }
        });
        cbBanMessage.setChecked(ApplicationModel.getMessage());
        llBanWelcomePage = (LinearLayout) findViewById(R.id.llBanWelcomePage);
        cbBanWelcomePage = (CheckBox) findViewById(R.id.cbBanWelcomePage);
        llBanWelcomePage.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean currentBanWelcomePage = cbBanWelcomePage.isChecked();
                cbBanWelcomePage.setChecked(!currentBanWelcomePage);
                ApplicationModel.setSetupOn(!currentBanWelcomePage);
            }
        });
        cbBanWelcomePage.setChecked(ApplicationModel.getSetupOn());
        llAbout = (LinearLayout)findViewById(R.id.llAbout);
        llAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
        //退出登陆，返回登陆页面
        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("exit", true);
                setResult(MainActivity.REQUEST_SETTINGS, intent);
                finish();
            }
        });
    }

    private void intiData(){
        //设置控件数据
        tvEmail.setText(ApplicationModel.getCount());
    }
}
