package com.yq.spbYq.mapper;

import com.yq.spbYq.domain.AIHealthAnalysisDomain;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * AI 健康分析 Mapper 接口
 */
public interface AIHealthAnalysisMapper {
    
    /**
     * 插入分析记录
     */
    int insert(AIHealthAnalysisDomain analysis);
    
    /**
     * 根据用户 ID 查询分析记录
     */
    List<AIHealthAnalysisDomain> findByUserId(@Param("userId") Integer userId);
    
    /**
     * 获取最新分析记录
     */
    AIHealthAnalysisDomain findLatestByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据 ID 查询分析记录
     */
    AIHealthAnalysisDomain findById(@Param("analysisId") Integer analysisId);
    
    /**
     * 更新分析记录
     */
    int update(AIHealthAnalysisDomain analysis);
    
    /**
     * 删除分析记录
     */
    int deleteById(@Param("analysisId") Integer analysisId);
}
