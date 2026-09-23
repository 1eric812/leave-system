package com.example.leave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.leave.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批记录 Mapper
 *
 * TODO: 定义审批记录相关自定义数据库操作方法
 */
@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord> {

}
