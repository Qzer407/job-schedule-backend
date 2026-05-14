package com.qzer.scheduler.util;

import com.qzer.scheduler.entity.TaskDependency;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DagTopologySortTest {

    @Test
    void testSortWithoutDependencies() {
        List<TaskDependency> dependencies = new ArrayList<>();
        List<Long> taskIds = Arrays.asList(1L, 2L, 3L);

        List<Long> result = DagTopologySort.sort(dependencies, taskIds);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(taskIds));
    }

    @Test
    void testSortWithSimpleDependency() {
        List<TaskDependency> dependencies = new ArrayList<>();
        TaskDependency dep = new TaskDependency();
        dep.setParentTaskId(1L);
        dep.setTaskId(2L);
        dependencies.add(dep);

        List<Long> taskIds = Arrays.asList(1L, 2L, 3L);

        List<Long> result = DagTopologySort.sort(dependencies, taskIds);

        assertNotNull(result);
        assertEquals(3, result.size());
        int index1 = result.indexOf(1L);
        int index2 = result.indexOf(2L);
        assertTrue(index1 < index2, "任务1应在任务2之前执行");
    }

    @Test
    void testSortWithMultipleDependencies() {
        List<TaskDependency> dependencies = new ArrayList<>();
        
        TaskDependency dep1 = new TaskDependency();
        dep1.setParentTaskId(1L);
        dep1.setTaskId(2L);
        dependencies.add(dep1);

        TaskDependency dep2 = new TaskDependency();
        dep2.setParentTaskId(2L);
        dep2.setTaskId(3L);
        dependencies.add(dep2);

        List<Long> taskIds = Arrays.asList(1L, 2L, 3L);

        List<Long> result = DagTopologySort.sort(dependencies, taskIds);

        assertNotNull(result);
        assertEquals(3, result.size());
        
        int index1 = result.indexOf(1L);
        int index2 = result.indexOf(2L);
        int index3 = result.indexOf(3L);
        
        assertTrue(index1 < index2, "任务1应在任务2之前执行");
        assertTrue(index2 < index3, "任务2应在任务3之前执行");
    }

    @Test
    void testSortWithCircularDependency() {
        List<TaskDependency> dependencies = new ArrayList<>();
        
        TaskDependency dep1 = new TaskDependency();
        dep1.setParentTaskId(1L);
        dep1.setTaskId(2L);
        dependencies.add(dep1);

        TaskDependency dep2 = new TaskDependency();
        dep2.setParentTaskId(2L);
        dep2.setTaskId(1L);
        dependencies.add(dep2);

        List<Long> taskIds = Arrays.asList(1L, 2L);

        assertThrows(RuntimeException.class, () -> DagTopologySort.sort(dependencies, taskIds));
    }

    @Test
    void testBuildDependencyMap() {
        List<TaskDependency> dependencies = new ArrayList<>();
        
        TaskDependency dep1 = new TaskDependency();
        dep1.setParentTaskId(1L);
        dep1.setTaskId(2L);
        dependencies.add(dep1);

        TaskDependency dep2 = new TaskDependency();
        dep2.setParentTaskId(1L);
        dep2.setTaskId(3L);
        dependencies.add(dep2);

        TaskDependency dep3 = new TaskDependency();
        dep3.setParentTaskId(2L);
        dep3.setTaskId(3L);
        dependencies.add(dep3);

        var result = DagTopologySort.buildDependencyMap(dependencies);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(1L).size());
        assertEquals(1, result.get(2L).size());
    }
}
