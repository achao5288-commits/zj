package com.yq.spbYq.entity;

import java.io.Serializable;

/**
 * (FamilyDoctors)实体类
 *
 * @author makejava
 * @since 2025-05-11 10:26:05
 */
public class FamilyDoctors implements Serializable {
    private static final long serialVersionUID = -66147806221587956L;

    private Integer doctorId;
    /**
     * 关联的用户ID
     */
    private Integer userId;
    /**
     * 主要专业领域
     */
    private String specialty;
    /**
     * 是否可预约
     */
    private Integer isAvailable;
    /**
     * 平均评分(0-5)
     */
    private Double averageRating;
    /**
     * 服务患者总数
     */
    private Integer totalPatients;
    /**
     * 已完成预约数
     */
    private Integer completedAppointments;

    private String createdAt;

    private String updatedAt;

    private String licenseNumber;


    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(Integer totalPatients) {
        this.totalPatients = totalPatients;
    }

    public Integer getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(Integer completedAppointments) {
        this.completedAppointments = completedAppointments;
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

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

}

