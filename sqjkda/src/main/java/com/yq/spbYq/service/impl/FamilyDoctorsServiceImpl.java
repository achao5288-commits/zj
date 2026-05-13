package com.yq.spbYq.service.impl;

import com.yq.spbYq.domain.FamilyDoctorsEntity;
import com.yq.spbYq.mapper.FamilyDoctorsMapper;
import com.yq.spbYq.service.FamilyDoctorsService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;

/**
 * (FamilyDoctors)表服务实现类
 *
 * @author makejava
 * @since 2025-05-11 10:57:07
 */
@Service("familyDoctorsService")
public class FamilyDoctorsServiceImpl implements FamilyDoctorsService {
    private static final int PAGE_DEFAULT = 1;
    private static final int SIZE_DEFAULT = 10;
    @Resource
    private FamilyDoctorsMapper familyDoctorsMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param doctorId 主键
     * @return 实例对象
     */
    @Override
    public FamilyDoctorsEntity queryById(Integer doctorId) {
        return this.familyDoctorsMapper.queryById(doctorId);
    }

    /**
     * 分页查询
     *
     * @param familyDoctors 筛选条件
     * @return 查询结果
     */
    @Override
    public Page<FamilyDoctorsEntity> queryByPage(FamilyDoctorsEntity familyDoctors, Integer page, Integer size, String orderCol, String orderDirect) {
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

        long total = this.familyDoctorsMapper.count(familyDoctors);

        return new PageImpl<>(this.familyDoctorsMapper.queryAllByLimit(familyDoctors, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param familyDoctors 实例对象
     * @return 实例对象
     */
    @Override
    public boolean insert(FamilyDoctorsEntity familyDoctors) {
        return this.familyDoctorsMapper.insert(familyDoctors) > 0;
    }

    /**
     * 修改数据
     *
     * @param familyDoctors 实例对象
     * @return 实例对象
     */
    @Override
    public boolean update(FamilyDoctorsEntity familyDoctors) {
        return this.familyDoctorsMapper.update(familyDoctors) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param doctorId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer doctorId) {
        return this.familyDoctorsMapper.deleteById(doctorId) > 0;
    }
}
