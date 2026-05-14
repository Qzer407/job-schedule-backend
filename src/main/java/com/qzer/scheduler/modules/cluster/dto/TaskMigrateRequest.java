
package com.qzer.scheduler.modules.cluster.dto;

import lombok.Data;

@Data
public class TaskMigrateRequest {
    private String targetNodeId;
}
