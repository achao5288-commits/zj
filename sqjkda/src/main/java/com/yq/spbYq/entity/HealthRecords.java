package com.yq.spbYq.entity;

import java.io.Serializable;

/**
 * (HealthRecords)实体类
 *
 * @author makejava
 * @since 2025-05-11 10:26:05
 */
public class HealthRecords implements Serializable {
    private static final long serialVersionUID = 167081260257145549L;

    private Integer recordId;

    private Integer userId;
    /**
     * 报告类型，如体检报告、血检报告等
     */
    private String recordType;

    private String reportName;

    private String reportDate;

    private String doctorName;
    /**
     * 报告摘要
     */
    private String summary;
    /**
     * 是否有异常指标
     */
    private Integer isAbnormal;

    private String uploadTime;

    private String lastViewed;


    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getIsAbnormal() {
        return isAbnormal;
    }

    public void setIsAbnormal(Integer isAbnormal) {
        this.isAbnormal = isAbnormal;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getLastViewed() {
        return lastViewed;
    }

    public void setLastViewed(String lastViewed) {
        this.lastViewed = lastViewed;
    }

}

