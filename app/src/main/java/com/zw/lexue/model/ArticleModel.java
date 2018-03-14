package com.zw.lexue.model;

/**
 * 文章数据类，存放首页列表相关数据
 * Created by Keshi Smith on 2017/7/14.
 */

public class ArticleModel {

    private Integer id;                 //文章ID
    private Integer accountId;          //账户ID
    private String accountName;         //账户名
    private String accountHeadFileId;   //头像文件ID
    private String title;               //文章标题
    private String contentFileId;       //文章内容文件ID
    private String label;               //标签
    private String attachFileId;        //附件文件ID
    private Integer commentCount;       //评论数量
    private Integer favourCount;        //收藏数量
    private String releaseTime;         //发布时间
    private Boolean favoured;           //是否收藏

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountHeadFileId() {
        return accountHeadFileId;
    }

    public void setAccountHeadFileId(String accountHeadFileId) {
        this.accountHeadFileId = accountHeadFileId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentFileId() {
        return contentFileId;
    }

    public void setContentFileId(String contentFileId) {
        this.contentFileId = contentFileId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAttachFileId() {
        return attachFileId;
    }

    public void setAttachFileId(String attachFileId) {
        this.attachFileId = attachFileId;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getFavourCount() {
        return favourCount;
    }

    public void setFavourCount(Integer favourCount) {
        this.favourCount = favourCount;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public Boolean getFavoured() {
        return favoured;
    }

    public void setFavoured(Boolean favoured) {
        this.favoured = favoured;
    }
}
