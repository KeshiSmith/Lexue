package com.zw.lexue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zw.lexue.R;
import com.zw.lexue.model.TagModel;

import java.util.List;

/**
 * 标签ListView适配器
 * Created by Keshi Smith on 2017/10/27.
 */

public class TagListAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private int resourceId;
    private List<TagModel> tags;
    private Callback callback;

    public interface Callback{
        void click(View view);
    }

    public TagListAdapter(Context context, int resourceId, List<TagModel> tags, Callback callback) {
        this.context = context;
        this.resourceId = resourceId;
        this.tags = tags;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public TagModel getItem(int position) {
        return tags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view=convertView;
        if(view==null){
            view= LayoutInflater.from(context).inflate(resourceId,null);
        }
        //获取Item布局中的控件
        TextView tagName = (TextView)view.findViewById(R.id.textViewTag);
        CheckBox tagChecked = (CheckBox)view.findViewById(R.id.checkboxTag);
        //设置控件监听器
        tagChecked.setTag(position);
        tagChecked.setOnClickListener(this);
        //设置Item控件属性值
        TagModel tag = getItem(position);
        tagName.setText(tag.getName());
        tagChecked.setChecked(tag.follow);
        if(tag.follow){
            tagChecked.setText("已关注");
        }
        else{
            tagChecked.setText("未关注");
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        //回调Activity中的click函数
        callback.click(view);
    }
}
