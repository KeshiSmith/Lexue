package com.zw.lexue.model;

/**
 * 横幅数据类-本类用于横幅控件测试，需要后期完善。
 * Created by Keshi Smith on 2017/7/13.
 */

public class BannerModel {
    private String title;
    private String bannerFileId;

    public BannerModel(String title, String bannerFileId){
        setTitle(title);
        setBannerFileId(bannerFileId);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBannerFileId() {
        return bannerFileId;
    }

    public void setBannerFileId(String bannerFileId) {
        this.bannerFileId = bannerFileId;
    }
}
