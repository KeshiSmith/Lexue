package com.zw.lexue.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zw.lexue.R;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.utils.CommonAysncTask;
import com.zw.lexue.utils.DownloadAysncTask;
import com.zw.lexue.utils.LoginAysncTask;
import com.zw.lexue.widgets.EmailAutoCompleteTextView;

import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 本文档为欢迎页加载的Java程序，功能为加载活动的布局文件并显示一定时间，然后返回主页面。
 * Created by Keshi Smith on 2017/6/2.
 */

public class WelcomeActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 0X1000; //登陆请求
    private static final int REQUEST_GET_SETUP = 0x1001; //获取启动画面

    private boolean isAutoLoginSucceed = false; //自动登陆成功标志

    private static final String[] words = {
            "昨夜西风凋碧树\n独上高楼 望尽天涯路",
            "衣带渐宽终不悔\n为伊消得人憔悴",
            "众里寻他千百度\n蓦然回首 那人却在灯火阑珊处"};

    //当前页面控件
    private FrameLayout background;
    private TextView textViewWord;

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
                            //自动登陆成功
                            isAutoLoginSucceed = true;
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
                //获取启动画面
                case REQUEST_GET_SETUP:
                    if(msg.obj != null){
                        switch ((String)msg.obj) {
                            case "failed":
                                showMessage("服务器连接失败，请稍后再试");
                                break;
                            case "error":
                                showMessage("连接失败，请检查网络设置");
                                break;
                            default:
                                //保存Setup启动画面
                                ApplicationModel.setSetup((String)msg.obj);
                                ApplicationModel.saveApplicationData(WelcomeActivity.this);
                                updateBackground();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置标题栏适配页面
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        //设置加载页面布局文件
        setContentView(R.layout.activity_welcome);
        //加载应用数据
        ApplicationModel.loadApplicationData(this);

        autoLogin();//自动登陆
        initView();//初始化控件布局
    }

    /**
     * 初始化布局控件
     */
    private void initView() {
        //初始化名言警句
        Random random = new Random();
        int index = random.nextInt(words.length);
        textViewWord = (TextView) findViewById(R.id.words);
        textViewWord.setText(words[index]);
        //获取最新启动图
        CommonAysncTask getSetupAysncTask = new CommonAysncTask(
                handler,
                ApplicationModel.getFileIdAddress(),
                "type=setup",
                REQUEST_GET_SETUP);
        getSetupAysncTask.execute();
        //加载背景图片
        background = (FrameLayout)findViewById(R.id.background);
        updateBackground();
        //定时器任务两秒跳转至至主页面，若自动登陆成功，则跳转至主页面，
        //否则跳转至登陆页面
        Timer timer = new Timer();
        final Intent loginActivityIntent = new Intent(this, LoginActivity.class);//设置跳转到登陆页面
        final Intent mainActivityIntent = new Intent(this, MainActivity.class);
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                if(isAutoLoginSucceed){
                    startActivity(mainActivityIntent);//启动跳转至主页面
                }
                else{
                    startActivity(loginActivityIntent);//启动跳转至主页面
                }
                finish();//结束当前页面
            }
        };
        timer.schedule(timerTask, 1000 * 2);//两秒后执行任务
    }

    private void updateBackground(){
        String setup = ApplicationModel.getSetup();
        if(setup==null){
            background.setBackgroundResource(R.drawable.welcome);
        }
        else{
            File file = new File(ApplicationModel.getCacheImagePath(), setup);
            if(file.exists()){
                //若头像文件已存在，则加载头像
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                background.setBackground(drawable);
            }
            else {
                //若头像文件不存在，则加载默认头像
                background.setBackgroundResource(R.drawable.welcome);
                //同时下载头像
                DownloadAysncTask downloadAysncTask = new DownloadAysncTask(
                        handler,
                        ApplicationModel.getFileDownloadAddress(),
                        ApplicationModel.getCacheImagePath(),
                        setup,
                        0);
                downloadAysncTask.execute();
            }
        }
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

    /**
     * 自动登陆账号，读取用户数据，若数据存在，则自动登陆账号。
     */
    private void autoLogin() {
        String email = ApplicationModel.getCount();
        String password = ApplicationModel.getPaw();
        if(email!=null && password!=null)
        {
            String url = ApplicationModel.getLoginAddress();
            onHttpLogin(url, email ,password);
        }
    }
}
