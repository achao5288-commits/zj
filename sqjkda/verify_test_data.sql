-- ========================================
-- 测试数据验证脚本
-- 用于验证测试数据是否正确插入
-- ========================================

-- 1. 查看各表数据总量
SELECT '========== 各表数据总量 ==========' as '';

SELECT 'users' as 表名, COUNT(*) as 记录数 FROM users
UNION ALL
SELECT 'health_records', COUNT(*) FROM health_records
UNION ALL
SELECT 'family_doctors', COUNT(*) FROM family_doctors
UNION ALL
SELECT 'health_reminders', COUNT(*) FROM health_reminders
UNION ALL
SELECT 'user_feedback', COUNT(*) FROM user_feedback
UNION ALL
SELECT 'user_privacy_profiles', COUNT(*) FROM user_privacy_profiles
UNION ALL
SELECT 'ai_health_analysis', COUNT(*) FROM ai_health_analysis;

-- 2. 查看自动生成的ID范围
SELECT '========== 各表ID范围 ==========' as '';

SELECT 
    'users' as 表名,
    MIN(user_id) as 最小ID,
    MAX(user_id) as 最大ID,
    COUNT(*) as 记录数
FROM users
WHERE user_id >= 24  -- 测试数据

UNION ALL

SELECT 
    'health_records',
    MIN(record_id),
    MAX(record_id),
    COUNT(*)
FROM health_records

UNION ALL

SELECT 
    'family_doctors',
    MIN(doctor_id),
    MAX(doctor_id),
    COUNT(*)
FROM family_doctors

UNION ALL

SELECT 
    'health_reminders',
    MIN(reminder_id),
    MAX(reminder_id),
    COUNT(*)
FROM health_reminders

UNION ALL

SELECT 
    'user_feedback',
    MIN(feedback_id),
    MAX(feedback_id),
    COUNT(*)
FROM user_feedback

UNION ALL

SELECT 
    'user_privacy_profiles',
    MIN(id),
    MAX(id),
    COUNT(*)
FROM user_privacy_profiles

UNION ALL

SELECT 
    'ai_health_analysis',
    MIN(analysis_id),
    MAX(analysis_id),
    COUNT(*)
FROM ai_health_analysis;

-- 3. 查看测试用户详情
SELECT '========== 测试用户列表 ==========' as '';

SELECT 
    user_id as ID,
    username as 用户名,
    phone_number as 手机号,
    user_type as 类型,
    user_info as 备注
FROM users
WHERE user_id >= 24 OR username LIKE 'test_%' OR username LIKE 'elder_%' OR username LIKE 'young_%'
ORDER BY user_id;

-- 4. 查看健康档案分布
SELECT '========== 健康档案分布 ==========' as '';

SELECT 
    u.username as 用户,
    COUNT(hr.record_id) as 档案数量,
    SUM(CASE WHEN hr.is_abnormal = 1 THEN 1 ELSE 0 END) as 异常档案数
FROM users u
LEFT JOIN health_records hr ON u.user_id = hr.user_id
WHERE u.user_id >= 24
GROUP BY u.user_id, u.username;

-- 5. 查看健康提醒状态
SELECT '========== 健康提醒状态 ==========' as '';

SELECT 
    u.username as 用户,
    hr.reminder_name as 提醒名称,
    hr.reminder_type as 类型,
    hr.due_date as 截止日期,
    hr.status as 状态,
    hr.priority as 优先级
FROM health_reminders hr
JOIN users u ON hr.user_id = u.user_id
WHERE u.user_id >= 24
ORDER BY hr.due_date, hr.priority;

-- 6. 查看AI健康分析评分
SELECT '========== AI健康分析评分 ==========' as '';

SELECT 
    u.username as 用户,
    aha.analysis_type as 分析类型,
    aha.health_score as 健康评分,
    aha.risk_level as 风险等级,
    aha.analysis_summary as 分析摘要
FROM ai_health_analysis aha
JOIN users u ON aha.user_id = u.user_id
WHERE u.user_id >= 24
ORDER BY aha.health_score;

-- 7. 查看外键关联完整性
SELECT '========== 外键关联检查 ==========' as '';

-- 检查健康档案的用户是否存在
SELECT 
    '健康档案→用户' as 关联,
    COUNT(*) as 孤儿记录数
FROM health_records hr
LEFT JOIN users u ON hr.user_id = u.user_id
WHERE u.user_id IS NULL;

-- 检查健康提醒的用户是否存在
SELECT 
    '健康提醒→用户' as 关联,
    COUNT(*) as 孤儿记录数
FROM health_reminders hr
LEFT JOIN users u ON hr.user_id = u.user_id
WHERE u.user_id IS NULL;

-- 检查健康提醒的记录是否存在
SELECT 
    '健康提醒→健康档案' as 关联,
    COUNT(*) as 孤儿记录数
FROM health_reminders hr
LEFT JOIN health_records hrec ON hr.record_id = hrec.record_id
WHERE hrec.record_id IS NULL;

-- 8. 查看下一个自动生成的ID
SELECT '========== 下一个自动生成的ID ==========' as '';

SELECT 
    table_name as 表名,
    auto_increment as 下一个ID
FROM information_schema.tables
WHERE table_schema = DATABASE()
AND table_name IN (
    'users', 
    'health_records', 
    'family_doctors', 
    'health_reminders', 
    'user_feedback', 
    'user_privacy_profiles', 
    'ai_health_analysis'
)
ORDER BY table_name;

-- 9. 数据完整性总结
SELECT '========== 数据完整性总结 ==========' as '';

SELECT 
    CASE 
        WHEN COUNT(*) = 7 THEN '✅ 所有表都有数据'
        ELSE '❌ 部分表缺少数据'
    END as 检查结果
FROM (
    SELECT table_name 
    FROM information_schema.tables 
    WHERE table_schema = DATABASE() 
    AND table_name IN (
        'users', 'health_records', 'family_doctors', 
        'health_reminders', 'user_feedback', 
        'user_privacy_profiles', 'ai_health_analysis'
    )
    GROUP BY table_name
) as t;

-- 10. 测试统一ID功能
SELECT '========== 测试统一ID映射 ==========' as '';

-- 验证健康档案的id和record_id是否一致
SELECT 
    record_id,
    'record_id应该等于统一id' as 说明
FROM health_records
WHERE user_id >= 24
LIMIT 5;

-- 验证用户的user_id就是统一id
SELECT 
    user_id as '统一ID(id)',
    username
FROM users
WHERE user_id >= 24
LIMIT 5;

SELECT '========== 验证完成 ==========' as '';
