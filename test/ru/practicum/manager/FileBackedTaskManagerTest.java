package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


class FileBackedTaskManagerTest {

    @Test
    void when_managerConstructsWithEmptyFile_should_beConstructed() throws IOException {
        // given
        var file = File.createTempFile("test", "FileBackedTaskManager");

        // do
        var manager = new FileBackedTaskManager(file);

        // expect
        var br = new BufferedReader(new FileReader(file));
        var actualHeader = br.readLine();
        var actualFileEnd = br.readLine();
        Assertions.assertEquals(FileBackedTaskManager.FILE_HEADER, actualHeader);
        Assertions.assertNull(actualFileEnd);

    }

    @Test
    void when_addsDifferentTasksToManagerAndOtherManagerLoadsFromTheSameFile_should_beEqual() throws IOException {
        // given
        var file = File.createTempFile("test", "FileBackedTaskManager");
        var originalManager = new FileBackedTaskManager(file);
        var task1 = new Task("Задача 1", "Описание задачи 1", 0, TaskStatus.NEW);
        var task2 = new Task("Задача 2", "Описание задачи 2", 0, TaskStatus.NEW);
        var epic1 = new Epic("Эпик 1", "Описание эпика 1", 0, TaskStatus.NEW);
        var epic2 = new Epic("Эпик 2", "Описание эпика 2", 0, TaskStatus.NEW);

        //do
        originalManager.createTask(task1);
        originalManager.createTask(task2);
        var epic1Id = originalManager.createEpic(epic1);
        var epic2Id = originalManager.createEpic(epic2);
        var subtask1 = new Subtask("Подзадача первого эпика 1", "Описание подзадачи 1", 0,
                TaskStatus.NEW, epic1Id);
        var subtask2 = new Subtask("Подзадача первого эпика 2", "Описание подзадачи 2", 0,
                TaskStatus.NEW, epic1Id);
        var subtask3 = new Subtask("Подзадача второго эпика", "Описание подзадачи 1", 0,
                TaskStatus.NEW, epic2Id);
        originalManager.createSubtask(subtask1);
        originalManager.createSubtask(subtask2);
        originalManager.createSubtask(subtask3);
        var managerFromFile = FileBackedTaskManager.loadFromFile(file);

        // expect
        for (var originalTask : originalManager.tasks.values()) {
            var taskFromFile = managerFromFile.getTask(originalTask.getId());
            Assertions.assertEquals(originalTask, taskFromFile);
        }

        for (var originalEpic : originalManager.epics.values()) {
            var epicFromFile = managerFromFile.getEpic(originalEpic.getId());
            Assertions.assertEquals(originalEpic, epicFromFile);
        }

        for (var originalSubtask : originalManager.subtasks.values()) {
            var subtaskFromFile = managerFromFile.getSubtask(originalSubtask.getId());
            Assertions.assertEquals(originalSubtask, subtaskFromFile);
        }

        Assertions.assertEquals(originalManager.tasks.size(), managerFromFile.tasks.size());
        Assertions.assertEquals(originalManager.epics.size(), managerFromFile.epics.size());
        Assertions.assertEquals(originalManager.subtasks.size(), managerFromFile.subtasks.size());
        Assertions.assertEquals(originalManager.uniqueTaskId, managerFromFile.uniqueTaskId);
    }
}