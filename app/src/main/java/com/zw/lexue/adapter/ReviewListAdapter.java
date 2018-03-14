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
import com.zw.lexue.activitys.ReviewActivity;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.model.ArticleModel;
import com.zw.lexue.model.ReviewModel;
import com.zw.lexue.utils.DownloadAysncTask;

import java.io.File;
import java.util.List;

/**
 * Created by Keshi Smith on 2017/7/14.
 */

public class ReviewListAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private Handler handler;
    private int resourceId;
    private List<ReviewModel> reviews;
    private Callback callback;

    public interface Callback{
        void click(View view);
    }

    public ReviewListAdapter(Context context, Handler handler, int resourceId, List<ReviewModel> reviews,
                             Callback callback) {
        this.context=context;
        this.handler =handler;
        this.resourceId=resourceId;
        this.reviews=reviews;
        this.callback=callback;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public ReviewModel getItem(int position) {
        return reviews.get(position);
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
        LinearLayout llReview=(LinearLayout)view.findViewById(R.id.llReview);
        RoundedImageView portrait=(RoundedImageView)view.findViewById(R.id.portrait);
        TextView userName=(TextView)view.findViewById(R.id.userName);
        TextView publishedTime=(TextView)view.findViewById(R.id.publishedTime);
        TextView reviewContent =(TextView)view.findViewById(R.id.tvReview);
        //设置控件监听器
        llReview.setTag(position);
        llReview.setOnClickListener(this);
        //设置Item控件属性值
        ReviewModel article=getItem(position);
        //加载头像文件
        String headFileId = article.getCommenterHeadFileId();
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
                        ReviewActivity.REQUEST_DOWNLOAD_REVIEW_PORTRAIT);
                downloadAysncTask.execute();
            }
        }
        //加载其他属性
        userName.setText(article.getCommenterName());
        publishedTime.setText(article.getReleaseTime());
        reviewContent.setText(article.getComment());
        return view;
    }

    @Override
    public void onClick(View view) {
        //回调Activity中的click函数
        callback.click(view);
    }
}
