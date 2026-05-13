package com.yq.spbYq.domain;

import java.io.Serializable;

import lombok.Data;

/**
 * (HealthReminders)实体类
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@Data
public class HealthRemindersEntity implements Serializable {
    private static final long serialVersionUID = -43154396586684263L;

    /**
     * 统一主键ID（用于前端统一操作）
     */
    private Integer id;

    private Integer reminderId;

    private Integer userId;
    /**
     * 关联的健康记录ID
     */
    private Integer recordId;

    private Object reminderType;
    /**
     * 提醒名称(如"服用降压药")
     */
    private String reminderName;
    /**
     * 应完成日期
     */
    private String dueDate;
    /**
     * 具体时间(如用药时间)
     */
    private String dueTime;

    private Object status;

    private Object priority;
    /**
     * 重复模式，如"DAILY", "WEEKLY", "MONTHLY"
     */
    private String recurrencePattern;
    /**
     * 重复结束日期
     */
    private String recurrenceEnd;
    /**
     * 附加说明
     */
    private String notes;

    private String createdAt;

    private String updatedAt;
    /**
     * 实际完成时间
     */
    private String completedAt;

    /**
     * 获取统一ID（映射到reminderId）
     */
    public Integer getId() {
        return reminderId;
    }

    /**
     * 设置统一ID（映射到reminderId）
     */
    public void setId(Integer id) {
        this.id = id;
        this.reminderId = id;
    }

}

