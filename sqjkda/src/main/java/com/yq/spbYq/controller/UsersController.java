package com.yq.spbYq.controller;

import com.yq.spbYq.domain.UsersEntity;
import com.yq.spbYq.service.UsersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.yq.spbYq.util.ReturnVO;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import static com.yq.spbYq.util.PasswordUtil.degistPwd;

/**
 * (Users)表控制层
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@RestController
@RequestMapping("users")
public class UsersController {
    /**
     * 服务对象
     */
    @Resource
    private UsersService usersService;

    /**
     * 分页查询
     *
     * @param request 请求对象
     * @param users 筛选条件
     * @return 查询结果
     */
    @RequestMapping("queryByPage")
    public ReturnVO queryByPage(HttpServletRequest request, UsersEntity users, Integer page, Integer size, String orderCol, String orderDirect) {
        // 权限校验：只有root用户可以查看用户列表
        if (!isRootUser(request)) {
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("无权限操作，仅管理员可查看用户管理");
            return vo;
        }
        
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();

        //查询
        Page<UsersEntity> pageVO =
                this.usersService.queryByPage(
                        users,
                        page, size,
                        orderCol, orderDirect);
        //没有数据
        if (pageVO.getContent().isEmpty()) {
            return returnVO;
        }
        //查询成功
        returnVO = ReturnVO.getSuccessDataReturnVO(pageVO);
        return returnVO;
    }

    /**
     * 通过主键查询单条数据
     *
     * @param request 请求对象
     * @param id 主键
     * @return 单条数据
     */
    @RequestMapping("queryById/{id}")
    public ReturnVO queryById(HttpServletRequest request, @PathVariable("id") Integer id) {
        // 权限校验：只有root用户可以查看用户详情
        if (!isRootUser(request)) {
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("无权限操作，仅管理员可查看用户信息");
            return vo;
        }
        
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        //查询单个
        UsersEntity users = this.usersService.queryById(id);

        if (users == null) {
            return returnVO;
        }
        //查询成功
        returnVO = ReturnVO.getSuccessDataReturnVO(users);

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
     * @param users 实体
     * @return 新增结果
     */
    @RequestMapping("add")
    public ReturnVO add(HttpServletRequest request, UsersEntity users) {
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        String regex = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(users.getPhoneNumber());
        if (!matcher.matches()){
            returnVO.setCode(0);
            returnVO.setMsg("手机格式错误");
            return returnVO;
        }
        if(this.usersService.isExistAccount(users.getPhoneNumber())){
            returnVO.setCode(0);
            returnVO.setMsg("账号已存在");
            return returnVO;
        }

        //判断密码是否符合
        String regexPw = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,16}$";
        Pattern patternPw = Pattern.compile(regexPw);
        Matcher matcherPw = patternPw.matcher(users.getPasswordHash());
        if (!matcherPw.matches()){
            returnVO.setCode(0);
            returnVO.setMsg("密码格式错误");
            return returnVO;
        }

        //对密码加密
        users.setPasswordHash(degistPwd(users.getPasswordHash()));
        //userType默认1普通用户
        if (users.getUserType() == null) {
            users.setUserType(1);
        }
        //入库
        if (!this.usersService.insert(users)) {
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
     * @param users 实体
     * @return 编辑结果
     */
    @RequestMapping("edit")
    public ReturnVO edit(HttpServletRequest request, UsersEntity users) {
        if (!isRootUser(request)) {
            ReturnVO vo = ReturnVO.getNodataFoundReturnVO();
            vo.setCode(0);
            vo.setMsg("无权限操作");
            return vo;
        }
        //定义失败的返回对象
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        //userType默认1普通用户
        if (users.getUserType() == null) {
            users.setUserType(1);
        }
        //修改
        if (!this.usersService.update(users)) {
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
        if (!this.usersService.deleteById(id)) {
            //删除失败
            return returnVO;
        }
        //修改成功
        returnVO.setCode(1);
        returnVO.setMsg("删除成功");

        return returnVO;
    }
    @RequestMapping("login")
    public ReturnVO login(HttpServletRequest request, UsersEntity users){
        ReturnVO returnVO = ReturnVO.getNodataFoundReturnVO();
        users.setPasswordHash(degistPwd(users.getPasswordHash()));
        UsersEntity user = this.usersService.loginAndGetUser(users);
        if (user == null) {
            returnVO.setCode(0);
            returnVO.setMsg("登陆失败");
            return returnVO;
        }
        // 登录成功，写入 session
        request.getSession().setAttribute("userType", user.getUserType());
        request.getSession().setAttribute("userId", user.getUserId());
        returnVO.setCode(1);
        returnVO.setMsg("登陆成功");
        returnVO.setContent(user);
        return returnVO;
    }
}
