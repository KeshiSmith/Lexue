package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.zw.lexue.model.SchoolAndMajorModel;

import java.util.List;

/**
 * 获取所有学校下专业异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class GetMajorListAysncTask extends AsyncTask<Void, Void, List<SchoolAndMajorModel>> {

    private Handler handler;    //消息句柄
    private String url;         //目的地址
    private Integer schoolId;   //学校编号
    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler 消息传递句柄
     * @param url 获取专业地址
     * @param schoolId 学校Id
     * @param requestCode 请求码
     */
    public GetMajorListAysncTask(Handler handler, String url, Integer schoolId, int requestCode){
        this.handler = handler;
        this.url = url;
        this.schoolId = schoolId;
        this.requestCode = requestCode;
    }

    @Override
    protected List<SchoolAndMajorModel> doInBackground(Void... voids) {
        String content = "schoolId=" + schoolId;
        HttpCommon getAccountDetailService = new HttpCommon();
        String jsonData = getAccountDetailService.postGetJson(url,content);
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
