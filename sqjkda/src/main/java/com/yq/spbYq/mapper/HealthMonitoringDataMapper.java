package com.yq.spbYq.mapper;

import com.yq.spbYq.domain.HealthMonitoringDataDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface HealthMonitoringDataMapper {
    
    int insert(HealthMonitoringDataDomain data);
    
    int update(HealthMonitoringDataDomain data);
    
    int deleteById(@Param("monitorId") Integer monitorId);
    
    HealthMonitoringDataDomain queryById(@Param("monitorId") Integer monitorId);
    
    List<HealthMonitoringDataDomain> queryByUserId(@Param("userId") Integer userId);
    
    List<HealthMonitoringDataDomain> queryByPage(@Param("data") HealthMonitoringDataDomain data,
                                                  @Param("offset") Integer offset,
                                                  @Param("limit") Integer limit);
    
    int countByPage(@Param("data") HealthMonitoringDataDomain data);
    
    List<Map<String, Object>> getStatisticsByType(@Param("userId") Integer userId);
    
    List<Map<String, Object>> getStatisticsByDate(@Param("userId") Integer userId,
                                                    @Param("days") Integer days);
}
