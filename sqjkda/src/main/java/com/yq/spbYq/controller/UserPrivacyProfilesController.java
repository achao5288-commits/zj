package com.yq.spbYq.controller;

import com.yq.spbYq.domain.UserPrivacyProfilesEntity;
import com.yq.spbYq.service.UserPrivacyProfilesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.yq.spbYq.util.ReturnVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * (UserPrivacyProfiles)表控制层
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@RestController
@RequestMapping("userPrivacyProfiles")
public class UserPrivacyProfilesController {
    /**
     * 服务对象
     */
    @Resource
    private UserPrivacyProfilesService userPrivacyProfilesService;

    /**
     * 分页查询
     *
     * @param userPrivacyProfiles 筛选条件
     * @return 查询结果
     */
    @RequestMapping("queryByPage")
    public ReturnVO queryByPage(HttpServletRequest request, UserPrivacyProfilesEntity userPrivacyProfiles, Integer page, Integer size, String orderCol, String orderDirect) {
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        Object userTypeObj = request.getSession().getAttribute("userType");
        Object userIdObj = request.getSession().getAttribute("userId");

        // 普通用户只能查自己的
        if (userTypeObj == null || !userTypeObj.equals(2)) {
            if (userIdObj == null) {
                return returnVO;
            }
            userPrivacyProfiles.setUserId((Integer) userIdObj);
        }
        // root用户允许前端userId参数生效

        Page<UserPrivacyProfilesEntity> pageVO = this.userPrivacyProfilesService.queryByPage(
                userPrivacyProfiles, page, size, orderCol, orderDirect);
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
            //定义失败的返回对象
            ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            
            if (id == null || id <= 0) {
                returnVO.setMsg("ID参数无效");
                return returnVO;
            }
            
            //查询单个
            UserPrivacyProfilesEntity userPrivacyProfiles = this.userPrivacyProfilesService.queryById(id);

            if (userPrivacyProfiles == null) {
                returnVO.setMsg("未找到ID为" + id + "的记录");
                return returnVO;
            }
            //查询成功
            returnVO = ReturnVO.getSuccessDataReturnVO(userPrivacyProfiles);

            return returnVO;
        } catch (Exception e) {
            ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
            returnVO.setMsg("查询失败: " + e.getMessage());
            e.printStackTrace();
            return returnVO;
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
     * @param userPrivacyProfiles 实体
     * @return 新增结果
     */
    @RequestMapping("add")
    public ReturnVO add(HttpServletRequest request, UserPrivacyProfilesEntity userPrivacyProfiles) {
        Object userTypeObj = request.getSession().getAttribute("userType");
        if (userTypeObj == null || !userTypeObj.equals(2)) {
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("无权限操作");
            return vo;
        }
        // root用户可操作
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        if (!this.userPrivacyProfilesService.insert(userPrivacyProfiles)) {
            return returnVO;
        }
        returnVO.setCode(1);
        returnVO.setMsg("添加成功");
        return returnVO;
    }

    /**
     * 编辑数据
     *
     * @param request 请求对象
     * @param userPrivacyProfiles 实体
     * @return 编辑结果
     */
    @RequestMapping("edit")
    public ReturnVO edit(HttpServletRequest request, UserPrivacyProfilesEntity userPrivacyProfiles) {
        Object userTypeObj = request.getSession().getAttribute("userType");
        if (userTypeObj == null || !userTypeObj.equals(2)) {
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("无权限操作");
            return vo;
        }
        // root用户可操作
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        if (!this.userPrivacyProfilesService.update(userPrivacyProfiles)) {
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
        Object userTypeObj = request.getSession().getAttribute("userType");
        if (userTypeObj == null || !userTypeObj.equals(2)) {
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("无权限操作");
            return vo;
        }
        // root用户可操作
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        if (!this.userPrivacyProfilesService.deleteById(id)) {
            return returnVO;
        }
        returnVO.setCode(1);
        returnVO.setMsg("删除成功");
        return returnVO;
    }

}
