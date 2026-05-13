package com.yq.spbYq.controller;

import com.yq.spbYq.domain.AIHealthAnalysisDomain;
import com.yq.spbYq.service.AIHealthAnalysisService;
import com.yq.spbYq.util.ReturnVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 健康分析控制器
 */
@RestController
@RequestMapping("/api/ai-analysis")
@CrossOrigin(origins = "*")
public class AIHealthAnalysisController {

    @Autowired
    private AIHealthAnalysisService aiHealthAnalysisService;

    /**
     * 对健康记录进行 AI 分析
     */
    @PostMapping("/analyze/{recordId}")
    public ReturnVO analyzeHealthRecord(
            @PathVariable Integer recordId,
            @RequestParam Integer userId) {
        try {
            AIHealthAnalysisDomain result = aiHealthAnalysisService.analyzeHealthRecord(recordId, userId);
            return ReturnVO.success("分析成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnVO.error("分析失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的 AI 分析报告列表
     */
    @GetMapping("/list")
    public ReturnVO getUserAnalyses(@RequestParam Integer userId) {
        try {
            List<AIHealthAnalysisDomain> analyses = aiHealthAnalysisService.getUserAnalyses(userId);
            return ReturnVO.success("查询成功", analyses);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnVO.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取最新 AI 分析报告
     */
    @GetMapping("/latest")
    public ReturnVO getLatestAnalysis(@RequestParam Integer userId) {
        try {
            AIHealthAnalysisDomain analysis = aiHealthAnalysisService.getLatestAnalysis(userId);
            if (analysis == null) {
                return ReturnVO.error("暂无分析报告");
            }
            return ReturnVO.success("查询成功", analysis);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnVO.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取分析报告详情
     */
    @GetMapping("/{analysisId}")
    public ReturnVO getAnalysisById(@PathVariable Integer analysisId) {
        try {
            AIHealthAnalysisDomain analysis = aiHealthAnalysisService.getAnalysisById(analysisId);
            if (analysis == null) {
                return ReturnVO.error("分析报告不存在");
            }
            return ReturnVO.success("查询成功", analysis);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnVO.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 生成综合健康评估报告
     */
    @GetMapping("/comprehensive-report")
    public ReturnVO generateComprehensiveReport(@RequestParam Integer userId) {
        try {
            Map<String, Object> report = aiHealthAnalysisService.generateComprehensiveReport(userId);
            return ReturnVO.success("生成成功", report);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnVO.error("生成失败：" + e.getMessage());
        }
    }

    /**
     * 批量分析用户的所有健康记录
     */
    @PostMapping("/batch-analyze")
    public ReturnVO batchAnalyze(@RequestBody Map<String, Object> params) {
        try {
            Integer userId = (Integer) params.get("userId");
            List<Integer> recordIds = (List<Integer>) params.get("recordIds");
            
            if (userId == null || recordIds == null || recordIds.isEmpty()) {
                return ReturnVO.error("参数错误");
            }
            
            List<AIHealthAnalysisDomain> results = new java.util.ArrayList<>();
            for (Integer recordId : recordIds) {
                try {
                    AIHealthAnalysisDomain result = aiHealthAnalysisService.analyzeHealthRecord(recordId, userId);
                    results.add(result);
                } catch (Exception e) {
                    // 单条记录分析失败不影响其他记录
                    continue;
                }
            }
            
            return ReturnVO.success("批量分析完成，成功" + results.size() + "条", results);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnVO.error("批量分析失败：" + e.getMessage());
        }
    }
}
