package com.yq.spbYq.domain;

import java.io.Serializable;

import lombok.Data;

/**
 * (UserPrivacyProfiles)实体类
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@Data
public class UserPrivacyProfilesEntity implements Serializable {
    private static final long serialVersionUID = 996037421931355204L;

    /**
     * 统一主键ID（用于前端统一操作）
     */
    private Integer id;
    /**
     * 关联用户ID
     */
    private Integer userId;
    /**
     * 档案可见性
     */
    private Object profileVisibility;
    /**
     * 健康数据是否可见
     */
    private Integer healthDataVisible;
    /**
     * 联系方式是否可见
     */
    private Integer contactInfoVisible;
    /**
     * 是否允许被搜索到
     */
    private Integer searchIndexed;
    /**
     * 是否启用双因素认证
     */
    private Integer twoFactorAuth;
    /**
     * 最后密码修改时间
     */
    private String lastPasswordChange;
    /**
     * 数据加密级别
     */
    private Object dataEncryptionLevel;
    /**
     * 自动登出时间(分钟)
     */
    private Integer autoLogoutMinutes;
    /**
     * 分享是否需要批准
     */
    private Integer shareApprovalRequired;
    /**
     * 默认分享有效期(天)
     */
    private Integer defaultShareExpiryDays;
    /**
     * 最后访问时间
     */
    private String lastAccess;
    /**
     * 最后访问IP
     */
    private String lastAccessIp;
    /**
     * 连续失败登录次数
     */
    private Integer failedLoginAttempts;

    private String createdAt;

    private String updatedAt;

}

