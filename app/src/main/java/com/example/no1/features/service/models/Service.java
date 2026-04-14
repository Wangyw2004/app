package com.example.no1.features.service.models;

import java.util.Date;
import java.util.List;

public class Service {
    private String id;
    private String type;           // "complaint" 或 "repair"
    private String category;       // 分类
    private String title;
    private String description;
    private List<String> images;
    private String contactPhone;
    private String status;         // pending, processing, completed, rejected
    private Date createTime;
    private Date updateTime;
    private String userId;
    private String userName;
    private List<Progress> progressList;

    // 报修特有字段
    private String urgency;        // normal, urgent, very_urgent
    private String expectedTime;
    private String remark;

    public static class Progress {
        private String content;
        private Date createTime;
        private String operator;

        public Progress(String content, String operator) {
            this.content = content;
            this.createTime = new Date();
            this.operator = operator;
        }

        // Getters and Setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public List<Progress> getProgressList() { return progressList; }
    public void setProgressList(List<Progress> progressList) { this.progressList = progressList; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getExpectedTime() { return expectedTime; }
    public void setExpectedTime(String expectedTime) { this.expectedTime = expectedTime; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getStatusText() {
        switch (status) {
            case "pending": return "待受理";
            case "processing": return "处理中";
            case "completed": return "已完成";
            case "rejected": return "已拒绝";
            default: return "未知";
        }
    }

    public int getStatusColor() {
        switch (status) {
            case "pending": return android.R.color.holo_orange_dark;
            case "processing": return android.R.color.holo_blue_dark;
            case "completed": return android.R.color.holo_green_dark;
            case "rejected": return android.R.color.holo_red_dark;
            default: return android.R.color.darker_gray;
        }
    }
}