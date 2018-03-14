package com.zw.lexue.activitys;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.zw.lexue.R;
import com.zw.lexue.adapter.TagListAdapter;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.model.TagModel;
import com.zw.lexue.utils.CommonAysncTask;
import com.zw.lexue.utils.GetTagListAysncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本文档为课程标签选择页面，用于关注或取消你喜欢的课程
 * Created by Keshi Smith on 2017/7/16.
 */

public class TagsActivity extends AppCompatActivity implements TagListAdapter.Callback {

    //自定义常量
    public static final int REQUEST_GET_TAGS = 0X1000;//获取所有标签
    public static final int REQUEST_GET_CONCERNED_TAGS = 0X1001;//获取关注标签
    public static final int REQUEST_ADD_CONCERED_TAGS = 0X1002;//添加关注标签
    public static final int REQUEST_REMOVE_CONCERED_TAGS = 0X1003;//移除关注标签
    //主界面控件
    private LinearLayout llBack;
    private ListView tagListView;
    private TagListAdapter tagAdapter;
    private List<TagModel> tags = new ArrayList<>();
    private Map<Integer, TagModel> tagsMap = new HashMap<>();
    //消息处理句柄
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //获取所有标签
                case REQUEST_GET_TAGS:
                    if(msg.obj!=null){
                        //更新其他标签数据
                        for(TagModel tag : (List<TagModel>) msg.obj){
                            if(!tagsMap.containsKey(tag.getId())){
                                tags.add(tag);
                                tagsMap.put(tag.getId(),tag);
                            }
                        }
                        tagAdapter.notifyDataSetChanged();
                    }
                    break;
                //获取关注标签
                case REQUEST_GET_CONCERNED_TAGS:
                    if(msg.obj!=null){
                        //获取所有标签
                        GetTagListAysncTask getTagListAysncTask = new GetTagListAysncTask(
                                handler,
                                ApplicationModel.getTagListAddress(),
                                REQUEST_GET_TAGS);
                        getTagListAysncTask.execute();
                        //更新数据，同时设置关注
                        tags.addAll((List<TagModel>) msg.obj);
                        for(TagModel tag : tags){
                            tag.follow = true;
                            tagsMap.put(tag.getId(),tag);
                        }
                        tagAdapter.notifyDataSetChanged();
                    }
                    break;
                //关注|取消关注标签
                case REQUEST_ADD_CONCERED_TAGS:
                case REQUEST_REMOVE_CONCERED_TAGS:
                    if(msg.obj!=null){
                        switch ((String) msg.obj){
                            case "failed":
                                showMessage("服务器连接失败，请稍后再试");
                                break;
                            case "error":
                                showMessage("连接失败，请检查网络设置");
                                break;
                            case "301":
                                showMessage("未登陆");
                                break;
                            case "302":
                                showMessage("参数不合法");
                                break;
                            case "200":
                                //成功无操作
                                break;
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        initHttpData();//获取网络数据
        intiView();//初始化布局控件
    }

    /**
     * 获取网络数据
     */
    private void initHttpData(){
        //获取关注列表
        GetTagListAysncTask getTagListAysncTask = new GetTagListAysncTask(
                handler,
                ApplicationModel.getTagConcernedAddress(),
                REQUEST_GET_CONCERNED_TAGS);
        getTagListAysncTask.execute();
    }

    private void intiView() {
        //结束本页面功能实现
        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //获取页面控件
        tagListView = (ListView)findViewById(R.id.listView);
        //设置适配器
        tagAdapter = new TagListAdapter(this,R.layout.item_tags,tags,this);
        tagListView.setAdapter(tagAdapter);
    }

    @Override
    public void click(View view) {
        Integer position = (Integer) view.getTag();
        TagModel tag = tags.get(position);
        switch (view.getId()){
            case R.id.checkboxTag:
                //更新页面显示
                tag.follow = !tag.follow;
                tagAdapter.notifyDataSetChanged();
                // 课程关注/取消操作
                if(tag.follow){
                    // 关注课程操作
                    CommonAysncTask commonAysncTask = new CommonAysncTask(
                            handler,
                            ApplicationModel.getAddConcernAddress(),
                            "labelId=" + tag.getId() + "&labelName=" + tag.getName(),
                            REQUEST_ADD_CONCERED_TAGS);
                    commonAysncTask.execute();
                }
                else {
                    // 取消关注课程操作
                    CommonAysncTask commonAysncTask = new CommonAysncTask(
                            handler,
                            ApplicationModel.getRemoveConcernAddress(),
                            "labelId=" + tag.getId(),
                            REQUEST_REMOVE_CONCERED_TAGS);
                    commonAysncTask.execute();
                }
                break;
        }
    }

    /**
     * 显示信息函数
     * @param msg
     */
    private void showMessage(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
