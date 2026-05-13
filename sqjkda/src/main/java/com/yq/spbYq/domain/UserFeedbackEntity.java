package com.yq.spbYq.domain;

import java.io.Serializable;

import lombok.Data;

/**
 * (UserFeedback)实体类
 *
 * @author makejava
 * @since 2025-05-11 10:57:07
 */
@Data
public class UserFeedbackEntity implements Serializable {
    private static final long serialVersionUID = 669525930889428436L;

    /**
     * 统一主键ID（用于前端统一操作）
     */
    private Integer id;

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

    /**
     * 获取统一ID（映射到feedbackId）
     */
    public Integer getId() {
        return feedbackId;
    }

    /**
     * 设置统一ID（映射到feedbackId）
     */
    public void setId(Integer id) {
        this.id = id;
        this.feedbackId = id;
    }

}

