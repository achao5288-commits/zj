package com.yq.spbYq.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康设备实体类
 */
@Data
public class HealthDevice implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer deviceId;
    private Integer userId;
    private String deviceName;
    private String deviceType;
    private String deviceBrand;
    private String deviceModel;
    private String deviceSn;
    private String connectionType;
    private Integer isActive;
    private LocalDateTime lastSyncTime;
    private LocalDateTime bindTime;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
