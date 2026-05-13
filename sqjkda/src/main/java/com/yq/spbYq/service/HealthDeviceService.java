package com.yq.spbYq.service;

import com.yq.spbYq.domain.HealthDeviceDomain;
import java.util.List;

public interface HealthDeviceService {
    HealthDeviceDomain queryById(Integer deviceId);
    List<HealthDeviceDomain> queryByUserId(Integer userId);
    List<HealthDeviceDomain> queryByPage(HealthDeviceDomain device, Integer page, Integer size);
    int countByPage(HealthDeviceDomain device);
    boolean insert(HealthDeviceDomain device);
    boolean update(HealthDeviceDomain device);
    boolean deleteById(Integer deviceId);
}
