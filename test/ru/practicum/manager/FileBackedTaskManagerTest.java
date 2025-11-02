package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.exception.IntersectedWIthOtherTasksException;
import ru.practicum.exception.ManagerLoadException;
import ru.practicum.exception.ManagerSaveException;
import ru.practicum.exception.RelatedEpicNotFoundException;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    public void beforeEach() throws IOException {
        var file = File.createTempFile("test", "FileBackedTaskManager");
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    void when_managerConstructsWithEmptyFile_should_beConstructed() throws IOException {
        // given
        var file = File.createTempFile("test", "FileBackedTaskManager");

        // do
        var manager = new FileBackedTaskManager(file);

        // expect
        var br = new BufferedReader(new FileReader(file));
        var actualFileEnd = br.readLine();
        Assertions.assertNull(actualFileEnd);

    }

    @Test
    void when_addsDifferentTasksToManagerAndOtherManagerLoadsFromTheSameFile_should_beEqual() throws IOException,
            IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
        // given
        var file = File.createTempFile("test", "FileBackedTaskManager");

        // do
        var originalManager = getFileBackedTaskManager(file);
        var managerFromFile = FileBackedTaskManager.loadFromFile(file);

        // expect
        for (var originalTask : originalManager.tasks.values()) {
            var taskFromFile = managerFromFile.getTask(originalTask.getId());
            assertTasksAreEqual(originalTask, taskFromFile);
        }

        for (var originalEpic : originalManager.epics.values()) {
            var epicFromFile = managerFromFile.getEpic(originalEpic.getId());
            assertEpicsAreEqual(originalEpic, epicFromFile);
        }

        for (var originalSubtask : originalManager.subtasks.values()) {
            var subtaskFromFile = managerFromFile.getSubtask(originalSubtask.getId());
            assertSubtasksAreEqual(originalSubtask, subtaskFromFile);
        }

        var originalPriorityTasks = originalManager.getPrioritizedTasks();
        var priorityTasksFromFile = managerFromFile.getPrioritizedTasks();
        Assertions.assertFalse(originalPriorityTasks.isEmpty());
        Assertions.assertFalse(priorityTasksFromFile.isEmpty());
        Assertions.assertEquals(originalPriorityTasks.size(), priorityTasksFromFile.size());
        for (int i = 0; i < originalPriorityTasks.size(); i++) {
            var originalPriorityTask = originalPriorityTasks.get(i);
            var priorityTaskFromFile = priorityTasksFromFile.get(i);
            assertTasksAreEqual(originalPriorityTask, priorityTaskFromFile);
        }

        Assertions.assertEquals(originalManager.tasks.size(), managerFromFile.tasks.size());
        Assertions.assertEquals(originalManager.epics.size(), managerFromFile.epics.size());
        Assertions.assertEquals(originalManager.subtasks.size(), managerFromFile.subtasks.size());
        Assertions.assertEquals(originalManager.uniqueTaskId, managerFromFile.uniqueTaskId);
    }

    private FileBackedTaskManager getFileBackedTaskManager(File file) throws IntersectedWIthOtherTasksException,
            RelatedEpicNotFoundException {
        var originalManager = new FileBackedTaskManager(file);
        var task1 = new Task("Задача 1", "Описание задачи 1", 0, TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(3));
        var task2 = new Task("Задача 2", "Описание задачи 2", 0, TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(3));
        var epic1 = new Epic("Эпик 1", "Описание эпика 1", 0, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epic2 = new Epic("Эпик 2", "Описание эпика 2", 0, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));

        //do
        originalManager.createTask(task1);
        originalManager.createTask(task2);
        var epic1Id = originalManager.createEpic(epic1);
        var epic2Id = originalManager.createEpic(epic2);
        var subtask1 = new Subtask("Подзадача первого эпика 1", "Описание подзадачи 1", 0,
                TaskStatus.NEW, epic1Id, LocalDateTime.of(2025, 11, 1, 0, 0),
                Duration.ofHours(3));
        var subtask2 = new Subtask("Подзадача первого эпика 2", "Описание подзадачи 2", 0,
                TaskStatus.NEW, epic1Id, LocalDateTime.of(2024, 10, 1, 0, 0),
                Duration.ofHours(3));
        var subtask3 = new Subtask("Подзадача второго эпика", "Описание подзадачи 1", 0,
                TaskStatus.NEW, epic2Id, LocalDateTime.of(2023, 12, 24, 0, 0),
                Duration.ofHours(3));
        originalManager.createSubtask(subtask1);
        originalManager.createSubtask(subtask2);
        originalManager.createSubtask(subtask3);
        return originalManager;
    }

    @Test
    public void shouldThrowManagerSaveException() {
        var file = new File("abracadabra/dull");
        var manager = new FileBackedTaskManager(file);
        var taskToSave = new Task("name", "description", 0, TaskStatus.NEW, LocalDateTime.MIN, Duration.ZERO);
        Assertions.assertThrows(ManagerSaveException.class, () -> manager.createTask(taskToSave));
    }

    @Test
    public void shouldThrowManagerLoadException() {
        var file = new File("abracadabra/dull");
        Assertions.assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }
}