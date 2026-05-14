
package com.qzer.scheduler.modules.cluster.dto;

import lombok.Data;

@Data
public class TaskAssignRequest {
    private Long taskId;
    private String nodeId;
}
