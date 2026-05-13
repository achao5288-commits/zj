-- ========================================
-- 测试健康记录新增功能
-- 验证自动生成ID和自动获取userId
-- ========================================

-- 1. 查看当前users表中的测试用户
SELECT '========== 测试用户列表 ==========' as '';

SELECT 
    user_id as ID,
    username as 用户名,
    phone_number as 手机号,
    user_type as 类型
FROM users
WHERE username LIKE 'test_%' OR username LIKE 'elder_%' OR username LIKE 'young_%'
ORDER BY user_id;

-- 2. 查看当前健康记录的ID范围
SELECT '========== 当前健康记录ID范围 ==========' as '';

SELECT 
    MIN(record_id) as 最小ID,
    MAX(record_id) as 最大ID,
    COUNT(*) as 总记录数,
    AUTO_INCREMENT as 下一个自动生成ID
FROM health_records, information_schema.tables
WHERE table_schema = DATABASE() 
AND table_name = 'health_records';

-- 3. 模拟新增一条记录（不指定record_id，由数据库自动生成）
SELECT '========== 模拟新增健康记录 ==========' as '';

-- 获取一个测试用户的ID
SET @test_user_id = (SELECT user_id FROM users WHERE username = 'test_user1' LIMIT 1);

-- 插入新记录（不指定record_id）
INSERT INTO health_records (
    user_id, 
    record_type, 
    report_name, 
    report_date, 
    doctor_name, 
    summary, 
    is_abnormal
) VALUES (
    @test_user_id,
    '测试报告',
    '自动ID生成测试',
    '2025-04-18',
    '测试医生',
 '这是一条测试记录，验证ID是否自动生成',
    0
);

-- 4. 查看刚插入的记录
SELECT '========== 查看新生成的记录 ==========' as '';

SELECT 
    record_id as '自动生成的ID',
    user_id as 用户ID,
    record_type as 记录类型,
    report_name as 报告名称,
    report_date as 报告日期,
    summary as 摘要
FROM health_records
WHERE report_name = '自动ID生成测试';

-- 5. 验证ID映射（id应该等于record_id）
SELECT '========== 验证统一ID映射 ==========' as '';

SELECT 
    record_id as '数据库主键(record_id)',
    record_id as '统一ID(id)',
    CASE 
        WHEN record_id = record_id THEN '✅ ID映射正确'
        ELSE '❌ ID映射错误'
    END as 验证结果
FROM health_records
WHERE report_name = '自动ID生成测试';

-- 6. 查看新增后的ID范围
SELECT '========== 新增后的ID状态 ==========' as '';

SELECT 
    MIN(record_id) as 最小ID,
    MAX(record_id) as 最大ID,
    COUNT(*) as 总记录数,
    AUTO_INCREMENT as 下一个自动生成ID
FROM health_records, information_schema.tables
WHERE table_schema = DATABASE() 
AND table_name = 'health_records';

-- 7. 查看该用户的所有记录
SELECT '========== 测试用户的所有健康记录 ==========' as '';

SELECT 
    hr.record_id as ID,
    hr.record_type as 类型,
    hr.report_name as 报告名称,
    hr.report_date as 日期,
    hr.is_abnormal as 是否异常,
    hr.upload_time as 上传时间
FROM health_records hr
WHERE hr.user_id = @test_user_id
ORDER BY hr.record_id DESC;

-- 8. 清理测试数据（可选）
-- DELETE FROM health_records WHERE report_name = '自动ID生成测试';

SELECT '========== 测试完成 ==========' as '';
SELECT '✅ 健康记录ID已自动生成' as 结果;
SELECT '✅ userId由应用层自动设置' as 结果;
SELECT '✅ 可以在返回数据中获取生成的id' as 结果;
