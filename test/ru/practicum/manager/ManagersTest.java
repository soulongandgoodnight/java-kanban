package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void getDefault() {
        var taskManager = Managers.getDefault();
        Assertions.assertNotNull(taskManager, "Менеджер задач должен быть проинициализирован");
    }

    @Test
    void getDefaultHistory() {
        var historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager, "Менеджер истории должен быть проинициализирован");
    }
}