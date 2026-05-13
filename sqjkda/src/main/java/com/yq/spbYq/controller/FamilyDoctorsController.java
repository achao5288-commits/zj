package com.yq.spbYq.controller;

import com.yq.spbYq.domain.FamilyDoctorsEntity;
import com.yq.spbYq.service.FamilyDoctorsService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.yq.spbYq.util.ReturnVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * (FamilyDoctors) 表控制层
 *
 * @author makejava
 * @since 2025-05-11 10:57:06
 */
@RestController
@RequestMapping("familyDoctors")
public class FamilyDoctorsController {
    /**
     * 服务对象
     */
    @Resource
    private FamilyDoctorsService familyDoctorsService;

    /**
     * 分页查询
     *
     * @param request 请求对象
     * @param familyDoctors 筛选条件
     * @return 查询结果
     */
    @RequestMapping("queryByPage")
    public ReturnVO queryByPage(HttpServletRequest request, FamilyDoctorsEntity familyDoctors, Integer page, Integer size, String orderCol, String orderDirect) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userTypeObj = request.getSession().getAttribute("userType");
        Object userIdObj = request.getSession().getAttribute("userId");

        // 普通用户只能查自己的
        if (userTypeObj == null || !userTypeObj.equals(2)) {
            if (userIdObj == null) {
                return returnVO;
            }
            familyDoctors.setUserId((Integer) userIdObj);
        }
        // root 用户允许前端 userId 参数生效

        Page<FamilyDoctorsEntity> pageVO = this.familyDoctorsService.queryByPage(
                familyDoctors, page, size, orderCol, orderDirect);
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
        try {
            ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            FamilyDoctorsEntity familyDoctors = this.familyDoctorsService.queryById(id);

            if (familyDoctors == null) {
                return returnVO;
            }
            returnVO = ReturnVO.getSuccessDataReturnVO(familyDoctors);

            return returnVO;
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnVO.error("查询失败：" + e.getMessage());
        }
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
     * @param familyDoctors 实体
     * @return 新增结果
     */
    @RequestMapping("add")
    public ReturnVO add(HttpServletRequest request, FamilyDoctorsEntity familyDoctors) {
        try {
            if (!isRootUser(request)) {
                ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
                vo.setCode(0);
                vo.setMsg("无权限操作");
                return vo;
            }
            ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            if (!this.familyDoctorsService.insert(familyDoctors)) {
                return returnVO;
            }
            returnVO.setCode(1);
            returnVO.setMsg("添加成功");

            return returnVO;
        } catch (Exception e) {
            e.printStackTrace();
            ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            returnVO.setMsg("添加失败: " + e.getMessage());
            return returnVO;
        }
    }

    /**
     * 编辑数据
     *
     * @param request 请求对象
     * @param familyDoctors 实体
     * @return 编辑结果
     */
    @RequestMapping("edit")
    public ReturnVO edit(HttpServletRequest request, FamilyDoctorsEntity familyDoctors) {
        if (!isRootUser(request)) {
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("无权限操作");
            return vo;
        }
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        if (!this.familyDoctorsService.update(familyDoctors)) {
            return returnVO;
        }
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
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        if (!this.familyDoctorsService.deleteById(id)) {
            return returnVO;
        }
        returnVO.setCode(1);
        returnVO.setMsg("删除成功");

        return returnVO;
    }

}
