package com.yq.spbYq.service;

import com.yq.spbYq.domain.UserFeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (UserFeedback)表服务接口
 *
 * @author makejava
 * @since 2025-05-11 10:57:07
 */
public interface UserFeedbackService {

    /**
     * 通过ID查询单条数据
     *
     * @param feedbackId 主键
     * @return 实例对象
     */
    UserFeedbackEntity queryById(Integer feedbackId);

    /**
     * 分页查询
     *
     * @param userFeedback 筛选条件
     * @return 查询结果
     */
    Page<UserFeedbackEntity> queryByPage(UserFeedbackEntity userFeedback, Integer page, Integer size, String orderCol, String orderDirect);

    /**
     * 新增数据
     *
     * @param userFeedback 实例对象
     * @return 实例对象
     */
    boolean insert(UserFeedbackEntity userFeedback);

    /**
     * 修改数据
     *
     * @param userFeedback 实例对象
     * @return 实例对象
     */
    boolean update(UserFeedbackEntity userFeedback);

    /**
     * 通过主键删除数据
     *
     * @param feedbackId 主键
     * @return 是否成功
     */
    boolean deleteById(Integer feedbackId);

}
