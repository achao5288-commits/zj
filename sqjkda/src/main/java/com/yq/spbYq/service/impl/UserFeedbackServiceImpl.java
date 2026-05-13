package com.yq.spbYq.service.impl;

import com.yq.spbYq.domain.UserFeedbackEntity;
import com.yq.spbYq.mapper.UserFeedbackMapper;
import com.yq.spbYq.service.UserFeedbackService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;

/**
 * (UserFeedback)表服务实现类
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@Service("userFeedbackService")
public class UserFeedbackServiceImpl implements UserFeedbackService {
    private static final int PAGE_DEFAULT = 1;
    private static final int SIZE_DEFAULT = 10;
    @Resource
    private UserFeedbackMapper userFeedbackMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param feedbackId 主键
     * @return 实例对象
     */
    @Override
    public UserFeedbackEntity queryById(Integer feedbackId) {
        return this.userFeedbackMapper.queryById(feedbackId);
    }

    /**
     * 分页查询
     *
     * @param userFeedback 筛选条件
     * @return 查询结果
     */
    @Override
    public Page<UserFeedbackEntity> queryByPage(UserFeedbackEntity userFeedback, Integer page, Integer size, String orderCol, String orderDirect) {
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

        long total = this.userFeedbackMapper.count(userFeedback);

        return new PageImpl<>(this.userFeedbackMapper.queryAllByLimit(userFeedback, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param userFeedback 实例对象
     * @return 实例对象
     */
    @Override
    public boolean insert(UserFeedbackEntity userFeedback) {
        return this.userFeedbackMapper.insert(userFeedback) > 0;
    }

    /**
     * 修改数据
     *
     * @param userFeedback 实例对象
     * @return 实例对象
     */
    @Override
    public boolean update(UserFeedbackEntity userFeedback) {
        return this.userFeedbackMapper.update(userFeedback) > 0;
    }

    /**
     * 通过主键删除数据
     *
     * @param feedbackId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer feedbackId) {
        return this.userFeedbackMapper.deleteById(feedbackId) > 0;
    }
}
