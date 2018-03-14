package com.zw.lexue.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.sivin.BannerAdapter;
import com.zw.lexue.R;
import com.zw.lexue.activitys.MainActivity;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.model.BannerModel;
import com.zw.lexue.utils.DownloadAysncTask;

import java.io.File;
import java.util.List;
import android.os.Handler;

/**
 * 本文档为BannerAdapter模板适配器关于BannerModel具体数据类的实例化。
 * Created by Keshi Smith on 2017/7/14.
 */

public class TheBannerAdapter extends BannerAdapter<BannerModel> {

    private Handler handler;

    public TheBannerAdapter(Handler handler,List dataList) {
        super(dataList);
        this.handler = handler;
    }

    @Override
    protected void bindTips(TextView tv, BannerModel bannerModel) {
        tv.setText(bannerModel.getTitle());
    }

    @Override
    public void bindImage(ImageView imageView, BannerModel bannerModel) {
        String bannerFileId = bannerModel.getBannerFileId();
        if(bannerFileId!=null){
            File file = new File(ApplicationModel.getCacheImagePath(), bannerFileId);
            if(file.exists()){
                //若头像文件已存在，则加载头像
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                imageView.setImageBitmap(bitmap);
            }
            else {
                //若头像文件不存在，则加载默认头像
                imageView.setImageResource(R.drawable.banner);
                //同时下载头像
                DownloadAysncTask downloadAysncTask = new DownloadAysncTask(
                        handler,
                        ApplicationModel.getFileDownloadAddress(),
                        ApplicationModel.getCacheImagePath(),
                        bannerFileId,
                        MainActivity.REQUEST_DOWNLOAD_BANNER);
                downloadAysncTask.execute();
            }
        }
    }
}
