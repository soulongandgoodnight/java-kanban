package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    public void shouldNotAddEpicAsSubtask() {
        var epicId = 29302;
        var epic = new Epic("Epic for tests name", "Epic for tests description", epicId, TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(3));
        var epicAsSubtask = new Subtask("Subtask name", "Subtask description", epicId, TaskStatus.NEW,
                epicId, LocalDateTime.now(), Duration.ofHours(3));
        taskManager.createEpic(epic);
        var subtaskId = taskManager.createSubtask(epicAsSubtask);

        Assertions.assertEquals(-1, subtaskId, "Подзадача не должна быть создана");
        Assertions.assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадача не должна быть создана");
        Assertions.assertEquals(0, taskManager.getSubtasksByEpic(epicId).size(), "Подзадача не должна быть создана");
    }


    @Test
    public void shouldNotConflictWithIdsWhenCreateTasks() {
        var taskId = 12312312;
        var task = new Task("name", "description", taskId, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        taskManager.createTask(task);
        var allTasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, allTasks.size(), "Должна быть создана только 1 задача");
        var createdTask = allTasks.stream().findFirst().get();
        Assertions.assertNotEquals(taskId, createdTask.getId(), "Идентификатор задачи должен контрилироваться менеджером");
    }

    @Test
    public void shouldNotChangeTaskWhenAddingTaskByManager() {
        var expectedTask = new Task("name", "description", 123213, TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(3));
        var newTaskId = taskManager.createTask(expectedTask);
        var actualTask = taskManager.getTask(newTaskId);
        Assertions.assertEquals(expectedTask.getName(), actualTask.getName(), "Имена не должны отличаться");
        Assertions.assertEquals(expectedTask.getDescription(), actualTask.getDescription(), "Описания не должны отличаться");
        Assertions.assertEquals(expectedTask.getStatus(), actualTask.getStatus(), "Статусы не должны отличаться");
    }

    @Test
    public void shouldSavePreviousStateOfTaskWhenTaskIsUpdated() {
        var expectedTask = new Task("Original name", "Original description", 123213, TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(3));
        var expectedTaskId = taskManager.createTask(expectedTask);
        taskManager.getTask(expectedTaskId);
        var updatedTask = new Task("Updated name", "Updated description", expectedTaskId,
                TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(3));
        taskManager.updateTask(updatedTask);
        var history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size(), "В истории должен быть 1 элемент");
        var actualHistoryTask = history.getFirst();
        Assertions.assertEquals(expectedTask.getName(), actualHistoryTask.getName(), "Имена должны совпадать");
        Assertions.assertEquals(expectedTask.getDescription(), actualHistoryTask.getDescription(), "Имена должны совпадать");
        Assertions.assertEquals(expectedTask.getStatus(), actualHistoryTask.getStatus(), "Имена должны совпадать");
    }

    @Test
    public void shouldReturnThreeTasksWhenAddedThreeTasks() {
        var task1 = new Task("name", "description", 1, TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(3));
        var task2 = new Task("name", "description", 2, TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(3));
        var task3 = new Task("name", "description", 3, TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 3, 0, 0), Duration.ofHours(3));
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
        var epic1 = new Epic("name", "description", 1, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epic2 = new Epic("name", "description", 2, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epic3 = new Epic("name", "description", 3, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
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
        var epic = new Epic("name", "description", 1, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var subtask1 = new Subtask("name", "description", 2, TaskStatus.NEW, epic.getId(),
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(3));
        var subtask2 = new Subtask("name", "description", 3, TaskStatus.NEW, epic.getId(),
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(3));
        var subtask3 = new Subtask("name", "description", 4, TaskStatus.NEW, epic.getId(),
                LocalDateTime.of(2025, 1, 3, 0, 0), Duration.ofHours(3));
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
        var task1 = new Task("name", "description", 1, TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(3));
        var task2 = new Task("name", "description", 2, TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(3));
        var task3 = new Task("name", "description", 3, TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 3, 0, 0), Duration.ofHours(3));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        Assertions.assertEquals(3, taskManager.getAllTasks().size(), "Количество созданных задач должно быть 3");
        taskManager.deleteAllTasks();
        Assertions.assertEquals(0, taskManager.getAllTasks().size(), "Все задачи должны быть удалены");
    }

    @Test
    public void shouldDeleteAllEpics() {
        var epic1 = new Epic("name", "description", 1, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epic2 = new Epic("name", "description", 2, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epic3 = new Epic("name", "description", 3, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var subtask1 = new Subtask("name", "description", 4, TaskStatus.NEW, epic1.getId(),
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(3));
        var subtask2 = new Subtask("name", "description", 5, TaskStatus.NEW, epic2.getId(),
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(3));
        var subtask3 = new Subtask("name", "description", 6, TaskStatus.NEW, epic3.getId(),
                LocalDateTime.of(2025, 1, 3, 0, 0), Duration.ofHours(3));
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
        var epic = new Epic("name", "description", 1, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var subtask1 = new Subtask("name", "description", 2, TaskStatus.NEW, epic.getId(),
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(3));
        var subtask2 = new Subtask("name", "description", 3, TaskStatus.NEW, epic.getId(),
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(3));
        var subtask3 = new Subtask("name", "description", 4, TaskStatus.NEW, epic.getId(),
                LocalDateTime.of(2025, 1, 3, 0, 0), Duration.ofHours(3));
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
        var task = new Task("name", "description", 1231242, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var taskId = taskManager.createTask(task);
        var taskById = taskManager.getTask(taskId);
        Assertions.assertNotNull(taskById, "Задача должна быть получена по её идентификатору");
    }

    @Test
    public void shouldReturnEpicById() {
        var epic = new Epic("name", "description", 1, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epicId = taskManager.createEpic(epic);
        var epicById = taskManager.getEpic(epicId);
        Assertions.assertNotNull(epicById, "Эпик должен быть получен по его идентификатору");

    }

    @Test
    public void shouldReturnSubtaskById() {
        var epic = new Epic("name", "description", 123213, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epicId = taskManager.createEpic(epic);
        var subtask = new Subtask("name", "description", 432123, TaskStatus.NEW, epicId,
                LocalDateTime.now(), Duration.ofHours(3));
        var subtaskId = taskManager.createSubtask(subtask);
        var subtaskById = taskManager.getSubtask(subtaskId);
        Assertions.assertNotNull(subtaskById, "Подзадача должна быть получена по её идентификатору");
    }

    @Test
    public void shouldCalculateCorrectStatusForEpic_whenSubtasksAreNew() {
        var epic = new Epic("name", "description", 0, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epicId = taskManager.createEpic(epic);

        var subtask1 = new Subtask("name", "description", 0, TaskStatus.NEW, epicId,
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(3));
        var subtask2 = new Subtask("name", "description", 0, TaskStatus.NEW, epicId,
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(3));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        var actualEpic = taskManager.getEpic(epicId);

        // expect
        Assertions.assertEquals(TaskStatus.NEW, actualEpic.getStatus());
    }

    @Test
    public void shouldCalculateCorrectStatusForEpic_whenSubtasksAreDone() {
        var epic = new Epic("name", "description", 0, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epicId = taskManager.createEpic(epic);

        var subtask1 = new Subtask("name", "description", 0, TaskStatus.DONE, epicId,
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(3));
        var subtask2 = new Subtask("name", "description", 0, TaskStatus.DONE, epicId,
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(3));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        var actualEpic = taskManager.getEpic(epicId);

        // expect
        Assertions.assertEquals(TaskStatus.DONE, actualEpic.getStatus());
    }

    @Test
    public void shouldCalculateCorrectStatusForEpic_whenSubtasksAreNewAndDone() {
        var epic = new Epic("name", "description", 0, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epicId = taskManager.createEpic(epic);

        var subtask1 = new Subtask("name", "description", 0, TaskStatus.NEW, epicId,
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(3));
        var subtask2 = new Subtask("name", "description", 0, TaskStatus.DONE, epicId,
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(3));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        var actualEpic = taskManager.getEpic(epicId);

        // expect
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, actualEpic.getStatus());
    }

    @Test
    public void shouldCalculateCorrectStatusForEpic_whenSubtasksAreInProgress() {
        var epic = new Epic("name", "description", 0, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epicId = taskManager.createEpic(epic);

        var subtask1 = new Subtask("name", "description", 0, TaskStatus.IN_PROGRESS, epicId,
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(3));
        var subtask2 = new Subtask("name", "description", 0, TaskStatus.IN_PROGRESS, epicId,
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(3));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        var actualEpic = taskManager.getEpic(epicId);

        // expect
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, actualEpic.getStatus());
    }

    @Test
    public void epicShouldHaveLinkedSubtasks() {
        var epic = new Epic("name", "description", 0, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epicId = taskManager.createEpic(epic);

        var subtask1 = new Subtask("name", "description", 0, TaskStatus.IN_PROGRESS, epicId,
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(3));
        var subtask2 = new Subtask("name", "description", 0, TaskStatus.IN_PROGRESS, epicId,
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(3));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        var actualEpic = taskManager.getEpic(epicId);
        var allSubtasks = taskManager.getAllSubtasks();
        assertSubtaskSetsAreEqual(actualEpic.getSubtasks().stream(), allSubtasks.stream());
        allSubtasks.forEach(subtask -> assertEpicsAreEqual(actualEpic,
                taskManager.getEpic(subtask.getEpicId())));
    }

    @Test
    public void should_returnTasksInCorrectOrder() {
        var epic = new Epic("name", "description", 0, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epicId = taskManager.createEpic(epic);
        var tasks = IntStream.range(3, 10).mapToObj(i -> new Task("task #" + i, "description #" + i,
                0, TaskStatus.NEW, LocalDateTime.of(2025, 1, i, 0, 0), Duration.ofHours(3)));

        var subtasks = IntStream.range(11, 20).mapToObj(i -> new Subtask("subtask #" + i,
                "description #" + i, 0, TaskStatus.NEW, epicId,
                LocalDateTime.of(2025, 1, i, 0, 0), Duration.ofHours(3)));

        subtasks.forEach(subtask -> taskManager.createSubtask(subtask));
        tasks.forEach(task -> taskManager.createTask(task));

        var previousStartTime = LocalDateTime.MIN;
        var prioritizedTasks = taskManager.getPrioritizedTasks();
        for (Task task : prioritizedTasks) {
            var currentStartTime = task.getStartTime();
            Assertions.assertTrue(currentStartTime.isAfter(previousStartTime));
            previousStartTime = currentStartTime;
        }
    }

    @Test
    public void should_notAddTask_whenTimeIntersectsWithExistingTask() {
        var existingTask = new Task("task #1", "description #1", 0, TaskStatus.NEW,
                LocalDateTime.of(2025, 6, 10, 0, 0), Duration.ofHours(3));

        var taskOverlapsFromLeft = new Task("task #2", "description #2", 0, TaskStatus.NEW,
                LocalDateTime.of(2025, 6, 9, 23, 0), Duration.ofHours(3));
        var taskOverlapsFromRight = new Task("task #3", "description #3", 0, TaskStatus.NEW,
                LocalDateTime.of(2025, 6, 10, 2, 0), Duration.ofHours(3));
        var taskOverlapsFromBothSides = new Task("task #4", "description #4", 0, TaskStatus.NEW,
                LocalDateTime.of(2025, 6, 9, 23, 0), Duration.ofHours(6));
        var taskIncludedInExistingTask = new Task("task #5", "description #5", 0, TaskStatus.NEW,
                LocalDateTime.of(2025, 6, 10, 1, 0), Duration.ofHours(1));

        var existingTaskId = taskManager.createTask(existingTask);
        taskManager.createTask(taskOverlapsFromLeft);
        taskManager.createTask(taskOverlapsFromRight);
        taskManager.createTask(taskOverlapsFromBothSides);
        taskManager.createTask(taskIncludedInExistingTask);

        var allTasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, allTasks.size());
        Assertions.assertEquals(existingTaskId, allTasks.stream().findFirst().get().getId());
    }

    protected void assertTasksAreEqual(Task left, Task right) {
        Assertions.assertEquals(left.getId(), right.getId());
        Assertions.assertEquals(left.getName(), right.getName());
        Assertions.assertEquals(left.getDescription(), right.getDescription());
        Assertions.assertEquals(left.getStatus(), right.getStatus());
        Assertions.assertEquals(left.getStartTime(), right.getStartTime());
        Assertions.assertEquals(left.getDuration(), right.getDuration());
        Assertions.assertEquals(left.getEndTime(), right.getEndTime());
    }

    protected void assertEpicsAreEqual(Epic left, Epic right) {
        assertTasksAreEqual(left, right);
        assertSubtaskSetsAreEqual(left.getSubtasks().stream(), right.getSubtasks().stream());
    }

    protected void assertSubtaskSetsAreEqual(Stream<Subtask> left, Stream<Subtask> right) {
        var leftSubtasks = left.collect(Collectors.toMap(Task::getId, v -> v));
        var rightSubtasks = right.collect(Collectors.toMap(Task::getId, v -> v));
        for (var leftSubtask : leftSubtasks.entrySet()) {
            var rightSubtask = rightSubtasks.get(leftSubtask.getKey());
            Assertions.assertNotNull(rightSubtask);
            assertSubtasksAreEqual(leftSubtask.getValue(), rightSubtask);
        }
    }

    protected void assertSubtasksAreEqual(Subtask left, Subtask right) {
        assertTasksAreEqual(left, right);
        Assertions.assertEquals(left.getEpicId(), right.getEpicId());
    }
}
