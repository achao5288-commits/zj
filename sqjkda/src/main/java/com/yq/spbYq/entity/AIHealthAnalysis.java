package com.yq.spbYq.entity;

import java.io.Serializable;

/**
 * AI 健康分析报告实体类
 */
public class AIHealthAnalysis implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Double getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(Double healthScore) {
        this.healthScore = healthScore;
    }

    public String getAbnormalIndicators() {
        return abnormalIndicators;
    }

    public void setAbnormalIndicators(String abnormalIndicators) {
        this.abnormalIndicators = abnormalIndicators;
    }

    public String getAiSuggestion() {
        return aiSuggestion;
    }

    public void setAiSuggestion(String aiSuggestion) {
        this.aiSuggestion = aiSuggestion;
    }

    public String getDiseaseRiskPrediction() {
        return diseaseRiskPrediction;
    }

    public void setDiseaseRiskPrediction(String diseaseRiskPrediction) {
        this.diseaseRiskPrediction = diseaseRiskPrediction;
    }

    public String getLifestyleAdvice() {
        return lifestyleAdvice;
    }

    public void setLifestyleAdvice(String lifestyleAdvice) {
        this.lifestyleAdvice = lifestyleAdvice;
    }

    public String getDietAdvice() {
        return dietAdvice;
    }

    public void setDietAdvice(String dietAdvice) {
        this.dietAdvice = dietAdvice;
    }

    public String getExerciseAdvice() {
        return exerciseAdvice;
    }

    public void setExerciseAdvice(String exerciseAdvice) {
        this.exerciseAdvice = exerciseAdvice;
    }

    public String getMedicalAdvice() {
        return medicalAdvice;
    }

    public void setMedicalAdvice(String medicalAdvice) {
        this.medicalAdvice = medicalAdvice;
    }

    public String getAnalysisSummary() {
        return analysisSummary;
    }

    public void setAnalysisSummary(String analysisSummary) {
        this.analysisSummary = analysisSummary;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
