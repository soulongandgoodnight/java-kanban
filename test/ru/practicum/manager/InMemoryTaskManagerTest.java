package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.util.ArrayList;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldNotAddEpicAsSubtask() {
        var epicId = 29302;
        var epic = new Epic("Epic for tests name", "Epic for tests description", epicId, TaskStatus.NEW);
        var epicAsSubtask = new Subtask("Subtask name", "Subtask description", epicId, TaskStatus.NEW, epicId);
        taskManager.createEpic(epic);
        var subtaskId = taskManager.createSubtask(epicAsSubtask);

        Assertions.assertEquals(-1, subtaskId, "Подзадача не должна быть создана");
        Assertions.assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадача не должна быть создана");
        Assertions.assertEquals(0, taskManager.getSubtasksByEpic(epicId).size(), "Подзадача не должна быть создана");
    }

    @Test
    public void shouldReturnThreeTasksWhenAddedThreeTasks() {
        var task1 = new Task("name", "description", 1, TaskStatus.NEW);
        var task2 = new Task("name", "description", 2, TaskStatus.NEW);
        var task3 = new Task("name", "description", 3, TaskStatus.NEW);
        ArrayList<Task> expectedTasks = new ArrayList<>();
        expectedTasks.add(task1);
        expectedTasks.add(task2);
        expectedTasks.add(task3);
        for (var expectedTask : expectedTasks) {
            taskManager.createTask(expectedTask);
        }

        var allTasks = taskManager.getAllTasks();

        Assertions.assertEquals(expectedTasks, allTasks, "Тестовые задачи и задачи от менеджера отличаются");
    }

    @Test
    public void shouldReturnThreeEpicsWhenAddedThreeEpics() {
        var epic1 = new Epic("name", "description", 1, TaskStatus.NEW);
        var epic2 = new Epic("name", "description", 2, TaskStatus.NEW);
        var epic3 = new Epic("name", "description", 3, TaskStatus.NEW);
        ArrayList<Epic> expectedEpics = new ArrayList<>();
        expectedEpics.add(epic1);
        expectedEpics.add(epic2);
        expectedEpics.add(epic3);
        for (var expectedEpic : expectedEpics) {
            taskManager.createEpic(expectedEpic);
        }

        var allEpics = taskManager.getAllEpics();

        Assertions.assertEquals(expectedEpics, allEpics, "Тестовые эпики и эпики от менеджера отличаются");
    }

    @Test
    public void shouldReturnThreeSubtasksWhenAddedThreeSubtasks() {
        var epic = new Epic("name", "description", 1, TaskStatus.NEW);
        var subtask1 = new Subtask("name", "description", 2, TaskStatus.NEW, epic.getId());
        var subtask2 = new Subtask("name", "description", 3, TaskStatus.NEW, epic.getId());
        var subtask3 = new Subtask("name", "description", 4, TaskStatus.NEW, epic.getId());
        ArrayList<Subtask> expectedSubtasks = new ArrayList<>();
        expectedSubtasks.add(subtask1);
        expectedSubtasks.add(subtask2);
        expectedSubtasks.add(subtask3);

        taskManager.createEpic(epic);
        for (var subtask : expectedSubtasks) {
            taskManager.createSubtask(subtask);
        }

        var allSubtasks = taskManager.getAllSubtasks();

        Assertions.assertEquals(expectedSubtasks, allSubtasks, "Тестовые подзадачи и подзадачи от менеджера отличаются");
    }

    @Test
    public void shouldDeleteAllTasks() {
        var task1 = new Task("name", "description", 1, TaskStatus.NEW);
        var task2 = new Task("name", "description", 2, TaskStatus.NEW);
        var task3 = new Task("name", "description", 3, TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        Assertions.assertEquals(3, taskManager.getAllTasks().size(), "Количество созданных задач должно быть 3");
        taskManager.deleteAllTasks();
        Assertions.assertEquals(0, taskManager.getAllTasks().size(), "Все задачи должны быть удалены");
    }

    @Test
    public void shouldDeleteAllEpics() {
        var epic1 = new Epic("name", "description", 1, TaskStatus.NEW);
        var epic2 = new Epic("name", "description", 2, TaskStatus.NEW);
        var epic3 = new Epic("name", "description", 3, TaskStatus.NEW);
        var subtask1 = new Subtask("name", "description", 4, TaskStatus.NEW, epic1.getId());
        var subtask2 = new Subtask("name", "description", 5, TaskStatus.NEW, epic2.getId());
        var subtask3 = new Subtask("name", "description", 6, TaskStatus.NEW, epic3.getId());
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        Assertions.assertEquals(3, taskManager.getAllEpics().size(), "Количество созданных эпиков должно быть 3");
        taskManager.deleteAllEpics();
        Assertions.assertEquals(0, taskManager.getAllEpics().size(), "Все эпики должны быть удалены");
        Assertions.assertEquals(0, taskManager.getAllSubtasks().size(), "Все подзадачи должны быть удалены");
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        var epic = new Epic("name", "description", 1, TaskStatus.NEW);
        var subtask1 = new Subtask("name", "description", 2, TaskStatus.NEW, epic.getId());
        var subtask2 = new Subtask("name", "description", 3, TaskStatus.NEW, epic.getId());
        var subtask3 = new Subtask("name", "description", 4, TaskStatus.NEW, epic.getId());
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Assertions.assertEquals(3, taskManager.getAllSubtasks().size(), "Количество созданных подзадач должно быть 3");
        taskManager.deleteAllSubtasks();
        Assertions.assertEquals(0, taskManager.getAllSubtasks().size(), "Все подзадачи должны быть удалены");
        Assertions.assertEquals(1, taskManager.getAllEpics().size(), "Эпик не должен удалиться");
    }

    @Test
    public void shouldReturnTaskById() {
        var task = new Task("name", "description", 1231242, TaskStatus.NEW);
        var taskId = taskManager.createTask(task);
        var taskById = taskManager.getTask(taskId);
        Assertions.assertNotNull(taskById, "Задача должна быть получена по её идентификатору");
    }

    @Test
    public void shouldReturnEpicById() {
        var epic = new Epic("name", "description", 1, TaskStatus.NEW);
        var epicId = taskManager.createEpic(epic);
        var epicById = taskManager.getEpic(epicId);
        Assertions.assertNotNull(epicById, "Эпик должен быть получен по его идентификатору");

    }

    @Test
    public void shouldReturnSubtaskById() {
        var epic = new Epic("name", "description", 123213, TaskStatus.NEW);
        var epicId = taskManager.createEpic(epic);
        var subtask = new Subtask("name", "description", 432123, TaskStatus.NEW, epicId);
        var subtaskId = taskManager.createSubtask(subtask);
        var subtaskById = taskManager.getSubtask(subtaskId);
        Assertions.assertNotNull(subtaskById, "Подзадача должна быть получена по её идентификатору");

    }

    @Test
    public void shouldNotConflictWithIdsWhenCreateTasks() {
        var taskId = 12312312;
        var task = new Task("name", "description", taskId, TaskStatus.NEW);
        taskManager.createTask(task);
        var allTasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, allTasks.size(), "Должна быть создана только 1 задача");
        var createdTask = allTasks.stream().findFirst().get();
        Assertions.assertNotEquals(taskId, createdTask.getId(), "Идентификатор задачи должен контрилироваться менеджером");
    }

    @Test
    public void shouldNotChangeTaskWhenAddingTaskByManager() {
        var expectedTask = new Task("name", "description", 123213, TaskStatus.NEW);
        var newTaskId = taskManager.createTask(expectedTask);
        var actualTask = taskManager.getTask(newTaskId);
        Assertions.assertEquals(expectedTask.getName(), actualTask.getName(), "Имена не должны отличаться");
        Assertions.assertEquals(expectedTask.getDescription(), actualTask.getDescription(), "Описания не должны отличаться");
        Assertions.assertEquals(expectedTask.getStatus(), actualTask.getStatus(), "Статусы не должны отличаться");
    }

    @Test
    public void shouldSavePreviousStateOfTaskWhenTaskIsUpdated() {
        var expectedTask = new Task("Original name", "Original description", 123213, TaskStatus.NEW);
        var expectedTaskId = taskManager.createTask(expectedTask);
        taskManager.getTask(expectedTaskId);
        var updatedTask = new Task("Updated name", "Updated description", expectedTaskId, TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask);
        var history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size(), "В истории должен быть 1 элемент");
        var actualHistoryTask = history.get(0);
        Assertions.assertEquals(expectedTask.getName(), actualHistoryTask.getName(), "Имена должны совпадать");
        Assertions.assertEquals(expectedTask.getDescription(), actualHistoryTask.getDescription(), "Имена должны совпадать");
        Assertions.assertEquals(expectedTask.getStatus(), actualHistoryTask.getStatus(), "Имена должны совпадать");
    }
}