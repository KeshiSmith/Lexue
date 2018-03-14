package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * 登陆异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class LoginAysncTask extends AsyncTask<Void, Void, String> {

    private Handler handler;    //消息句柄
    private String url;         //登陆地址
    private String count;       //邮箱
    private String paw;         //密码
    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler   消息传递句柄
     * @param url       登陆服务器地址
     * @param count     登陆用户邮箱
     * @param paw       登陆用户密码
     * @param requestCode  请求码
     */
    public LoginAysncTask(Handler handler, String url, String count, String paw, int requestCode){
        this.handler = handler;
        this.url = url;
        this.count = count;
        this.paw = paw;
        this.requestCode =requestCode;
    }

    @Override
    protected String doInBackground(Void... voids) {
        //登陆操作，Post方式登陆，返回值
        String content = "count=" + count + "&paw=" + paw;
        HttpCommon loginService = new HttpCommon();
        String jsonData = loginService
                .setNeedSaveSession(true)
                .postGetJson(url,content);
        return jsonData;

    }

    protected void onPostExecute(String jsonData) {
        Message msg = new Message();
        msg.what = requestCode; //消息请求码
        msg.obj = jsonData;     //登陆返回码
        handler.sendMessage(msg);
    }
}
