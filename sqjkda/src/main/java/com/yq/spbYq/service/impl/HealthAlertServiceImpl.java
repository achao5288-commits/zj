package com.yq.spbYq.service.impl;

import com.yq.spbYq.domain.HealthAlertDomain;
import com.yq.spbYq.mapper.HealthAlertMapper;
import com.yq.spbYq.service.HealthAlertService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service("healthAlertService")
public class HealthAlertServiceImpl implements HealthAlertService {
    
    @Resource
    private HealthAlertMapper healthAlertMapper;

    @Override
    public HealthAlertDomain queryById(Integer alertId) {
        return healthAlertMapper.queryById(alertId);
    }

    @Override
    public List<HealthAlertDomain> queryByUserId(Integer userId) {
        return healthAlertMapper.queryByUserId(userId);
    }

    @Override
    public List<HealthAlertDomain> queryUnreadByUserId(Integer userId) {
        return healthAlertMapper.queryUnreadByUserId(userId);
    }

    @Override
    public List<HealthAlertDomain> queryByPage(HealthAlertDomain alert, Integer page, Integer size) {
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 10;
        int offset = (page - 1) * size;
        return healthAlertMapper.queryByPage(alert, offset, size);
    }

    @Override
    public int countByPage(HealthAlertDomain alert) {
        return healthAlertMapper.countByPage(alert);
    }

    @Override
    public int countUnreadByUserId(Integer userId) {
        return healthAlertMapper.countUnreadByUserId(userId);
    }

    @Override
    public boolean insert(HealthAlertDomain alert) {
        return healthAlertMapper.insert(alert) > 0;
    }

    @Override
    public boolean update(HealthAlertDomain alert) {
        return healthAlertMapper.update(alert) > 0;
    }

    @Override
    public boolean deleteById(Integer alertId) {
        return healthAlertMapper.deleteById(alertId) > 0;
    }

    @Override
    public boolean markAsRead(Integer alertId) {
        HealthAlertDomain alert = new HealthAlertDomain();
        alert.setAlertId(alertId);
        alert.setIsRead(1);
        return healthAlertMapper.update(alert) > 0;
    }

    @Override
    public boolean handleAlert(Integer alertId, String handleResult) {
        HealthAlertDomain alert = new HealthAlertDomain();
        alert.setAlertId(alertId);
        alert.setIsHandled(1);
        alert.setHandleResult(handleResult);
        alert.setHandleTime(java.time.LocalDateTime.now().toString());
        return healthAlertMapper.update(alert) > 0;
    }
}
