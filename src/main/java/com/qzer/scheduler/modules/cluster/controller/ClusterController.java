
package com.qzer.scheduler.modules.cluster.controller;

import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.cluster.dto.NodeRegisterRequest;
import com.qzer.scheduler.modules.cluster.dto.TaskAssignRequest;
import com.qzer.scheduler.modules.cluster.dto.TaskMigrateRequest;
import com.qzer.scheduler.modules.cluster.entity.ClusterNode;
import com.qzer.scheduler.modules.cluster.entity.ClusterEvent;
import com.qzer.scheduler.modules.cluster.entity.FailureRecord;
import com.qzer.scheduler.modules.cluster.entity.TaskShard;
import com.qzer.scheduler.modules.cluster.service.ClusterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cluster")
@RequiredArgsConstructor
public class ClusterController {

    private final ClusterService clusterService;

    // ================================== 节点管理 ==================================
    
    @PostMapping("/nodes/register")
    public ApiResponse&lt;String&gt; registerNode(@RequestBody NodeRegisterRequest request) {
        return ApiResponse.success(clusterService.registerNode(
                request.getNodeName(), request.getNodeIp(), request.getNodePort(), request.getRole()));
    }

    @PostMapping("/nodes/{nodeId}/heartbeat")
    public ApiResponse&lt;Void&gt; heartbeat(@PathVariable String nodeId) {
        clusterService.heartbeat(nodeId);
        return ApiResponse.success(null);
    }

    @GetMapping("/nodes")
    public ApiResponse&lt;List&lt;ClusterNode&gt;&gt; listNodes() {
        return ApiResponse.success(clusterService.listNodes());
    }

    @GetMapping("/nodes/{nodeId}")
    public ApiResponse&lt;ClusterNode&gt; getNode(@PathVariable String nodeId) {
        return ApiResponse.success(clusterService.getNode(nodeId));
    }

    @DeleteMapping("/nodes/{nodeId}")
    public ApiResponse&lt;Void&gt; removeNode(@PathVariable String nodeId) {
        clusterService.removeNode(nodeId);
        return ApiResponse.success(null);
    }

    // ================================== 任务分片 ==================================
    
    @GetMapping("/shards")
    public ApiResponse&lt;List&lt;TaskShard&gt;&gt; listShards() {
        return ApiResponse.success(clusterService.listShards());
    }

    @PostMapping("/shards/assign")
    public ApiResponse&lt;Void&gt; assignTask(@RequestBody TaskAssignRequest request) {
        clusterService.assignTaskToNode(request.getTaskId(), request.getNodeId());
        return ApiResponse.success(null);
    }

    @PostMapping("/shards/rebalance")
    public ApiResponse&lt;Void&gt; rebalanceShards() {
        clusterService.rebalanceShards();
        return ApiResponse.success(null);
    }

    @PostMapping("/shards/{taskId}/migrate")
    public ApiResponse&lt;Void&gt; migrateTask(@PathVariable Long taskId, @RequestBody TaskMigrateRequest request) {
        clusterService.migrateTask(taskId, request.getTargetNodeId());
        return ApiResponse.success(null);
    }

    // ================================== 集群事件 ==================================
    
    @GetMapping("/events")
    public ApiResponse&lt;List&lt;ClusterEvent&gt;&gt; listEvents() {
        return ApiResponse.success(clusterService.listEvents());
    }

    // ================================== 故障管理 ==================================
    
    @GetMapping("/failures")
    public ApiResponse&lt;List&lt;FailureRecord&gt;&gt; listFailures() {
        return ApiResponse.success(clusterService.listFailures());
    }

    @GetMapping("/failures/{id}")
    public ApiResponse&lt;FailureRecord&gt; getFailure(@PathVariable Long id) {
        return ApiResponse.success(clusterService.getFailure(id));
    }

    @PostMapping("/failures/{id}/recover")
    public ApiResponse&lt;Void&gt; recoverFailure(@PathVariable Long id) {
        clusterService.recoverFailure(id);
        return ApiResponse.success(null);
    }

    // ================================== 集群状态 ==================================
    
    @GetMapping("/status")
    public ApiResponse&lt;String&gt; getClusterStatus() {
        return ApiResponse.success(clusterService.getClusterStatus());
    }
}
