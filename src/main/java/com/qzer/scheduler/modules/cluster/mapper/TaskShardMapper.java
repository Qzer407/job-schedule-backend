
package com.qzer.scheduler.modules.cluster.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzer.scheduler.modules.cluster.entity.TaskShard;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskShardMapper extends BaseMapper&lt;TaskShard&gt; {
}
