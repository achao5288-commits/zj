package com.yq.spbYq.service;

import com.yq.spbYq.domain.HealthCheckinDomain;
import java.util.List;

public interface HealthCheckinService {
    HealthCheckinDomain queryById(Integer checkinId);
    HealthCheckinDomain queryByUserIdAndDate(Integer userId, String checkinDate);
    List<HealthCheckinDomain> queryByUserId(Integer userId);
    List<HealthCheckinDomain> queryByPage(HealthCheckinDomain checkin, Integer page, Integer size);
    int countByPage(HealthCheckinDomain checkin);
    boolean insert(HealthCheckinDomain checkin);
    boolean update(HealthCheckinDomain checkin);
    boolean deleteById(Integer checkinId);
}
