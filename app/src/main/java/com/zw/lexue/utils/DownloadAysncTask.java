package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import com.zw.lexue.model.FileCodeModel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 下载文件异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class DownloadAysncTask extends AsyncTask<Void, Void, FileCodeModel> {

    private Handler handler;    //消息句柄
    private String url;         //目的地址
    private String path;        //下载目的地址
    private String fileId;      //用文件标识
    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler   消息传递句柄
     * @param url 源地址
     * @param path 目的地址
     * @param fileId 需要下载的文件标识
     * @param requestCode 请求码
     */
    public DownloadAysncTask(Handler handler, String url, String path,  String fileId, int requestCode){
        this.handler = handler;
        this.url = url;
        this.path = path;
        this.fileId = fileId;
        this.requestCode = requestCode;
    }

    @Override
    protected FileCodeModel doInBackground(Void... voids) {
        String content = "fileId=" + fileId;
        HttpCommon getAccountDetailService = new HttpCommon();
        String jsonData = getAccountDetailService
                .isToBase64String(true)
                .postGetJson(url, content);
        JsonParser<FileCodeModel> parser = new JsonParser((Class)FileCodeModel.class);
        FileCodeModel model = parser.parseJSONWithJSONObject(jsonData);
        //文件下载成功，则Json解析失败，即model必定为null
        if(model == null){
            try {
                String tmp = path + '/' + fileId;
                FileOutputStream os = new FileOutputStream(tmp);
                os.write(Base64.decode(jsonData,Base64.DEFAULT));
                os.flush();
                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            model = new FileCodeModel();
            //服务器连接错误返回码为0
            if(jsonData.equalsIgnoreCase("failed")){
                model.setCode(0);
            }
            //连接失败返回码为
            else if(jsonData.equalsIgnoreCase("error")){
                model.setCode(-1);
            }
        }
        return model;
    }

    protected void onPostExecute(FileCodeModel model) {
        Message message = new Message();
        message.what = requestCode;
        message.obj = model;
        handler.sendMessage(message);
    }
}
