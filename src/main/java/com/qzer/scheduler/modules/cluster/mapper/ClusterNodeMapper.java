
package com.qzer.scheduler.modules.cluster.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzer.scheduler.modules.cluster.entity.ClusterNode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClusterNodeMapper extends BaseMapper&lt;ClusterNode&gt; {
}
