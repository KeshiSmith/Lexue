package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.zw.lexue.model.SchoolAndMajorModel;

import java.util.List;

/**
 * 获取所有学校信息异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class GetSchoolListAysncTask extends AsyncTask<Void, Void, List<SchoolAndMajorModel>> {

    private Handler handler;    //消息句柄
    private String url;         //目的地址
    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler   消息传递句柄
     * @param url 获取学校地址
     * @param requestCode 请求码
     */
    public GetSchoolListAysncTask(Handler handler, String url, int requestCode){
        this.handler = handler;
        this.url = url;
        this.requestCode = requestCode;
    }

    @Override
    protected List<SchoolAndMajorModel> doInBackground(Void... voids) {
        HttpCommon getAccountDetailService = new HttpCommon();
        String jsonData = getAccountDetailService.postGetJson(url,"");
        JsonParser<SchoolAndMajorModel> parser = new JsonParser((Class)SchoolAndMajorModel.class);
        List<SchoolAndMajorModel> model = parser.parseJSONWithJSONObjects(jsonData);
        return model;
    }

    protected void onPostExecute(List<SchoolAndMajorModel> model) {
        Message message = new Message();
        message.what = requestCode;
        message.obj = model;
        handler.sendMessage(message);
    }
}
