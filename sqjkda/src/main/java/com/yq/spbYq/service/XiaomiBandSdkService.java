package com.yq.spbYq.service;

import com.yq.spbYq.domain.HealthDeviceDomain;
import com.yq.spbYq.domain.HealthMonitoringDataDomain;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 模拟小米手环SDK服务
 * 用于验证SDK码和模拟设备数据同步
 */
@Service
public class XiaomiBandSdkService {
    
    // 模拟的SDK码数据库
    private static final Map<String, SdkDeviceInfo> SDK_CODE_DATABASE = new HashMap<>();
    
    static {
        // 初始化一些有效的SDK码
        SDK_CODE_DATABASE.put("MI-BAND-SDK-2024", new SdkDeviceInfo("小米", "MI-BAND-7", "智能手环", "Bluetooth"));
        SDK_CODE_DATABASE.put("MI-BAND-SDK-2023", new SdkDeviceInfo("小米", "MI-BAND-6", "智能手环", "Bluetooth"));
        SDK_CODE_DATABASE.put("MI-BAND-SDK-2025", new SdkDeviceInfo("小米", "MI-BAND-8", "智能手环", "Bluetooth"));
        SDK_CODE_DATABASE.put("OMRON-BP-SDK-2024", new SdkDeviceInfo("欧姆龙", "HEM-7136", "血压计", "Bluetooth"));
        SDK_CODE_DATABASE.put("ROCHE-GLU-SDK-2024", new SdkDeviceInfo("罗氏", "Accu-Chek", "血糖仪", "Bluetooth"));
        SDK_CODE_DATABASE.put("HUAWEI-SCALE-SDK-2024", new SdkDeviceInfo("华为", "AH100", "体脂秤", "WiFi"));
    }
    
    /**
     * 验证SDK码
     * @param sdkCode SDK码
     * @return 设备信息，如果无效则返回null
     */
    public SdkDeviceInfo validateSdkCode(String sdkCode) {
        if (sdkCode == null || sdkCode.trim().isEmpty()) {
            return null;
        }
        
        // 检查SDK码是否在数据库中
        SdkDeviceInfo deviceInfo = SDK_CODE_DATABASE.get(sdkCode.toUpperCase());
        if (deviceInfo != null) {
            return deviceInfo;
        }
        
        // 如果不在数据库中，检查格式是否有效
        if (isValidSdkFormat(sdkCode)) {
            // 动态生成设备信息
            return generateDeviceInfoFromSdkCode(sdkCode);
        }
        
        return null;
    }
    
    /**
     * 验证SDK码格式
     */
    private boolean isValidSdkFormat(String sdkCode) {
        String pattern = "^[A-Z]+-[A-Z]+-SDK-\\d{4}$";
        return sdkCode.toUpperCase().matches(pattern);
    }
    
    /**
     * 从SDK码生成设备信息
     */
    private SdkDeviceInfo generateDeviceInfoFromSdkCode(String sdkCode) {
        String[] parts = sdkCode.toUpperCase().split("-");
        if (parts.length >= 4) {
            String brandPrefix = parts[0];
            String deviceType = parts[1];
            
            String brand = getBrandFromPrefix(brandPrefix);
            String model = deviceType + "-" + parts[3];
            String type = getDeviceTypeFromCode(deviceType);
            String connectionType = "Bluetooth"; // 默认蓝牙
            
            return new SdkDeviceInfo(brand, model, type, connectionType);
        }
        return null;
    }
    
    /**
     * 从品牌前缀获取品牌名
     */
    private String getBrandFromPrefix(String prefix) {
        switch (prefix) {
            case "MI": return "小米";
            case "OMRON": return "欧姆龙";
            case "ROCHE": return "罗氏";
            case "HUAWEI": return "华为";
            default: return prefix;
        }
    }
    
    /**
     * 从设备类型代码获取设备类型
     */
    private String getDeviceTypeFromCode(String code) {
        switch (code) {
            case "BAND": return "智能手环";
            case "BP": return "血压计";
            case "GLU": return "血糖仪";
            case "SCALE": return "体脂秤";
            default: return "其他";
        }
    }
    
    /**
     * 模拟从设备获取健康数据
     * @param deviceSn 设备序列号
     * @return 模拟的健康数据列表
     */
    public List<HealthMonitoringDataDomain> syncDeviceData(String deviceSn) {
        List<HealthMonitoringDataDomain> dataList = new ArrayList<>();
        
        // 根据设备类型生成不同的模拟数据
        if (deviceSn.toUpperCase().contains("MI-BAND")) {
            // 小米手环数据
            dataList.add(createMonitoringData("heart_rate", BigDecimal.valueOf(75), "bpm", null, null, false));
            dataList.add(createMonitoringData("oxygen", BigDecimal.valueOf(98), "%", null, null, false));
            dataList.add(createMonitoringData("temperature", BigDecimal.valueOf(36.5), "°C", null, null, false));
            dataList.add(createMonitoringData("steps", BigDecimal.valueOf(8500), "步", null, null, false));
        } else if (deviceSn.toUpperCase().contains("HEM")) {
            // 血压计数据
            dataList.add(createMonitoringData("blood_pressure", BigDecimal.valueOf(120), "mmHg", 
                    BigDecimal.valueOf(120), BigDecimal.valueOf(80), false));
        } else if (deviceSn.toUpperCase().contains("ACCU-CHEK")) {
            // 血糖仪数据
            dataList.add(createMonitoringData("blood_glucose", BigDecimal.valueOf(5.6), "mmol/L", null, null, false));
        } else if (deviceSn.toUpperCase().contains("AH")) {
            // 体脂秤数据
            dataList.add(createMonitoringData("weight", BigDecimal.valueOf(68.5), "kg", null, null, false));
        }
        
        return dataList;
    }
    
    /**
     * 创建监测数据
     */
    private HealthMonitoringDataDomain createMonitoringData(String dataType, BigDecimal value, String unit, 
                                                           BigDecimal systolic, BigDecimal diastolic, boolean isAbnormal) {
        HealthMonitoringDataDomain data = new HealthMonitoringDataDomain();
        data.setDataType(dataType);
        data.setDataValue(value);
        data.setDataUnit(unit);
        data.setSystolic(systolic);
        data.setDiastolic(diastolic);
        data.setIsAbnormal(isAbnormal ? 1 : 0);
        data.setMeasurementTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return data;
    }
    
    /**
     * SDK设备信息类
     */
    public static class SdkDeviceInfo {
        private String brand;
        private String model;
        private String deviceType;
        private String connectionType;
        
        public SdkDeviceInfo(String brand, String model, String deviceType, String connectionType) {
            this.brand = brand;
            this.model = model;
            this.deviceType = deviceType;
            this.connectionType = connectionType;
        }
        
        public String getBrand() { return brand; }
        public String getModel() { return model; }
        public String getDeviceType() { return deviceType; }
        public String getConnectionType() { return connectionType; }
    }
}
