package com.yq.spbYq.controller;

import com.yq.spbYq.domain.HealthDeviceDomain;
import com.yq.spbYq.service.HealthDeviceService;
import com.yq.spbYq.util.ReturnVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/healthDevice")
public class HealthDeviceController {
    
    @Resource
    private HealthDeviceService healthDeviceService;

    @GetMapping("/queryByPage")
    public ReturnVO queryByPage(HttpServletRequest request, HealthDeviceDomain device, 
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer size) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        Object userTypeObj = request.getSession().getAttribute("userType");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        // 普通用户只能查自己的设备
        if (!userTypeObj.equals(2)) {
            device.setUserId((Integer) userIdObj);
        }
        
        List<HealthDeviceDomain> list = healthDeviceService.queryByPage(device, page, size);
        int total = healthDeviceService.countByPage(device);
        
        if (list.isEmpty()) {
            return returnVO;
        }
        
        returnVO = ReturnVO.getSuccessDataReturnVO(list);
        returnVO.setContent(new org.springframework.data.domain.PageImpl<>(list, 
            org.springframework.data.domain.PageRequest.of(page - 1, size), total));
        return returnVO;
    }

    @GetMapping("/queryById/{deviceId}")
    public ReturnVO queryById(@PathVariable Integer deviceId) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        HealthDeviceDomain device = healthDeviceService.queryById(deviceId);
        if (device == null) {
            return returnVO;
        }
        returnVO = ReturnVO.getSuccessDataReturnVO(device);
        return returnVO;
    }

    @PostMapping("/add")
    public ReturnVO add(HttpServletRequest request, HealthDeviceDomain device) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        device.setUserId((Integer) userIdObj);
        boolean result = healthDeviceService.insert(device);
        
        if (result) {
            returnVO.setCode(1);
            returnVO.setMsg("添加成功");
            returnVO.setContent(device);
        } else {
            returnVO.setMsg("添加失败");
        }
        return returnVO;
    }

    @PostMapping("/edit")
    public ReturnVO edit(HttpServletRequest request, HealthDeviceDomain device) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        Object userTypeObj = request.getSession().getAttribute("userType");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        // 权限检查
        if (!userTypeObj.equals(2)) {
            HealthDeviceDomain existing = healthDeviceService.queryById(device.getDeviceId());
            if (existing == null || !existing.getUserId().equals(userIdObj)) {
                returnVO.setMsg("无权限操作");
                return returnVO;
            }
        }
        
        boolean result = healthDeviceService.update(device);
        if (result) {
            returnVO.setCode(1);
            returnVO.setMsg("修改成功");
        } else {
            returnVO.setMsg("修改失败");
        }
        return returnVO;
    }

    @PostMapping("/deleteById/{deviceId}")
    public ReturnVO deleteById(HttpServletRequest request, @PathVariable Integer deviceId) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userTypeObj = request.getSession().getAttribute("userType");
        
        if (userTypeObj == null || !userTypeObj.equals(2)) {
            returnVO.setMsg("无权限操作");
            return returnVO;
        }
        
        boolean result = healthDeviceService.deleteById(deviceId);
        if (result) {
            returnVO.setCode(1);
            returnVO.setMsg("删除成功");
        } else {
            returnVO.setMsg("删除失败");
        }
        return returnVO;
    }
}
