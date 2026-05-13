package com.yq.spbYq.domain;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 健康监测数据Domain类
 */
@Data
public class HealthMonitoringDataDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer monitorId;
    private Integer userId;
    private Integer deviceId;
    private String dataType;
    private BigDecimal dataValue;
    private String dataUnit;
    private BigDecimal systolic;
    private BigDecimal diastolic;
    private String measurementTime;
    private Integer isAbnormal;
    private String abnormalLevel;
    private String notes;
    private String syncStatus;
    private String createTime;
}
