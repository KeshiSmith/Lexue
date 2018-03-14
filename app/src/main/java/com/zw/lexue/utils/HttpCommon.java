package com.zw.lexue.utils;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 本文件为Http连接相关服务，提供一系列服务器连接接口。
 * Created by Keshi Smith on 2017/10/17.
 */

public class HttpCommon {

    //Content-Type
    public enum CONTENT_TYPE {
        APPLICATION_URLENCODED,
        MULTIPART_FORM_DATA,
        TEXT_PLAIN
    }

    //HTTP通信参数
    private int connectTimeout = 15000;     //连接超时
    private int readTimeout = 15000;        //读数据超时

    //Session相关参数
    private static String lastSessionId = null;    //上一次的Session值
    private String curSessionId = null;            //当前的Session值
    private boolean needSaveSession = false;        //保存Session值标志
    private boolean useLastSessionId = false;       //使用上一次的Session值标志

    //其他相关设置
    private CONTENT_TYPE contentType =
            CONTENT_TYPE.APPLICATION_URLENCODED;    //Content-Type
    private String boundary = "123456789abcdef";//分界线
    private boolean toBase64String = false;         //Base64标志，用于获取二进制文件

    //文件上传时参数
    private String name = "file";
    private String filename = "default.jpg";
    private String type ="text/plain";

    //获取连接超时
    public int getConnectTimeout(){
        return connectTimeout;
    }

    //获取读数据超时
    public int getreadTimeout(){
        return readTimeout;
    }

    //设置连接超时,默认值为15000
    public HttpCommon setConnectTimeout(int connectTimeout){
        this.connectTimeout = connectTimeout;
        return this;
    }

    //设置读数据超时，默认值为15000
    public HttpCommon setReadTimeout(int readTimeout){
        this.readTimeout =readTimeout;
        return this;
    }

    //设置是否保存此次Session值到上一次的Session值
    public HttpCommon setNeedSaveSession(boolean needSaveSession){
        this.needSaveSession = needSaveSession;
        return this;
    }

    //设置和是否使用上一次的Session值
    public HttpCommon setUseLastSessionId(boolean useLastSessionId){
        this.useLastSessionId = useLastSessionId;
        return this;
    }

    //使用多部分数据
    public HttpCommon setContentType(CONTENT_TYPE contentType){
        this.contentType = contentType;
        return this;
    }

    //设置输出是否未Base64字符串
    public HttpCommon isToBase64String(boolean toBase64String){
        this.toBase64String = toBase64String;
        return this;
    }

    //设置文件上传控件名
    public HttpCommon setName(String name){
        this.name = name;
        return this;
    }

    //设置文件上传文件名
    public HttpCommon setFilename(String filename){
        this.filename = filename;
        return this;
    }

    //设置文件类型
    public HttpCommon setType(String type){
        this.type = type;
        return this;
    }

    /**
     * 使用POST方式连接服务器并获取JSON文件。
     * @param url 目的地址
     * @param content 需要通过POST传递的参数
     * @return JSON文件字符串
     */
    public String postGetJson(String url, String content){
        try {
            URL mUrl = new URL(url);
            HttpURLConnection mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
            //设置链接超时时间
            mHttpURLConnection.setReadTimeout(connectTimeout);
            //设置读取超时时间
            mHttpURLConnection.setReadTimeout(readTimeout);
            //设置请求参数
            mHttpURLConnection.setRequestMethod("POST");
            //添加Header
            switch (contentType){
                //默认模式，提交一般表单
                case APPLICATION_URLENCODED:
                    //采用异步访问
                    mHttpURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                    mHttpURLConnection.setRequestProperty("Content-type",
                            "application/x-www-form-urlencoded; charset=UTF-8");//配置选项，避免中文乱码
                    break;
                //上传文件时使用
                case MULTIPART_FORM_DATA:
                    mHttpURLConnection.setRequestProperty("Connection","keep-alive");
                    mHttpURLConnection.setRequestProperty("Content-type",
                            "multipart/form-data; boundary=" + boundary);//配置多部分表格数据，用于上传文件
                    break;
                //主要用于表单发送邮件，未使用
                case TEXT_PLAIN:
                    break;
            }
            //使用上一次的Session值
            if(useLastSessionId && lastSessionId != null){
                mHttpURLConnection.setRequestProperty("Cookie", lastSessionId);//设置sessionId
            }
            //接收输入流
            mHttpURLConnection.setDoInput(true);
            //传递参数时需开启
            mHttpURLConnection.setDoOutput(true);
            //Post方法不能缓存，需手动设置为false
            mHttpURLConnection.setUseCaches(false);

            //连接服务器地址
            mHttpURLConnection.connect();

            //传递参数或文件
            DataOutputStream dos = new DataOutputStream(mHttpURLConnection.getOutputStream());
            switch (contentType){
                //默认模式，提交一般表单
                case APPLICATION_URLENCODED:
                    dos.write(content.getBytes());
                    break;
                //上传文件时使用，要求传入的content为Base64字符串
                case MULTIPART_FORM_DATA:
                    //数据准备
                    StringBuffer headText = new StringBuffer();
                    headText.append("--").append(boundary).append("\r\n");
                    headText.append("Content-Disposition: form-data; name=\"")
                            .append(name)
                            .append("\"; filename=\"")
                            .append(filename)
                            .append("\"\r\n");
                    headText.append("Content-Type: ").append(type).append("\r\n\r\n");
                    StringBuffer tailText = new StringBuffer();
                    tailText.append("\r\n--").append(boundary).append("--\r\n");
                    //写入开头
                    dos.write(headText.toString().getBytes());
                    //写入文件，要求content为Base64字符串
                    dos.write(Base64.decode(content,Base64.DEFAULT));
                    //写入结尾
                    dos.write(tailText.toString().getBytes());
                    break;
                //主要用于表单发送邮件，未使用……
                case TEXT_PLAIN:
                    break;
            }
            dos.flush();
            dos.close(); //执行完dos.close()后，POST请求结束。

            //获取代码返回值
            int respondCode = mHttpURLConnection.getResponseCode();

            //获取返回内容类型
            //String type = mHttpURLConnection.getContentType();
            //String encoding = mHttpURLConnection.getContentEncoding();
            //获取返回内容长度，单位字节
            //int length = mHttpURLConnection.getContentLength();
            //获取完整的头信息Map
            //Map<String,List<String>> headerFields = mHttpURLConnection.getHeaderFields();

            //获取Cookie值
            String cookieValue=mHttpURLConnection.getHeaderField("Set-Cookie");
            //获取Session值
            if(cookieValue!=null && cookieValue != ""){
                curSessionId = cookieValue.substring(0, cookieValue.indexOf(";"));
                //保存当前的Session值
                if(needSaveSession){
                    lastSessionId = curSessionId;
                }
            }

            if(respondCode == 200)
            {
                InputStream is = mHttpURLConnection.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                //定义读取的长度
                int len = 0;
                //定义缓冲区
                byte buffer[] = new byte[1024];
                //按照缓存区的大小，循环读取
                while((len = is.read(buffer))!=-1) {
                    //根据读取的长度写入到os对象
                    os.write(buffer,0,len);
                }
                //释放资源
                is.close();
                os.close();
                //关闭Http连接
                mHttpURLConnection.disconnect();
                //获取返回信息
                String jsonData;
                //若使用Base64则编码为字符串，否则直接转为字符串
                //下载二进制文件时，建议设置toBase64String为true
                if(toBase64String){
                    jsonData = Base64.encodeToString(os.toByteArray(),Base64.DEFAULT);
                }
                else{
                    jsonData= new String(os.toByteArray());
                }
                return jsonData;
            }
            return "failed";

        } catch (Exception e) {
            return "error";
        }
    }
}