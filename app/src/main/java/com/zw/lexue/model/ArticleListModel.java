package com.zw.lexue.model;

import java.util.List;

/**
 * 文章列表类，用于网络获取文章列表
 * Created by Keshi Smith on 2017/10/27.
 */

public class ArticleListModel {
    private List<ArticleModel> articleList;
    private Integer currentPage;
    private Integer totalPage;

    public List<ArticleModel> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<ArticleModel> articleList) {
        this.articleList = articleList;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
