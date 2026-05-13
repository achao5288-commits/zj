package com.yq.spbYq.service;

import com.yq.spbYq.domain.HealthRemindersEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (HealthReminders)表服务接口
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
public interface HealthRemindersService {

    /**
     * 通过ID查询单条数据
     *
     * @param reminderId 主键
     * @return 实例对象
     */
    HealthRemindersEntity queryById(Integer reminderId);

    /**
     * 分页查询
     *
     * @param healthReminders 筛选条件
     * @return 查询结果
     */
    Page<HealthRemindersEntity> queryByPage(HealthRemindersEntity healthReminders, Integer page, Integer size, String orderCol, String orderDirect);

    /**
     * 新增数据
     *
     * @param healthReminders 实例对象
     * @return 实例对象
     */
    boolean insert(HealthRemindersEntity healthReminders);

    /**
     * 修改数据
     *
     * @param healthReminders 实例对象
     * @return 实例对象
     */
    boolean update(HealthRemindersEntity healthReminders);

    /**
     * 通过主键删除数据
     *
     * @param reminderId 主键
     * @return 是否成功
     */
    boolean deleteById(Integer reminderId);

}
