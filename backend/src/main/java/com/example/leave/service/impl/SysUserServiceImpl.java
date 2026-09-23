package com.example.leave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.leave.entity.SysUser;
import com.example.leave.mapper.SysUserMapper;
import com.example.leave.service.SysUserService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
/**
 * 用户管理服务实现
 *
 * TODO: 实现用户管理业务逻辑
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {



    @Override
    public SysUser getUserById(String userId){
    return this.getById(userId);
}
@Override
    public SysUser getApproverByLevel(Integer level){
    return this.lambdaQuery()
            .eq(SysUser::getLevel, level)
            .last("LIMIT 1")
            .one();
    }
}
