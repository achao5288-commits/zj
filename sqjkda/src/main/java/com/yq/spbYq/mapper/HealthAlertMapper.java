package com.yq.spbYq.mapper;

import com.yq.spbYq.domain.HealthAlertDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface HealthAlertMapper {
    
    int insert(HealthAlertDomain alert);
    
    int update(HealthAlertDomain alert);
    
    int deleteById(@Param("alertId") Integer alertId);
    
    HealthAlertDomain queryById(@Param("alertId") Integer alertId);
    
    List<HealthAlertDomain> queryByUserId(@Param("userId") Integer userId);
    
    List<HealthAlertDomain> queryUnreadByUserId(@Param("userId") Integer userId);
    
    List<HealthAlertDomain> queryByPage(@Param("alert") HealthAlertDomain alert,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);
    
    int countByPage(@Param("alert") HealthAlertDomain alert);
    
    int countUnreadByUserId(@Param("userId") Integer userId);
}
