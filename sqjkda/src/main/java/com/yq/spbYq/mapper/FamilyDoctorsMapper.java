package com.yq.spbYq.mapper;

import com.yq.spbYq.domain.FamilyDoctorsEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * (FamilyDoctors)表数据库访问层
 *
 * @author makejava
 * @since 2025-05-11 10:57:06
 */
public interface FamilyDoctorsMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param doctorId 主键
     * @return 实例对象
     */
    FamilyDoctorsEntity queryById(Integer doctorId);

    /**
     * 查询指定行数据
     *
     * @param familyDoctors 查询条件
     * @param pageable      分页对象
     * @return 对象列表
     */
    List<FamilyDoctorsEntity> queryAllByLimit(@Param("familyDoctors") FamilyDoctorsEntity familyDoctors, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param familyDoctors 查询条件
     * @return 总行数
     */
    long count(FamilyDoctorsEntity familyDoctors);

    /**
     * 新增数据
     *
     * @param familyDoctors 实例对象
     * @return 影响行数
     */
    int insert(FamilyDoctorsEntity familyDoctors);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<FamilyDoctorsEntity> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<FamilyDoctorsEntity> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<FamilyDoctorsEntity> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<FamilyDoctorsEntity> entities);

    /**
     * 修改数据
     *
     * @param familyDoctors 实例对象
     * @return 影响行数
     */
    int update(FamilyDoctorsEntity familyDoctors);

    /**
     * 通过主键删除数据
     *
     * @param doctorId 主键
     * @return 影响行数
     */
    int deleteById(Integer doctorId);

}

