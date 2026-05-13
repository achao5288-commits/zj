package com.yq.spbYq.controller;

import com.yq.spbYq.domain.HealthRemindersEntity;
import com.yq.spbYq.service.HealthRemindersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.yq.spbYq.util.ReturnVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * (HealthReminders)表控制层
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@RestController
@RequestMapping("healthReminders")
public class HealthRemindersController {
    /**
     * 服务对象
     */
    @Resource
    private HealthRemindersService healthRemindersService;

    /**
     * 分页查询
     *
     * @param healthReminders 筛选条件
     * @return 查询结果
     */
    @RequestMapping("queryByPage")
    public ReturnVO queryByPage(HttpServletRequest request, HealthRemindersEntity healthReminders, Integer page, Integer size, String orderCol, String orderDirect) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userTypeObj = request.getSession().getAttribute("userType");
        Object userIdObj = request.getSession().getAttribute("userId");

        // 普通用户只能查自己的
        if (userTypeObj == null || !userTypeObj.equals(2)) {
            if (userIdObj == null) {
                return returnVO;
            }
            healthReminders.setUserId((Integer) userIdObj);
        }
        // root用户允许前端userId参数生效

        Page<HealthRemindersEntity> pageVO = this.healthRemindersService.queryByPage(
                healthReminders, page, size, orderCol, orderDirect);
        if (pageVO.getContent().isEmpty()) {
            return returnVO;
        }
        returnVO = ReturnVO.getSuccessDataReturnVO(pageVO);
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
        HealthRemindersEntity healthReminders = this.healthRemindersService.queryById(id);

        if (healthReminders == null) {
            return returnVO;
        }
        //查询成功
        returnVO = ReturnVO.getSuccessDataReturnVO(healthReminders);

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
     * @param healthReminders 实体
     * @return 新增结果
     */
    @RequestMapping("add")
    public ReturnVO add(HttpServletRequest request, HealthRemindersEntity healthReminders) {
        if (!isRootUser(request)) {
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("无权限操作");
            return vo;
        }
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        //入库
        if (!this.healthRemindersService.insert(healthReminders)) {
            //入库失败
            return returnVO;
        }
        //入库成功
        returnVO.setCode(1);
        returnVO.setMsg("添加成功");

        return returnVO;
    }

    /**
     * 编辑数据
     *
     * @param request 请求对象
     * @param healthReminders 实体
     * @return 编辑结果
     */
    @RequestMapping("edit")
    public ReturnVO edit(HttpServletRequest request, HealthRemindersEntity healthReminders) {
        if (!isRootUser(request)) {
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("无权限操作");
            return vo;
        }
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        //修改
        if (!this.healthRemindersService.update(healthReminders)) {
            //修改失败
            return returnVO;
        }
        //修改成功
        returnVO.setCode(1);
        returnVO.setMsg("修改成功");

        return returnVO;
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
        if (!isRootUser(request)) {
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("无权限操作");
            return vo;
        }
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        //删除
        if (!this.healthRemindersService.deleteById(id)) {
            //删除失败
            return returnVO;
        }
        //修改成功
        returnVO.setCode(1);
        returnVO.setMsg("删除成功");

        return returnVO;
    }

}
