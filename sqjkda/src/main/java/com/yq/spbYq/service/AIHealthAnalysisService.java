package com.yq.spbYq.service;

import com.yq.spbYq.domain.AIHealthAnalysisDomain;
import java.util.List;
import java.util.Map;

/**
 * AI 健康分析服务接口
 */
public interface AIHealthAnalysisService {
    
    /**
     * 对健康记录进行 AI 分析
     * @param recordId 健康记录 ID
     * @param userId 用户 ID
     * @return 分析结果
     */
    AIHealthAnalysisDomain analyzeHealthRecord(Integer recordId, Integer userId);
    
    /**
     * 获取用户的 AI 分析报告列表
     * @param userId 用户 ID
     * @return 分析报告列表
     */
    List<AIHealthAnalysisDomain> getUserAnalyses(Integer userId);
    
    /**
     * 获取最新的 AI 分析报告
     * @param userId 用户 ID
     * @return 最新分析报告
     */
    AIHealthAnalysisDomain getLatestAnalysis(Integer userId);
    
    /**
     * 根据 ID 获取分析报告详情
     * @param analysisId 分析报告 ID
     * @return 分析报告详情
     */
    AIHealthAnalysisDomain getAnalysisById(Integer analysisId);
    
    /**
     * 生成综合健康评估报告
     * @param userId 用户 ID
     * @return 综合评估报告
     */
    Map<String, Object> generateComprehensiveReport(Integer userId);
}
