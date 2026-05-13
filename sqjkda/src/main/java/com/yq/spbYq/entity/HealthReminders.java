package com.yq.spbYq.entity;

import java.io.Serializable;

/**
 * (HealthReminders)实体类
 *
 * @author makejava
 * @since 2025-05-11 10:26:05
 */
public class HealthReminders implements Serializable {
    private static final long serialVersionUID = -15786277242753702L;

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


    public Integer getReminderId() {
        return reminderId;
    }

    public void setReminderId(Integer reminderId) {
        this.reminderId = reminderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Object getReminderType() {
        return reminderType;
    }

    public void setReminderType(Object reminderType) {
        this.reminderType = reminderType;
    }

    public String getReminderName() {
        return reminderName;
    }

    public void setReminderName(String reminderName) {
        this.reminderName = reminderName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Object getPriority() {
        return priority;
    }

    public void setPriority(Object priority) {
        this.priority = priority;
    }

    public String getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public String getRecurrenceEnd() {
        return recurrenceEnd;
    }

    public void setRecurrenceEnd(String recurrenceEnd) {
        this.recurrenceEnd = recurrenceEnd;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

}

