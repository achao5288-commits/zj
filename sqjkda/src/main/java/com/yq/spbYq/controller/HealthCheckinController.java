package com.yq.spbYq.controller;

import com.yq.spbYq.domain.HealthCheckinDomain;
import com.yq.spbYq.service.HealthCheckinService;
import com.yq.spbYq.util.ReturnVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/healthCheckin")
public class HealthCheckinController {
    
    @Resource
    private HealthCheckinService healthCheckinService;

    @GetMapping("/queryByPage")
    public ReturnVO queryByPage(HttpServletRequest request, HealthCheckinDomain checkin,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer size) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        checkin.setUserId((Integer) userIdObj);
        List<HealthCheckinDomain> list = healthCheckinService.queryByPage(checkin, page, size);
        
        if (list.isEmpty()) {
            return returnVO;
        }
        
        returnVO = ReturnVO.getSuccessDataReturnVO(list);
        return returnVO;
    }

    @GetMapping("/today")
    public ReturnVO getTodayCheckin(HttpServletRequest request) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        String today = java.time.LocalDate.now().toString();
        HealthCheckinDomain checkin = healthCheckinService.queryByUserIdAndDate((Integer) userIdObj, today);
        
        if (checkin != null) {
            returnVO = ReturnVO.getSuccessDataReturnVO(checkin);
        }
        return returnVO;
    }

    @PostMapping("/add")
    public ReturnVO add(HttpServletRequest request, HealthCheckinDomain checkin) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        
        try {
            Object userIdObj = request.getSession().getAttribute("userId");
            
            if (userIdObj == null) {
                returnVO.setMsg("请先登录");
                return returnVO;
            }
            
            checkin.setUserId((Integer) userIdObj);
            if (checkin.getCheckinDate() == null) {
                checkin.setCheckinDate(java.time.LocalDate.now().toString());
            }
            
            // 检查今天是否已打卡
            HealthCheckinDomain existing = healthCheckinService.queryByUserIdAndDate(
                (Integer) userIdObj, checkin.getCheckinDate());
            
            boolean result;
            if (existing != null) {
                // 更新已有打卡
                checkin.setCheckinId(existing.getCheckinId());
                result = healthCheckinService.update(checkin);
                if (result) {
                    returnVO.setCode(1);
                    returnVO.setMsg("打卡已更新");
                }
            } else {
                // 新增打卡
                result = healthCheckinService.insert(checkin);
                if (result) {
                    returnVO.setCode(1);
                    returnVO.setMsg("打卡成功");
                }
            }
            
            if (!result) {
                returnVO.setMsg("打卡失败，请稍后重试");
            }
            
            returnVO.setContent(checkin);
        } catch (Exception e) {
            System.err.println("健康打卡异常: " + e.getMessage());
            e.printStackTrace();
            
            // 判断是否是重复打卡的错误
            if (e.getMessage() != null && e.getMessage().contains("Duplicate")) {
                returnVO.setMsg("今天已经打卡过了");
            } else {
                returnVO.setMsg("打卡失败: " + e.getMessage());
            }
        }
        
        return returnVO;
    }
}
