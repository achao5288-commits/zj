package com.yq.spbYq.mapper;

import com.yq.spbYq.domain.HealthRemindersEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * (HealthReminders)表数据库访问层
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
public interface HealthRemindersMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param reminderId 主键
     * @return 实例对象
     */
    HealthRemindersEntity queryById(Integer reminderId);

    /**
     * 查询指定行数据
     *
     * @param healthReminders 查询条件
     * @param pageable        分页对象
     * @return 对象列表
     */
    List<HealthRemindersEntity> queryAllByLimit(@Param("healthReminders") HealthRemindersEntity healthReminders, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param healthReminders 查询条件
     * @return 总行数
     */
    long count(HealthRemindersEntity healthReminders);

    /**
     * 新增数据
     *
     * @param healthReminders 实例对象
     * @return 影响行数
     */
    int insert(HealthRemindersEntity healthReminders);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<HealthRemindersEntity> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<HealthRemindersEntity> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<HealthRemindersEntity> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<HealthRemindersEntity> entities);

    /**
     * 修改数据
     *
     * @param healthReminders 实例对象
     * @return 影响行数
     */
    int update(HealthRemindersEntity healthReminders);

    /**
     * 通过主键删除数据
     *
     * @param reminderId 主键
     * @return 影响行数
     */
    int deleteById(Integer reminderId);

}

