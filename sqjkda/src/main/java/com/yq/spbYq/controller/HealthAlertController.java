package com.yq.spbYq.controller;

import com.yq.spbYq.domain.HealthAlertDomain;
import com.yq.spbYq.service.HealthAlertService;
import com.yq.spbYq.util.ReturnVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/healthAlert")
public class HealthAlertController {
    
    @Resource
    private HealthAlertService healthAlertService;

    @GetMapping("/queryByPage")
    public ReturnVO queryByPage(HttpServletRequest request, HealthAlertDomain alert,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "20") Integer size) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        alert.setUserId((Integer) userIdObj);
        List<HealthAlertDomain> list = healthAlertService.queryByPage(alert, page, size);
        int total = healthAlertService.countByPage(alert);
        
        if (list.isEmpty()) {
            return returnVO;
        }
        
        returnVO = ReturnVO.getSuccessDataReturnVO(list);
        return returnVO;
    }

    @GetMapping("/unread/count")
    public ReturnVO getUnreadCount(HttpServletRequest request) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        int count = healthAlertService.countUnreadByUserId((Integer) userIdObj);
        returnVO = ReturnVO.getSuccessDataReturnVO(count);
        return returnVO;
    }

    @GetMapping("/unread")
    public ReturnVO getUnreadAlerts(HttpServletRequest request) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        List<HealthAlertDomain> list = healthAlertService.queryUnreadByUserId((Integer) userIdObj);
        returnVO = ReturnVO.getSuccessDataReturnVO(list);
        return returnVO;
    }

    @PostMapping("/markAsRead/{alertId}")
    public ReturnVO markAsRead(HttpServletRequest request, @PathVariable Integer alertId) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        boolean result = healthAlertService.markAsRead(alertId);
        if (result) {
            returnVO.setCode(1);
            returnVO.setMsg("已标记为已读");
        } else {
            returnVO.setMsg("操作失败");
        }
        return returnVO;
    }

    @PostMapping("/handle")
    public ReturnVO handleAlert(HttpServletRequest request, 
                               @RequestParam Integer alertId,
                               @RequestParam String handleResult) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userIdObj = request.getSession().getAttribute("userId");
        Object userTypeObj = request.getSession().getAttribute("userType");
        
        if (userIdObj == null) {
            returnVO.setMsg("请先登录");
            return returnVO;
        }
        
        boolean result = healthAlertService.handleAlert(alertId, handleResult);
        if (result) {
            returnVO.setCode(1);
            returnVO.setMsg("处理成功");
        } else {
            returnVO.setMsg("处理失败");
        }
        return returnVO;
    }
}
