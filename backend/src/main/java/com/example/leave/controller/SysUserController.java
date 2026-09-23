package com.example.leave.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.leave.entity.SysUser;
import com.example.leave.common.Result;
import com.example.leave.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /** 获取所有用户列表 */
    @GetMapping
    public Result<List<SysUser>> getAllUsers() {
        List<SysUser> list = sysUserService.list();
        return Result.success(list);
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody Map<String, String> params) {
        String userId = params.get("userId");
        String password = params.get("password");
        if (userId == null || password == null) {
            return Result.badRequest("账号和密码不能为空");
        }
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        if (!user.getPassword().equals(password)) {
            return Result.error(401, "密码错误");
        }
        return Result.success(user);
    }

    /** 根据ID获取用户 */
    @GetMapping("/{id}")
    public Result<SysUser> getUserById(@PathVariable String id) {
        SysUser user = sysUserService.getUserById(id);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        return Result.success(user);
    }

    /** 根据级别获取审批人 */
    @GetMapping("/approver/{level}")
    public Result<SysUser> getApproverByLevel(@PathVariable Integer level) {
        SysUser approver = sysUserService.getApproverByLevel(level);
        if (approver == null) {
            return Result.notFound("审批人不存在");
        }
        return Result.success(approver);
    }

    /** 添加员工 */
    @PostMapping
    public Result<SysUser> addUser(@RequestBody SysUser user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            // 自动生成ID
            user.setId("emp" + System.currentTimeMillis());
        }
        user.setPassword(user.getPassword() == null || user.getPassword().isEmpty() ? "123456" : user.getPassword());
        if (user.getLevel() == null) {
            user.setLevel("manager1".equals(user.getRole()) ? 2 : 1);
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        boolean saved = sysUserService.save(user);
        if (!saved) {
            return Result.error("添加员工失败");
        }
        return Result.success(user);
    }

    /** 删除员工 */
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable String userId) {
        boolean removed = sysUserService.removeById(userId);
        if (!removed) {
            return Result.notFound("用户不存在");
        }
        return Result.success();
    }
}
