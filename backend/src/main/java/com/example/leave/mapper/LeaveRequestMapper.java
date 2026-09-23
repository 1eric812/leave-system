package com.example.leave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.leave.entity.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * 请假申请 Mapper
 *
 * TODO: 定义请假申请相关自定义数据库操作方法
 */
@Mapper
public interface LeaveRequestMapper extends BaseMapper<LeaveRequest> {
    /**int updateStatusAndLevel(
            @Param("id") String id,
            @Param("status") String status,
            @Param("approvalLevel") Integer approvalLevel
    );**/

}
