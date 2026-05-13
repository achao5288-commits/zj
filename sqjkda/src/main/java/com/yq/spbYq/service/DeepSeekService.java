package com.yq.spbYq.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yq.spbYq.domain.HealthRecordsEntity;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DeepSeek AI 服务类
 * 用于调用 DeepSeek API 进行智能健康分析
 */
@Service
public class DeepSeekService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.api.model}")
    private String model;

    @Value("${deepseek.api.temperature}")
    private double temperature;

    @Value("${deepseek.api.enabled:true}")
    private boolean enabled;

    private final OkHttpClient client;

    public DeepSeekService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 调用 DeepSeek API 进行健康分析
     * @param record 健康记录
     * @return AI 分析结果的 JSON 字符串
     */
    public String analyzeHealthRecord(HealthRecordsEntity record) {
        if (!enabled) {
            throw new RuntimeException("DeepSeek API 未启用");
        }

        try {
            // 构建提示词
            String prompt = buildHealthAnalysisPrompt(record);
            
            // 调用 DeepSeek API
            String response = callDeepSeekAPI(prompt);
            
            // 解析并返回 AI 的分析结果
            return extractAIResult(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DeepSeek AI 分析失败：" + e.getMessage());
        }
    }

    /**
     * 构建健康分析的提示词
     */
    private String buildHealthAnalysisPrompt(HealthRecordsEntity record) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的健康分析医生，拥有丰富的医学知识。请根据以下健康记录进行详细分析，并严格按照 JSON 格式返回结果。\n\n");
        prompt.append("【健康记录信息】\n");
        prompt.append("记录类型：").append(record.getRecordType() != null ? record.getRecordType() : "未知").append("\n");
        prompt.append("报告名称：").append(record.getReportName() != null ? record.getReportName() : "无").append("\n");
        prompt.append("报告日期：").append(record.getReportDate() != null ? record.getReportDate() : "无").append("\n");
        prompt.append("医生姓名：").append(record.getDoctorName() != null ? record.getDoctorName() : "无").append("\n");
        prompt.append("检查摘要：").append(record.getSummary() != null ? record.getSummary() : "无").append("\n");
        prompt.append("是否异常：").append(record.getIsAbnormal() != null && record.getIsAbnormal() == 1 ? "是" : "否").append("\n");
        
        prompt.append("\n【分析要求】\n");
        prompt.append("请基于上述健康记录，从以下几个维度进行分析：\n");
        prompt.append("1. 健康评分（0-100分）\n");
        prompt.append("2. 风险等级评估\n");
        prompt.append("3. 异常指标识别\n");
        prompt.append("4. 疾病风险预测\n");
        prompt.append("5. AI 综合建议\n");
        prompt.append("6. 生活方式建议\n");
        prompt.append("7. 饮食建议\n");
        prompt.append("8. 运动建议\n");
        prompt.append("9. 医疗建议\n");
        prompt.append("10. 分析摘要\n\n");
        
        prompt.append("【返回格式】\n");
        prompt.append("请严格按照以下 JSON 格式返回，不要添加任何其他内容：\n");
        prompt.append("{\n");
        prompt.append("  \"healthScore\": 75.5,\n");
        prompt.append("  \"riskLevel\": \"低风险/中低风险/中等风险/中高风险/高风险\",\n");
        prompt.append("  \"abnormalIndicators\": \"异常指标的具体描述\",\n");
        prompt.append("  \"diseaseRiskPrediction\": \"疾病风险预测\",\n");
        prompt.append("  \"aiSuggestion\": \"AI综合建议\",\n");
        prompt.append("  \"lifestyleAdvice\": \"生活方式建议\",\n");
        prompt.append("  \"dietAdvice\": \"饮食建议\",\n");
        prompt.append("  \"exerciseAdvice\": \"运动建议\",\n");
        prompt.append("  \"medicalAdvice\": \"医疗建议\",\n");
        prompt.append("  \"analysisSummary\": \"分析摘要\"\n");
        prompt.append("}\n\n");
        prompt.append("请确保返回的是合法的 JSON 格式，所有字段都必须存在。");
        
        return prompt.toString();
    }

    /**
     * 调用 DeepSeek API
     */
    private String callDeepSeekAPI(String prompt) throws IOException {
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", temperature);
        
        // 构建消息
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        requestBody.put("messages", messages);
        
        // 转换为 JSON
        String jsonBody = JSON.toJSONString(requestBody);
        
        // 创建 HTTP 请求
        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(body)
                .build();
        
        // 执行请求
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无错误信息";
                throw new IOException(String.format(
                        "DeepSeek API 调用失败: HTTP %d, 错误信息: %s",
                        response.code(),
                        errorBody
                ));
            }
            
            String responseBody = response.body().string();
            System.out.println("DeepSeek API 响应: " + responseBody);
            return responseBody;
        }
    }

    /**
     * 提取 AI 返回的分析结果
     */
    private String extractAIResult(String jsonResponse) {
        try {
            JSONObject response = JSON.parseObject(jsonResponse);
            JSONArray choices = response.getJSONArray("choices");
            
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("DeepSeek API 返回数据格式错误：缺少 choices 字段");
            }
            
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            
            if (message == null) {
                throw new RuntimeException("DeepSeek API 返回数据格式错误：缺少 message 字段");
            }
            
            String content = message.getString("content");
            
            // 提取 JSON 部分（AI 可能在 JSON 外添加说明文字）
            int jsonStart = content.indexOf("{");
            int jsonEnd = content.lastIndexOf("}");
            
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                String jsonResult = content.substring(jsonStart, jsonEnd + 1);
                System.out.println("提取的 AI 分析结果: " + jsonResult);
                return jsonResult;
            }
            
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析 DeepSeek API 响应失败：" + e.getMessage());
        }
    }

    /**
     * 检查 DeepSeek API 是否可用
     */
    public boolean isAvailable() {
        return enabled && apiKey != null && !apiKey.isEmpty();
    }
}
