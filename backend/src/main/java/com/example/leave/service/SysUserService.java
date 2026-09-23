package com.example.leave.service;


import com.baomidou.mybatisplus.extension.service.IService;


import com.example.leave.entity.SysUser;

import java.io.Serializable;

/**
 * 用户管理服务接口
 *
 * TODO: 定义用户相关业务方法
 */
public interface SysUserService extends IService<SysUser> {
    SysUser getUserById(String userId);

    SysUser getApproverByLevel(Integer level);
}
