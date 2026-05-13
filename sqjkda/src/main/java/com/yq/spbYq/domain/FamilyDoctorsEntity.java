package com.yq.spbYq.domain;

import java.io.Serializable;

import lombok.Data;

/**
 * (FamilyDoctors)实体类
 *
 * @author makejava
 * @since 2025-05-11 10:57:07
 */
@Data
public class FamilyDoctorsEntity implements Serializable {
    private static final long serialVersionUID = -87172082568155642L;

    /**
     * 统一主键ID（用于前端统一操作）
     */
    private Integer id;

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

    /**
     * 获取统一ID（映射到doctorId）
     */
    public Integer getId() {
        return doctorId;
    }

    /**
     * 设置统一ID（映射到doctorId）
     */
    public void setId(Integer id) {
        this.id = id;
        this.doctorId = id;
    }

}

