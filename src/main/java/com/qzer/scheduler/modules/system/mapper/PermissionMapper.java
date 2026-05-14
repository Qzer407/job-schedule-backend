package com.qzer.scheduler.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzer.scheduler.modules.system.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
