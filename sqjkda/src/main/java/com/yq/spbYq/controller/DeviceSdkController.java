package com.yq.spbYq.controller;

import com.yq.spbYq.domain.HealthDeviceDomain;
import com.yq.spbYq.domain.HealthMonitoringDataDomain;
import com.yq.spbYq.service.HealthDeviceService;
import com.yq.spbYq.service.HealthMonitoringDataService;
import com.yq.spbYq.service.XiaomiBandSdkService;
import com.yq.spbYq.util.ReturnVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 设备SDK接入控制器
 * 处理设备SDK验证和数据同步
 */
@RestController
@RequestMapping("/deviceSdk")
public class DeviceSdkController {
    
    @Resource
    private XiaomiBandSdkService xiaomiBandSdkService;
    
    @Resource
    private HealthDeviceService healthDeviceService;
    
    @Resource
    private HealthMonitoringDataService healthMonitoringDataService;
    
    /**
     * 验证SDK码
     * @param sdkCode SDK码
     * @return 设备信息
     */
    @GetMapping("/validate")
    public ReturnVO validateSdkCode(@RequestParam String sdkCode) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        
        if (sdkCode == null || sdkCode.trim().isEmpty()) {
            returnVO.setMsg("SDK码不能为空");
            return returnVO;
        }
        
        XiaomiBandSdkService.SdkDeviceInfo deviceInfo = xiaomiBandSdkService.validateSdkCode(sdkCode);
        
        if (deviceInfo != null) {
            returnVO.setCode(1);
            returnVO.setMsg("SDK码验证成功");
            returnVO.setContent(deviceInfo);
        } else {
            returnVO.setMsg("无效的SDK码");
        }
        
        return returnVO;
    }
    
    /**
     * 通过SDK码添加设备
     * @param request HTTP请求
     * @param sdkCode SDK码
     * @param deviceName 设备名称
     * @return 添加结果
     */
    @PostMapping("/addBySdk")
    public ReturnVO addDeviceBySdk(HttpServletRequest request, 
                                   @RequestParam String sdkCode,
                                   @RequestParam String deviceName) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        
        try {
            Object userIdObj = request.getSession().getAttribute("userId");
            if (userIdObj == null) {
                returnVO.setMsg("请先登录");
                return returnVO;
            }
            
            // 验证SDK码
            XiaomiBandSdkService.SdkDeviceInfo deviceInfo = xiaomiBandSdkService.validateSdkCode(sdkCode);
            if (deviceInfo == null) {
                returnVO.setMsg("无效的SDK码");
                return returnVO;
            }
            
            // 创建设备对象
            HealthDeviceDomain device = new HealthDeviceDomain();
            device.setUserId((Integer) userIdObj);
            device.setDeviceName(deviceName);
            device.setDeviceType(deviceInfo.getDeviceType());
            device.setDeviceBrand(deviceInfo.getBrand());
            device.setDeviceModel(deviceInfo.getModel());
            device.setDeviceSn(sdkCode);
            device.setConnectionType(deviceInfo.getConnectionType());
            device.setIsActive(1);
            device.setStatus("online");
            
            // 保存到数据库
            boolean result = healthDeviceService.insert(device);
            
            if (result) {
                returnVO.setCode(1);
                returnVO.setMsg("设备添加成功");
                returnVO.setContent(device);
                
                // 自动同步设备数据（使用新插入的设备对象，deviceId应该已经被回填）
                syncDeviceDataAsync(device);
            } else {
                returnVO.setMsg("设备添加失败");
            }
        } catch (Exception e) {
            System.err.println("添加设备异常: " + e.getMessage());
            e.printStackTrace();
            returnVO.setMsg("添加设备时发生错误: " + e.getMessage());
        }
        
        return returnVO;
    }
    
    /**
     * 同步设备数据
     * @param deviceId 设备ID
     * @return 同步结果
     */
    @PostMapping("/sync/{deviceId}")
    public ReturnVO syncDeviceData(@PathVariable Integer deviceId, HttpServletRequest request) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        
        Object userIdObj = request.getSession().getAttribute("userId");
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        // 查询设备信息
        HealthDeviceDomain device = healthDeviceService.queryById(deviceId);
        if (device == null) {
            returnVO.setMsg("设备不存在");
            return returnVO;
        }
        
        // 模拟同步设备数据
        List<HealthMonitoringDataDomain> dataList = xiaomiBandSdkService.syncDeviceData(device.getDeviceSn());
        
        // 保存监测数据
        int successCount = 0;
        for (HealthMonitoringDataDomain data : dataList) {
            data.setUserId((Integer) userIdObj);
            data.setDeviceId(deviceId);
            if (healthMonitoringDataService.insert(data)) {
                successCount++;
            }
        }
        
        // 更新设备最后同步时间
        device.setLastSyncTime(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        healthDeviceService.update(device);
        
        returnVO.setCode(1);
        returnVO.setMsg("同步成功，获取" + successCount + "条数据");
        returnVO.setContent(successCount);
        
        return returnVO;
    }
    
    /**
     * 异步同步设备数据（设备添加后自动调用）
     */
    private void syncDeviceDataAsync(HealthDeviceDomain device) {
        // 在实际应用中，这里应该使用异步任务或消息队列
        // 这里简单模拟同步
        try {
            // 检查deviceId是否已回填
            if (device.getDeviceId() == null) {
                System.err.println("设备ID未回填，尝试通过设备SN查询...");
                // 如果deviceId为null，可能需要重新查询
                // 这里简化处理，直接跳过同步
                return;
            }
            
            List<HealthMonitoringDataDomain> dataList = xiaomiBandSdkService.syncDeviceData(device.getDeviceSn());
            
            for (HealthMonitoringDataDomain data : dataList) {
                data.setUserId(device.getUserId());
                data.setDeviceId(device.getDeviceId());
                healthMonitoringDataService.insert(data);
            }
            
            // 更新最后同步时间
            device.setLastSyncTime(java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            healthDeviceService.update(device);
            
            System.out.println("设备数据同步成功: " + device.getDeviceName() + ", 同步了 " + dataList.size() + " 条数据");
        } catch (Exception e) {
            // 记录错误日志，但不影响设备添加
            System.err.println("设备数据同步失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
