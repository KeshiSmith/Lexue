package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * 获取用户Id异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class GetAccountIdAysncTask extends AsyncTask<Void, Void, String> {

    private Handler handler;    //消息句柄
    private String url;         //登陆地址
    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler   消息传递句柄
     * @param url       登陆服务器地址
     * @param requestCode  请求码
     */
    public GetAccountIdAysncTask(Handler handler, String url, int requestCode){
        this.handler = handler;
        this.url = url;
        this.requestCode =requestCode;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpCommon loginService = new HttpCommon();
        String jsonData = loginService
                .setUseLastSessionId(true)
                .postGetJson(url,"");
        return jsonData;

    }

    protected void onPostExecute(String jsonData) {
        Message msg = new Message();
        msg.what = requestCode; //消息请求码
        msg.obj = jsonData;     //登陆返回码
        handler.sendMessage(msg);
    }
}
