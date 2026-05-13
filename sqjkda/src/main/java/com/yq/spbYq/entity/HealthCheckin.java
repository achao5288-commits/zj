package com.yq.spbYq.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 健康打卡实体类
 */
@Data
public class HealthCheckin implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer checkinId;
    private Integer userId;
    private LocalDate checkinDate;
    private LocalTime checkinTime;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
