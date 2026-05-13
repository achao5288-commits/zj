package com.yq.spbYq.domain;

import lombok.Data;
import java.io.Serializable;

/**
 * 健康预警Domain类
 */
@Data
public class HealthAlertDomain implements Serializable {
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
    private String notifyTime;
    private String createTime;
    private String handleTime;
}
