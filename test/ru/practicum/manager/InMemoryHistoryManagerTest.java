package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

public class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldAddAllTypesOfTasks() {
        var task = new Task("task #1", "description #1", 1, TaskStatus.NEW,
                LocalDateTime.of(2025, 6, 10, 0, 0), Duration.ofHours(3));

        var epic = new Epic("name", "description", 2, TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(3));

        var subtask = new Subtask("name", "description", 432123, TaskStatus.NEW, 2,
                LocalDateTime.now(), Duration.ofHours(3));

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        var history = historyManager.getHistory();
        Assertions.assertEquals(3, history.size());
        var actualTask = history.get(0);
        var actualEpic = history.get(1);
        var actualSubtask = (Subtask) history.get(2);
        assertEquals(task, actualTask);
        assertEquals(epic, actualEpic);
        assertEquals(subtask, actualSubtask);

    }

    @Test
    public void shouldNotExceedHistoryMaxCountAndDeleteLatestTasksWhenOverflows() {
        var tasks = IntStream.rangeClosed(1, 20).mapToObj(i -> new Task("task #" + i, "description #" + i,
                i, TaskStatus.NEW, LocalDateTime.of(2025, 1, i, 0, 0), Duration.ofHours(3)));

        // do
        tasks.forEachOrdered(task -> historyManager.add(task));
        var actualHistory = historyManager.getHistory();

        // expect
        var expectedIdentifier = 11;
        for (var historyItem : actualHistory) {
            Assertions.assertEquals(expectedIdentifier, historyItem.getId());
            expectedIdentifier++;
        }
    }

    private void assertEquals(Task left, Task right) {
        Assertions.assertEquals(left.getId(), right.getId());
        Assertions.assertEquals(left.getName(), right.getName());
        Assertions.assertEquals(left.getDescription(), right.getDescription());
        Assertions.assertEquals(left.getStatus(), right.getStatus());
        Assertions.assertEquals(left.getStartTime(), right.getStartTime());
        Assertions.assertEquals(left.getDuration(), right.getDuration());
        Assertions.assertEquals(left.getEndTime(), right.getEndTime());
    }

    private void assertEquals(Subtask left, Subtask right) {
        assertEquals(left, (Task) right);
        Assertions.assertEquals(left.getEpicId(), right.getEpicId());
    }


}
