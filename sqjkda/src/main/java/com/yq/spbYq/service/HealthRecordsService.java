package com.yq.spbYq.service;

import com.yq.spbYq.domain.HealthRecordsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (HealthRecords)表服务接口
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
public interface HealthRecordsService {

    /**
     * 通过ID查询单条数据
     *
     * @param recordId 主键
     * @return 实例对象
     */
    HealthRecordsEntity queryById(Integer recordId);

    /**
     * 分页查询
     *
     * @param healthRecords 筛选条件
     * @return 查询结果
     */
    Page<HealthRecordsEntity> queryByPage(HealthRecordsEntity healthRecords, Integer page, Integer size, String orderCol, String orderDirect);

    /**
     * 新增数据
     *
     * @param healthRecords 实例对象
     * @return 实例对象
     */
    boolean insert(HealthRecordsEntity healthRecords);

    /**
     * 修改数据
     *
     * @param healthRecords 实例对象
     * @return 实例对象
     */
    boolean update(HealthRecordsEntity healthRecords);

    /**
     * 通过主键删除数据
     *
     * @param recordId 主键
     * @return 是否成功
     */
    boolean deleteById(Integer recordId);

}
