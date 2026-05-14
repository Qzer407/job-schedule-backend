
package com.qzer.scheduler.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzer.scheduler.modules.message.entity.DeadLetter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeadLetterMapper extends BaseMapper&lt;DeadLetter&gt; {
}

