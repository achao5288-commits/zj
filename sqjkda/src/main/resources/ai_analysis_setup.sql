-- ============================================
-- AI 健康分析模块 - 数据库快速部署脚本
-- 适用环境：MySQL 8.0+
-- 执行方式：mysql -u root -p < ai_analysis_setup.sql
-- ============================================

-- 请选择您的数据库（将 spb_yd 替换为您的实际数据库名）
-- USE spb_yd;

-- 1. 创建 AI 健康分析表
DROP TABLE IF EXISTS `ai_health_analysis`;
CREATE TABLE `ai_health_analysis` (
  `analysis_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户 ID',
  `record_id` int DEFAULT NULL COMMENT '健康记录 ID',
  `analysis_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '分析类型',
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '风险等级',
  `health_score` decimal(5,2) DEFAULT NULL COMMENT '健康评分',
  `abnormal_indicators` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '异常指标',
  `ai_suggestion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT 'AI 建议',
  `disease_risk_prediction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '疾病风险预测',
  `lifestyle_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '生活方式建议',
  `diet_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '饮食建议',
  `exercise_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '运动建议',
  `medical_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '医疗建议',
  `analysis_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '分析摘要',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`analysis_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_record_id` (`record_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_ai_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ai_record` FOREIGN KEY (`record_id`) REFERENCES `health_records` (`record_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=Dynamic;

-- 2. 插入测试数据（可选）
-- 注意：确保系统中已存在 user_id=1 和 record_id=1 的数据
-- 如果插入失败，可以跳过此步骤，不影响正常使用

SET NAMES utf8mb4;

INSERT INTO `ai_health_analysis` (`user_id`, `record_id`, `analysis_type`, `risk_level`, `health_score`, `abnormal_indicators`, `ai_suggestion`, `disease_risk_prediction`, `lifestyle_advice`, `diet_advice`, `exercise_advice`, `medical_advice`, `analysis_summary`, `create_time`) 
VALUES 
(1, 1, '体检报告', '低风险', 88.50, '无明显异常', '您的健康状况整体良好，请继续保持良好的生活习惯。', '当前未发现明显疾病风险，建议保持健康生活方式', '1. 保持规律作息，每天保证 7-8 小时睡眠; 2. 避免熬夜和过度劳累; 3. 保持心情舒畅，适当减压; 4. 培养兴趣爱好，丰富业余生活。', '1. 均衡饮食，多吃蔬菜水果; 2. 控制油盐糖摄入; 3. 多喝水，每日饮水量 1500-2000ml。', '建议每周进行 150 分钟中等强度有氧运动，如快走、游泳、骑自行车; 配合 2-3 次力量训练，增强肌肉力量。', '建议每年进行一次常规体检; 关注身体变化，出现异常及时就诊。', '本次体检报告分析显示，您的健康评分为 88.5 分，风险等级为低风险。请继续保持良好的健康状态。', NOW());

-- 3. 验证表创建成功
SELECT 'AI 健康分析表创建成功！' as status;
SHOW TABLES LIKE 'ai_health_analysis';

-- 4. 查看表结构
DESC `ai_health_analysis`;
