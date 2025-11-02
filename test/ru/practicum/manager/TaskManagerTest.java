package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.exception.IntersectedWIthOtherTasksException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RelatedEpicNotFoundException;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    public void shouldNotAddEpicAsSubtask() throws IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
        var epicId = 29302;
        var epic = new Epic("Epic for tests name", "Epic for tests description", epicId, TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(3));
        var epicAsSubtask = new Subtask("Subtask name", "Subtask description", epicId, TaskStatus.NEW,
                epicId, LocalDateTime.now(), Duration.ofHours(3));
        taskManager.createEpic(epic);
        var subtaskId = taskManager.createSubtask(epicAsSubtask);

        Assertions.assertEquals(Optional.empty(), subtaskId, "Подзадача не должна быть создана");
        Assertions.assertEquals(0, taskManager.getSubtasks().size(), "Подзадача не должна быть создана");
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getSubtasksByEpic(epicId));
    }


    @Test
    public void shouldNotConflictWithIdsWhenCreateTasks() throws IntersectedWIthOtherTasksException {
        var taskId = 12312312;
        var task = new Task("name", "description", taskId, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        taskManager.createTask(task);
        var allTasks = taskManager.getTasks();
        Assertions.assertEquals(1, allTasks.size(), "Должна быть создана только 1 задача");
        var createdTask = allTasks.stream().findFirst().get();
        Assertions.assertNotEquals(taskId, createdTask.getId(), "Идентификатор задачи должен контрилироваться менеджером");
    }

    @Test
    public void shouldNotChangeTaskWhenAddingTaskByManager() throws NotFoundException, IntersectedWIthOtherTasksException {
        var expectedTask = new Task("name", "description", 123213, TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(3));
        var newTaskId = taskManager.createTask(expectedTask);
        var actualTask = taskManager.getTask(newTaskId);
        Assertions.assertEquals(expectedTask.getName(), actualTask.getName(), "Имена не должны отличаться");
        Assertions.assertEquals(expectedTask.getDescription(), actualTask.getDescription(), "Описания не должны отличаться");
        Assertions.assertEquals(expectedTask.getStatus(), actualTask.getStatus(), "Статусы не должны отличаться");
    }

    @Test
    public void shouldSavePreviousStateOfTaskWhenTaskIsUpdated() throws NotFoundException, IntersectedWIthOtherTasksException {
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
    public void shouldReturnThreeTasksWhenAddedThreeTasks() throws IntersectedWIthOtherTasksException {
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

        var allTasks = taskManager.getTasks();

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

        var allEpics = taskManager.getEpics();

        Assertions.assertEquals(expectedEpics, allEpics, "Тестовые эпики и эпики от менеджера отличаются");
    }

    @Test
    public void shouldReturnThreeSubtasksWhenAddedThreeSubtasks() throws IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
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

        var allSubtasks = taskManager.getSubtasks();

        Assertions.assertEquals(expectedSubtasks, allSubtasks, "Тестовые подзадачи и подзадачи от менеджера отличаются");
    }

    @Test
    public void shouldDeleteAllTasks() throws IntersectedWIthOtherTasksException {
        var task1 = new Task("name", "description", 1, TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(3));
        var task2 = new Task("name", "description", 2, TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(3));
        var task3 = new Task("name", "description", 3, TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 3, 0, 0), Duration.ofHours(3));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        Assertions.assertEquals(3, taskManager.getTasks().size(), "Количество созданных задач должно быть 3");
        taskManager.deleteTasks();
        Assertions.assertEquals(0, taskManager.getTasks().size(), "Все задачи должны быть удалены");
    }

    @Test
    public void shouldDeleteAllEpics() throws IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
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
        Assertions.assertEquals(3, taskManager.getEpics().size(), "Количество созданных эпиков должно быть 3");
        taskManager.deleteEpics();
        Assertions.assertEquals(0, taskManager.getEpics().size(), "Все эпики должны быть удалены");
        Assertions.assertEquals(0, taskManager.getSubtasks().size(), "Все подзадачи должны быть удалены");
    }

    @Test
    public void shouldDeleteAllSubtasks() throws IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
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

        Assertions.assertEquals(3, taskManager.getSubtasks().size(), "Количество созданных подзадач должно быть 3");
        taskManager.deleteSubtasks();
        Assertions.assertEquals(0, taskManager.getSubtasks().size(), "Все подзадачи должны быть удалены");
        Assertions.assertEquals(1, taskManager.getEpics().size(), "Эпик не должен удалиться");
    }

    @Test
    public void shouldReturnTaskById() throws NotFoundException, IntersectedWIthOtherTasksException {
        var task = new Task("name", "description", 1231242, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var taskId = taskManager.createTask(task);
        var taskById = taskManager.getTask(taskId);
        Assertions.assertNotNull(taskById, "Задача должна быть получена по её идентификатору");
    }

    @Test
    public void shouldReturnEpicById() throws NotFoundException {
        var epic = new Epic("name", "description", 1, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));
        var epicId = taskManager.createEpic(epic);
        var epicById = taskManager.getEpic(epicId);
        Assertions.assertNotNull(epicById, "Эпик должен быть получен по его идентификатору");

    }

    @Test
    public void shouldReturnSubtaskById() throws NotFoundException, IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
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
    public void shouldCalculateCorrectStatusForEpic_whenSubtasksAreNew() throws NotFoundException, IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
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
    public void shouldCalculateCorrectStatusForEpic_whenSubtasksAreDone() throws NotFoundException,
            IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
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
    public void shouldCalculateCorrectStatusForEpic_whenSubtasksAreNewAndDone() throws NotFoundException,
            IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
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
    public void shouldCalculateCorrectStatusForEpic_whenSubtasksAreInProgress() throws NotFoundException,
            IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
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
    public void epicShouldHaveLinkedSubtasks() throws NotFoundException, IntersectedWIthOtherTasksException,
            RelatedEpicNotFoundException {
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
        var allSubtasks = taskManager.getSubtasks();
        assertSubtaskSetsAreEqual(actualEpic.getSubtasks().stream(), allSubtasks.stream());
        allSubtasks.forEach(subtask -> {
            try {
                assertEpicsAreEqual(actualEpic,
                        taskManager.getEpic(subtask.getEpicId()));
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        });
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

        subtasks.forEach(subtask -> {
            try {
                taskManager.createSubtask(subtask);
            } catch (IntersectedWIthOtherTasksException | RelatedEpicNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        tasks.forEach(task -> {
            try {
                taskManager.createTask(task);
            } catch (IntersectedWIthOtherTasksException e) {
                throw new RuntimeException(e);
            }
        });

        var previousStartTime = LocalDateTime.MIN;
        var prioritizedTasks = taskManager.getPrioritizedTasks();
        for (Task task : prioritizedTasks) {
            var currentStartTime = task.getStartTime();
            Assertions.assertTrue(currentStartTime.isAfter(previousStartTime));
            previousStartTime = currentStartTime;
        }
    }

    @Test
    public void should_notAddTask_whenTimeIntersectsWithExistingTask() throws IntersectedWIthOtherTasksException {
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

        var allTasks = taskManager.getTasks();
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
