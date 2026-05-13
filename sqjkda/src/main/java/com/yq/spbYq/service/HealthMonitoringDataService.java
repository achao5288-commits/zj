package com.yq.spbYq.service;

import com.yq.spbYq.domain.HealthMonitoringDataDomain;
import java.util.List;
import java.util.Map;

public interface HealthMonitoringDataService {
    HealthMonitoringDataDomain queryById(Integer monitorId);
    List<HealthMonitoringDataDomain> queryByUserId(Integer userId);
    List<HealthMonitoringDataDomain> queryByPage(HealthMonitoringDataDomain data, Integer page, Integer size);
    int countByPage(HealthMonitoringDataDomain data);
    boolean insert(HealthMonitoringDataDomain data);
    boolean update(HealthMonitoringDataDomain data);
    boolean deleteById(Integer monitorId);
    List<Map<String, Object>> getStatisticsByType(Integer userId);
    List<Map<String, Object>> getStatisticsByDate(Integer userId, Integer days);
}
