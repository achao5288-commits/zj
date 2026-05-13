package com.yq.spbYq.service.impl;

import com.yq.spbYq.domain.UserPrivacyProfilesEntity;
import com.yq.spbYq.mapper.UserPrivacyProfilesMapper;
import com.yq.spbYq.service.UserPrivacyProfilesService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;

/**
 * (UserPrivacyProfiles)表服务实现类
 *
 * @author makejava
 * @since 2025-05-11 10:57:09
 */
@Service("userPrivacyProfilesService")
public class UserPrivacyProfilesServiceImpl implements UserPrivacyProfilesService {
    private static final int PAGE_DEFAULT = 1;
    private static final int SIZE_DEFAULT = 10;
    @Resource
    private UserPrivacyProfilesMapper userPrivacyProfilesMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public UserPrivacyProfilesEntity queryById(Integer id) {
        return this.userPrivacyProfilesMapper.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param userPrivacyProfiles 筛选条件
     * @return 查询结果
     */
    @Override
    public Page<UserPrivacyProfilesEntity> queryByPage(UserPrivacyProfilesEntity userPrivacyProfiles, Integer page, Integer size, String orderCol, String orderDirect) {
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

        long total = this.userPrivacyProfilesMapper.count(userPrivacyProfiles);

        return new PageImpl<>(this.userPrivacyProfilesMapper.queryAllByLimit(userPrivacyProfiles, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param userPrivacyProfiles 实例对象
     * @return 实例对象
     */
    @Override
    public boolean insert(UserPrivacyProfilesEntity userPrivacyProfiles) {
        return this.userPrivacyProfilesMapper.insert(userPrivacyProfiles) > 0;
    }

    /**
     * 修改数据
     *
     * @param userPrivacyProfiles 实例对象
     * @return 实例对象
     */
    @Override
    public boolean update(UserPrivacyProfilesEntity userPrivacyProfiles) {
        return this.userPrivacyProfilesMapper.update(userPrivacyProfiles) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.userPrivacyProfilesMapper.deleteById(id) > 0;
    }
}
