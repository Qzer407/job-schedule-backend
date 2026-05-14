package com.qzer.scheduler.common.util;

import com.qzer.scheduler.modules.workflow.entity.TaskDependency;

import java.util.*;

public class DagTopologySort {

    public static List<Long> sort(List<TaskDependency> dependencies, List<Long> allTaskIds) {
        Map<Long, List<Long>> graph = new HashMap<>();
        Map<Long, Integer> inDegree = new HashMap<>();

        for (Long taskId : allTaskIds) {
            graph.put(taskId, new ArrayList<>());
            inDegree.put(taskId, 0);
        }

        for (TaskDependency dependency : dependencies) {
            Long from = dependency.getParentTaskId();
            Long to = dependency.getTaskId();
            if (from != null && to != null) {
                graph.get(from).add(to);
                inDegree.put(to, inDegree.get(to) + 1);
            }
        }

        Queue<Long> queue = new LinkedList<>();
        for (Long taskId : allTaskIds) {
            if (inDegree.get(taskId) == 0) {
                queue.add(taskId);
            }
        }

        List<Long> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            Long current = queue.poll();
            result.add(current);

            for (Long neighbor : graph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (result.size() != allTaskIds.size()) {
            throw new RuntimeException("工作流存在循环依赖");
        }

        return result;
    }

    public static Map<Long, List<TaskDependency>> buildDependencyMap(List<TaskDependency> dependencies) {
        Map<Long, List<TaskDependency>> map = new HashMap<>();
        for (TaskDependency dependency : dependencies) {
            map.computeIfAbsent(dependency.getParentTaskId(), k -> new ArrayList<>()).add(dependency);
        }
        return map;
    }
}
