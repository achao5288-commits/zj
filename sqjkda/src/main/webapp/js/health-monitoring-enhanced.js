/**
 * 居家健康监测模块 - 功能增强脚本
 * 包含：设备编辑、图表展示、打卡历史、预警处理等完整功能
 */

// ==================== 设备管理增强 ====================

/**
 * 编辑设备信息
 */
function editDevice(deviceId) {
  // 获取设备详情
  $.get('/spbYq/healthDevice/queryById/' + deviceId, function(res) {
    if (res.code === 1 && res.data) {
      var device = res.data;
      
      // 填充编辑表单
      $('#editDeviceId').val(device.deviceId);
      $('#editDeviceName').val(device.deviceName);
      $('#editDeviceType').val(device.deviceType);
      $('#editDeviceBrand').val(device.deviceBrand);
      $('#editDeviceModel').val(device.deviceModel);
      $('#editConnectionType').val(device.connectionType);
      $('#editDeviceStatus').val(device.status);
      
      // 显示编辑模态框
      var modal = new bootstrap.Modal(document.getElementById('editDeviceModal'));
      modal.show();
    } else {
      alert('获取设备信息失败');
    }
  });
}

/**
 * 保存设备编辑
 */
function saveDeviceEdit() {
  var formData = {
    deviceId: $('#editDeviceId').val(),
    deviceName: $('#editDeviceName').val(),
    deviceType: $('#editDeviceType').val(),
    deviceBrand: $('#editDeviceBrand').val(),
    deviceModel: $('#editDeviceModel').val(),
    connectionType: $('#editConnectionType').val(),
    status: $('#editDeviceStatus').val()
  };
  
  $.post('/spbYq/healthDevice/edit', formData, function(res) {
    if (res.code === 1) {
      alert('设备信息更新成功');
      var modal = bootstrap.Modal.getInstance(document.getElementById('editDeviceModal'));
      modal.hide();
      loadDevices();
    } else {
      alert('更新失败: ' + (res.msg || '未知错误'));
    }
  });
}

/**
 * 查看设备详情
 */
function viewDeviceDetail(deviceId) {
  $.get('/spbYq/healthDevice/queryById/' + deviceId, function(res) {
    if (res.code === 1 && res.data) {
      var device = res.data;
      var detailHtml = `
        <div class="device-detail">
          <h5>设备详情</h5>
          <table class="table">
            <tr><th>设备ID</th><td>${device.deviceId}</td></tr>
            <tr><th>设备名称</th><td>${device.deviceName}</td></tr>
            <tr><th>设备类型</th><td>${device.deviceType}</td></tr>
            <tr><th>品牌</th><td>${device.deviceBrand}</td></tr>
            <tr><th>型号</th><td>${device.deviceModel}</td></tr>
            <tr><th>序列号</th><td>${device.deviceSn}</td></tr>
            <tr><th>连接方式</th><td>${device.connectionType}</td></tr>
            <tr><th>状态</th><td>${device.status === 'online' ? '在线' : '离线'}</td></tr>
            <tr><th>最后同步</th><td>${device.lastSyncTime || '未同步'}</td></tr>
            <tr><th>绑定时间</th><td>${device.bindTime}</td></tr>
          </table>
        </div>
      `;
      
      $('#deviceDetailContent').html(detailHtml);
      var modal = new bootstrap.Modal(document.getElementById('deviceDetailModal'));
      modal.show();
    }
  });
}

// ==================== 监测数据图表 ====================

/**
 * 初始化心率图表
 */
function initHeartRateChart() {
  var ctx = document.getElementById('heartRateChart');
  if (!ctx) return;
  
  // 获取心率数据
  $.get('/spbYq/healthMonitoring/statistics/type', {dataType: 'heart_rate'}, function(res) {
    if (res.code === 1 && res.data) {
      var labels = res.data.map(d => d.measurementTime);
      var values = res.data.map(d => d.dataValue);
      
      new Chart(ctx, {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{
            label: '心率 (bpm)',
            data: values,
            borderColor: 'rgb(255, 99, 132)',
            backgroundColor: 'rgba(255, 99, 132, 0.1)',
            tension: 0.1,
            fill: true
          }]
        },
        options: {
          responsive: true,
          plugins: {
            title: {
              display: true,
              text: '心率趋势图'
            }
          },
          scales: {
            y: {
              beginAtZero: false,
              min: 50,
              max: 120
            }
          }
        }
      });
    }
  });
}

/**
 * 初始化血压图表
 */
function initBloodPressureChart() {
  var ctx = document.getElementById('bloodPressureChart');
  if (!ctx) return;
  
  $.get('/spbYq/healthMonitoring/statistics/type', {dataType: 'blood_pressure'}, function(res) {
    if (res.code === 1 && res.data) {
      var labels = res.data.map(d => d.measurementTime);
      var systolic = res.data.map(d => d.systolic);
      var diastolic = res.data.map(d => d.diastolic);
      
      new Chart(ctx, {
        type: 'line',
        data: {
          labels: labels,
          datasets: [
            {
              label: '收缩压',
              data: systolic,
              borderColor: 'rgb(54, 162, 235)',
              tension: 0.1
            },
            {
              label: '舒张压',
              data: diastolic,
              borderColor: 'rgb(255, 206, 86)',
              tension: 0.1
            }
          ]
        },
        options: {
          responsive: true,
          plugins: {
            title: {
              display: true,
              text: '血压趋势图'
            }
          },
          scales: {
            y: {
              beginAtZero: false,
              min: 50,
              max: 180
            }
          }
        }
      });
    }
  });
}

// ==================== 健康打卡增强 ====================

/**
 * 查看打卡历史
 */
function viewCheckinHistory() {
  $.get('/spbYq/healthCheckin/queryByPage', {page: 1, size: 30}, function(res) {
    if (res.code === 1) {
      var records = [];
      if (res.content && res.content.content) {
        records = res.content.content;
      } else if (res.data && res.data.content) {
        records = res.data.content;
      } else if (Array.isArray(res.data)) {
        records = res.data;
      }
      
      var html = '<h5>打卡历史记录</h5><table class="table">';
      html += '<thead><tr><th>日期</th><th>整体感受</th><th>睡眠</th><th>运动</th><th>打卡时间</th></tr></thead>';
      html += '<tbody>';
      
      records.forEach(function(r) {
        html += `<tr>
          <td>${r.checkinDate}</td>
          <td>${r.overallFeeling || '-'}</td>
          <td>${r.sleepQuality || '-'} (${r.sleepHours || 0}h)</td>
          <td>${r.exerciseStatus || '-'} (${r.exerciseMinutes || 0}min)</td>
          <td>${r.checkinTime || '-'}</td>
        </tr>`;
      });
      
      html += '</tbody></table>';
      
      $('#checkinHistoryContent').html(html || '<p>暂无打卡记录</p>');
      var modal = new bootstrap.Modal(document.getElementById('checkinHistoryModal'));
      modal.show();
    }
  });
}

// ==================== 预警通知增强 ====================

/**
 * 标记预警为已读
 */
function markAlertAsRead(alertId) {
  $.post('/spbYq/healthAlert/markAsRead/' + alertId, function(res) {
    if (res.code === 1) {
      alert('已标记为已读');
      loadAlerts();
    } else {
      alert('操作失败');
    }
  });
}

/**
 * 处理预警
 */
function handleAlert(alertId) {
  var handleResult = prompt('请输入处理结果：');
  if (handleResult) {
    $.post('/spbYq/healthAlert/handle', {
      alertId: alertId,
      handleResult: handleResult
    }, function(res) {
      if (res.code === 1) {
        alert('预警处理成功');
        loadAlerts();
      } else {
        alert('处理失败');
      }
    });
  }
}

/**
 * 查看所有预警
 */
function viewAllAlerts() {
  $.get('/spbYq/healthAlert/queryByPage', {page: 1, size: 50}, function(res) {
    if (res.code === 1) {
      var alerts = [];
      if (res.content && res.content.content) {
        alerts = res.content.content;
      } else if (res.data && res.data.content) {
        alerts = res.data.content;
      } else if (Array.isArray(res.data)) {
        alerts = res.data;
      }
      
      var html = '<h5>所有预警</h5>';
      alerts.forEach(function(a) {
        var levelClass = a.alertLevel === 'critical' ? 'badge-danger' : 
                        (a.alertLevel === 'warning' ? 'badge-warning' : 'badge-online');
        var readStatus = a.isRead === 1 ? '已读' : '未读';
        var handleStatus = a.isHandled === 1 ? '已处理' : '未处理';
        
        html += `<div class="alert ${a.isRead === 1 ? 'alert-light' : 'alert-warning'} mb-2">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <strong><span class="${levelClass}">${a.alertLevel}</span> ${a.alertTitle}</strong>
              <p class="mb-1">${a.alertContent}</p>
              <small class="text-muted">${a.createTime} | ${readStatus} | ${handleStatus}</small>
            </div>
            <div>
              ${a.isRead === 0 ? `<button class="btn btn-sm btn-primary me-1" onclick="markAlertAsRead(${a.alertId})">标记已读</button>` : ''}
              ${a.isHandled === 0 ? `<button class="btn btn-sm btn-success" onclick="handleAlert(${a.alertId})">处理</button>` : ''}
            </div>
          </div>
        </div>`;
      });
      
      $('#allAlertsContent').html(html || '<p>暂无预警</p>');
      var modal = new bootstrap.Modal(document.getElementById('allAlertsModal'));
      modal.show();
    }
  });
}

// ==================== 数据筛选 ====================

/**
 * 筛选监测数据
 */
function filterMonitoringData() {
  var dataType = $('#filterDataType').val();
  var startDate = $('#filterStartDate').val();
  var endDate = $('#filterEndDate').val();
  
  var params = {page: 1, size: 50};
  if (dataType) params.dataType = dataType;
  if (startDate) params.startDate = startDate;
  if (endDate) params.endDate = endDate;
  
  $.get('/spbYq/healthMonitoring/queryByPage', params, function(res) {
    if (res.code === 1) {
      var data = [];
      if (res.content && res.content.content) {
        data = res.content.content;
      } else if (res.data && res.data.content) {
        data = res.data.content;
      } else if (Array.isArray(res.data)) {
        data = res.data;
      }
      
      var html = '';
      data.forEach(function(d) {
        var status = d.isAbnormal === 1 ? '<span class="badge-danger">异常</span>' : '<span class="badge-online">正常</span>';
        html += `<tr><td>${d.dataType}</td><td>${d.dataValue} ${d.dataUnit || ''}</td><td>${d.measurementTime}</td><td>${status}</td></tr>`;
      });
      
      $('#monitoringTable').html(html || '<tr><td colspan="4" class="text-center">暂无数据</td></tr>');
    }
  });
}

// ==================== 页面初始化 ====================

/**
 * 初始化所有功能
 */
function initHealthMonitoring() {
  console.log('初始化居家健康监测模块...');
  
  // 加载基础数据
  loadDevices();
  loadMonitoringData();
  loadAlerts();
  checkTodayCheckin();
  
  // 初始化图表（延迟加载）
  setTimeout(function() {
    initHeartRateChart();
    initBloodPressureChart();
  }, 500);
  
  console.log('初始化完成');
}

// 页面加载完成后初始化
$(document).ready(function() {
  initHealthMonitoring();
});
