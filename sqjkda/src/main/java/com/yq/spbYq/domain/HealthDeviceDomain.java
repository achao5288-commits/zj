package com.yq.spbYq.domain;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康设备Domain类
 */
@Data
public class HealthDeviceDomain implements Serializable {
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
    private String lastSyncTime;
    private String bindTime;
    private String status;
    private String createTime;
    private String updateTime;
}
