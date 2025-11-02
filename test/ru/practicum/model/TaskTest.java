package ru.practicum.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {

    @Test
    void tasksShouldBeEqualWhenTheyHaveSameId() {
        var firstTask = new Task("Task 1", "Description 1", 1, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var secondTask = new Task("Task 2", "Description 2", 1, TaskStatus.DONE,
                LocalDateTime.now(), Duration.ofHours(3));
        Assertions.assertEquals(firstTask, secondTask);
    }

    @Test
    void tasksShouldBeDifferentWhenTheyHaveDifferentId() {
        var firstTask = new Task("Task 1", "Description 1", 1, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var secondTask = new Task("Task 1", "Description 1", 2, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        Assertions.assertNotEquals(firstTask, secondTask);
    }

    @Test
    void taskAndEpicShouldBeEqualWhenTheyHaveSameId() {
        var task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epic = new Epic("Epic name", "Epic description,", 1, TaskStatus.DONE,
                LocalDateTime.now(), Duration.ofHours(3));
        Assertions.assertEquals(task, epic);
    }

    @Test
    void taskAndSubtaskShouldBeEqualWhenTheyHaveSameId() {
        var task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var subtask = new Subtask("Subtask name", "Subtask description", 1, TaskStatus.NEW,
                0, LocalDateTime.now(), Duration.ofHours(3));
        Assertions.assertEquals(task, subtask);
    }

    @Test
    void taskInheritorsShouldBeEqualWhenTheyHaveSameId() {
        var subtask = new Subtask("Subtask name", "Subtask description", 1, TaskStatus.NEW,
                0, LocalDateTime.now(), Duration.ofHours(3));
        var epic = new Epic("Epic name", "Epic description,", 1, TaskStatus.DONE,
                LocalDateTime.now(), Duration.ofHours(3));
        Assertions.assertEquals(subtask, epic);
    }
}