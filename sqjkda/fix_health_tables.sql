-- ============================================
-- 一键修复脚本 - 创建居家健康监测模块所需的所有表
-- 数据库: zhao
-- ============================================

USE zhao;

-- 1. 健康设备表
CREATE TABLE IF NOT EXISTS `health_devices` (
  `device_id` INT NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `device_name` VARCHAR(100) NOT NULL COMMENT '设备名称',
  `device_type` VARCHAR(50) NOT NULL COMMENT '设备类型(手环/血压计/血糖仪/体脂秤等)',
  `device_brand` VARCHAR(100) COMMENT '设备品牌',
  `device_model` VARCHAR(100) COMMENT '设备型号',
  `device_sn` VARCHAR(100) COMMENT '设备序列号',
  `connection_type` VARCHAR(50) COMMENT '连接方式(Bluetooth/WiFi/USB)',
  `is_active` TINYINT DEFAULT 1 COMMENT '是否启用(1启用/0禁用)',
  `last_sync_time` DATETIME COMMENT '最后同步时间',
  `bind_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `status` VARCHAR(20) DEFAULT 'online' COMMENT '设备状态(online/offline)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`device_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_device_type` (`device_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康设备表';

-- 2. 健康监测数据表
CREATE TABLE IF NOT EXISTS `health_monitoring_data` (
  `monitor_id` INT NOT NULL AUTO_INCREMENT COMMENT '监测ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `device_id` INT COMMENT '设备ID(可为空,支持手动录入)',
  `data_type` VARCHAR(50) NOT NULL COMMENT '数据类型(heart_rate/blood_pressure/blood_glucose/weight/temperature/oxygen/spo2等)',
  `data_value` DECIMAL(10,2) NOT NULL COMMENT '数据值',
  `data_unit` VARCHAR(20) COMMENT '数据单位',
  `systolic` DECIMAL(10,2) COMMENT '收缩压(血压专用)',
  `diastolic` DECIMAL(10,2) COMMENT '舒张压(血压专用)',
  `measurement_time` DATETIME NOT NULL COMMENT '测量时间',
  `is_abnormal` TINYINT DEFAULT 0 COMMENT '是否异常(1异常/0正常)',
  `abnormal_level` VARCHAR(20) COMMENT '异常等级(low/medium/high)',
  `notes` TEXT COMMENT '备注说明',
  `sync_status` VARCHAR(20) DEFAULT 'synced' COMMENT '同步状态(synced/pending/failed)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`monitor_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_data_type` (`data_type`),
  KEY `idx_measurement_time` (`measurement_time`),
  KEY `idx_is_abnormal` (`is_abnormal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康监测数据表';

-- 3. 健康打卡表
CREATE TABLE IF NOT EXISTS `health_checkin` (
  `checkin_id` INT NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `checkin_date` DATE NOT NULL COMMENT '打卡日期',
  `checkin_time` TIME DEFAULT CURRENT_TIME COMMENT '打卡时间',
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

-- 4. 健康预警表
CREATE TABLE IF NOT EXISTS `health_alerts` (
  `alert_id` INT NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `monitor_id` INT COMMENT '监测数据ID',
  `alert_type` VARCHAR(50) NOT NULL COMMENT '预警类型(device_abnormal/checkin_missed/trend_warning)',
  `alert_level` VARCHAR(20) NOT NULL COMMENT '预警级别(info/warning/critical)',
  `alert_title` VARCHAR(200) NOT NULL COMMENT '预警标题',
  `alert_content` TEXT NOT NULL COMMENT '预警内容',
  `data_type` VARCHAR(50) COMMENT '相关数据类型',
  `data_value` VARCHAR(100) COMMENT '相关数据值',
  `normal_range` VARCHAR(100) COMMENT '正常范围',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读(1已读/0未读)',
  `is_handled` TINYINT DEFAULT 0 COMMENT '是否已处理(1已处理/0未处理)',
  `handle_result` TEXT COMMENT '处理结果',
  `notify_user` TINYINT DEFAULT 1 COMMENT '是否通知用户(1是/0否)',
  `notify_doctor` TINYINT DEFAULT 0 COMMENT '是否通知医生(1是/0否)',
  `notify_time` DATETIME COMMENT '通知时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handle_time` DATETIME COMMENT '处理时间',
  PRIMARY KEY (`alert_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_alert_level` (`alert_level`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康预警表';

-- 验证表是否创建成功
SHOW TABLES LIKE 'health_%';

-- 显示表结构
DESC health_devices;
DESC health_monitoring_data;
DESC health_checkin;
DESC health_alerts;

SELECT '✅ 所有表创建成功！' AS status;
