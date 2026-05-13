-- 创建测试用户（user_id=1）
INSERT INTO users (username, phone_number, password_hash, user_info, user_type, del_mark) VALUES
('testuser', '13800138000', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', 1, 0);

-- 验证用户是否创建成功
SELECT user_id, username, phone_number FROM users WHERE user_id = 1;

-- 然后再插入健康记录
INSERT INTO health_records (user_id, record_type, report_name, report_date, doctor_name, summary, is_abnormal) VALUES
(1, '体检报告', '2025 年度全面体检', '2025-03-15', '张医生', '整体健康状况良好。血压 120/80mmHg，心率 72 次/分，各项指标基本正常。', 0),
(1, '血检报告', '血常规检查', '2025-05-20', '李医生', '血糖值 6.8mmol/L 略高于正常范围。建议控制饮食。', 1),
(1, '血脂检查', '血脂四项检查', '2025-06-10', '王医生', '总胆固醇偏高，建议低脂饮食。', 1);

-- 最终验证
SELECT COUNT(*) as count FROM health_records WHERE user_id = 1;
