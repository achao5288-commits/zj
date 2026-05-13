package com.yq.spbYq.domain;

import lombok.Data;
import java.io.Serializable;

/**
 * AI 健康分析报告 Domain
 */
@Data
public class AIHealthAnalysisDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 统一主键ID（用于前端统一操作）
     */
    private Integer id;

    private Integer analysisId;
    
    private Integer userId;
    
    private Integer recordId;
    
    private String analysisType;
    
    private String riskLevel;
    
    private Double healthScore;
    
    private String abnormalIndicators;
    
    private String aiSuggestion;
    
    private String diseaseRiskPrediction;
    
    private String lifestyleAdvice;
    
    private String dietAdvice;
    
    private String exerciseAdvice;
    
    private String medicalAdvice;
    
    private String analysisSummary;
    
    private String createTime;
    
    private String updateTime;

    /**
     * 获取统一ID（映射到analysisId）
     */
    public Integer getId() {
        return analysisId;
    }

    /**
     * 设置统一ID（映射到analysisId）
     */
    public void setId(Integer id) {
        this.id = id;
        this.analysisId = id;
    }
}
