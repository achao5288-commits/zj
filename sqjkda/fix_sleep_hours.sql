-- ============================================
-- 修复 health_checkin 表字段范围问题
-- ============================================

USE zhao;

-- 修改 sleep_hours 字段，扩大范围
-- DECIMAL(4,1) 表示最大值 999.9 小时（足够用了）
ALTER TABLE `health_checkin` 
MODIFY COLUMN `sleep_hours` DECIMAL(4,1) COMMENT '睡眠时长(小时)';

-- 修改 sleep_hours 字段允许NULL值
ALTER TABLE `health_checkin` 
MODIFY COLUMN `sleep_hours` DECIMAL(4,1) NULL COMMENT '睡眠时长(小时)';

-- 验证修改结果
SHOW COLUMNS FROM `health_checkin` LIKE 'sleep_hours';

SELECT '✅ sleep_hours 字段修复完成！' AS status;
