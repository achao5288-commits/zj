package com.yq.spbYq.service.impl;

import com.yq.spbYq.domain.UsersEntity;
import com.yq.spbYq.mapper.UsersMapper;
import com.yq.spbYq.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Users)表服务实现类
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@Service("usersService")
public class UsersServiceImpl implements UsersService {
    private static final int PAGE_DEFAULT = 1;
    private static final int SIZE_DEFAULT = 10;
    @Resource
    private UsersMapper usersMapper;
    @Autowired
    private StringHttpMessageConverter stringHttpMessageConverter;

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    @Override
    public UsersEntity queryById(Integer userId) {
        return this.usersMapper.queryById(userId);
    }

    /**
     * 分页查询
     *
     * @param users 筛选条件
     * @return 查询结果
     */
    @Override
    public Page<UsersEntity> queryByPage(UsersEntity users, Integer page, Integer size, String orderCol, String orderDirect) {

        if (page == null || page <= 0) {
            page = PAGE_DEFAULT;
        }
        if (size == null || size <= 0) {
            size = SIZE_DEFAULT;
        }

        Sort sort = null;
        if (orderCol != null) {
            Sort.Order order = new Sort.Order(("DESC".equals(orderDirect) ? Sort.Direction.DESC : Sort.Direction.ASC), orderCol);
            sort = Sort.by(order);
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        if (sort != null) {
            pageRequest = PageRequest.of(page - 1, size, sort);
        }

        long total = this.usersMapper.count(users);

        return new PageImpl<>(this.usersMapper.queryAllByLimit(users, pageRequest), pageRequest, total);
    }
     /**
     @param users 实例对象
     * @return 实例对象
     */
    @Override
    public boolean insert(UsersEntity users) {
        return this.usersMapper.insert(users) > 0;
    }

    /**
     * 修改数据
     *
     * @param users 实例对象
     * @return 实例对象
     */
    @Override
    public boolean update(UsersEntity users) {
        return this.usersMapper.update(users) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param userId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer userId) {
        return this.usersMapper.deleteById(userId) > 0;
    }
    @Override
    public boolean isExistAccount(String phoneNumber){
        int page = 1;
        int size = 10;
        String orderCol = "userId";
        String orderDirect = "DESC";
        Sort sort = null;

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        UsersEntity users1 = new UsersEntity();
        users1.setPhoneNumber(phoneNumber);

        List list =this.usersMapper.queryAllByLimit(users1,pageRequest);
        for (Object object : list) {
            System.out.println(object);
        }
        if (list.size()>0) {
            return true;
        }
        return false;
    }


    @Override
    public boolean login (UsersEntity users){

        List list = this.usersMapper.queryAllByLimit(users,null);
        if (list.size()>0) {
            return true;
        }
        return false;
    }

    @Override
    public UsersEntity loginAndGetUser(UsersEntity users) {
        return this.usersMapper.queryOneForLogin(users);
    }
}
