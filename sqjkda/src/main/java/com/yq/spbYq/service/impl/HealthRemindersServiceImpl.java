package com.yq.spbYq.service.impl;

import com.yq.spbYq.domain.HealthRemindersEntity;
import com.yq.spbYq.mapper.HealthRemindersMapper;
import com.yq.spbYq.service.HealthRemindersService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;

/**
 * (HealthReminders)表服务实现类
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@Service("healthRemindersService")
public class HealthRemindersServiceImpl implements HealthRemindersService {
    private static final int PAGE_DEFAULT = 1;
    private static final int SIZE_DEFAULT = 10;
    @Resource
    private HealthRemindersMapper healthRemindersMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param reminderId 主键
     * @return 实例对象
     */
    @Override
    public HealthRemindersEntity queryById(Integer reminderId) {
        return this.healthRemindersMapper.queryById(reminderId);
    }

    /**
     * 分页查询
     *
     * @param healthReminders 筛选条件
     * @return 查询结果
     */
    @Override
    public Page<HealthRemindersEntity> queryByPage(HealthRemindersEntity healthReminders, Integer page, Integer size, String orderCol, String orderDirect) {
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

        long total = this.healthRemindersMapper.count(healthReminders);

        return new PageImpl<>(this.healthRemindersMapper.queryAllByLimit(healthReminders, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param healthReminders 实例对象
     * @return 实例对象
     */
    @Override
    public boolean insert(HealthRemindersEntity healthReminders) {
        return this.healthRemindersMapper.insert(healthReminders) > 0;
    }

    /**
     * 修改数据
     *
     * @param healthReminders 实例对象
     * @return 实例对象
     */
    @Override
    public boolean update(HealthRemindersEntity healthReminders) {
        return this.healthRemindersMapper.update(healthReminders) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param reminderId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer reminderId) {
        return this.healthRemindersMapper.deleteById(reminderId) > 0;
    }
}
