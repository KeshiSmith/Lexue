package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.zw.lexue.model.ReviewModel;

import java.util.List;

/**
 * 获取文章下评论异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class GetReviewListAysncTask extends AsyncTask<Void, Void, List<ReviewModel>> {

    private Handler handler;    //消息句柄
    private String url;         //目的地址
    private Integer articleId;  //学校编号
    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler 消息传递句柄
     * @param url 获取专业地址
     * @param articleId 文章Id
     * @param requestCode 请求码
     */
    public GetReviewListAysncTask(Handler handler, String url, Integer articleId, int requestCode){
        this.handler = handler;
        this.url = url;
        this.articleId = articleId;
        this.requestCode = requestCode;
    }

    @Override
    protected List<ReviewModel> doInBackground(Void... voids) {
        String content = "articleId=" + articleId;
        HttpCommon getAccountDetailService = new HttpCommon();
        String jsonData = getAccountDetailService.postGetJson(url,content);
        JsonParser<ReviewModel> parser = new JsonParser((Class)ReviewModel.class);
        List<ReviewModel> model = parser.parseJSONWithJSONObjects(jsonData);
        return model;
    }

    protected void onPostExecute(List<ReviewModel> model) {
        Message message = new Message();
        message.what = requestCode;
        message.obj = model;
        handler.sendMessage(message);
    }
}
