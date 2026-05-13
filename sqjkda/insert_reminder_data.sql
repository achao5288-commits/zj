-- ========================================
-- 档案提醒数据插入脚本
-- 执行日期: 2025-04-18
-- 说明: 为现有用户添加健康提醒数据
-- ========================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 先为现有用户添加健康记录（如果不存在）
-- ========================================

-- 为用户1添加健康记录
INSERT INTO `health_records` (`user_id`, `record_type`, `report_name`, `report_date`, `doctor_name`, `summary`, `is_abnormal`) VALUES
(1, '体检报告', '2025年度常规体检', '2025-01-15', '王医生', '血压略偏高，建议定期监测。', 1);

-- 为用户9添加健康记录
INSERT INTO `health_records` (`user_id`, `record_type`, `report_name`, `report_date`, `doctor_name`, `summary`, `is_abnormal`) VALUES
(9, '血检报告', '血脂检查', '2025-02-20', '李医生', '血脂正常，整体健康良好。', 0);

-- 为用户13添加健康记录
INSERT INTO `health_records` (`user_id`, `record_type`, `report_name`, `report_date`, `doctor_name`, `summary`, `is_abnormal`) VALUES
(13, '体检报告', '高血压专项检查', '2025-03-10', '刘医生', '血压145/92mmHg，需要调整用药。', 1);

-- 为用户18添加健康记录
INSERT INTO `health_records` (`user_id`, `record_type`, `report_name`, `report_date`, `doctor_name`, `summary`, `is_abnormal`) VALUES
(18, 'X光检查', '颈椎X光检查', '2025-03-15', '陈医生', '颈椎轻度退变，建议理疗。', 1);

-- 为用户19添加健康记录
INSERT INTO `health_records` (`user_id`, `record_type`, `report_name`, `report_date`, `doctor_name`, `summary`, `is_abnormal`) VALUES
(19, '血检报告', '血糖检测', '2025-04-01', '赵医生', '空腹血糖6.8mmol/L，略偏高。', 1);

-- 为用户22添加健康记录
INSERT INTO `health_records` (`user_id`, `record_type`, `report_name`, `report_date`, `doctor_name`, `summary`, `is_abnormal`) VALUES
(22, '体检报告', '年度体检', '2025-03-20', '周医生', '整体健康良好，建议保持规律作息。', 0);

-- 为用户23添加健康记录
INSERT INTO `health_records` (`user_id`, `record_type`, `report_name`, `report_date`, `doctor_name`, `summary`, `is_abnormal`) VALUES
(23, '超声检查', '骨密度检查', '2025-04-05', '孙医生', '骨密度正常，建议补充维生素D。', 0);

-- ========================================
-- 为现有用户添加健康提醒数据
-- ========================================

-- 注意：以下record_id需要使用上面插入后实际生成的ID
-- 可以通过 SELECT LAST_INSERT_ID() 或查询获取实际ID
-- 这里假设record_id依次为数据库中最新的记录

-- 为用户1添加提醒（用药提醒）
INSERT INTO `health_reminders` (`user_id`, `record_id`, `reminder_type`, `reminder_name`, `due_date`, `due_time`, `status`, `priority`, `recurrence_pattern`, `notes`) 
SELECT 1, record_id, 'medication', '服用降压药', '2025-04-20', '08:00:00', 'pending', 'high', 'DAILY', '每日早8点服用，每次5mg'
FROM health_records WHERE user_id = 1 ORDER BY record_id DESC LIMIT 1;

-- 为用户9添加提醒（体检提醒）
INSERT INTO `health_reminders` (`user_id`, `record_id`, `reminder_type`, `reminder_name`, `due_date`, `due_time`, `status`, `priority`, `recurrence_pattern`, `notes`) 
SELECT 9, record_id, 'checkup', '年度体检', '2025-05-15', '09:00:00', 'pending', 'medium', NULL, '预约年度常规体检'
FROM health_records WHERE user_id = 9 ORDER BY record_id DESC LIMIT 1;

-- 为用户13添加提醒（复诊提醒）
INSERT INTO `health_reminders` (`user_id`, `record_id`, `reminder_type`, `reminder_name`, `due_date`, `due_time`, `status`, `priority`, `recurrence_pattern`, `notes`) 
SELECT 13, record_id, 'followup', '高血压复诊', '2025-05-01', '10:00:00', 'pending', 'high', NULL, '预约医生复诊，调整降压药剂量'
FROM health_records WHERE user_id = 13 ORDER BY record_id DESC LIMIT 1;

-- 为用户18添加提醒（治疗提醒）
INSERT INTO `health_reminders` (`user_id`, `record_id`, `reminder_type`, `reminder_name`, `due_date`, `due_time`, `status`, `priority`, `recurrence_pattern`, `notes`) 
SELECT 18, record_id, 'therapy', '颈椎理疗', '2025-04-22', '15:00:00', 'pending', 'medium', 'WEEKLY', '每周二下午3点进行颈椎理疗'
FROM health_records WHERE user_id = 18 ORDER BY record_id DESC LIMIT 1;

-- 为用户19添加提醒（检查提醒）
INSERT INTO `health_reminders` (`user_id`, `record_id`, `reminder_type`, `reminder_name`, `due_date`, `due_time`, `status`, `priority`, `recurrence_pattern`, `notes`) 
SELECT 19, record_id, 'test', '血糖检测', '2025-04-21', '07:00:00', 'pending', 'high', 'DAILY', '每日早晨空腹检测血糖并记录'
FROM health_records WHERE user_id = 19 ORDER BY record_id DESC LIMIT 1;

-- 为用户22添加提醒（生活方式提醒 - 使用checkup类型作为健康检查提醒）
INSERT INTO `health_reminders` (`user_id`, `record_id`, `reminder_type`, `reminder_name`, `due_date`, `due_time`, `status`, `priority`, `recurrence_pattern`, `notes`) 
SELECT 22, record_id, 'checkup', '早睡提醒', '2025-04-19', '23:00:00', 'pending', 'low', 'DAILY', '每晚11点前入睡，改善作息'
FROM health_records WHERE user_id = 22 ORDER BY record_id DESC LIMIT 1;

-- 为用户23添加提醒（多重提醒）
INSERT INTO `health_reminders` (`user_id`, `record_id`, `reminder_type`, `reminder_name`, `due_date`, `due_time`, `status`, `priority`, `recurrence_pattern`, `notes`) 
SELECT 23, record_id, 'medication', '服用维生素D', '2025-04-20', '09:00:00', 'pending', 'medium', 'DAILY', '每日早9点服用，每次1000IU'
FROM health_records WHERE user_id = 23 ORDER BY record_id DESC LIMIT 1;

INSERT INTO `health_reminders` (`user_id`, `record_id`, `reminder_type`, `reminder_name`, `due_date`, `due_time`, `status`, `priority`, `recurrence_pattern`, `notes`) 
SELECT 23, record_id, 'checkup', '骨密度检查', '2025-06-15', '10:00:00', 'pending', 'medium', NULL, '半年一次骨密度检查'
FROM health_records WHERE user_id = 23 ORDER BY record_id DESC LIMIT 1;

-- ========================================
-- 数据插入完成
-- ========================================
-- 说明:
-- 1. 所有ID字段均由数据库AUTO_INCREMENT自动生成，无需手动指定
-- 2. 插入顺序考虑了外键约束关系
-- 3. 提醒类型包括: medication(用药), checkup(体检), test(检测), therapy(治疗), followup(复诊)
-- 4. 优先级包括: low(低), medium(中), high(高), critical(紧急)
-- 5. 状态包括: pending(待处理), completed(已完成), snoozed(暂缓), skipped(跳过)
-- ========================================

SET FOREIGN_KEY_CHECKS = 1;

-- ========================================
-- 验证数据插入（可选执行）
-- ========================================
-- SELECT COUNT(*) as '健康提醒总数' FROM health_reminders;
-- SELECT * FROM health_reminders ORDER BY user_id, due_date;
