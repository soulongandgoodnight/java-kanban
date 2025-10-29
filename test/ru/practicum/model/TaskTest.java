package ru.practicum.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void tasksShouldBeEqualWhenTheyHaveSameId() {
        var firstTask = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        var secondTask = new Task("Task 2", "Description 2", 1, TaskStatus.DONE);
        Assertions.assertEquals(firstTask, secondTask);
    }

    @Test
    void tasksShouldBeDifferentWhenTheyHaveDifferentId() {
        var firstTask = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        var secondTask = new Task("Task 1", "Description 1", 2, TaskStatus.NEW);
        Assertions.assertNotEquals(firstTask, secondTask);
    }

    @Test
    void taskAndEpicShouldBeEqualWhenTheyHaveSameId() {
        var task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        var epic = new Epic("Epic name", "Epic description,", 1, TaskStatus.DONE);
        Assertions.assertEquals(task, epic);
    }

    @Test
    void taskAndSubtaskShouldBeEqualWhenTheyHaveSameId() {
        var task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        var subtask = new Subtask("Subtask name", "Subtask description", 1, TaskStatus.NEW, 0);
        Assertions.assertEquals(task, subtask);
    }

    @Test
    void taskInheritorsShouldBeEqualWhenTheyHaveSameId() {
        var subtask = new Subtask("Subtask name", "Subtask description", 1, TaskStatus.NEW, 0);
        var epic = new Epic("Epic name", "Epic description,", 1, TaskStatus.DONE);
        Assertions.assertEquals(subtask, epic);
    }
}