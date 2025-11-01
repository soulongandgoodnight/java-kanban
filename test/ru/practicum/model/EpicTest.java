package ru.practicum.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EpicTest {
    private static Epic epicForTests;

    @BeforeEach
    public void beforeEach() {
        epicForTests = new Epic("Epic for tests name", "Epic for tests description", 29302, TaskStatus.NEW);
    }

    @Test
    public void shouldReturnSameFieldsWhenCreateEpic() {
        var expectedName = "expected epic name";
        var expectedDescription = "expected epic description";
        var expectedTaskStatus = TaskStatus.NEW;
        var expectedId = 12;
        var epic = new Epic(expectedName, expectedDescription, expectedId, expectedTaskStatus);

        Assertions.assertEquals(expectedName, epic.getName(), "Имя эпика отличается от ожидаемого");
        Assertions.assertEquals(expectedDescription, epic.getDescription(), "Описание эпика отличается от ожидаемого");
        Assertions.assertEquals(expectedId, epic.getId(), "Идентификатор эпика отличается от ожидаемого");
        Assertions.assertEquals(expectedTaskStatus, epic.getStatus(), "Статус эпика отличается от ожидаемого");
    }

    @Test
    public void shouldAddSubtaskWhenAddSubtask() {
        var subtask = new Subtask("Subtask name", "Subtask description", 192, TaskStatus.NEW, epicForTests.getId());
        epicForTests.addSubtask(subtask);

        var epicSubtasks = epicForTests.getSubtasks();
        Assertions.assertEquals(1, epicSubtasks.size(), "Количество подзадач не совпадает с ожидаемым");
        Assertions.assertEquals(subtask, epicSubtasks.stream().findFirst().get(), "Созданная и добавленная подзадачи различаются");
    }

    @Test
    public void shouldRemoveSubtaskWhenRemoveSubtask() {
        var subtask1 = new Subtask("Subtask name 1", "Subtask description 1", 192, TaskStatus.NEW, epicForTests.getId());
        var subtask2 = new Subtask("Subtask name 2", "Subtask description 2", 348, TaskStatus.NEW, epicForTests.getId());
        epicForTests.addSubtask(subtask1);
        epicForTests.addSubtask(subtask2);
        epicForTests.removeSubtask(subtask1);

        var remainingEpicTasks = epicForTests.getSubtasks();

        Assertions.assertEquals(1, remainingEpicTasks.size(), "Количество подзадач не совпадает с ожидаемым");
        Assertions.assertEquals(subtask2, remainingEpicTasks.stream().findFirst().get(), "Оставшаяся подзадача не совпадает с ожидаемой");
    }
}