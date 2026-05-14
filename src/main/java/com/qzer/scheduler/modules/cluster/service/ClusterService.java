
package com.qzer.scheduler.modules.cluster.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.cluster.entity.ClusterNode;
import com.qzer.scheduler.modules.cluster.entity.ClusterEvent;
import com.qzer.scheduler.modules.cluster.entity.FailureRecord;
import com.qzer.scheduler.modules.cluster.entity.TaskShard;

import java.util.List;

public interface ClusterService {
    // 节点管理
    String registerNode(String nodeName, String nodeIp, Integer nodePort, String role);
    void heartbeat(String nodeId);
    List&lt;ClusterNode&gt; listNodes();
    ClusterNode getNode(String nodeId);
    void removeNode(String nodeId);
    
    // 任务分片
    List&lt;TaskShard&gt; listShards();
    void assignTaskToNode(Long taskId, String nodeId);
    void rebalanceShards();
    void migrateTask(Long taskId, String targetNodeId);
    
    // 集群事件
    List&lt;ClusterEvent&gt; listEvents();
    void recordEvent(String eventType, String eventData, String sourceNode, String targetNode);
    
    // 故障管理
    List&lt;FailureRecord&gt; listFailures();
    FailureRecord getFailure(Long id);
    void recoverFailure(Long id);
    void checkNodeHealth();
    
    // 集群状态
    String getClusterStatus();
}
