
package com.qzer.scheduler.modules.cluster.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.cluster.entity.ClusterNode;
import com.qzer.scheduler.modules.cluster.entity.ClusterEvent;
import com.qzer.scheduler.modules.cluster.entity.FailureRecord;
import com.qzer.scheduler.modules.cluster.entity.TaskShard;
import com.qzer.scheduler.modules.cluster.mapper.ClusterNodeMapper;
import com.qzer.scheduler.modules.cluster.mapper.ClusterEventMapper;
import com.qzer.scheduler.modules.cluster.mapper.FailureRecordMapper;
import com.qzer.scheduler.modules.cluster.mapper.TaskShardMapper;
import com.qzer.scheduler.modules.cluster.service.ClusterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterServiceImpl implements ClusterService {

    private final ClusterNodeMapper clusterNodeMapper;
    private final ClusterEventMapper clusterEventMapper;
    private final FailureRecordMapper failureRecordMapper;
    private final TaskShardMapper taskShardMapper;

    private static final int HEARTBEAT_TIMEOUT_SECONDS = 60;
    private static final int STATUS_OFFLINE = 0;
    private static final int STATUS_ONLINE = 1;
    private static final int STATUS_FAILED = 2;
    private static final int SHARD_STATUS_PENDING = 0;
    private static final int SHARD_STATUS_ASSIGNED = 1;
    private static final int SHARD_STATUS_MIGRATING = 2;

    @Override
    public String registerNode(String nodeName, String nodeIp, Integer nodePort, String role) {
        String nodeId = UUID.randomUUID().toString();
        
        LambdaQueryWrapper&lt;ClusterNode&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        wrapper.eq(ClusterNode::getNodeIp, nodeIp)
                .eq(ClusterNode::getNodePort, nodePort);
        ClusterNode existingNode = clusterNodeMapper.selectOne(wrapper);
        
        if (existingNode != null) {
            log.info("节点已存在: {}:{}", nodeIp, nodePort);
            return existingNode.getNodeId();
        }
        
        ClusterNode node = new ClusterNode();
        node.setNodeId(nodeId);
        node.setNodeName(nodeName);
        node.setNodeIp(nodeIp);
        node.setNodePort(nodePort);
        node.setRole(role);
        node.setStatus(STATUS_ONLINE);
        node.setLastHeartbeat(LocalDateTime.now());
        node.setCreateTime(LocalDateTime.now());
        node.setUpdateTime(LocalDateTime.now());
        
        clusterNodeMapper.insert(node);
        
        recordEvent("node_join", String.format("节点 %s 加入集群", nodeName), null, nodeId);
        
        log.info("节点注册成功: {}", nodeId);
        return nodeId;
    }

    @Override
    public void heartbeat(String nodeId) {
        ClusterNode node = getNode(nodeId);
        if (node != null) {
            node.setLastHeartbeat(LocalDateTime.now());
            node.setStatus(STATUS_ONLINE);
            node.setUpdateTime(LocalDateTime.now());
            clusterNodeMapper.updateById(node);
            log.debug("节点心跳更新: {}", nodeId);
        }
    }

    @Override
    public List&lt;ClusterNode&gt; listNodes() {
        return clusterNodeMapper.selectList(new LambdaQueryWrapper&lt;ClusterNode&gt;()
                .orderByDesc(ClusterNode::getCreateTime));
    }

    @Override
    public ClusterNode getNode(String nodeId) {
        LambdaQueryWrapper&lt;ClusterNode&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        wrapper.eq(ClusterNode::getNodeId, nodeId);
        return clusterNodeMapper.selectOne(wrapper);
    }

    @Override
    public void removeNode(String nodeId) {
        ClusterNode node = getNode(nodeId);
        if (node == null) {
            return;
        }
        
        // 迁移该节点上的任务
        LambdaQueryWrapper&lt;TaskShard&gt; shardWrapper = new LambdaQueryWrapper&lt;&gt;();
        shardWrapper.eq(TaskShard::getAssignedNodeId, nodeId);
        List&lt;TaskShard&gt; shards = taskShardMapper.selectList(shardWrapper);
        
        for (TaskShard shard : shards) {
            shard.setShardStatus(SHARD_STATUS_PENDING);
            shard.setAssignedNodeId(null);
            shard.setUpdateTime(LocalDateTime.now());
            taskShardMapper.updateById(shard);
        }
        
        recordEvent("node_leave", String.format("节点 %s 离开集群", node.getNodeName()), nodeId, null);
        
        clusterNodeMapper.deleteById(node.getId());
        log.info("节点移除成功: {}", nodeId);
    }

    @Override
    public List&lt;TaskShard&gt; listShards() {
        return taskShardMapper.selectList(new LambdaQueryWrapper&lt;TaskShard&gt;()
                .orderByDesc(TaskShard::getCreateTime));
    }

    @Override
    public void assignTaskToNode(Long taskId, String nodeId) {
        LambdaQueryWrapper&lt;TaskShard&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        wrapper.eq(TaskShard::getTaskId, taskId);
        TaskShard existingShard = taskShardMapper.selectOne(wrapper);
        
        if (existingShard != null) {
            existingShard.setAssignedNodeId(nodeId);
            existingShard.setShardStatus(SHARD_STATUS_ASSIGNED);
            existingShard.setUpdateTime(LocalDateTime.now());
            taskShardMapper.updateById(existingShard);
        } else {
            TaskShard shard = new TaskShard();
            shard.setTaskId(taskId);
            shard.setAssignedNodeId(nodeId);
            shard.setShardStatus(SHARD_STATUS_ASSIGNED);
            shard.setCreateTime(LocalDateTime.now());
            shard.setUpdateTime(LocalDateTime.now());
            taskShardMapper.insert(shard);
        }
        
        recordEvent("task_assign", String.format("任务 %d 分配到节点 %s", taskId, nodeId), null, nodeId);
        log.info("任务分配成功: {} -&gt; {}", taskId, nodeId);
    }

    @Override
    public void rebalanceShards() {
        List&lt;ClusterNode&gt; onlineNodes = clusterNodeMapper.selectList(
                new LambdaQueryWrapper&lt;ClusterNode&gt;().eq(ClusterNode::getStatus, STATUS_ONLINE));
        
        if (onlineNodes.isEmpty()) {
            log.warn("没有可用节点，无法进行负载均衡");
            return;
        }
        
        List&lt;TaskShard&gt; unassignedShards = taskShardMapper.selectList(
                new LambdaQueryWrapper&lt;TaskShard&gt;().eq(TaskShard::getShardStatus, SHARD_STATUS_PENDING));
        
        int nodeIndex = 0;
        for (TaskShard shard : unassignedShards) {
            ClusterNode targetNode = onlineNodes.get(nodeIndex % onlineNodes.size());
            shard.setAssignedNodeId(targetNode.getNodeId());
            shard.setShardStatus(SHARD_STATUS_ASSIGNED);
            shard.setUpdateTime(LocalDateTime.now());
            taskShardMapper.updateById(shard);
            nodeIndex++;
        }
        
        log.info("任务负载均衡完成，分配了 {} 个任务", unassignedShards.size());
    }

    @Override
    public void migrateTask(Long taskId, String targetNodeId) {
        LambdaQueryWrapper&lt;TaskShard&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        wrapper.eq(TaskShard::getTaskId, taskId);
        TaskShard shard = taskShardMapper.selectOne(wrapper);
        
        if (shard == null) {
            log.warn("任务分片不存在: {}", taskId);
            return;
        }
        
        String sourceNodeId = shard.getAssignedNodeId();
        shard.setShardStatus(SHARD_STATUS_MIGRATING);
        taskShardMapper.updateById(shard);
        
        shard.setAssignedNodeId(targetNodeId);
        shard.setShardStatus(SHARD_STATUS_ASSIGNED);
        shard.setUpdateTime(LocalDateTime.now());
        taskShardMapper.updateById(shard);
        
        recordEvent("task_migrate", String.format("任务 %d 从 %s 迁移到 %s", taskId, sourceNodeId, targetNodeId), 
                sourceNodeId, targetNodeId);
        log.info("任务迁移成功: {} -&gt; {}", taskId, targetNodeId);
    }

    @Override
    public List&lt;ClusterEvent&gt; listEvents() {
        return clusterEventMapper.selectList(new LambdaQueryWrapper&lt;ClusterEvent&gt;()
                .orderByDesc(ClusterEvent::getEventTime));
    }

    @Override
    public void recordEvent(String eventType, String eventData, String sourceNode, String targetNode) {
        ClusterEvent event = new ClusterEvent();
        event.setEventType(eventType);
        event.setEventData(eventData);
        event.setSourceNode(sourceNode);
        event.setTargetNode(targetNode);
        event.setEventTime(LocalDateTime.now());
        event.setCreateTime(LocalDateTime.now());
        clusterEventMapper.insert(event);
    }

    @Override
    public List&lt;FailureRecord&gt; listFailures() {
        return failureRecordMapper.selectList(new LambdaQueryWrapper&lt;FailureRecord&gt;()
                .orderByDesc(FailureRecord::getFailureTime));
    }

    @Override
    public FailureRecord getFailure(Long id) {
        return failureRecordMapper.selectById(id);
    }

    @Override
    @Transactional
    public void recoverFailure(Long id) {
        FailureRecord failure = getFailure(id);
        if (failure == null) {
            return;
        }
        
        failure.setRecoveryStatus(1);
        failure.setRecoveryTime(LocalDateTime.now());
        failure.setUpdateTime(LocalDateTime.now());
        failureRecordMapper.updateById(failure);
        
        // 恢复节点状态
        ClusterNode node = getNode(failure.getNodeId());
        if (node != null) {
            node.setStatus(STATUS_ONLINE);
            node.setUpdateTime(LocalDateTime.now());
            clusterNodeMapper.updateById(node);
        }
        
        recordEvent("failover_recover", String.format("节点 %s 故障恢复", failure.getNodeId()), 
                null, failure.getNodeId());
        log.info("故障恢复成功: {}", id);
    }

    @Override
    @Scheduled(fixedRate = 30000)
    public void checkNodeHealth() {
        List&lt;ClusterNode&gt; nodes = listNodes();
        for (ClusterNode node : nodes) {
            if (node.getStatus() == STATUS_ONLINE) {
                LocalDateTime lastHeartbeat = node.getLastHeartbeat();
                if (lastHeartbeat == null) {
                    continue;
                }
                
                long secondsSinceHeartbeat = java.time.Duration.between(lastHeartbeat, LocalDateTime.now()).getSeconds();
                if (secondsSinceHeartbeat &gt; HEARTBEAT_TIMEOUT_SECONDS) {
                    // 节点故障
                    node.setStatus(STATUS_FAILED);
                    node.setUpdateTime(LocalDateTime.now());
                    clusterNodeMapper.updateById(node);
                    
                    // 记录故障
                    FailureRecord failure = new FailureRecord();
                    failure.setNodeId(node.getNodeId());
                    failure.setFailureType("heartbeat_timeout");
                    failure.setFailureDetail(String.format("节点心跳超时 %d 秒", secondsSinceHeartbeat));
                    failure.setRecoveryStatus(0);
                    failure.setFailureTime(LocalDateTime.now());
                    failure.setCreateTime(LocalDateTime.now());
                    failure.setUpdateTime(LocalDateTime.now());
                    failureRecordMapper.insert(failure);
                    
                    recordEvent("failover", String.format("节点 %s 故障", node.getNodeName()), 
                            node.getNodeId(), null);
                    log.warn("节点故障: {}", node.getNodeId());
                    
                    // 迁移该节点上的任务
                    triggerFailover(node.getNodeId());
                }
            }
        }
    }

    private void triggerFailover(String failedNodeId) {
        // 获取故障节点上的任务
        LambdaQueryWrapper&lt;TaskShard&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        wrapper.eq(TaskShard::getAssignedNodeId, failedNodeId);
        List&lt;TaskShard&gt; shards = taskShardMapper.selectList(wrapper);
        
        // 获取可用节点
        List&lt;ClusterNode&gt; onlineNodes = clusterNodeMapper.selectList(
                new LambdaQueryWrapper&lt;ClusterNode&gt;().eq(ClusterNode::getStatus, STATUS_ONLINE));
        
        if (!onlineNodes.isEmpty() &amp;&amp; !shards.isEmpty()) {
            int nodeIndex = 0;
            for (TaskShard shard : shards) {
                ClusterNode targetNode = onlineNodes.get(nodeIndex % onlineNodes.size());
                shard.setAssignedNodeId(targetNode.getNodeId());
                shard.setUpdateTime(LocalDateTime.now());
                taskShardMapper.updateById(shard);
                nodeIndex++;
            }
            log.info("故障转移完成，迁移了 {} 个任务", shards.size());
        }
    }

    @Override
    public String getClusterStatus() {
        List&lt;ClusterNode&gt; allNodes = listNodes();
        long onlineCount = allNodes.stream().filter(n -&gt; n.getStatus() == STATUS_ONLINE).count();
        long failedCount = allNodes.stream().filter(n -&gt; n.getStatus() == STATUS_FAILED).count();
        
        return String.format("{\"totalNodes\": %d, \"onlineNodes\": %d, \"failedNodes\": %d}", 
                allNodes.size(), onlineCount, failedCount);
    }
}
