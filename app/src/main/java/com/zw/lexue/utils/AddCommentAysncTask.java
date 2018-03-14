package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * 添加评论异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class AddCommentAysncTask extends AsyncTask<Void, Void, String> {

    private Handler handler;    //消息句柄
    private String url;         //登陆地址
    private int articleId;      //文章Id
    private String comment;     //评论
    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler   消息传递句柄
     * @param url       登陆服务器地址
     * @param articleId 文章ID
     * @param comment   评论
     * @param requestCode  请求码
     */
    public AddCommentAysncTask(Handler handler, String url, int articleId, String comment  , int requestCode){
        this.handler = handler;
        this.url = url;
        this.articleId = articleId;
        this.comment = comment;
        this.requestCode =requestCode;
    }

    @Override
    protected String doInBackground(Void... voids) {
        //登陆操作，Post方式登陆，返回值
        String content = "articleId=" + articleId + "&comment=" + comment;
        HttpCommon addCommentService = new HttpCommon();
        String jsonData = addCommentService
                .setUseLastSessionId(true)
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
