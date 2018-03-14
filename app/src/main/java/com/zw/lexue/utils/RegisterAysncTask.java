package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.zw.lexue.model.ApplicationModel;

/**
 * 注册异步任务
 * Created by Keshi Smith on 2017/10/17.
 */

public class RegisterAysncTask extends AsyncTask<Void, Void, String> {

    private Handler handler;//消息句柄
    private String url;     //目的地址
    private String name;    //用户名
    private String count;   //邮箱
    private String paw;     //密码
    private int type;       //类型
    private int requestCode;//请求码

    public  RegisterAysncTask(Handler handler, String url, String name, String count,
                              String paw, int type, int requestCode){
        this.handler = handler;
        this.url = url;
        this.name = name;
        this.count = count;
        this.paw = paw;
        this.type = type;
        this.requestCode = requestCode;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String content = "name=" + name + "&count=" + count + "&paw=" + paw + "&type=" +type;
        HttpCommon registerService = new HttpCommon();
        String jsonData = registerService.postGetJson(url, content);
        return jsonData;
    }

    protected void onPostExecute(String jsonData) {
        Message msg = new Message();
        msg.what = requestCode; //消息请求码
        msg.obj = jsonData;     //注册返回码
        handler.sendMessage(msg);
    }
}
