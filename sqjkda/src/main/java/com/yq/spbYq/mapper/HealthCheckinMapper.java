package com.yq.spbYq.mapper;

import com.yq.spbYq.domain.HealthCheckinDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface HealthCheckinMapper {
    
    int insert(HealthCheckinDomain checkin);
    
    int update(HealthCheckinDomain checkin);
    
    int deleteById(@Param("checkinId") Integer checkinId);
    
    HealthCheckinDomain queryById(@Param("checkinId") Integer checkinId);
    
    HealthCheckinDomain queryByUserIdAndDate(@Param("userId") Integer userId,
                                              @Param("checkinDate") String checkinDate);
    
    List<HealthCheckinDomain> queryByUserId(@Param("userId") Integer userId);
    
    List<HealthCheckinDomain> queryByPage(@Param("checkin") HealthCheckinDomain checkin,
                                           @Param("offset") Integer offset,
                                           @Param("limit") Integer limit);
    
    int countByPage(@Param("checkin") HealthCheckinDomain checkin);
}
