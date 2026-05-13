package com.yq.spbYq.domain;

import java.io.Serializable;

import lombok.Data;

/**
 * (Users)实体类
 *
 * @author makejava
 * @since 2025-05-11 10:57:08
 */
@Data
public class UsersEntity implements Serializable {
    private static final long serialVersionUID = -93199088455729001L;

    /**
     * 统一主键ID（用于前端统一操作）
     */
    private Integer id;

    private Integer userId;

    private String username;

    private String phoneNumber;

    private String passwordHash;

    private String userInfo;

    private String userProfile;

    private String createdAt;

    private String updatedAt;

    private Integer userType; // 1普通用户 2root

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    /**
     * 获取统一ID（映射到userId）
     */
    public Integer getId() {
        return userId;
    }

    /**
     * 设置统一ID（映射到userId）
     */
    public void setId(Integer id) {
        this.id = id;
        this.userId = id;
    }

}

