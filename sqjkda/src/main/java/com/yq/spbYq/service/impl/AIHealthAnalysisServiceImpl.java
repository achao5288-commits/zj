package com.yq.spbYq.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yq.spbYq.domain.AIHealthAnalysisDomain;
import com.yq.spbYq.domain.HealthRecordsEntity;
import com.yq.spbYq.mapper.AIHealthAnalysisMapper;
import com.yq.spbYq.mapper.HealthRecordsMapper;
import com.yq.spbYq.service.AIHealthAnalysisService;
import com.yq.spbYq.service.DeepSeekService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * AI 健康分析服务实现类
 */
@Service("aiHealthAnalysisService")
@Transactional
public class AIHealthAnalysisServiceImpl implements AIHealthAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AIHealthAnalysisServiceImpl.class);

    @Autowired
    private AIHealthAnalysisMapper aiHealthAnalysisMapper;
    
    @Autowired
    private HealthRecordsMapper healthRecordsMapper;
    
    @Autowired
    private DeepSeekService deepSeekService;
    
    @Value("${deepseek.api.enabled:true}")
    private boolean deepseekEnabled;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public AIHealthAnalysisDomain analyzeHealthRecord(Integer recordId, Integer userId) {
        try {
            // 1. 获取健康记录
            HealthRecordsEntity record = healthRecordsMapper.queryById(recordId);
            if (record == null) {
                throw new RuntimeException("健康记录不存在");
            }

            // 2. 执行 AI 分析
            AIHealthAnalysisDomain analysis = performAIAnalysis(record, userId);
            
            // 3. 保存分析结果
            aiHealthAnalysisMapper.insert(analysis);
            
            return analysis;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI 分析失败：" + e.getMessage());
        }
    }

    /**
     * 执行 AI 健康分析（优先使用 DeepSeek，失败时降级为规则分析）
     */
    private AIHealthAnalysisDomain performAIAnalysis(HealthRecordsEntity record, Integer userId) {
        AIHealthAnalysisDomain analysis = new AIHealthAnalysisDomain();
        
        analysis.setUserId(userId);
        analysis.setRecordId(record.getRecordId());
        analysis.setAnalysisType(record.getRecordType());
        analysis.setCreateTime(sdf.format(new Date()));
        analysis.setUpdateTime(sdf.format(new Date()));

        try {
            // 尝试使用 DeepSeek AI 进行分析
            if (deepseekEnabled && deepSeekService.isAvailable()) {
                logger.info("========================================");
                logger.info("🤖 使用 DeepSeek AI 进行健康分析");
                logger.info("记录ID: {}, 用户ID: {}", record.getRecordId(), userId);
                logger.info("========================================");
                String aiResultJson = deepSeekService.analyzeHealthRecord(record);
                analysis = parseAIResult(analysis, aiResultJson);
                logger.info("✅ DeepSeek AI 分析完成");
            } else {
                logger.info("========================================");
                logger.info("⚠️ DeepSeek 未启用或不可用，使用基于规则的分析");
                logger.info("DeepSeek 启用状态: {}", deepseekEnabled);
                logger.info("DeepSeek 可用状态: {}", deepSeekService.isAvailable());
                logger.info("========================================");
                analysis = performRuleBasedAnalysis(record, analysis);
            }
        } catch (Exception e) {
            logger.error("========================================");
            logger.error("❌ DeepSeek AI 分析失败，降级为规则分析");
            logger.error("错误信息: {}", e.getMessage());
            logger.error("========================================");
            // 如果 AI 调用失败，降级为基于规则的分析
            analysis = performRuleBasedAnalysis(record, analysis);
        }
        
        return analysis;
    }
    
    /**
     * 解析 DeepSeek AI 返回的结果
     */
    private AIHealthAnalysisDomain parseAIResult(AIHealthAnalysisDomain analysis, String aiResultJson) {
        try {
            JSONObject aiResult = JSONObject.parseObject(aiResultJson);
            
            analysis.setHealthScore(aiResult.getDoubleValue("healthScore"));
            analysis.setRiskLevel(aiResult.getString("riskLevel"));
            analysis.setAbnormalIndicators(aiResult.getString("abnormalIndicators"));
            analysis.setDiseaseRiskPrediction(aiResult.getString("diseaseRiskPrediction"));
            analysis.setAiSuggestion(aiResult.getString("aiSuggestion"));
            analysis.setLifestyleAdvice(aiResult.getString("lifestyleAdvice"));
            analysis.setDietAdvice(aiResult.getString("dietAdvice"));
            analysis.setExerciseAdvice(aiResult.getString("exerciseAdvice"));
            analysis.setMedicalAdvice(aiResult.getString("medicalAdvice"));
            analysis.setAnalysisSummary(aiResult.getString("analysisSummary"));
            
            logger.info("========================================");
            logger.info("✅ DeepSeek AI 分析结果解析成功");
            logger.info("健康评分: {}", analysis.getHealthScore());
            logger.info("风险等级: {}", analysis.getRiskLevel());
            logger.info("========================================");
        } catch (Exception e) {
            logger.error("解析 AI 结果失败: {}", e.getMessage());
            throw new RuntimeException("解析 AI 结果失败：" + e.getMessage());
        }
        
        return analysis;
    }
    
    /**
     * 基于规则的智能分析（降级方案）
     */
    private AIHealthAnalysisDomain performRuleBasedAnalysis(HealthRecordsEntity record, AIHealthAnalysisDomain analysis) {
        logger.info("📋 执行基于规则的健康分析");
        // 基于规则的智能分析（实际项目中可接入真实的 AI 模型）
        String summary = record.getSummary() != null ? record.getSummary() : "";
        boolean hasAbnormal = record.getIsAbnormal() != null && record.getIsAbnormal() == 1;
        
        // 1. 健康评分计算 (0-100)
        double healthScore = calculateHealthScore(record, summary, hasAbnormal);
        analysis.setHealthScore(healthScore);
        
        // 2. 风险等级评估
        String riskLevel = assessRiskLevel(healthScore, hasAbnormal, summary);
        analysis.setRiskLevel(riskLevel);
        
        // 3. 异常指标识别
        String abnormalIndicators = identifyAbnormalIndicators(summary, hasAbnormal);
        analysis.setAbnormalIndicators(abnormalIndicators);
        
        // 4. 疾病风险预测
        String diseaseRisk = predictDiseaseRisk(summary, record.getRecordType());
        analysis.setDiseaseRiskPrediction(diseaseRisk);
        
        // 5. 生成 AI 建议
        analysis.setAiSuggestion(generateAISuggestion(healthScore, riskLevel, abnormalIndicators));
        
        // 6. 生活方式建议
        analysis.setLifestyleAdvice(generateLifestyleAdvice(record, summary));
        
        // 7. 饮食建议
        analysis.setDietAdvice(generateDietAdvice(summary, hasAbnormal));
        
        // 8. 运动建议
        analysis.setExerciseAdvice(generateExerciseAdvice(healthScore, record));
        
        // 9. 医疗建议
        analysis.setMedicalAdvice(generateMedicalAdvice(riskLevel, hasAbnormal, summary));
        
        // 10. 分析摘要
        analysis.setAnalysisSummary(generateAnalysisSummary(healthScore, riskLevel, record.getRecordType()));
        
        return analysis;
    }

    /**
     * 计算健康评分
     */
    private double calculateHealthScore(HealthRecordsEntity record, String summary, boolean hasAbnormal) {
        double baseScore = 100.0;
        
        // 有异常指标扣分
        if (hasAbnormal) {
            baseScore -= 20.0;
        }
        
        // 根据摘要关键词进一步评分
        if (summary != null) {
            String lowerSummary = summary.toLowerCase();
            if (lowerSummary.contains("严重") || lowerSummary.contains("异常")) {
                baseScore -= 15.0;
            }
            if (lowerSummary.contains("轻微") || lowerSummary.contains("正常")) {
                baseScore += 5.0;
            }
            if (lowerSummary.contains("优秀") || lowerSummary.contains("良好")) {
                baseScore += 10.0;
            }
        }
        
        // 确保分数在 0-100 之间
        return Math.max(0.0, Math.min(100.0, baseScore));
    }

    /**
     * 评估风险等级
     */
    private String assessRiskLevel(double healthScore, boolean hasAbnormal, String summary) {
        if (healthScore >= 90) {
            return "低风险";
        } else if (healthScore >= 75) {
            return "中低风险";
        } else if (healthScore >= 60) {
            return "中等风险";
        } else if (healthScore >= 40) {
            return "中高风险";
        } else {
            return "高风险";
        }
    }

    /**
     * 识别异常指标
     */
    private String identifyAbnormalIndicators(String summary, boolean hasAbnormal) {
        if (!hasAbnormal && summary == null) {
            return "未发现明显异常指标";
        }
        
        StringBuilder indicators = new StringBuilder();
        if (summary != null) {
            // 提取常见的异常指标关键词
            String[] keywords = {"血压", "血糖", "血脂", "胆固醇", "心率", "体重", "BMI"};
            for (String keyword : keywords) {
                if (summary.contains(keyword)) {
                    if (indicators.length() > 0) {
                        indicators.append(", ");
                    }
                    indicators.append(keyword);
                }
            }
        }
        
        return indicators.length() > 0 ? indicators.toString() : "需要结合具体检查数据判断";
    }

    /**
     * 预测疾病风险
     */
    private String predictDiseaseRisk(String summary, String recordType) {
        StringBuilder risks = new StringBuilder();
        
        if (summary != null) {
            String lowerSummary = summary.toLowerCase();
            
            if (lowerSummary.contains("血压") || lowerSummary.contains("高血压")) {
                risks.append("心血管疾病风险; ");
            }
            if (lowerSummary.contains("血糖") || lowerSummary.contains("糖尿病")) {
                risks.append("糖尿病风险; ");
            }
            if (lowerSummary.contains("血脂") || lowerSummary.contains("胆固醇")) {
                risks.append("高血脂风险; ");
            }
            if (lowerSummary.contains("肥胖") || lowerSummary.contains("超重")) {
                risks.append("代谢综合征风险; ");
            }
        }
        
        return risks.length() > 0 ? risks.toString() : "当前未发现明显疾病风险，建议保持健康生活方式";
    }

    /**
     * 生成 AI 建议
     */
    private String generateAISuggestion(double healthScore, String riskLevel, String abnormalIndicators) {
        StringBuilder suggestion = new StringBuilder();
        suggestion.append("根据您的健康数据分析，");
        
        if (healthScore >= 85) {
            suggestion.append("您的健康状况整体良好，");
        } else if (healthScore >= 65) {
            suggestion.append("您的健康状况一般，需要关注部分指标，");
        } else {
            suggestion.append("您的健康状况需要引起重视，");
        }
        
        if (!"低风险".equals(riskLevel)) {
            suggestion.append("建议您调整生活方式并定期复查。");
        } else {
            suggestion.append("请继续保持良好的生活习惯。");
        }
        
        return suggestion.toString();
    }

    /**
     * 生成生活方式建议
     */
    private String generateLifestyleAdvice(HealthRecordsEntity record, String summary) {
        StringBuilder advice = new StringBuilder();
        advice.append("1. 保持规律作息，每天保证 7-8 小时睡眠; ");
        advice.append("2. 避免熬夜和过度劳累; ");
        advice.append("3. 保持心情舒畅，适当减压; ");
        
        if (summary != null && (summary.contains("压力") || summary.contains("疲劳"))) {
            advice.append("4. 建议进行冥想或深呼吸练习，缓解压力。");
        } else {
            advice.append("4. 培养兴趣爱好，丰富业余生活。");
        }
        
        return advice.toString();
    }

    /**
     * 生成饮食建议
     */
    private String generateDietAdvice(String summary, boolean hasAbnormal) {
        StringBuilder advice = new StringBuilder();
        advice.append("1. 均衡饮食，多吃蔬菜水果; ");
        advice.append("2. 控制油盐糖摄入; ");
        
        if (hasAbnormal || (summary != null && summary.contains("血糖"))) {
            advice.append("3. 减少精制碳水化合物摄入，选择低 GI 食物; ");
        }
        
        if (summary != null && summary.contains("血脂")) {
            advice.append("4. 减少饱和脂肪和反式脂肪摄入; ");
        }
        
        advice.append("5. 多喝水，每日饮水量 1500-2000ml。");
        
        return advice.toString();
    }

    /**
     * 生成运动建议
     */
    private String generateExerciseAdvice(double healthScore, HealthRecordsEntity record) {
        StringBuilder advice = new StringBuilder();
        
        if (healthScore >= 80) {
            advice.append("建议每周进行 150 分钟中等强度有氧运动，如快走、游泳、骑自行车; ");
            advice.append("配合 2-3 次力量训练，增强肌肉力量。");
        } else if (healthScore >= 60) {
            advice.append("建议从轻度运动开始，如散步、太极拳，逐渐增加运动量; ");
            advice.append("每周至少 3 次，每次 30 分钟以上。");
        } else {
            advice.append("建议在医生指导下进行适度运动; ");
            advice.append("推荐散步、瑜伽等低强度运动，避免剧烈运动。");
        }
        
        return advice.toString();
    }

    /**
     * 生成医疗建议
     */
    private String generateMedicalAdvice(String riskLevel, boolean hasAbnormal, String summary) {
        StringBuilder advice = new StringBuilder();
        
        if ("高风险".equals(riskLevel) || "中高风险".equals(riskLevel)) {
            advice.append("建议尽快就医，进行进一步检查; ");
            advice.append("遵医嘱进行治疗，定期复查相关指标。");
        } else if ("中等风险".equals(riskLevel)) {
            advice.append("建议 3 个月内进行一次全面体检; ");
            advice.append("如有不适及时就医。");
        } else {
            advice.append("建议每年进行一次常规体检; ");
            advice.append("关注身体变化，出现异常及时就诊。");
        }
        
        return advice.toString();
    }

    /**
     * 生成分析摘要
     */
    private String generateAnalysisSummary(double healthScore, String riskLevel, String recordType) {
        return String.format("本次%s分析显示，您的健康评分为%.1f 分，风险等级为%s。%s",
                recordType, 
                healthScore, 
                riskLevel,
                riskLevel.contains("高") ? "建议高度重视并采取改善措施。" : "请继续保持良好的健康状态。");
    }

    @Override
    public List<AIHealthAnalysisDomain> getUserAnalyses(Integer userId) {
        return aiHealthAnalysisMapper.findByUserId(userId);
    }

    @Override
    public AIHealthAnalysisDomain getLatestAnalysis(Integer userId) {
        return aiHealthAnalysisMapper.findLatestByUserId(userId);
    }

    @Override
    public AIHealthAnalysisDomain getAnalysisById(Integer analysisId) {
        return aiHealthAnalysisMapper.findById(analysisId);
    }

    @Override
    public Map<String, Object> generateComprehensiveReport(Integer userId) {
        Map<String, Object> report = new HashMap<>();
        
        // 获取所有分析记录
        List<AIHealthAnalysisDomain> analyses = getUserAnalyses(userId);
        
        if (analyses.isEmpty()) {
            report.put("message", "暂无分析数据");
            return report;
        }
        
        // 计算平均健康评分
        double avgScore = analyses.stream()
                .mapToDouble(a -> a.getHealthScore() != null ? a.getHealthScore() : 0)
                .average()
                .orElse(0.0);
        
        // 统计风险等级分布
        Map<String, Long> riskDistribution = analyses.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        AIHealthAnalysisDomain::getRiskLevel,
                        java.util.stream.Collectors.counting()));
        
        // 健康趋势分析
        String healthTrend = analyzeHealthTrend(analyses);
        
        report.put("avgHealthScore", avgScore);
        report.put("totalAnalyses", analyses.size());
        report.put("riskDistribution", riskDistribution);
        report.put("healthTrend", healthTrend);
        report.put("latestAnalysis", analyses.get(0));
        
        return report;
    }

    /**
     * 分析健康趋势
     */
    private String analyzeHealthTrend(List<AIHealthAnalysisDomain> analyses) {
        if (analyses.size() < 2) {
            return "数据不足，无法判断趋势";
        }
        
        double firstScore = analyses.get(analyses.size() - 1).getHealthScore();
        double lastScore = analyses.get(0).getHealthScore();
        
        double change = lastScore - firstScore;
        
        if (change > 5) {
            return String.format("呈上升趋势，健康状况有所改善 (+%.1f 分)", change);
        } else if (change < -5) {
            return String.format("呈下降趋势，需关注健康变化 (-%.1f 分)", Math.abs(change));
        } else {
            return String.format("基本稳定，波动较小 (变化 %.1f 分)", change);
        }
    }
}
