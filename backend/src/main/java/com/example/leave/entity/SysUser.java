package com.example.leave.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user")
public class SysUser {

    /** 用户ID（如 emp001, mgr001） */
    @Id
    @Column(length = 32, nullable = false)
    private String id;

    /** 用户姓名 */
    @Column(length = 50, nullable = false)
    private String name;

    /** 所属部门 */
    @Column(length = 50, nullable = false)
    private String dept;

    /** 角色（employee / manager1 / manager2） */
    @Column(length = 20, nullable = false)
    private String role;

    /** 审批级别（1=员工, 2=一级经理, 3=二级经理） */
    @Column(nullable = false)
    private Integer level;

    /** 登录密码 */
    @Column(length = 100, nullable = false)
    private String password;

    /** 创建时间（数据库自动维护） */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间（数据库自动维护） */
    private LocalDateTime updatedAt;
}
