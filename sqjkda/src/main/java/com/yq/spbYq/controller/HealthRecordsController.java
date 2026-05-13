package com.yq.spbYq.controller;

import com.yq.spbYq.domain.HealthRecordsEntity;
import com.yq.spbYq.service.HealthRecordsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.yq.spbYq.util.ReturnVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * (HealthRecords)表控制层
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@RestController
@RequestMapping("healthRecords")
public class HealthRecordsController {
    /**
     * 服务对象
     */
    @Resource
    private HealthRecordsService healthRecordsService;

    /**
     * 分页查询
     *
     * @param healthRecords 筛选条件
     * @return 查询结果
     */
    @RequestMapping("queryByPage")
    public ReturnVO queryByPage(HttpServletRequest request, HealthRecordsEntity healthRecords, Integer page, Integer size, String orderCol, String orderDirect) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userTypeObj = request.getSession().getAttribute("userType");
        Object userIdObj = request.getSession().getAttribute("userId");
    
        // 普通用户只能查自己的
        if (userTypeObj == null || !userTypeObj.equals(2)) {
            if (userIdObj == null) {
                return returnVO;
            }
            healthRecords.setUserId((Integer) userIdObj);
        }
        // root 用户允许前端 userId 参数生效
    
        Page<HealthRecordsEntity> pageVO = this.healthRecordsService.queryByPage(
                healthRecords, page, size, orderCol, orderDirect);
        if (pageVO.getContent().isEmpty()) {
            return returnVO;
        }
        returnVO = ReturnVO.getSuccessDataReturnVO(pageVO);
        return returnVO;
    }
    
    /**
     * 查询用户的所有健康记录（不分页）
     *
     * @param userId 用户 ID
     * @return 查询结果
     */
    @GetMapping("list")
    public ReturnVO list(@RequestParam Integer userId) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            
        if (userId == null) {
            return returnVO;
        }
            
        // 查询该用户的所有记录
        HealthRecordsEntity healthRecords = new HealthRecordsEntity();
        healthRecords.setUserId(userId);
            
        // 使用分页查询但返回所有数据
        Page<HealthRecordsEntity> pageVO = this.healthRecordsService.queryByPage(
                healthRecords, 1, 1000, "report_date", "DESC");
            
        if (pageVO.getContent().isEmpty()) {
            return returnVO;
        }
            
        returnVO = ReturnVO.getSuccessDataReturnVO(pageVO.getContent());
        return returnVO;
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @RequestMapping("queryById/{id}")
    public ReturnVO queryById(@PathVariable("id") Integer id) {
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        //查询单个
        HealthRecordsEntity healthRecords = this.healthRecordsService.queryById(id);

        if (healthRecords == null) {
            return returnVO;
        }
        //查询成功
        returnVO = ReturnVO.getSuccessDataReturnVO(healthRecords);

        return returnVO;
    }

    // 权限校验方法
    private boolean isRootUser(HttpServletRequest request) {
        Object userTypeObj = request.getSession().getAttribute("userType");
        return userTypeObj != null && userTypeObj.equals(2);
    }

    /**
     * 新增数据
     *
     * @param request 请求对象
     * @param healthRecords 实体
     * @return 新增结果
     */
    @RequestMapping("add")
    public ReturnVO add(HttpServletRequest request, HealthRecordsEntity healthRecords) {
        try {
            //定义失败的返回对象
            ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            
            // 检查登录状态
            Object userTypeObj = request.getSession().getAttribute("userType");
            Object userIdObj = request.getSession().getAttribute("userId");
            
            if (userTypeObj == null || userIdObj == null) {
                returnVO.setCode(0);
                returnVO.setMsg("请先登录");
                return returnVO;
            }
            
            Integer userType = (Integer) userTypeObj;
            
            // 普通用户只能添加自己的记录
            if (userType != 2) {
                // 自动设置userId为当前登录用户
                healthRecords.setUserId((Integer) userIdObj);
            }
            // root用户可以为任意用户添加记录（需要前端传入userId）
            
            // 检查必填字段
            if (healthRecords.getUserId() == null) {
                returnVO.setCode(0);
                returnVO.setMsg("用户ID不能为空");
                return returnVO;
            }
            
            // 入库
            boolean result = this.healthRecordsService.insert(healthRecords);
            if (!result) {
                returnVO.setCode(0);
                returnVO.setMsg("添加失败，请检查数据");
                return returnVO;
            }
            
            //入库成功，返回生成的id
            returnVO.setCode(1);
            returnVO.setMsg("添加成功");
            returnVO.setContent(healthRecords);  // 返回包含自动生成id的完整数据

            return returnVO;
        } catch (Exception e) {
            e.printStackTrace();
            ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            returnVO.setCode(0);
            returnVO.setMsg("添加失败：" + e.getMessage());
            return returnVO;
        }
    }

    /**
     * 编辑数据
     *
     * @param request 请求对象
     * @param healthRecords 实体
     * @return 编辑结果
     */
    @RequestMapping("edit")
    public ReturnVO edit(HttpServletRequest request, HealthRecordsEntity healthRecords) {
        try {
            // 检查登录状态
            Object userTypeObj = request.getSession().getAttribute("userType");
            if (userTypeObj == null) {
                ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
                vo.setCode(0);
                vo.setMsg("请先登录");
                return vo;
            }
            
            // 权限检查
            if (!isRootUser(request)) {
                ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
                vo.setCode(0);
                vo.setMsg("无权限操作，需要root权限");
                return vo;
            }
            
            // 检查id是否有效
            if (healthRecords.getId() == null) {
                ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
                vo.setCode(0);
                vo.setMsg("ID不能为空");
                return vo;
            }
            
            // 修改
            boolean result = this.healthRecordsService.update(healthRecords);
            if (!result) {
                ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
                returnVO.setCode(0);
                returnVO.setMsg("修改失败，记录可能不存在");
                return returnVO;
            }
            
            // 修改成功
            ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            returnVO.setCode(1);
            returnVO.setMsg("修改成功");
            return returnVO;
        } catch (Exception e) {
            e.printStackTrace();
            ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            returnVO.setCode(0);
            returnVO.setMsg("修改失败：" + e.getMessage());
            return returnVO;
        }
    }

    /**
     * 删除数据
     *
     * @param request 请求对象
     * @param id 主键
     * @return 删除是否成功
     */
    @RequestMapping("deleteById/{id}")
    public ReturnVO deleteById(HttpServletRequest request, @PathVariable("id") Integer id) {
        try {
            // 检查登录状态
            Object userTypeObj = request.getSession().getAttribute("userType");
            if (userTypeObj == null) {
                ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
                vo.setCode(0);
                vo.setMsg("请先登录");
                return vo;
            }
            
            // 权限检查
            if (!isRootUser(request)) {
                ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
                vo.setCode(0);
                vo.setMsg("无权限操作，需要root权限");
                return vo;
            }
            
            // 检查id是否有效
            if (id == null) {
                ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
                vo.setCode(0);
                vo.setMsg("ID不能为空");
                return vo;
            }
            
            // 删除
            boolean result = this.healthRecordsService.deleteById(id);
            if (!result) {
                ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
                returnVO.setCode(0);
                returnVO.setMsg("删除失败，记录可能不存在");
                return returnVO;
            }
            
            // 删除成功
            ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            returnVO.setCode(1);
            returnVO.setMsg("删除成功");
            return returnVO;
        } catch (Exception e) {
            e.printStackTrace();
            ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            returnVO.setCode(0);
            returnVO.setMsg("删除失败：" + e.getMessage());
            return returnVO;
        }
    }

}
