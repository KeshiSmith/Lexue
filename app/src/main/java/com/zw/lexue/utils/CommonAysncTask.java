package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * 通用异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class CommonAysncTask extends AsyncTask<Void, Void, String> {

    private Handler handler;    //消息句柄
    private String url;         //登陆地址
    private String content;     //邮箱
    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler   消息传递句柄
     * @param url       登陆服务器地址
     * @param content   传递信息
     * @param requestCode  请求码
     */
    public CommonAysncTask(Handler handler, String url, String content, int requestCode){
        this.handler = handler;
        this.url = url;
        this.content = content;
        this.requestCode =requestCode;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpCommon commonService = new HttpCommon();
        String jsonData = commonService
                .setUseLastSessionId(true)
                .postGetJson(url,content == null? "": content);
        return jsonData;

    }

    protected void onPostExecute(String jsonData) {
        Message msg = new Message();
        msg.what = requestCode; //消息请求码
        msg.obj = jsonData;     //登陆返回码
        handler.sendMessage(msg);
    }
}
