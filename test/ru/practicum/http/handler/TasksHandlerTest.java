package ru.practicum.http.handler;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.practicum.http.HttpTaskServerTest;
import ru.practicum.model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.model.ValidationExtensions.assertTaskSetsAreEqual;
import static ru.practicum.model.ValidationExtensions.assertTasksAreEqual;

public class TasksHandlerTest extends HttpTaskServerTest {
    @Test
    public void when_AddTask_should_beOk() throws IOException, InterruptedException {
        // given
        var task = createTask(null);

        //do
        var response = makeRequest("/tasks", "POST", task);

        // expect
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        var actualTask = tasksFromManager.getFirst();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(task.getName(), actualTask.getName());
        assertEquals(task.getDescription(), actualTask.getDescription());
        assertEquals(task.getStatus(), actualTask.getStatus());
        assertEquals(task.getStatus(), actualTask.getStatus());
    }

    @Test
    public void when_updateTask_should_beOk() throws IOException, InterruptedException {
        // given
        var task = createTask(null);
        var taskId = manager.createTask(task);
        task.setId(taskId);
        task.setName("Updated Name #" + rnd.nextInt());

        // do
        var response = makeRequest("/tasks", "POST", task);

        // expect
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        var actualTask = tasksFromManager.getFirst();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertTasksAreEqual(task, actualTask);
    }

    @Test
    public void when_updateNonExistingTask_should_returnNotFound() throws IOException, InterruptedException {
        // given
        var task = createTask(rnd.nextInt());

        // do
        var response = makeRequest("/tasks", "POST", task);

        // expect
        assertEquals(404, response.statusCode());
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void when_updateNull_should_returnBadRequest() throws IOException, InterruptedException {
        // do
        var response = makeRequest("/tasks", "POST", null);

        // expect
        assertEquals(400, response.statusCode());
    }

    @Test
    public void when_createIntersectingTask_should_returnNotAcceptable() throws IOException, InterruptedException {
        // given
        var task = createTask(null);
        manager.createTask(task);
        var intersected = createTask(null);
        intersected.setStartTime(task.getStartTime());

        // do
        var response = makeRequest("/tasks", "POST", intersected);

        // expect
        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getTasks().size());
    }

    @Test
    public void when_getTask_should_returnExpected() throws IOException, InterruptedException {
        // given
        var task = createTask(null);
        var taskId = manager.createTask(task);
        var expectedTask = manager.getTask(taskId);

        // do
        var response = makeRequest("/tasks/" + taskId, "GET", null);

        // expect
        assertEquals(200, response.statusCode());
        var actualTask = gson.fromJson(response.body(), Task.class);
        assertTasksAreEqual(actualTask, expectedTask);
    }

    @Test
    public void when_getTasks_should_returnExpected() throws IOException, InterruptedException {
        // given
        manager.createTask(createTask(null));
        manager.createTask(createTask(null));
        manager.createTask(createTask(null));
        var expectedTasks = manager.getTasks();

        // do
        var response = makeRequest("/tasks", "GET", null);

        // expect
        assertEquals(200, response.statusCode());
        ArrayList<Task> actualTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertTaskSetsAreEqual(actualTasks.stream(), expectedTasks.stream());
    }

    @Test
    public void when_deleteTask_should_beOk() throws IOException, InterruptedException {
        // given
        var taskId = manager.createTask(createTask(null));
        assertEquals(1, manager.getTasks().size());

        // do
        var response = makeRequest("/tasks/" + taskId, "DELETE", null);

        // expect
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getTasks().size());

    }
}