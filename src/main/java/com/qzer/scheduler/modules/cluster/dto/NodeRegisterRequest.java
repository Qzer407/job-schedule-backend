
package com.qzer.scheduler.modules.cluster.dto;

import lombok.Data;

@Data
public class NodeRegisterRequest {
    private String nodeName;
    private String nodeIp;
    private Integer nodePort;
    private String role;
}
