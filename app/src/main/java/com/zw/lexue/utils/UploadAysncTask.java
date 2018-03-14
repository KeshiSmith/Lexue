package com.zw.lexue.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import com.zw.lexue.model.FileCodeModel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 下载文件异步任务
 * Created by Keshi Smith on 2017/10/17.
 */
public class UploadAysncTask extends AsyncTask<Void, Void, FileCodeModel> {

    private Handler handler;    //消息句柄
    private String url;         //目的地址
    private String path;        //上传文件路径
    private String name;        //上传文件控件名
    private String filename;    //文件名
    private String type;        //文件类型
    private int requestCode;    //请求码

    /**
     * 构造函数
     * @param handler   消息传递句柄
     * @param url 目的地址
     * @param path 上传文件的路径
     * @param name 上传文件控件名
     * @param filename 文件名
     * @param type 文件类型
     * @param requestCode 请求码
     */
    public UploadAysncTask(Handler handler, String url, String path, String name,
                           String filename, String type, int requestCode){
        this.handler = handler;
        this.url = url;
        this.path = path;
        this.name = name;
        this.filename = filename;
        this.type = type;
        this.requestCode = requestCode;
    }

    @Override
    protected FileCodeModel doInBackground(Void... voids) {
        String content = "";
        try {
            FileInputStream is = new FileInputStream(path);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            //定义读取的长度
            int len = 0;
            //定义缓冲区
            byte buffer[] = new byte[1024];
            //按照缓存区的大小，循环读取
            while((len = is.read(buffer))!=-1) {
                //根据读取的长度写入到content
                os.write(buffer,0,len);
            }
            //释放资源
            is.close();
            os.close();
            //对文件进行编码
            content = Base64.encodeToString(os.toByteArray(),Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //连接服务器
        HttpCommon getAccountDetailService = new HttpCommon();
        String jsonData = getAccountDetailService
                .setContentType(HttpCommon.CONTENT_TYPE.MULTIPART_FORM_DATA)
                .setName(name)
                .setFilename(filename)
                .setType(type)
                .postGetJson(url, content);
        JsonParser<FileCodeModel> parser = new JsonParser((Class)FileCodeModel.class);
        FileCodeModel model = parser.parseJSONWithJSONObject(jsonData);
        //文件下载成功，则Json解析失败，即model必定为null
        if(model != null && model.getCode() == null){
            model = new FileCodeModel();
            //服务器连接错误返回码为0
            if(jsonData.equalsIgnoreCase("failed")){
                model.setCode(0);
            }
            //连接失败返回码为-1
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
