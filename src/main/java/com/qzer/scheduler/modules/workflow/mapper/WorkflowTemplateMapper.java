package com.qzer.scheduler.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzer.scheduler.modules.workflow.entity.WorkflowTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkflowTemplateMapper extends BaseMapper<WorkflowTemplate> {

    List<WorkflowTemplate> selectByType(@Param("templateType") String templateType);
}
