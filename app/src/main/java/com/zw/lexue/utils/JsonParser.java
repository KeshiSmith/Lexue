package com.zw.lexue.utils;

import com.zw.lexue.model.ArticleModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Json解析的模板类，其中T为解析为Object的模板。
 * 要求T为一个标准的JavaBean类
 * Created by Keshi Smith on 2017/10/23.
 */
public class JsonParser<T> {

    private Class<T> clazz;

    public JsonParser(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * 将Json数据转为相应的JavaBean类
     * @param jsonData Json数据
     * @return 相应的JavaBean类
     */
    public T parseJSONWithJSONObject(String jsonData){
        //获取模板 T 的类型
        Class<T> entityClass = clazz;
        Method[] methods = entityClass.getMethods();

        //初始化数据
        T bean = null;
        try {
            // 将Json字符串JsonData装入JSON对象，即JSONObject
            JSONObject jsonObject = new JSONObject(jsonData);

            //创建模板JavaBean实例
            bean = entityClass.newInstance();
            //遍历所有公开的方法名
            for(Method method : methods){
                //选择所有以set开头的方法名
                if(method.getName().startsWith("set")){
                    //获取参数的名字
                    String paramName = method.getName().substring(3);
                    //参数首字母变为小写
                    paramName = paramName.substring(0,1).toLowerCase() + paramName.substring(1);
                    //获取参数类型
                    Class paramType = method.getParameterTypes()[0];
                    //根据不同的参数类型解析JSON
                    if(paramType.isInstance(new Integer(0))||paramType.isInstance(0)){
                        Integer param = jsonObject.getInt(paramName);
                        method.invoke(bean, param);
                    }
                    else if(paramType.isInstance(new String())){
                        String param = jsonObject.getString(paramName);
                        method.invoke(bean, param);
                    }
                    else if(paramType == List.class){
                        //TODO 解析数据，此处使用了“脏方法”，后期将采用GSON优化
                        JSONArray subJsonArray = jsonObject.getJSONArray(paramName);
                        //初始化数据
                        List<ArticleModel> list = new ArrayList<>();
                        for(int i = 0; i < subJsonArray.length(); i++){
                            //循环遍历，依次取出JSONObject对象
                            JSONObject subJsonObject = subJsonArray.getJSONObject(i);
                            //获取参数
                            ArticleModel model = new ArticleModel();
                            model.setId(subJsonObject.getInt("id"));
                            model.setAccountId(subJsonObject.getInt("accountId"));
                            model.setAccountName(subJsonObject.getString("accountName"));
                            model.setAccountHeadFileId(subJsonObject.getString("accountHeadFileId"));
                            model.setTitle(subJsonObject.getString("title"));
                            model.setContentFileId(subJsonObject.getString("contentFileId"));
                            model.setLabel(subJsonObject.getString("label"));
                            model.setAttachFileId(subJsonObject.getString("attachFileId"));
                            model.setCommentCount(subJsonObject.getInt("commentCount"));
                            model.setFavourCount(subJsonObject.getInt("favourCount"));
                            model.setReleaseTime(subJsonObject.getString("releaseTime"));
                            model.setFavoured(subJsonObject.getBoolean("favoured"));
                            //添加新对象
                            list.add(model);
                        }
                        method.invoke(bean, list);
                    }
                    else{
                        throw new Exception();
                    }
                }

            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 将Json数据转为相应的JavaBean类列表
     * @param jsonData Json数据
     * @return 相应的JavaBean类列表
     */
    public List<T> parseJSONWithJSONObjects(String jsonData){
        //获取模板 T 的类型
        Class<T> entityClass = clazz;
        Method[] methods = entityClass.getMethods();

        //初始化数据
        List<T> beans = new ArrayList();
        try {
            // 将Json字符串JsonData装入JSON列表，即JSONArray
            JSONArray jsonArray = new JSONArray(jsonData);

            for(int i=0;i<jsonArray.length();i++){
                //循环遍历，依次取出JSONObject对象
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //创建模板JavaBean实例
                T bean = entityClass.newInstance();
                //遍历所有公开的方法名
                for(Method method : methods){
                    //选择所有以set开头的方法名
                    if(method.getName().startsWith("set")){
                        //获取参数的名字
                        String paramName = method.getName().substring(3);
                        //参数首字母变为小写
                        paramName = paramName.substring(0,1).toLowerCase() + paramName.substring(1);
                        //获取参数类型
                        Class paramType = method.getParameterTypes()[0];
                        //根据不同的参数类型解析JSON
                        if(paramType.isInstance(new Integer(0))||paramType.isInstance(0)){
                            Integer param = jsonObject.getInt(paramName);
                            method.invoke(bean, param);
                        }
                        else if(paramType.isInstance(new String())){
                            String param = jsonObject.getString(paramName);
                            method.invoke(bean, param);
                        }
                        else{
                            throw new Exception();
                        }
                    }
                }
                //添加新对象
                beans.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beans;
    }
}
