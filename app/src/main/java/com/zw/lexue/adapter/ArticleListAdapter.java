package com.zw.lexue.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zw.lexue.R;
import com.zw.lexue.activitys.MainActivity;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.model.ArticleModel;
import com.zw.lexue.utils.DownloadAysncTask;

import java.io.File;
import java.util.List;

/**
 * Created by Keshi Smith on 2017/7/14.
 */

public class ArticleListAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private Handler handler;
    private int resourceId;
    private List<ArticleModel> articles;
    private Callback callback;

    public interface Callback{
        void click(View view);
    }

    public ArticleListAdapter(Context context, Handler handler, int resourceId, List<ArticleModel> articles,
                              Callback callback) {
        this.context=context;
        this.handler =handler;
        this.resourceId=resourceId;
        this.articles=articles;
        this.callback=callback;
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public ArticleModel getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view=convertView;
        if(view==null){
            view=LayoutInflater.from(context).inflate(resourceId,null);
        }
        //获取Item中的布局控件
        LinearLayout llArticle=(LinearLayout)view.findViewById(R.id.llArticle);
        LinearLayout llCollect=(LinearLayout)view.findViewById(R.id.llCollect);
        LinearLayout llReview=(LinearLayout)view.findViewById(R.id.llReview);
        RoundedImageView portrait=(RoundedImageView)view.findViewById(R.id.portrait);
        TextView userName=(TextView)view.findViewById(R.id.userName);
        TextView publishedTime=(TextView)view.findViewById(R.id.publishedTime);
        TextView tag=(TextView)view.findViewById(R.id.tag);
        TextView title=(TextView)view.findViewById(R.id.title);
        ImageView collect=(ImageView)view.findViewById(R.id.collect);
        TextView collectCount=(TextView)view.findViewById(R.id.collectCount);
        TextView reviewCount=(TextView)view.findViewById(R.id.reviewCount);
        //设置控件监听器
        llArticle.setTag(position);
        llArticle.setOnClickListener(this);
        llCollect.setTag(position);
        llCollect.setOnClickListener(this);
        llReview.setTag(position);
        llReview.setOnClickListener(this);
        portrait.setTag(position);
        portrait.setOnClickListener(this);
        tag.setTag(position);
        tag.setOnClickListener(this);
        //设置Item控件属性值
        ArticleModel article=getItem(position);
        //加载头像文件
        String headFileId = article.getAccountHeadFileId();
        if(headFileId != null){
            File file = new File(ApplicationModel.getCacheImagePath(), headFileId);
            if(file.exists()){
                //若头像文件已存在，则加载头像
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                portrait.setImageBitmap(bitmap);
            }
            else {
                //若头像文件不存在，则加载默认头像
                portrait.setImageResource(R.drawable.no_image);
                //同时下载头像
                DownloadAysncTask downloadAysncTask = new DownloadAysncTask(
                        handler,
                        ApplicationModel.getFileDownloadAddress(),
                        ApplicationModel.getCacheImagePath(),
                        headFileId,
                        MainActivity.REQUEST_DOWNLOAD_ARTICLE_PORTRAIT);
                downloadAysncTask.execute();
            }
        }
        //加载其他属性
        userName.setText(article.getAccountName());
        publishedTime.setText(article.getReleaseTime());
        tag.setText(article.getLabel());
        title.setText(article.getTitle());
        collect.setSelected(article.getFavoured());
        collectCount.setText(Integer.toString(article.getFavourCount()));
        reviewCount.setText(Integer.toString(article.getCommentCount()));
        return view;
    }

    @Override
    public void onClick(View view) {
        //回调Activity中的click函数
        callback.click(view);
    }
}
