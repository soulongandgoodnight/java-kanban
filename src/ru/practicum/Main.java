package ru.practicum;

import ru.practicum.manager.InMemoryTaskManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.util.HashSet;

public class Main {

    public static void main(String[] args) {
        var taskManager = new InMemoryTaskManager();
        testEpicsFlow(taskManager);
        testTasksFlow(taskManager);
    }

    private static void testTasksFlow(TaskManager taskManager){
        var task1 = new Task("Задача 1", "Описание задачи 1", 0, TaskStatus.NEW);
        var task2 = new Task("Задача 2", "Описание задачи 2", 0, TaskStatus.NEW);
        var task1Id  = taskManager.createTask(task1);
        taskManager.createTask(task2);
        var allTasks = taskManager.getAllTasks();

        System.out.println("Тестирование задач. Создание задач. Ожидаемое количество задач: 2; " +
                "Реальное количество задач: " + allTasks.size());

        var taskForUpdate = taskManager.getTask(task1Id);
        var updatedTask1 = new Task(taskForUpdate.getName(), taskForUpdate.getDescription(), taskForUpdate.getId(),
                TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask1);

        var actualUpdatedTask = taskManager.getAllTasks().stream()
                .filter(task -> task.getId() == updatedTask1.getId()).findFirst().get();
        System.out.println("Тестирование задач. Обновление задачи. Ожидаемый статус задачи: " + TaskStatus.IN_PROGRESS
                + "; Реальный статус задачи: " + actualUpdatedTask.getStatus());

        var actualTaskById = taskManager.getTask(taskForUpdate.getId());
        System.out.println("Тестирование задач. Получение задачи по id. id задачи: " + taskForUpdate.getId()
                + "; Полученная задача: " + actualTaskById);

        taskManager.deleteTask(actualTaskById.getId());
        System.out.println("Тестирование задач. Удаление задачи по id. id задачи: " + actualTaskById.getId()
                + "; Полученная задача: " + taskManager.getTask(actualTaskById.getId()));

        taskManager.deleteAllTasks();
        var remainingTasks = taskManager.getAllTasks();
        System.out.println("Тестирование задач. Удаление всех задач. Все имеющиеся задачи после удаления: "
                + remainingTasks);
    }

    private static void testEpicsFlow(TaskManager taskManager) {
        HashSet<Subtask> subtasks1 = new HashSet<>();
        subtasks1.add(new Subtask("Подзадача первого эпика 1", "Описание подзадачи 1", 0,
                TaskStatus.NEW, 1));
        subtasks1.add(new Subtask("Подзадача певолшл эпика 2", "Описание подзадачи 2", 1,
                TaskStatus.NEW, 1));
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 0, TaskStatus.NEW);
        var epic1Id = taskManager.createEpic(epic1);

        HashSet<Subtask> subtasks2 = new HashSet<>();
        subtasks2.add(new Subtask("Подзадача второго эпика", "Описание подзадачи 1", 0,
                TaskStatus.NEW, 2));
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", 0, TaskStatus.NEW);
        taskManager.createEpic(epic2);

        var allEpics = taskManager.getAllEpics();
        System.out.println("Тестирование эпиков. Создание эпиков. Ожидаемое количество эпиков: 2; " +
                "Реальное количество эпиков: " + allEpics.size());

        for (var subtask: subtasks1) {
            taskManager.createSubtask(subtask);
        }

        for(var subtask: subtasks2) {
            taskManager.createSubtask(subtask);
        }

        var actualSubtasksCount = 0;
        for (var epic: allEpics) {
            actualSubtasksCount += epic.getSubtasks().size();
        }
        System.out.println("Тестирование эпиков. Создание эпиков. Ожидаемое количество подзадач: 3; " +
                "Реальное количество подзадач: " + actualSubtasksCount);

        var actualEpicById = taskManager.getEpic(epic1Id);
        System.out.println("Тестирование эпиков. Получение эпика по id. id эпика: " + epic1Id
                + "; Полученный эпик: " + actualEpicById);

        var epicForStatusUpdate = new Epic(actualEpicById.getName(), actualEpicById.getDescription(), actualEpicById.getId(),
                TaskStatus.DONE);
        taskManager.updateEpic(epicForStatusUpdate);
        var actualEpicForStatusUpdate = taskManager.getEpic(epicForStatusUpdate.getId());
        System.out.println("Тестирование эпиков. Обновление статуса эпика. Ожидаемый статус: " + TaskStatus.NEW
                + "; Реальный статус: " + actualEpicForStatusUpdate.getStatus());

        var epicSubtask = actualEpicById.getSubtasks().stream().findFirst().get();
        epicSubtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(epicSubtask);

        var actualEpicWithUpdatedSubtask = taskManager.getEpic(epicForStatusUpdate.getId());
        System.out.println("Тестирование эпиков. Обновление статуса подзадачи. Ожидаемый статус эпика: "
                + TaskStatus.IN_PROGRESS + "; Реальный статус :" + actualEpicWithUpdatedSubtask.getStatus());

        taskManager.deleteSubtask(epicSubtask.getId());

        System.out.println("Тестирование эпиков. Удаление подзадачи. Ожидаемое количество подзадач: 2; "
                + "Реальное количество подзадач:" + taskManager.getAllSubtasks().size());

        System.out.println("Тестирование эпиков. Удаление подзадачи. Ожидаемый статус эпика: "
                + TaskStatus.NEW + "; Реальный статус :" + taskManager.getEpic(epic1Id).getStatus());

        taskManager.deleteEpic(epicForStatusUpdate.getId());
        var epicsCount = taskManager.getAllEpics().size();
        var subtasksCount = taskManager.getAllSubtasks().size();

        System.out.println("Тестирование эпиков. Удаление эпиков. Ожидаемое количество эпиков: 1; " +
                "Реальное количество эпиков: " + epicsCount);
        System.out.println("Тестирование эпиков. Удаление эпиков. Ожидаемое количество подзадач: 1; " +
                "Реальное количество подзадач: " + subtasksCount);

    }
}
