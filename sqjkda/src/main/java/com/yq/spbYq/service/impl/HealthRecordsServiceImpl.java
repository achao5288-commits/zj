package com.yq.spbYq.service.impl;

import com.yq.spbYq.domain.HealthRecordsEntity;
import com.yq.spbYq.mapper.HealthRecordsMapper;
import com.yq.spbYq.service.HealthRecordsService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;

/**
 * (HealthRecords)表服务实现类
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@Service("healthRecordsService")
public class HealthRecordsServiceImpl implements HealthRecordsService {
    private static final int PAGE_DEFAULT = 1;
    private static final int SIZE_DEFAULT = 10;
    @Resource
    private HealthRecordsMapper healthRecordsMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param recordId 主键
     * @return 实例对象
     */
    @Override
    public HealthRecordsEntity queryById(Integer recordId) {
        return this.healthRecordsMapper.queryById(recordId);
    }

    /**
     * 分页查询
     *
     * @param healthRecords 筛选条件
     * @return 查询结果
     */
    @Override
    public Page<HealthRecordsEntity> queryByPage(HealthRecordsEntity healthRecords, Integer page, Integer size, String orderCol, String orderDirect) {
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

        long total = this.healthRecordsMapper.count(healthRecords);

        return new PageImpl<>(this.healthRecordsMapper.queryAllByLimit(healthRecords, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param healthRecords 实例对象
     * @return 实例对象
     */
    @Override
    public boolean insert(HealthRecordsEntity healthRecords) {
        return this.healthRecordsMapper.insert(healthRecords) > 0;
    }

    /**
     * 修改数据
     *
     * @param healthRecords 实例对象
     * @return 实例对象
     */
    @Override
    public boolean update(HealthRecordsEntity healthRecords) {
        return this.healthRecordsMapper.update(healthRecords) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param recordId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer recordId) {
        return this.healthRecordsMapper.deleteById(recordId) > 0;
    }
}
