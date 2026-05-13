# 小米手环SDK接入功能使用说明

## 功能概述

本功能实现了模拟小米手环等健康设备的SDK接入，用户可以通过输入SDK码将设备添加到系统中，并自动同步健康数据。

## 使用流程

### 1. 添加设备

1. 登录系统后，进入"居家健康监测"页面
2. 在"设备管理"标签页，点击"添加设备"按钮
3. 在弹出的模态框中填写以下信息：

   **必填信息：**
   - **设备SDK码**: 输入设备厂商提供的SDK码
   - **设备名称**: 为你的设备起一个名称（例如：我的小米手环）

   **选填信息（系统会自动填充）：**
   - 设备类型
   - 连接方式

### 2. 有效的SDK码格式

系统支持以下SDK码格式（不区分大小写）：

#### 小米手环
- `MI-BAND-SDK-2024` - 小米手环7
- `MI-BAND-SDK-2023` - 小米手环6
- `MI-BAND-SDK-2025` - 小米手环8
- 或任意格式：`MI-BAND-SDK-XXXX`（XXXX为4位数字）

#### 其他设备
- `OMRON-BP-SDK-2024` - 欧姆龙血压计
- `ROCHE-GLU-SDK-2024` - 罗氏血糖仪
- `HUAWEI-SCALE-SDK-2024` - 华为体脂秤

### 3. 设备验证和添加

1. 输入SDK码后，点击"验证并添加"按钮
2. 系统会：
   - 验证SDK码的有效性
   - 显示设备预览信息（品牌、型号等）
   - 自动将设备添加到你的设备列表
   - **自动同步设备的健康数据**

### 4. 查看同步数据

设备添加成功后：
1. 在"设备管理"标签页可以看到新添加的设备
2. 切换到"监测数据"标签页可以查看同步的健康数据
3. 点击设备列表中的"同步"按钮可以手动同步最新数据

## 自动同步的健康数据

根据不同设备类型，系统会自动同步以下数据：

### 小米手环
- 心率 (heart_rate) - 单位: bpm
- 血氧 (oxygen) - 单位: %
- 体温 (temperature) - 单位: °C
- 步数 (steps) - 单位: 步

### 血压计
- 血压 (blood_pressure) - 单位: mmHg
  - 收缩压 (systolic)
  - 舒张压 (diastolic)

### 血糖仪
- 血糖 (blood_glucose) - 单位: mmol/L

### 体脂秤
- 体重 (weight) - 单位: kg

## 技术实现

### 后端实现

1. **XiaomiBandSdkService.java** - 模拟SDK服务
   - `validateSdkCode()` - 验证SDK码
   - `syncDeviceData()` - 同步设备数据

2. **DeviceSdkController.java** - SDK接入控制器
   - `GET /deviceSdk/validate` - 验证SDK码
   - `POST /deviceSdk/addBySdk` - 通过SDK码添加设备
   - `POST /deviceSdk/sync/{deviceId}` - 手动同步设备数据

### 前端实现

1. **health-monitoring.html** - 健康监测页面
   - 添加设备模态框
   - SDK码输入和验证
   - 设备列表管理
   - 手动同步功能

## 测试示例

### 测试步骤

1. 启动项目
2. 访问 `http://localhost:8080/spbYq/health-monitoring.html`
3. 登录系统
4. 点击"添加设备"
5. 输入以下测试SDK码：
   ```
   MI-BAND-SDK-2024
   ```
6. 输入设备名称：`我的小米手环`
7. 点击"验证并添加"
8. 查看设备列表和监测数据

### 预期结果

- 设备成功添加到列表
- 设备状态显示为"在线"
- 自动同步4条健康数据（心率、血氧、体温、步数）
- 可以在"监测数据"标签页查看这些数据

## 扩展建议

### 真实SDK集成

要将此功能对接真实的设备SDK，需要：

1. **注册设备厂商开发者账号**
   - 小米健康开放平台
   - 华为健康Kit
   - Apple HealthKit

2. **获取真实的SDK和API密钥**

3. **修改XiaomiBandSdkService**
   ```java
   // 替换模拟验证为真实API调用
   public SdkDeviceInfo validateSdkCode(String sdkCode) {
       // 调用小米健康API验证设备
       return xiaomiHealthApi.validateDevice(sdkCode);
   }
   
   // 替换模拟数据为真实数据同步
   public List<HealthMonitoringDataDomain> syncDeviceData(String deviceSn) {
       // 调用设备API获取真实数据
       return xiaomiHealthApi.getDeviceData(deviceSn);
   }
   ```

4. **实现OAuth授权流程**
   - 用户授权应用访问其健康数据
   - 获取access_token
   - 定期刷新token

5. **实现实时数据推送**
   - 使用WebSocket接收设备实时数据
   - 或使用定时任务轮询API

### 安全性增强

1. 加密存储SDK码和API密钥
2. 实现SDK码一次性使用机制
3. 添加设备绑定验证（短信/邮件验证码）
4. 记录设备操作日志

## 常见问题

### Q: SDK码验证失败怎么办？
A: 请检查SDK码格式是否正确。小米手环SDK码格式为：`MI-BAND-SDK-XXXX`（XXXX为4位数字）

### Q: 添加设备后没有看到数据？
A: 
1. 检查设备是否成功添加
2. 点击设备列表中的"同步"按钮手动同步
3. 查看浏览器控制台是否有错误信息

### Q: 可以添加多个相同类型的设备吗？
A: 可以，每个设备需要使用不同的SDK码

### Q: 数据同步频率是多少？
A: 
- 添加设备时自动同步一次
- 之后可以手动点击"同步"按钮
- 未来可以实现定时自动同步（如每5分钟）

## 注意事项

1. 当前版本为**模拟实现**，使用预设的测试数据
2. 生产环境需要对接真实的设备SDK
3. SDK码验证和数据同步都是本地模拟
4. 所有健康数据仅用于演示目的

---

**版本**: v1.0  
**更新日期**: 2024-04-22  
**作者**: AI Assistant
