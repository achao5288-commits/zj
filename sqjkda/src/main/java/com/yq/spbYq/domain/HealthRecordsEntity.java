package com.yq.spbYq.domain;

import java.io.Serializable;

import lombok.Data;

/**
 * (HealthRecords)实体类
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@Data
public class HealthRecordsEntity implements Serializable {
    private static final long serialVersionUID = 490858546547736510L;

    /**
     * 统一主键ID（用于前端统一操作）
     */
    private Integer id;

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

    /**
     * 获取统一ID（映射到recordId）
     */
    public Integer getId() {
        return recordId;
    }

    /**
     * 设置统一ID（映射到recordId）
     */
    public void setId(Integer id) {
        this.id = id;
        this.recordId = id;
    }

}

