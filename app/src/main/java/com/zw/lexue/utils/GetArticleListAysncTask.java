package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.zw.lexue.model.ArticleListModel;

/**
 * 获取文章列表异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class GetArticleListAysncTask extends AsyncTask<Void, Void, ArticleListModel> {

    private Handler handler;    //消息句柄
    private String url;         //目的地址
    private String content;     //更新文章页数

    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler   消息传递句柄
     * @param url 获取文章列表地址
     * @param requestCode 请求码
     */
    public GetArticleListAysncTask(Handler handler, String url, String content, int requestCode){
        this.handler = handler;
        this.url = url;
        this.content = content;
        this.requestCode = requestCode;
    }

    @Override
    protected ArticleListModel doInBackground(Void... voids) {
        HttpCommon getArticleListService = new HttpCommon();
        String jsonData = getArticleListService
                .setUseLastSessionId(true)
                .postGetJson(url,content==null? "":content);
        JsonParser<ArticleListModel> parser = new JsonParser((Class)ArticleListModel.class);
        ArticleListModel model = parser.parseJSONWithJSONObject(jsonData);
        return model;
    }

    protected void onPostExecute(ArticleListModel model) {
        Message message = new Message();
        message.what = requestCode;
        message.obj = model;
        handler.sendMessage(message);
    }
}
