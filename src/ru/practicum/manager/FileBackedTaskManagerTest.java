package ru.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.io.*;


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
    void when_addsDifferentTasksToManager_should_beSavedIntoTheFile() throws IOException {
        // given
        var file = File.createTempFile("test", "FileBackedTaskManager");
        var manager = new FileBackedTaskManager(file);
        var task1 = new Task("Задача 1", "Описание задачи 1", 0, TaskStatus.NEW);
        var task2 = new Task("Задача 2", "Описание задачи 2", 0, TaskStatus.NEW);
        var epic1 = new Epic("Эпик 1", "Описание эпика 1", 0, TaskStatus.NEW);
        var epic2 = new Epic("Эпик 2", "Описание эпика 2", 0, TaskStatus.NEW);

        //do
        manager.createTask(task1);
        manager.createTask(task2);
        var epic1Id = manager.createEpic(epic1);
        var epic2Id = manager.createEpic(epic2);
        var subtask1 = new Subtask("Подзадача первого эпика 1", "Описание подзадачи 1", 0,
                TaskStatus.NEW, epic1Id);
        var subtask2 = new Subtask("Подзадача первого эпика 2", "Описание подзадачи 2", 0,
                TaskStatus.NEW, epic1Id);
        var subtask3 = new Subtask("Подзадача второго эпика", "Описание подзадачи 1", 0,
                TaskStatus.NEW, epic2Id);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        //expect
        var br = new BufferedReader(new FileReader(file));
        var actualHeader = br.readLine();
        var savedTask1 = br.readLine();
        var savedTask2 = br.readLine();
        var savedEpic1 = br.readLine();
        var savedEpic2 = br.readLine();
        var savedSubtask1 = br.readLine();
        var savedSubtask2 = br.readLine();
        var savedSubtask3 = br.readLine();
        var actualFileEnd = br.readLine();
        Assertions.assertEquals(FileBackedTaskManager.FILE_HEADER, actualHeader);
        Assertions.assertEquals("1,TASK,Задача 1,NEW,Описание задачи 1,", savedTask1);
        Assertions.assertEquals("2,TASK,Задача 2,NEW,Описание задачи 2,", savedTask2);
        Assertions.assertEquals("3,EPIC,Эпик 1,NEW,Описание эпика 1,", savedEpic1);
        Assertions.assertEquals("4,EPIC,Эпик 2,NEW,Описание эпика 2,", savedEpic2);
        Assertions.assertEquals("5,SUBTASK,Подзадача первого эпика 1,NEW,Описание подзадачи 1,3", savedSubtask1);
        Assertions.assertEquals("6,SUBTASK,Подзадача первого эпика 2,NEW,Описание подзадачи 2,3", savedSubtask2);
        Assertions.assertEquals("7,SUBTASK,Подзадача второго эпика,NEW,Описание подзадачи 1,4", savedSubtask3);
        Assertions.assertNull(actualFileEnd);
    }

    @Test
    void when_loadFromFileDifferentTasks_should_properlyLoadAll() throws IOException {
        //given
        var file = File.createTempFile("test", "FileBackedTaskManager");
        var bw = new BufferedWriter(new FileWriter(file));
        bw.write(savedTasks);
        bw.close();

        // do
        var manager = FileBackedTaskManager.loadFromFile(file);

        //expect
        Assertions.assertEquals(2, manager.getAllTasks().size());
        Assertions.assertEquals(2, manager.getAllEpics().size());
        Assertions.assertEquals(3, manager.getAllSubtasks().size());
        Assertions.assertNotNull(manager.getTask(1));
        Assertions.assertNotNull(manager.getTask(2));
        Assertions.assertNotNull(manager.getEpic(3));
        Assertions.assertNotNull(manager.getEpic(4));
        Assertions.assertNotNull(manager.getSubtask(5));
        Assertions.assertNotNull(manager.getSubtask(6));
        Assertions.assertNotNull(manager.getSubtask(7));
    }

    private static final String savedTasks = """
            id,type,name,status,description,epic
            1,TASK,Задача 1,NEW,Описание задачи 1,
            2,TASK,Задача 2,NEW,Описание задачи 2,
            3,EPIC,Эпик 1,NEW,Описание эпика 1,
            4,EPIC,Эпик 2,NEW,Описание эпика 2,
            5,SUBTASK,Подзадача первого эпика 1,NEW,Описание подзадачи 1,3
            6,SUBTASK,Подзадача первого эпика 2,NEW,Описание подзадачи 2,3
            7,SUBTASK,Подзадача второго эпика,NEW,Описание подзадачи 1,4
            """;
}