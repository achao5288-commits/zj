package com.yq.spbYq.mapper;

import com.yq.spbYq.domain.UserFeedbackEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * (UserFeedback)表数据库访问层
 *
 * @author makejava
 * @since 2025-05-11 10:57:07
 */
public interface UserFeedbackMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param feedbackId 主键
     * @return 实例对象
     */
    UserFeedbackEntity queryById(Integer feedbackId);

    /**
     * 查询指定行数据
     *
     * @param userFeedback 查询条件
     * @param pageable     分页对象
     * @return 对象列表
     */
    List<UserFeedbackEntity> queryAllByLimit(@Param("userFeedback") UserFeedbackEntity userFeedback, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param userFeedback 查询条件
     * @return 总行数
     */
    long count(UserFeedbackEntity userFeedback);

    /**
     * 新增数据
     *
     * @param userFeedback 实例对象
     * @return 影响行数
     */
    int insert(UserFeedbackEntity userFeedback);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<UserFeedbackEntity> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<UserFeedbackEntity> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<UserFeedbackEntity> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<UserFeedbackEntity> entities);

    /**
     * 修改数据
     *
     * @param userFeedback 实例对象
     * @return 影响行数
     */
    int update(UserFeedbackEntity userFeedback);

    /**
     * 通过主键删除数据
     *
     * @param feedbackId 主键
     * @return 影响行数
     */
    int deleteById(Integer feedbackId);

}

