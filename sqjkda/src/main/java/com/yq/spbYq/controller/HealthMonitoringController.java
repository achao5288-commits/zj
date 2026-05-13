package com.yq.spbYq.controller;

import com.yq.spbYq.domain.HealthMonitoringDataDomain;
import com.yq.spbYq.service.HealthMonitoringDataService;
import com.yq.spbYq.util.ReturnVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/healthMonitoring")
public class HealthMonitoringController {
    
    @Resource
    private HealthMonitoringDataService healthMonitoringDataService;

    @GetMapping("/queryByPage")
    public ReturnVO queryByPage(HttpServletRequest request, HealthMonitoringDataDomain data,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "20") Integer size) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        data.setUserId((Integer) userIdObj);
        List<HealthMonitoringDataDomain> list = healthMonitoringDataService.queryByPage(data, page, size);
        
        if (list.isEmpty()) {
            return returnVO;
        }
        
        returnVO = ReturnVO.getSuccessDataReturnVO(list);
        return returnVO;
    }

    @PostMapping("/add")
    public ReturnVO add(HttpServletRequest request, HealthMonitoringDataDomain data) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        data.setUserId((Integer) userIdObj);
        boolean result = healthMonitoringDataService.insert(data);
        
        if (result) {
            returnVO.setCode(1);
            returnVO.setMsg("添加成功");
            returnVO.setContent(data);
        } else {
            returnVO.setMsg("添加失败");
        }
        return returnVO;
    }

    @GetMapping("/statistics/type")
    public ReturnVO getStatisticsByType(HttpServletRequest request) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        List<Map<String, Object>> stats = healthMonitoringDataService.getStatisticsByType((Integer) userIdObj);
        returnVO = ReturnVO.getSuccessDataReturnVO(stats);
        return returnVO;
    }

    @GetMapping("/statistics/date")
    public ReturnVO getStatisticsByDate(HttpServletRequest request, 
                                        @RequestParam(defaultValue = "30") Integer days) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        List<Map<String, Object>> stats = healthMonitoringDataService.getStatisticsByDate((Integer) userIdObj, days);
        returnVO = ReturnVO.getSuccessDataReturnVO(stats);
        return returnVO;
    }
}
