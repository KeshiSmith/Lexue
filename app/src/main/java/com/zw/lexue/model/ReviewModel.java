package com.zw.lexue.model;

/**
 * 评论数据类
 * Created by Keshi Smith on 2017/10/28.
 */

public class ReviewModel {
    private Integer id;                 //评论Id
    private Integer articleId;          //文章Id
    private Integer commenterAccountId; //评论者Id
    private String commenterHeadFileId;//评论者头像Id
    private String commenterName;       //评论者名字
    private String comment;             //评论内容
    private String releaseTime;         //发布时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getCommenterAccountId() {
        return commenterAccountId;
    }

    public void setCommenterAccountId(Integer commenterAccountId) {
        this.commenterAccountId = commenterAccountId;
    }

    public String getCommenterHeadFileId() {
        return commenterHeadFileId;
    }

    public void setCommenterHeadFileId(String commenterHeadFileId) {
        this.commenterHeadFileId = commenterHeadFileId;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }
}
