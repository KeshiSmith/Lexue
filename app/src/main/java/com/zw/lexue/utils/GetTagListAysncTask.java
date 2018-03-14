package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.zw.lexue.model.TagModel;

import java.util.List;

/**
 * 获取所有标签异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class GetTagListAysncTask extends AsyncTask<Void, Void, List<TagModel>> {

    private Handler handler;    //消息句柄
    private String url;         //目的地址
    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler   消息传递句柄
     * @param url 获取标签地址
     * @param requestCode 请求码
     */
    public GetTagListAysncTask(Handler handler, String url, int requestCode){
        this.handler = handler;
        this.url = url;
        this.requestCode = requestCode;
    }

    @Override
    protected List<TagModel> doInBackground(Void... voids) {
        HttpCommon getAccountDetailService = new HttpCommon();
        String jsonData = getAccountDetailService
                .setUseLastSessionId(true)
                .postGetJson(url,"");
        JsonParser<TagModel> parser = new JsonParser((Class)TagModel.class);
        List<TagModel> model = parser.parseJSONWithJSONObjects(jsonData);
        return model;
    }

    protected void onPostExecute(List<TagModel> model) {
        Message message = new Message();
        message.what = requestCode;
        message.obj = model;
        handler.sendMessage(message);
    }
}
