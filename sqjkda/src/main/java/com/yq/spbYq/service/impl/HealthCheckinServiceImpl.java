package com.yq.spbYq.service.impl;

import com.yq.spbYq.domain.HealthCheckinDomain;
import com.yq.spbYq.mapper.HealthCheckinMapper;
import com.yq.spbYq.service.HealthCheckinService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("healthCheckinService")
public class HealthCheckinServiceImpl implements HealthCheckinService {
    
    @Resource
    private HealthCheckinMapper healthCheckinMapper;

    @Override
    public HealthCheckinDomain queryById(Integer checkinId) {
        return healthCheckinMapper.queryById(checkinId);
    }

    @Override
    public HealthCheckinDomain queryByUserIdAndDate(Integer userId, String checkinDate) {
        return healthCheckinMapper.queryByUserIdAndDate(userId, checkinDate);
    }

    @Override
    public List<HealthCheckinDomain> queryByUserId(Integer userId) {
        return healthCheckinMapper.queryByUserId(userId);
    }

    @Override
    public List<HealthCheckinDomain> queryByPage(HealthCheckinDomain checkin, Integer page, Integer size) {
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
        int offset = (page - 1) * size;
        return healthCheckinMapper.queryByPage(checkin, offset, size);
    }

    @Override
    public int countByPage(HealthCheckinDomain checkin) {
        return healthCheckinMapper.countByPage(checkin);
    }

    @Override
    public boolean insert(HealthCheckinDomain checkin) {
        return healthCheckinMapper.insert(checkin) > 0;
    }

    @Override
    public boolean update(HealthCheckinDomain checkin) {
        return healthCheckinMapper.update(checkin) > 0;
    }

    @Override
    public boolean deleteById(Integer checkinId) {
        return healthCheckinMapper.deleteById(checkinId) > 0;
    }
}
