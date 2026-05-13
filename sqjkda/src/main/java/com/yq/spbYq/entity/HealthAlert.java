package com.yq.spbYq.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 健康预警实体类
 */
@Data
public class HealthAlert implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer alertId;
    private Integer userId;
    private Integer monitorId;
    private String alertType;
    private String alertLevel;
    private String alertTitle;
    private String alertContent;
    private String dataType;
    private String dataValue;
    private String normalRange;
    private Integer isRead;
    private Integer isHandled;
    private String handleResult;
    private Integer notifyUser;
    private Integer notifyDoctor;
    private LocalDateTime notifyTime;
    private LocalDateTime createTime;
    private LocalDateTime handleTime;
}
