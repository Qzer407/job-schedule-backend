package com.qzer.scheduler.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzer.scheduler.modules.task.entity.SubTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubTaskMapper extends BaseMapper<SubTask> {

    List<SubTask> selectByParentTaskId(@Param("parentTaskId") Long parentTaskId);

    List<SubTask> selectByParentTaskIds(@Param("parentTaskIds") List<Long> parentTaskIds);

    int deleteByParentTaskId(@Param("parentTaskId") Long parentTaskId);
}
