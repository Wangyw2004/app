package com.example.no1.features.counter.models;

public class CounterModel {
    private int count;
    private long lastUpdateTime;

    public CounterModel() {
        this.count = 0;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public CounterModel(int count, long lastUpdateTime) {
        this.count = count;
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void increment() {
        this.count++;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void reset() {
        this.count = 0;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
}