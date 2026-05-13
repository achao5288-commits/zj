package com.yq.spbYq.mapper;

import com.yq.spbYq.domain.HealthDeviceDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface HealthDeviceMapper {
    
    int insert(HealthDeviceDomain device);
    
    int update(HealthDeviceDomain device);
    
    int deleteById(@Param("deviceId") Integer deviceId);
    
    HealthDeviceDomain queryById(@Param("deviceId") Integer deviceId);
    
    List<HealthDeviceDomain> queryByUserId(@Param("userId") Integer userId);
    
    List<HealthDeviceDomain> queryByPage(@Param("device") HealthDeviceDomain device,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);
    
    int countByPage(@Param("device") HealthDeviceDomain device);
}
