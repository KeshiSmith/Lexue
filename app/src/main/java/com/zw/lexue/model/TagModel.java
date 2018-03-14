package com.zw.lexue.model;

/**
 * 课程标签数据类
 * Created by Keshi Smith on 2017/10/27.
 */

public class TagModel {
    private Integer id;
    private String name;
    public boolean follow = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
