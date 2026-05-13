package com.yq.spbYq.service;

import com.yq.spbYq.domain.UserPrivacyProfilesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (UserPrivacyProfiles)表服务接口
 *
 * @author makejava
 * @since 2025-05-11 10:57:09
 */
public interface UserPrivacyProfilesService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    UserPrivacyProfilesEntity queryById(Integer id);

    /**
     * 分页查询
     *
     * @param userPrivacyProfiles 筛选条件
     * @return 查询结果
     */
    Page<UserPrivacyProfilesEntity> queryByPage(UserPrivacyProfilesEntity userPrivacyProfiles, Integer page, Integer size, String orderCol, String orderDirect);

    /**
     * 新增数据
     *
     * @param userPrivacyProfiles 实例对象
     * @return 实例对象
     */
    boolean insert(UserPrivacyProfilesEntity userPrivacyProfiles);

    /**
     * 修改数据
     *
     * @param userPrivacyProfiles 实例对象
     * @return 实例对象
     */
    boolean update(UserPrivacyProfilesEntity userPrivacyProfiles);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}
