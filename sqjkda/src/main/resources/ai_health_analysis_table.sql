-- AI 健康分析表
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
