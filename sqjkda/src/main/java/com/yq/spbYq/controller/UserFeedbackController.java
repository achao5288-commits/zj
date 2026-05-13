package com.yq.spbYq.controller;

import com.yq.spbYq.domain.UserFeedbackEntity;
import com.yq.spbYq.service.UserFeedbackService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.yq.spbYq.util.ReturnVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * (UserFeedback)表控制层
 *
 * @author makejava
 * @since 2025-05-11 10:57:07
 */
@RestController
@RequestMapping("userFeedback")
public class UserFeedbackController {
    /**
     * 服务对象
     */
    @Resource
    private UserFeedbackService userFeedbackService;

    /**
     * 分页查询
     *
     * @param userFeedback 筛选条件
     * @return 查询结果
     */
    @RequestMapping("queryByPage")
    public ReturnVO queryByPage(HttpServletRequest request, UserFeedbackEntity userFeedback, Integer page, Integer size, String orderCol, String orderDirect) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userTypeObj = request.getSession().getAttribute("userType");
        Object userIdObj = request.getSession().getAttribute("userId");

        // 普通用户只能查自己的
        if (userTypeObj == null || !userTypeObj.equals(2)) {
            if (userIdObj == null) {
                return returnVO;
            }
            userFeedback.setUserId((Integer) userIdObj);
        }
        // root用户允许前端userId参数生效

        Page<UserFeedbackEntity> pageVO = this.userFeedbackService.queryByPage(
                userFeedback, page, size, orderCol, orderDirect);
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
        UserFeedbackEntity userFeedback = this.userFeedbackService.queryById(id);

        if (userFeedback == null) {
            return returnVO;
        }
        //查询成功
        returnVO = ReturnVO.getSuccessDataReturnVO(userFeedback);

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
     * @param userFeedback 实体
     * @return 新增结果
     */
    @RequestMapping("add")
    public ReturnVO add(HttpServletRequest request, UserFeedbackEntity userFeedback) {
        try {
            System.out.println("开始添加用户反馈，参数：" + userFeedback);
            
            // 获取当前登录用户信息
            Object userIdObj = request.getSession().getAttribute("userId");
            Object userTypeObj = request.getSession().getAttribute("userType");
            
            // 如果未登录，返回错误
            if (userIdObj == null) {
                System.out.println("用户未登录，无法添加反馈");
                ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
                vo.setCode(0);
                vo.setMsg("请先登录");
                return vo;
            }
            
            Integer currentUserId = (Integer) userIdObj;
            Integer currentUserType = userTypeObj != null ? (Integer) userTypeObj : 1;
            
            // 普通用户只能为自己提交反馈，root用户可以为任意用户提交
            if (currentUserType != 2) {
                // 普通用户，强制使用当前登录用户的ID
                userFeedback.setUserId(currentUserId);
            }
            
            // 如果userId为空，使用当前登录用户ID
            if (userFeedback.getUserId() == null) {
                userFeedback.setUserId(currentUserId);
            }
            
            System.out.println("插入反馈数据：userId=" + userFeedback.getUserId() + ", feedbackType=" + userFeedback.getFeedbackType() + ", title=" + userFeedback.getTitle());
            
            //入库
            boolean result = this.userFeedbackService.insert(userFeedback);
            
            if (!result) {
                System.out.println("添加用户反馈失败");
                ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
                vo.setCode(0);
                vo.setMsg("添加失败");
                return vo;
            }
            
            System.out.println("添加用户反馈成功，feedbackId=" + userFeedback.getFeedbackId());
            //入库成功
            ReturnVO returnVO = ReturnVO.getSuccessDataReturnVO(userFeedback);
            returnVO.setCode(1);
            returnVO.setMsg("添加成功");
            return returnVO;
        } catch (Exception e) {
            System.out.println("添加用户反馈异常：" + e.getMessage());
            e.printStackTrace();
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("添加失败：" + e.getMessage());
            return vo;
        }
    }

    /**
     * 编辑数据
     *
     * @param request 请求对象
     * @param userFeedback 实体
     * @return 编辑结果
     */
    @RequestMapping("edit")
    public ReturnVO edit(HttpServletRequest request, UserFeedbackEntity userFeedback) {
        if (!isRootUser(request)) {
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("无权限操作");
            return vo;
        }
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        //修改
        if (!this.userFeedbackService.update(userFeedback)) {
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
        if (!this.userFeedbackService.deleteById(id)) {
            //删除失败
            return returnVO;
        }
        //修改成功
        returnVO.setCode(1);
        returnVO.setMsg("删除成功");

        return returnVO;
    }

}
