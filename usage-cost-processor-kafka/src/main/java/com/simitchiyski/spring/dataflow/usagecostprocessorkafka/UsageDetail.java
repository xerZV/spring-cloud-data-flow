package com.simitchiyski.spring.dataflow.usagecostprocessorkafka;

public class UsageDetail {
    private String userId;
    private int duration;
    private int data;

    public UsageDetail() {
    }

    public UsageDetail(String userId, int duration, int data) {
        this.userId = userId;
        this.duration = duration;
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
