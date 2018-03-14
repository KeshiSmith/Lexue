package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.zw.lexue.model.AccountModel;

/**
 * 获取账户基本信息异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class GetAccountAysncTask extends AsyncTask<Void, Void, AccountModel> {

    private Handler handler;    //消息句柄
    private String url;         //目的地址
    private Integer accountId;  //用户ID
    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler   消息传递句柄
     * @param url 获取账户基本信息地址
     * @param accountId 用户Id
     * @param requestCode 请求码
     */
    public GetAccountAysncTask(Handler handler, String url, Integer accountId, int requestCode){
        this.handler = handler;
        this.url = url;
        this.accountId = accountId;
        this.requestCode = requestCode;
    }

    @Override
    protected AccountModel doInBackground(Void... voids) {
        String content = "accountId=" + accountId;
        HttpCommon getAccountDetailService = new HttpCommon();
        String jsonData = getAccountDetailService.postGetJson(url,content);
        JsonParser<AccountModel> parser = new JsonParser((Class)AccountModel.class);
        AccountModel model = parser.parseJSONWithJSONObject(jsonData);
        return model;
    }

    protected void onPostExecute(AccountModel model) {
        Message message = new Message();
        message.what = requestCode;
        message.obj = model;
        handler.sendMessage(message);
    }
}
