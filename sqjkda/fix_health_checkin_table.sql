-- ============================================
-- 修复 health_checkin 表
-- ============================================

USE zhao;

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS `health_checkin`;

-- 重新创建表
CREATE TABLE `health_checkin` (
  `checkin_id` INT NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `checkin_date` DATE NOT NULL COMMENT '打卡日期',
  `checkin_time` TIME DEFAULT CURRENT_TIME() COMMENT '打卡时间',
  `overall_feeling` VARCHAR(50) COMMENT '整体感受(良好/一般/不适)',
  `sleep_quality` VARCHAR(20) COMMENT '睡眠质量(优秀/良好/一般/差)',
  `sleep_hours` DECIMAL(3,1) COMMENT '睡眠时长(小时)',
  `mood_status` VARCHAR(20) COMMENT '心情状态(愉快/平静/焦虑/抑郁)',
  `exercise_status` VARCHAR(20) COMMENT '运动状态(无运动/轻度/中度/重度)',
  `exercise_minutes` INT COMMENT '运动时长(分钟)',
  `diet_status` VARCHAR(20) COMMENT '饮食状态(正常/偏少/偏多)',
  `water_intake` INT COMMENT '饮水量(毫升)',
  `temperature` DECIMAL(4,1) COMMENT '体温',
  `weight` DECIMAL(5,2) COMMENT '体重(kg)',
  `blood_pressure` VARCHAR(50) COMMENT '血压',
  `heart_rate` INT COMMENT '心率',
  `symptoms` TEXT COMMENT '症状描述',
  `medication_status` TINYINT DEFAULT 0 COMMENT '是否按时服药(1是/0否)',
  `notes` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`checkin_id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`),
  KEY `idx_checkin_date` (`checkin_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康打卡表';

-- 验证表结构
DESC health_checkin;

SELECT '✅ health_checkin 表创建成功！' AS status;
