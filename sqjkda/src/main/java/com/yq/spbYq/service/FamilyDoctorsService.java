package com.yq.spbYq.service;

import com.yq.spbYq.domain.FamilyDoctorsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (FamilyDoctors)表服务接口
 *
 * @author makejava
 * @since 2025-05-11 10:57:07
 */
public interface FamilyDoctorsService {

    /**
     * 通过ID查询单条数据
     *
     * @param doctorId 主键
     * @return 实例对象
     */
    FamilyDoctorsEntity queryById(Integer doctorId);

    /**
     * 分页查询
     *
     * @param familyDoctors 筛选条件
     * @return 查询结果
     */
    Page<FamilyDoctorsEntity> queryByPage(FamilyDoctorsEntity familyDoctors, Integer page, Integer size, String orderCol, String orderDirect);

    /**
     * 新增数据
     *
     * @param familyDoctors 实例对象
     * @return 实例对象
     */
    boolean insert(FamilyDoctorsEntity familyDoctors);

    /**
     * 修改数据
     *
     * @param familyDoctors 实例对象
     * @return 实例对象
     */
    boolean update(FamilyDoctorsEntity familyDoctors);

    /**
     * 通过主键删除数据
     *
     * @param doctorId 主键
     * @return 是否成功
     */
    boolean deleteById(Integer doctorId);

}
