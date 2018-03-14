package com.zw.lexue.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zw.lexue.R;

/**
 * 本文档为通用页面文档，用于目前阶段的测试使用，未来将会根据具体情况分化完善。
 * Created by Keshi Smith on 2017/7/16.
 */

public class CommonActivity extends AppCompatActivity {

    private LinearLayout llBack;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

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
        //获取上个页面数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //设置当前页面数据
        String title = (String)bundle.get("title");
        if(title!=null||title.length()>0){
            tvTitle=(TextView)findViewById(R.id.tvTitle);
            tvTitle.setText(title);
        }
    }
}
