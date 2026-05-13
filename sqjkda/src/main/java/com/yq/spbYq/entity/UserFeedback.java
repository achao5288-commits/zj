package com.yq.spbYq.entity;

import java.io.Serializable;

/**
 * (UserFeedback)实体类
 *
 * @author makejava
 * @since 2025-05-11 10:26:06
 */
public class UserFeedback implements Serializable {
    private static final long serialVersionUID = -90298096560240270L;

    private Integer feedbackId;
    /**
     * 提交用户ID
     */
    private Integer userId;

    private Object feedbackType;
    /**
     * 反馈标题
     */
    private String title;
    /**
     * 反馈内容
     */
    private String content;

    private Object status;
    /**
     * 管理员处理意见
     */
    private String adminNotes;
    /**
     * 回复内容
     */
    private String response;

    private String ipAddress;

    private String deviceInfo;

    private String appVersion;

    private String createdAt;

    private String updatedAt;


    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Object getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(Object feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}

