package com.yq.spbYq.service.impl;

import com.yq.spbYq.domain.HealthMonitoringDataDomain;
import com.yq.spbYq.mapper.HealthMonitoringDataMapper;
import com.yq.spbYq.service.HealthMonitoringDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("healthMonitoringDataService")
public class HealthMonitoringDataServiceImpl implements HealthMonitoringDataService {
    
    @Resource
    private HealthMonitoringDataMapper healthMonitoringDataMapper;

    @Override
    public HealthMonitoringDataDomain queryById(Integer monitorId) {
        return healthMonitoringDataMapper.queryById(monitorId);
    }

    @Override
    public List<HealthMonitoringDataDomain> queryByUserId(Integer userId) {
        return healthMonitoringDataMapper.queryByUserId(userId);
    }

    @Override
    public List<HealthMonitoringDataDomain> queryByPage(HealthMonitoringDataDomain data, Integer page, Integer size) {
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
        int offset = (page - 1) * size;
        return healthMonitoringDataMapper.queryByPage(data, offset, size);
    }

    @Override
    public int countByPage(HealthMonitoringDataDomain data) {
        return healthMonitoringDataMapper.countByPage(data);
    }

    @Override
    public boolean insert(HealthMonitoringDataDomain data) {
        return healthMonitoringDataMapper.insert(data) > 0;
    }

    @Override
    public boolean update(HealthMonitoringDataDomain data) {
        return healthMonitoringDataMapper.update(data) > 0;
    }

    @Override
    public boolean deleteById(Integer monitorId) {
        return healthMonitoringDataMapper.deleteById(monitorId) > 0;
    }

    @Override
    public List<Map<String, Object>> getStatisticsByType(Integer userId) {
        return healthMonitoringDataMapper.getStatisticsByType(userId);
    }

    @Override
    public List<Map<String, Object>> getStatisticsByDate(Integer userId, Integer days) {
        return healthMonitoringDataMapper.getStatisticsByDate(userId, days);
    }
}
