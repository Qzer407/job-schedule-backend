
package com.qzer.scheduler.modules.cluster.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzer.scheduler.modules.cluster.entity.FailureRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FailureRecordMapper extends BaseMapper&lt;FailureRecord&gt; {
}
