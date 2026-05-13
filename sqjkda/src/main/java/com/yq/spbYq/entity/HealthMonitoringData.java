package com.yq.spbYq.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康监测数据实体类
 */
@Data
public class HealthMonitoringData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer monitorId;
    private Integer userId;
    private Integer deviceId;
    private String dataType;
    private BigDecimal dataValue;
    private String dataUnit;
    private BigDecimal systolic;
    private BigDecimal diastolic;
    private LocalDateTime measurementTime;
    private Integer isAbnormal;
    private String abnormalLevel;
    private String notes;
    private String syncStatus;
    private LocalDateTime createTime;
}
