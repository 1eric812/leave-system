package com.example.leave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.leave.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户管理 Mapper
 *
 * TODO: 定义用户相关自定义数据库操作方法
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
