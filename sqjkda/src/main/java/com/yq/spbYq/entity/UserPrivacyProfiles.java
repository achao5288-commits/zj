package com.yq.spbYq.entity;

import java.io.Serializable;

/**
 * (UserPrivacyProfiles)实体类
 *
 * @author makejava
 * @since 2025-05-11 10:26:06
 */
public class UserPrivacyProfiles implements Serializable {
    private static final long serialVersionUID = -68729193412248478L;

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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Object getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(Object profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public Integer getHealthDataVisible() {
        return healthDataVisible;
    }

    public void setHealthDataVisible(Integer healthDataVisible) {
        this.healthDataVisible = healthDataVisible;
    }

    public Integer getContactInfoVisible() {
        return contactInfoVisible;
    }

    public void setContactInfoVisible(Integer contactInfoVisible) {
        this.contactInfoVisible = contactInfoVisible;
    }

    public Integer getSearchIndexed() {
        return searchIndexed;
    }

    public void setSearchIndexed(Integer searchIndexed) {
        this.searchIndexed = searchIndexed;
    }

    public Integer getTwoFactorAuth() {
        return twoFactorAuth;
    }

    public void setTwoFactorAuth(Integer twoFactorAuth) {
        this.twoFactorAuth = twoFactorAuth;
    }

    public String getLastPasswordChange() {
        return lastPasswordChange;
    }

    public void setLastPasswordChange(String lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }

    public Object getDataEncryptionLevel() {
        return dataEncryptionLevel;
    }

    public void setDataEncryptionLevel(Object dataEncryptionLevel) {
        this.dataEncryptionLevel = dataEncryptionLevel;
    }

    public Integer getAutoLogoutMinutes() {
        return autoLogoutMinutes;
    }

    public void setAutoLogoutMinutes(Integer autoLogoutMinutes) {
        this.autoLogoutMinutes = autoLogoutMinutes;
    }

    public Integer getShareApprovalRequired() {
        return shareApprovalRequired;
    }

    public void setShareApprovalRequired(Integer shareApprovalRequired) {
        this.shareApprovalRequired = shareApprovalRequired;
    }

    public Integer getDefaultShareExpiryDays() {
        return defaultShareExpiryDays;
    }

    public void setDefaultShareExpiryDays(Integer defaultShareExpiryDays) {
        this.defaultShareExpiryDays = defaultShareExpiryDays;
    }

    public String getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(String lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getLastAccessIp() {
        return lastAccessIp;
    }

    public void setLastAccessIp(String lastAccessIp) {
        this.lastAccessIp = lastAccessIp;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}

