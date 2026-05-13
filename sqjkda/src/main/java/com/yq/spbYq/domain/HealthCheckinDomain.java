package com.yq.spbYq.domain;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 健康打卡Domain类
 */
@Data
public class HealthCheckinDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer checkinId;
    private Integer userId;
    private String checkinDate;
    private String checkinTime;
    private String overallFeeling;
    private String sleepQuality;
    private BigDecimal sleepHours;
    private String moodStatus;
    private String exerciseStatus;
    private Integer exerciseMinutes;
    private String dietStatus;
    private Integer waterIntake;
    private BigDecimal temperature;
    private BigDecimal weight;
    private String bloodPressure;
    private Integer heartRate;
    private String symptoms;
    private Integer medicationStatus;
    private String notes;
    private String createTime;
    private String updateTime;
}
