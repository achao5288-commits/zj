-- ========================================
-- 全模块测试数据插入脚本（动态ID版本）
-- 使用MySQL变量自动处理ID关联
-- 所有ID由数据库自动生成 (AUTO_INCREMENT)
-- ========================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 1. 插入测试用户
-- ========================================
INSERT INTO `users` (`username`, `phone_number`, `password_hash`, `user_type`, `user_info`, `user_profile`) VALUES
('test_user1', '13800000001', 'lfjKkThKbfQ+A89bGeVHWA==', 1, '测试用户1，血压偏高', NULL),
('test_user2', '13800000002', 'lfjKkThKbfQ+A89bGeVHWA==', 1, '测试用户2，健康状态', NULL),
('test_admin', '13800000003', 'lfjKkThKbfQ+A89bGeVHWA==', 2, '系统管理员', '管理员档案'),
('elder_zhang', '13800000004', 'lfjKkThKbfQ+A89bGeVHWA==', 1, '张大爷，65岁，退休人员', '高血压、糖尿病患者'),
('young_li', '13800000005', 'lfjKkThKbfQ+A89bGeVHWA==', 1, '小李，28岁，程序员', '亚健康状态，需要调理');

-- 保存生成的用户ID到变量
SET @user_test1 = (SELECT user_id FROM users WHERE username = 'test_user1' LIMIT 1);
SET @user_test2 = (SELECT user_id FROM users WHERE username = 'test_user2' LIMIT 1);
SET @user_admin = (SELECT user_id FROM users WHERE username = 'test_admin' LIMIT 1);
SET @user_elder = (SELECT user_id FROM users WHERE username = 'elder_zhang' LIMIT 1);
SET @user_young = (SELECT user_id FROM users WHERE username = 'young_li' LIMIT 1);

SELECT 
    @user_test1 as 'test_user1的ID',
    @user_test2 as 'test_user2的ID',
    @user_admin as 'test_admin的ID',
    @user_elder as 'elder_zhang的ID',
    @user_young as 'young_li的ID';

-- ========================================
-- 2. 插入健康档案（使用变量关联用户）
-- ========================================
INSERT INTO `health_records` (`user_id`, `record_type`, `report_name`, `report_date`, `doctor_name`, `summary`, `is_abnormal`) VALUES
-- test_user1 的健康档案
(@user_test1, '体检报告', '2025年度常规体检', '2025-01-15', '王医生', '整体健康状况良好，血压略偏高(135/88mmHg)，建议定期监测。血常规、尿常规正常。', 1),
(@user_test1, '血检报告', '血脂四项检查', '2025-02-20', '李医生', '总胆固醇5.8mmol/L，略高于正常值。甘油三酯正常。建议清淡饮食。', 1),
(@user_test1, '心电图', '常规心电图检查', '2025-01-15', '王医生', '窦性心律，心率72次/分，心电图正常。', 0),

-- test_user2 的健康档案
(@user_test2, '体检报告', '入职体检报告', '2025-03-01', '赵医生', '身体健康，各项指标正常，符合入职要求。', 0),
(@user_test2, 'X光检查', '胸部X光检查', '2025-03-01', '赵医生', '双肺纹理清晰，心影正常，膈肌光滑。', 0),

-- elder_zhang 的健康档案 - 老年人，有慢性病
(@user_elder, '体检报告', '2025年第一季度体检', '2025-01-10', '刘医生', '高血压140/95mmHg，空腹血糖7.2mmol/L。建议调整用药，控制饮食。', 1),
(@user_elder, '血检报告', '糖化血红蛋白检测', '2025-02-15', '刘医生', 'HbA1c 7.5%，血糖控制欠佳。建议增加运动，调整降糖药物剂量。', 1),
(@user_elder, '超声检查', '腹部彩超', '2025-01-10', '陈医生', '轻度脂肪肝，肝肾功能正常。建议低脂饮食，适量运动。', 1),
(@user_elder, '眼科检查', '眼底检查', '2025-03-05', '孙医生', '双眼轻度糖尿病视网膜病变，建议控制血糖，定期复查。', 1),

-- young_li 的健康档案 - 年轻人，亚健康
(@user_young, '体检报告', '年度体检', '2025-02-28', '周医生', '颈椎轻度退变，视力下降。建议改善工作姿势，减少用眼疲劳。', 1),
(@user_young, '血检报告', '肝功能检查', '2025-02-28', '周医生', '转氨酶轻度升高，可能与熬夜、饮食不规律有关。建议调整作息。', 1);

-- 保存生成的record_id到变量（按用户分组）
SET @record_test1_1 = (SELECT record_id FROM health_records WHERE user_id = @user_test1 ORDER BY record_id LIMIT 1);
SET @record_test1_2 = (SELECT record_id FROM health_records WHERE user_id = @user_test1 ORDER BY record_id LIMIT 1 OFFSET 1);
SET @record_test2_1 = (SELECT record_id FROM health_records WHERE user_id = @user_test2 ORDER BY record_id LIMIT 1);
SET @record_elder_1 = (SELECT record_id FROM health_records WHERE user_id = @user_elder ORDER BY record_id LIMIT 1);
SET @record_elder_2 = (SELECT record_id FROM health_records WHERE user_id = @user_elder ORDER BY record_id LIMIT 1 OFFSET 1);
SET @record_elder_3 = (SELECT record_id FROM health_records WHERE user_id = @user_elder ORDER BY record_id LIMIT 1 OFFSET 2);
SET @record_elder_4 = (SELECT record_id FROM health_records WHERE user_id = @user_elder ORDER BY record_id LIMIT 1 OFFSET 3);
SET @record_young_1 = (SELECT record_id FROM health_records WHERE user_id = @user_young ORDER BY record_id LIMIT 1);
SET @record_young_2 = (SELECT record_id FROM health_records WHERE user_id = @user_young ORDER BY record_id LIMIT 1 OFFSET 1);

-- ========================================
-- 3. 插入家庭医生（使用变量关联用户）
-- ========================================
INSERT INTO `family_doctors` (`user_id`, `specialty`, `is_available`, `average_rating`, `total_patients`, `completed_appointments`, `license_number`) VALUES
(@user_elder, '内科/慢性病管理', 1, 4.85, 156, 142, '2015-06-15'),  -- 张大爷的家庭医生
(@user_young, '全科/亚健康调理', 1, 4.72, 98, 87, '2018-03-20'),   -- 小李的家庭医生
(@user_test1, '心血管内科', 1, 4.90, 203, 195, '2012-09-10');      -- test_user1的家庭医生

-- ========================================
-- 4. 插入健康提醒（使用变量关联用户和记录）
-- ========================================
INSERT INTO `health_reminders` (`user_id`, `record_id`, `reminder_type`, `reminder_name`, `due_date`, `due_time`, `status`, `priority`, `recurrence_pattern`, `notes`) VALUES
-- elder_zhang 的提醒 - 慢性病需要定期用药和检查
(@user_elder, @record_elder_1, 'medication', '服用降压药（氨氯地平）', '2025-04-20', '08:00:00', 'pending', 'critical', 'DAILY', '每日早8点服用，每次5mg'),
(@user_elder, @record_elder_1, 'medication', '服用降糖药（二甲双胍）', '2025-04-20', '08:30:00', 'pending', 'critical', 'DAILY', '每日早8:30服用，每次500mg，餐后服用'),
(@user_elder, @record_elder_1, 'medication', '服用降糖药（二甲双胍）', '2025-04-20', '18:30:00', 'pending', 'critical', 'DAILY', '每日晚6:30服用，每次500mg，餐后服用'),
(@user_elder, @record_elder_2, 'test', '复查糖化血红蛋白', '2025-05-15', NULL, 'pending', 'high', NULL, '距离上次检查已3个月，需要复查'),
(@user_elder, @record_elder_1, 'followup', '高血压复诊', '2025-05-01', '10:00:00', 'pending', 'high', NULL, '预约刘医生复诊，调整降压药剂量'),
(@user_elder, @record_elder_4, 'checkup', '眼科复查', '2025-06-05', '14:00:00', 'pending', 'medium', NULL, '糖尿病视网膜病变定期复查'),

-- test_user1 的提醒 - 血压监测
(@user_test1, @record_test1_1, 'medication', '服用降压药', '2025-04-20', '09:00:00', 'pending', 'high', 'DAILY', '每日一次，每次10mg'),
(@user_test1, @record_test1_1, 'checkup', '血压自测记录', '2025-04-20', '07:00:00', 'pending', 'medium', 'DAILY', '每日早晨测量血压并记录'),
(@user_test1, @record_test1_2, 'test', '复查血脂', '2025-05-20', NULL, 'pending', 'medium', NULL, '3个月后复查血脂四项'),

-- young_li 的提醒 - 亚健康调理
(@user_young, @record_young_2, 'therapy', '颈椎理疗', '2025-04-22', '15:00:00', 'pending', 'medium', 'WEEKLY', '每周二下午3点进行颈椎理疗'),
(@user_young, @record_young_2, 'checkup', '复查肝功能', '2025-05-28', NULL, 'pending', 'medium', NULL, '1个月后复查肝功能'),
(@user_young, @record_young_1, 'lifestyle', '早睡提醒', '2025-04-19', '23:00:00', 'pending', 'low', 'DAILY', '每晚11点前入睡，改善作息');

-- ========================================
-- 5. 插入用户反馈（使用变量关联用户）
-- ========================================
INSERT INTO `user_feedback` (`user_id`, `feedback_type`, `title`, `content`, `status`, `ip_address`, `device_info`, `app_version`) VALUES
(@user_test1, 'suggestion', '建议增加数据导出功能', '希望能将健康档案导出为PDF格式，方便携带和分享给其他医生。', 'pending', '192.168.1.100', 'Chrome 120 / Windows 10', 'v1.0.0'),
(@user_elder, 'bug', '提醒功能有时不生效', '设置的用药提醒有几次没有弹出通知，担心错过服药时间。', 'acknowledged', '192.168.1.101', 'Safari / iPhone 15', 'v1.0.0'),
(@user_young, 'rating', '系统使用体验良好', '界面简洁，操作方便，健康分析很专业。希望能增加更多运动建议。', 'resolved', '192.168.1.102', 'Chrome 120 / macOS', 'v1.0.0'),
(@user_test2, 'general', '咨询如何使用AI分析', '请问如何使用AI健康分析功能？需要上传什么格式的报告？', 'pending', '192.168.1.103', 'Edge / Windows 11', 'v1.0.0'),
(@user_test1, 'complaint', '页面加载速度慢', '健康档案列表页面加载很慢，特别是图片多的时候。', 'pending', '192.168.1.100', 'Chrome 120 / Windows 10', 'v1.0.0');

-- ========================================
-- 6. 插入用户隐私配置（使用变量关联用户）
-- ========================================
INSERT INTO `user_privacy_profiles` (`user_id`, `profile_visibility`, `health_data_visible`, `contact_info_visible`, `search_indexed`, `two_factor_auth`, `data_encryption_level`, `auto_logout_minutes`, `share_approval_required`, `default_share_expiry_days`) VALUES
(@user_test1, 'friends', 0, 0, 1, 0, 'basic', 30, 1, 7),    -- 普通用户，隐私保护一般
(@user_test2, 'private', 0, 0, 0, 1, 'enhanced', 15, 1, 3), -- 注重隐私，开启双因素认证
(@user_elder, 'friends', 1, 0, 1, 0, 'basic', 30, 1, 7),    -- 老年人，允许家庭医生查看健康数据
(@user_young, 'public', 1, 1, 1, 0, 'basic', 60, 0, 14);    -- 年轻人，开放度高

-- ========================================
-- 7. 插入AI健康分析（使用变量关联用户和记录）
-- ========================================
INSERT INTO `ai_health_analysis` (`user_id`, `record_id`, `analysis_type`, `risk_level`, `health_score`, `abnormal_indicators`, `ai_suggestion`, `disease_risk_prediction`, `lifestyle_advice`, `diet_advice`, `exercise_advice`, `medical_advice`, `analysis_summary`) VALUES
-- elder_zhang 的AI分析 - 高风险，慢性病患者
(@user_elder, @record_elder_1, '综合健康分析', 'high', 62.50, 
 '血压140/95mmHg(偏高)、空腹血糖7.2mmol/L(偏高)、糖化血红蛋白7.5%(偏高)、轻度脂肪肝',
 '您的健康状况需要重点关注。建议严格控制血压和血糖，按时服药，定期复查。',
 '未来5年心血管疾病风险35%，糖尿病并发症风险28%。建议加强管理。',
 '保持规律作息，避免熬夜。每日散步30分钟，保持心情愉悦。避免剧烈运动。',
 '低盐低脂饮食，每日盐摄入<5g。控制碳水化合物摄入，选择低GI食物。多食蔬菜、粗粮。',
 '每日早晚各散步20-30分钟，太极拳等温和运动。避免剧烈运动和重体力劳动。',
 '继续当前降压、降糖治疗方案。1个月后复查血压、血糖。3个月后复查糖化血红蛋白。',
 '高血压、糖尿病控制欠佳，需要调整治疗方案。存在多系统并发症风险，建议加强随访管理。'),

-- test_user1 的AI分析 - 中等风险，血压偏高
(@user_test1, @record_test1_1, '心血管风险评估', 'medium', 75.30,
 '血压135/88mmHg(临界偏高)、总胆固醇5.8mmol/L(偏高)',
 '您的心血管风险处于中等水平。建议改善生活方式，必要时考虑药物治疗。',
 '未来10年心血管疾病风险15%。通过生活方式干预可降低至8%。',
 '减轻工作压力，保证充足睡眠。戒烟限酒，保持心理健康。',
 '减少高脂肪、高胆固醇食物。增加富含Omega-3的食物，如深海鱼。多食新鲜蔬果。',
 '每周至少150分钟中等强度有氧运动，如快走、游泳。配合力量训练。',
 '继续监测血压，如持续偏高建议就诊考虑降压药物。3个月后复查血脂。',
 '血压、血脂轻度异常，通过生活方式干预有望恢复正常。建议定期监测。'),

-- young_li 的AI分析 - 低风险，亚健康状态
(@user_young, @record_young_2, '亚健康状态评估', 'low', 82.00,
 '转氨酶轻度升高、颈椎轻度退变、视力下降',
 '您处于亚健康状态，主要与工作压力大、作息不规律有关。建议调整生活方式。',
 '如不改善，未来3-5年可能发展为脂肪肝、颈椎病加重。风险可控。',
 '规律作息，每晚11点前入睡。工作1小时休息5-10分钟，活动颈椎。减少熬夜。',
 '规律三餐，减少外卖和快餐。增加优质蛋白摄入。少饮含糖饮料，多喝水。',
 '每日进行颈椎保健操。每周3-4次有氧运动，如跑步、游泳。注意坐姿。',
 '1个月后复查肝功能。如转氨酶持续升高建议就诊。可考虑颈椎理疗。',
 '整体健康状况良好，亚健康状态通过生活方式调整可改善。建议养成良好习惯。'),

-- test_user2 的AI分析 - 健康状态
(@user_test2, @record_test2_1, '健康体检分析', 'low', 92.50,
 '各项指标均在正常范围内',
 '恭喜您！健康状况优秀。请继续保持健康的生活方式。',
 '维持当前状态，慢性疾病风险低于同龄人平均水平。',
 '继续保持规律作息和运动习惯。定期体检，预防胜于治疗。',
 '均衡饮食，荤素搭配。每日摄入足够蔬果。适量补充维生素和矿物质。',
 '每周保持150分钟以上中等强度运动。可尝试多样化运动方式。',
 '每年进行一次常规体检。关注家族病史相关指标。',
 '身体健康状况优秀，各项指标正常。建议保持良好生活习惯，定期体检。');

-- ========================================
-- 数据插入完成
-- ========================================
SELECT '========== 数据插入完成 ==========' as '';

-- 显示插入的数据统计
SELECT 'users' as 表名, COUNT(*) as 新增记录数 FROM users WHERE username LIKE 'test_%' OR username LIKE 'elder_%' OR username LIKE 'young_%'
UNION ALL
SELECT 'health_records', COUNT(*) FROM health_records WHERE user_id IN (@user_test1, @user_test2, @user_elder, @user_young)
UNION ALL
SELECT 'family_doctors', COUNT(*) FROM family_doctors WHERE user_id IN (@user_test1, @user_test2, @user_elder, @user_young)
UNION ALL
SELECT 'health_reminders', COUNT(*) FROM health_reminders WHERE user_id IN (@user_test1, @user_test2, @user_elder, @user_young)
UNION ALL
SELECT 'user_feedback', COUNT(*) FROM user_feedback WHERE user_id IN (@user_test1, @user_test2, @user_elder, @user_young)
UNION ALL
SELECT 'user_privacy_profiles', COUNT(*) FROM user_privacy_profiles WHERE user_id IN (@user_test1, @user_test2, @user_elder, @user_young)
UNION ALL
SELECT 'ai_health_analysis', COUNT(*) FROM ai_health_analysis WHERE user_id IN (@user_test1, @user_test2, @user_elder, @user_young);

SELECT '========== 所有ID均由数据库自动生成 ==========' as '';
SELECT '========== 可以在应用层使用统一的id字段访问 ==========' as '';

SET FOREIGN_KEY_CHECKS = 1;
