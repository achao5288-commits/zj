package com.yq.spbYq.service;

import com.yq.spbYq.domain.HealthAlertDomain;
import java.util.List;

public interface HealthAlertService {
    HealthAlertDomain queryById(Integer alertId);
    List<HealthAlertDomain> queryByUserId(Integer userId);
    List<HealthAlertDomain> queryUnreadByUserId(Integer userId);
    List<HealthAlertDomain> queryByPage(HealthAlertDomain alert, Integer page, Integer size);
    int countByPage(HealthAlertDomain alert);
    int countUnreadByUserId(Integer userId);
    boolean insert(HealthAlertDomain alert);
    boolean update(HealthAlertDomain alert);
    boolean deleteById(Integer alertId);
    boolean markAsRead(Integer alertId);
    boolean handleAlert(Integer alertId, String handleResult);
}
