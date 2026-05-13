# Session丢失问题修复说明

## 🔍 问题描述

```
POST http://localhost:8081/spbYq/healthRecords/deleteById/1268 500 (Internal Server Error)
```

所有需要权限验证的操作（新增、编辑、删除）都返回500错误。

## 🔍 问题原因

### 根本原因：Session丢失

前端fetch请求**没有携带Cookie**，导致后端无法获取Session中的用户信息（userId、userType）。

### 详细分析

1. **登录流程**：
   - 用户登录成功
   - 后端创建Session，存储userId和userType
   - 后端返回Set-Cookie头（包含JSESSIONID）
   
2. **问题所在**：
   - 前端fetch默认**不携带Cookie**
   - 后端收不到JSESSIONID
   - 无法获取Session中的userId和userType
   - 权限校验失败 → 500错误

3. **CORS配置问题**：
   - `config.setAllowCredentials(true)` 被注释
   - 使用了 `addAllowedOrigin("*")`
   - 当需要携带Cookie时，不能使用`*`，必须指定具体域名

## ✅ 修复内容

### 1. 修改前端fetch请求（hr.html）

**为所有fetch请求添加 `credentials: 'include'`**：

```javascript
// ✅ 查询请求
fetch(url, {
  credentials: 'include'  // 携带Cookie（Session ID）
})

// ✅ POST请求（新增、编辑、删除）
fetch(url, {
  method: 'POST',
  credentials: 'include',  // 携带Cookie（Session ID）
  headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  body: formData.toString()
})
```

**修改的位置**：
- ✅ fetchRecords() - 查询列表
- ✅ submitForm() - 新增/编辑
- ✅ editRecord() - 查询单个
- ✅ deleteRecord() - 删除

### 2. 修改登录请求（login.html）

**为AJAX请求添加 `withCredentials: true`**：

```javascript
$.ajax({
    url: 'http://localhost:8081/spbYq/users/login',
    type: 'POST',
    xhrFields: {
        withCredentials: true  // 携带Cookie（Session ID）
    },
    data: {
        phoneNumber: phoneNumber,
        passwordHash: password,
        username: username
    },
    ...
})
```

### 3. 修改后端CORS配置（CORSConfiguration.java）

**允许携带Cookie，并指定允许的域名**：

```java
@Bean
public FilterRegistrationBean corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    
    // ✅ 启用允许携带凭证（Cookie）
    config.setAllowCredentials(true);
    
    // ✅ 指定允许的域名（不能用*）
    config.addAllowedOrigin("http://localhost:8081");
    
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    
    source.registerCorsConfiguration("/**", config);
    FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
    bean.setOrder(0);
    return bean;
}
```

## 📋 修改文件清单

| 文件 | 修改内容 | 状态 |
|-----|---------|------|
| hr.html | 所有fetch添加credentials: 'include' | ✅ 已修复 |
| login.html | AJAX添加withCredentials: true | ✅ 已修复 |
| CORSConfiguration.java | 启用allowCredentials，指定域名 | ✅ 已修复 |

## 🧪 测试步骤

### 1. 重启后端服务

修改了Java代码，必须重启Spring Boot应用：

```bash
# 停止当前运行的服务
# 然后重新启动
mvn spring-boot:run
```

### 2. 清除浏览器Cookie

打开浏览器开发者工具（F12）：
1. 切换到 **Application** 标签
2. 左侧选择 **Cookies**
3. 右键点击 `http://localhost:8081`
4. 选择 **Clear**

### 3. 重新登录

1. 访问 `http://localhost:8081/spbYq/login.html`
2. 输入账号密码登录
3. 登录成功后，检查Cookie：
   - Application → Cookies → http://localhost:8081
   - 应该能看到 `JSESSIONID=xxx`

### 4. 测试删除功能

1. 访问 `http://localhost:8081/spbYq/hr.html`
2. 点击某条记录的"删除"按钮
3. 确认删除
4. 应该显示"删除成功"

### 5. 测试新增功能

1. 点击"新增健康记录"
2. 填写表单
3. 点击提交
4. 应该显示"操作成功"

## 🔍 验证方法

### 方法一：浏览器开发者工具

1. 按F12打开开发者工具
2. 切换到 **Network** 标签
3. 执行删除操作
4. 点击 `deleteById/xxx` 请求
5. 查看 **Headers**：
   - **Request Headers** 中应该有 `Cookie: JSESSIONID=xxx`
   - **Response Headers** 中应该有 `Set-Cookie: JSESSIONID=xxx`

### 方法二：后端日志

在Controller中添加日志：

```java
@RequestMapping("deleteById/{id}")
public ReturnVO deleteById(HttpServletRequest request, @PathVariable("id") Integer id) {
    System.out.println("Session ID: " + request.getSession().getId());
    System.out.println("userId: " + request.getSession().getAttribute("userId"));
    System.out.println("userType: " + request.getSession().getAttribute("userType"));
    
    // ... 原有代码
}
```

如果能看到userId和userType，说明Session正常。

## ⚠️ 重要说明

### 1. 权限要求

- **新增健康记录**：任何登录用户都可以（自动获取userId）
- **编辑健康记录**：需要root权限（userType=2）
- **删除健康记录**：需要root权限（userType=2）

如果您的账户是普通用户（userType=1），编辑和删除会返回"无权限操作"。

### 2. 测试账号

根据测试数据，有以下账号：

| 手机号 | 密码 | 用户类型 | 权限 |
|--------|------|---------|------|
| 13800000001 | Qq1234567890. | 普通用户(1) | 只能新增和查询 |
| 13800000002 | Qq1234567890. | 普通用户(1) | 只能新增和查询 |
| admin | Admin123456. | root用户(2) | 所有权限 |

### 3. 如果仍然500错误

**检查清单**：
- [ ] 后端服务是否已重启？
- [ ] 浏览器Cookie是否已清除？
- [ ] 是否重新登录？
- [ ] Network标签中请求是否携带Cookie？
- [ ] 后端控制台是否有异常日志？

**查看详细错误**：
1. 查看后端控制台的异常堆栈
2. 查看浏览器Network标签的Response内容
3. 告诉我具体的错误信息

## 🎯 常见问题

### Q1: 为什么要用credentials: 'include'？

**A**: fetch默认不携带Cookie，添加这个选项后会自动携带当前域名的Cookie（包括JSESSIONID）。

### Q2: 为什么CORS不能用*？

**A**: 当需要携带Cookie时，浏览器安全策略要求必须指定具体的域名，不能用通配符。

### Q3: 为什么修改Java代码后要重启？

**A**: Java是编译型语言，修改代码后必须重新编译和重启才能生效。

### Q4: 如果我是普通用户，怎么测试删除功能？

**A**: 
1. 使用root账号登录（admin / Admin123456.）
2. 或者修改数据库将您的账号userType改为2

## 🎉 修复完成

现在所有功能应该都能正常工作了！

**关键修改**：
1. ✅ 前端所有请求携带Cookie
2. ✅ 后端CORS允许携带Cookie
3. ✅ 登录请求携带Cookie

**测试顺序**：
1. 重启后端
2. 清除浏览器Cookie
3. 重新登录
4. 测试新增、编辑、删除功能

如果还有问题，请告诉我具体的错误信息！
