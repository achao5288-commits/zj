package com.yq.spbYq.service.impl;

import com.yq.spbYq.domain.HealthDeviceDomain;
import com.yq.spbYq.mapper.HealthDeviceMapper;
import com.yq.spbYq.service.HealthDeviceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("healthDeviceService")
public class HealthDeviceServiceImpl implements HealthDeviceService {
    
    @Resource
    private HealthDeviceMapper healthDeviceMapper;

    @Override
    public HealthDeviceDomain queryById(Integer deviceId) {
        return healthDeviceMapper.queryById(deviceId);
    }

    @Override
    public List<HealthDeviceDomain> queryByUserId(Integer userId) {
        return healthDeviceMapper.queryByUserId(userId);
    }

    @Override
    public List<HealthDeviceDomain> queryByPage(HealthDeviceDomain device, Integer page, Integer size) {
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
        int offset = (page - 1) * size;
        return healthDeviceMapper.queryByPage(device, offset, size);
    }

    @Override
    public int countByPage(HealthDeviceDomain device) {
        return healthDeviceMapper.countByPage(device);
    }

    @Override
    public boolean insert(HealthDeviceDomain device) {
        return healthDeviceMapper.insert(device) > 0;
    }

    @Override
    public boolean update(HealthDeviceDomain device) {
        return healthDeviceMapper.update(device) > 0;
    }

    @Override
    public boolean deleteById(Integer deviceId) {
        return healthDeviceMapper.deleteById(deviceId) > 0;
    }
}
