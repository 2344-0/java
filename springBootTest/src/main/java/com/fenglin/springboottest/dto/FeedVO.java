package com.fenglin.springboottest.dto;

/**
 * 斗者动态展示 VO：在原动态内容基础上补充「片刻前 / 昨日」这类相对时间文案
 */
public class FeedVO {

    /** 发布者（斗者名） */
    private String name;

    /** 相对时间文案，如：片刻前 / 半个时辰前 / 昨日 */
    private String time;

    /** 动态内容 */
    private String text;

    public FeedVO() {
    }

    public FeedVO(String name, String time, String text) {
        this.name = name;
        this.time = time;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
