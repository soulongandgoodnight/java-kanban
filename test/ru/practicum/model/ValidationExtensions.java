package ru.practicum.model;

import org.junit.jupiter.api.Assertions;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidationExtensions {
    public static void assertTasksAreEqual(Task left, Task right) {
        Assertions.assertEquals(left.getId(), right.getId());
        Assertions.assertEquals(left.getName(), right.getName());
        Assertions.assertEquals(left.getDescription(), right.getDescription());
        Assertions.assertEquals(left.getStatus(), right.getStatus());
        Assertions.assertEquals(left.getStartTime(), right.getStartTime());
        Assertions.assertEquals(left.getDuration(), right.getDuration());
        Assertions.assertEquals(left.getEndTime(), right.getEndTime());
    }

    public static void assertEpicsAreEqual(Epic left, Epic right) {
        assertTasksAreEqual(left, right);
        assertTaskSetsAreEqual(left.getSubtasks().stream(), right.getSubtasks().stream());
    }

    public static void assertTaskSetsAreEqual(Stream<? extends Task> left, Stream<? extends Task> right) {
        var leftSubtasks = left.collect(Collectors.toMap(Task::getId, v -> v));
        var rightSubtasks = right.collect(Collectors.toMap(Task::getId, v -> v));
        for (var leftTask : leftSubtasks.entrySet()) {
            var rightTask = rightSubtasks.get(leftTask.getKey());
            Assertions.assertNotNull(rightTask);
            if (leftTask.getValue() instanceof Subtask leftSubtask && rightTask instanceof Subtask rightSubtask) {
                assertSubtasksAreEqual(leftSubtask, rightSubtask);
            } else if (leftTask.getValue() instanceof Epic leftEpic && rightTask instanceof Epic rightEpic) {
                assertEpicsAreEqual(leftEpic, rightEpic);
            } else {
                assertTasksAreEqual(leftTask.getValue(), rightTask);
            }
        }
    }

    public static void assertSubtasksAreEqual(Subtask left, Subtask right) {
        assertTasksAreEqual(left, right);
        Assertions.assertEquals(left.getEpicId(), right.getEpicId());
    }
}
