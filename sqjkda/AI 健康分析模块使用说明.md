# AI 健康分析模块使用说明

## 一、模块概述

AI 健康分析模块是社区健康管理系统中的智能分析组件，通过人工智能技术对用户的健康数据进行深度分析，提供个性化的健康建议和疾病风险预测。

## 二、主要功能

### 1. 智能健康评估
- **健康评分系统**：基于多项指标综合计算，生成 0-100 分的健康评分
- **风险等级评估**：分为低风险、中低风险、中等风险、中高风险、高风险五个等级
- **异常指标识别**：自动识别体检报告中的异常指标并重点提示

### 2. 疾病风险预测
- 心血管疾病风险评估
- 糖尿病风险筛查
- 高血脂风险预警
- 代谢综合征风险分析

### 3. 个性化健康建议
- **AI 智能建议**：基于分析结果的综合性建议
- **生活方式指导**：作息、压力管理等方面的建议
- **饮食建议**：针对性的饮食调整方案
- **运动建议**：根据健康状况推荐合适的运动方式
- **医疗建议**：就医指导和复查建议

## 三、技术架构

### 后端技术栈
- **框架**：Spring Boot 2.7.5
- **持久层**：MyBatis
- **数据库**：MySQL 8.0
- **接口规范**：RESTful API

### 前端技术栈
- **UI 框架**：Bootstrap 4
- **图标库**：Material Design Icons
- **交互**：jQuery + AJAX

## 四、部署步骤

### 1. 数据库配置

执行以下 SQL 创建 AI 分析表：

```bash
# 在 MySQL 中执行
mysql -u root -p < src/main/resources/ai_health_analysis_table.sql
```

或者手动执行 `src/main/resources/ai_health_analysis_table.sql` 文件中的 SQL 语句。

### 2. 编译项目

```bash
mvn clean install
```

### 3. 启动应用

```bash
mvn spring-boot:run
```

或在 IDE 中运行 `SpbYqApplication.java` 主类。

### 4. 访问应用

浏览器访问：http://localhost:8080/spbYq/ai-analysis.html

## 五、API 接口说明

### 1. 单条记录 AI 分析
**接口**：`POST /api/ai-analysis/analyze/{recordId}`

**参数**：
- `recordId`: 健康记录 ID（路径参数）
- `userId`: 用户 ID（查询参数）

**返回示例**：
```json
{
  "code": 200,
  "msg": "分析成功",
  "data": {
    "analysisId": 1,
    "healthScore": 85.5,
    "riskLevel": "低风险",
    "aiSuggestion": "根据您的健康数据分析...",
    "abnormalIndicators": "血压，血糖",
    "diseaseRiskPrediction": "心血管疾病风险",
    "lifestyleAdvice": "1. 保持规律作息...",
    "dietAdvice": "1. 均衡饮食...",
    "exerciseAdvice": "建议每周进行 150 分钟...",
    "medicalAdvice": "建议每年进行一次..."
  }
}
```

### 2. 获取分析报告列表
**接口**：`GET /api/ai-analysis/list?userId={userId}`

### 3. 获取最新分析报告
**接口**：`GET /api/ai-analysis/latest?userId={userId}`

### 4. 获取分析报告详情
**接口**：`GET /api/ai-analysis/{analysisId}`

### 5. 生成综合健康评估报告
**接口**：`GET /api/ai-analysis/comprehensive-report?userId={userId}`

**返回内容**：
- 平均健康评分
- 分析总次数
- 风险等级分布
- 健康趋势分析
- 最新分析报告

### 6. 批量分析
**接口**：`POST /api/ai-analysis/batch-analyze`

**请求体**：
```json
{
  "userId": 1,
  "recordIds": [1, 2, 3]
}
```

## 六、使用流程

### 用户使用步骤

1. **登录系统**
   - 访问系统首页 http://localhost:8080/spbYq/
   - 输入用户名和密码登录

2. **进入 AI 分析模块**
   - 在首页点击"AI 健康分析"卡片
   - 或直接在地址栏输入 ai-analysis.html

3. **选择健康记录**
   - 从下拉列表中选择要分析的体检报告或健康记录
   - 系统会自动加载该用户的所有健康记录

4. **开始分析**
   - 点击"开始 AI 分析"按钮
   - 等待 AI 分析完成（通常 1-3 秒）

5. **查看分析报告**
   - 查看健康评分和风险等级
   - 阅读各项详细建议
   - 可截图保存或打印报告

### 管理员功能

管理员可以查看系统中所有用户的 AI 分析报告，进行统计分析：

```sql
-- 查看所有分析报告统计
SELECT 
    COUNT(*) as total_analyses,
    AVG(health_score) as avg_score,
    risk_level,
    COUNT(*) as count
FROM ai_health_analysis
GROUP BY risk_level;
```

## 七、算法说明

### 健康评分计算逻辑

基础分 100 分，根据以下规则调整：

1. **异常指标扣分**
   - 有异常指标：-20 分
   - 严重异常：额外 -15 分

2. **关键词加分/扣分**
   - 包含"优秀"、"良好"：+10 分
   - 包含"正常"、"轻微"：+5 分
   - 包含"严重"、"异常"：-15 分

3. **风险等级划分**
   - 90-100 分：低风险
   - 75-89 分：中低风险
   - 60-74 分：中等风险
   - 40-59 分：中高风险
   - 0-39 分：高风险

### 疾病风险预测规则

系统根据健康记录中的关键词自动识别风险：

- **心血管风险**：包含"血压"、"高血压"等关键词
- **糖尿病风险**：包含"血糖"、"糖尿病"等关键词
- **高血脂风险**：包含"血脂"、"胆固醇"等关键词
- **代谢综合征**：包含"肥胖"、"超重"等关键词

## 八、数据字典

### AI 健康分析表 (ai_health_analysis)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| analysis_id | int | 主键 ID |
| user_id | int | 用户 ID |
| record_id | int | 健康记录 ID |
| analysis_type | varchar(50) | 分析类型 |
| risk_level | varchar(20) | 风险等级 |
| health_score | decimal(5,2) | 健康评分 |
| abnormal_indicators | text | 异常指标 |
| ai_suggestion | text | AI 建议 |
| disease_risk_prediction | text | 疾病风险预测 |
| lifestyle_advice | text | 生活方式建议 |
| diet_advice | text | 饮食建议 |
| exercise_advice | text | 运动建议 |
| medical_advice | text | 医疗建议 |
| analysis_summary | text | 分析摘要 |
| create_time | timestamp | 创建时间 |
| update_time | timestamp | 更新时间 |

## 九、常见问题

### Q1: 分析失败怎么办？
**A**: 检查以下几点：
1. 确认健康记录是否存在
2. 确认用户 ID 是否正确
3. 查看后台日志错误信息
4. 确保数据库连接正常

### Q2: 健康评分为什么是 0 分或 100 分？
**A**: 评分系统设置了边界值保护，实际分数会在 0-100 之间自动调整。

### Q3: 能否自定义分析规则？
**A**: 可以修改 `AIHealthAnalysisServiceImpl.java` 中的分析方法，调整评分规则和建議生成逻辑。

### Q4: 是否支持接入真实的 AI 模型？
**A**: 当前版本使用基于规则的分析，可以通过修改 `performAIAnalysis` 方法接入机器学习模型或第三方 AI 服务。

## 十、扩展开发建议

### 1. 接入真实 AI 模型
```java
// 示例：调用第三方 AI 服务
private AIHealthAnalysisDomain performAIAnalysis(...) {
    // 调用医疗 AI API
    String apiResult = callMedicalAI(record.getSummary());
    // 解析 AI 返回结果
    return parseAIResult(apiResult);
}
```

### 2. 增加更多分析维度
- 心理健康评估
- 营养状况分析
- 睡眠质量评估
- 运动能力评估

### 3. 生成可视化报告
- 使用 ECharts 生成健康趋势图
- 添加雷达图展示各项指标
- 生成 PDF 格式的详细报告

### 4. 智能提醒功能
- 定期自动分析新上传的报告
- 异常情况及时推送警告
- 复查时间智能提醒

## 十一、技术支持

如有问题或建议，请联系：
- 系统管理员：查看系统设置
- 技术支持：查看项目文档
- 紧急问题：查看应用日志

---

**版本信息**：
- 版本号：v1.0.0
- 更新日期：2026-03-25
- 适用系统：社区健康管理系统
