package ru.practicum.http.handler;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.practicum.http.HttpTaskServerTest;
import ru.practicum.model.Subtask;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.model.ValidationExtensions.assertSubtasksAreEqual;
import static ru.practicum.model.ValidationExtensions.assertTaskSetsAreEqual;

public class SubtasksHandlerTest extends HttpTaskServerTest {

    @Test
    public void when_AddSubtask_should_beOk() throws IOException, InterruptedException {
        // given
        var epic = createEpic(null);
        var epicId = manager.createEpic(epic);
        var subtask = createSubtask(null, epicId);

        //do
        var response = makeRequest("/subtasks", "POST", subtask);

        // expect
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        var subtasksFromManager = manager.getSubtasks();

        var actualSubtask = subtasksFromManager.getFirst();
        assertNotNull(subtasksFromManager);
        assertEquals(1, subtasksFromManager.size());
        assertEquals(subtask.getName(), actualSubtask.getName());
        assertEquals(subtask.getDescription(), actualSubtask.getDescription());
        assertEquals(subtask.getStatus(), actualSubtask.getStatus());
        assertEquals(subtask.getStatus(), actualSubtask.getStatus());
    }

    @Test
    public void when_updateSubtask_should_beOk() throws IOException, InterruptedException {
        // given
        var epic = createEpic(null);
        var epicId = manager.createEpic(epic);
        var subtask = createSubtask(null, epicId);
        var subtaskId = manager.createSubtask(subtask);
        subtask.setId(subtaskId);
        subtask.setName("Updated Name #" + rnd.nextInt());

        // do
        var response = makeRequest("/subtasks", "POST", subtask);

        // expect
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        var subtasksFromManager = manager.getSubtasks();

        var actualSubtask = subtasksFromManager.getFirst();
        assertNotNull(subtasksFromManager);
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertSubtasksAreEqual(subtask, actualSubtask);
    }

    @Test
    public void when_updateNonExistingSubtask_should_returnNotFound() throws IOException, InterruptedException {
        // given
        var epic = createEpic(null);
        var epicId = manager.createEpic(epic);

        var subtask = createSubtask(rnd.nextInt(), epicId);

        // do
        var response = makeRequest("/subtasks", "POST", subtask);

        // expect
        assertEquals(404, response.statusCode());
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    public void when_updateNull_should_returnBadRequest() throws IOException, InterruptedException {
        // do
        var response = makeRequest("/subtasks", "POST", null);

        // expect
        assertEquals(400, response.statusCode());
    }

    @Test
    public void when_createIntersectingSubtask_should_returnNotAcceptable() throws IOException, InterruptedException {
        // given
        var epic = createEpic(null);
        var epicId = manager.createEpic(epic);
        var subtask = createSubtask(null, epicId);
        manager.createSubtask(subtask);
        var intersected = createSubtask(null, epicId);
        intersected.setStartTime(subtask.getStartTime());

        // do
        var response = makeRequest("/subtasks", "POST", intersected);

        // expect
        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getSubtasks().size());
    }

    @Test
    public void when_getSubtask_should_returnExpected() throws IOException, InterruptedException {
        // given
        var epic = createEpic(null);
        var epicId = manager.createEpic(epic);
        var subtask = createSubtask(null, epicId);
        var subtaskId = manager.createSubtask(subtask);
        var expectedSubtask = manager.getSubtask(subtaskId);

        // do
        var response = makeRequest("/subtasks/" + subtaskId, "GET", null);

        // expect
        assertEquals(200, response.statusCode());
        var actualSubtask = gson.fromJson(response.body(), Subtask.class);
        assertSubtasksAreEqual(actualSubtask, expectedSubtask);
    }

    @Test
    public void when_getSubtasks_should_returnExpected() throws IOException, InterruptedException {
        // given
        var epic = createEpic(null);
        var epicId = manager.createEpic(epic);
        manager.createSubtask(createSubtask(null, epicId));
        manager.createSubtask(createSubtask(null, epicId));
        manager.createSubtask(createSubtask(null, epicId));
        var expectedSubtasks = manager.getSubtasks();

        // do
        var response = makeRequest("/subtasks", "GET", null);

        // expect
        assertEquals(200, response.statusCode());
        ArrayList<Subtask> actualSubtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        assertTaskSetsAreEqual(actualSubtasks.stream(), expectedSubtasks.stream());
    }

    @Test
    public void when_deleteSubtask_should_beOk() throws IOException, InterruptedException {
        // given
        var epic = createEpic(null);
        var epicId = manager.createEpic(epic);
        var subtaskId = manager.createSubtask(createSubtask(null, epicId));
        assertEquals(1, manager.getSubtasks().size());

        // do
        var response = makeRequest("/subtasks/" + subtaskId, "DELETE", null);

        // expect
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getSubtasks().size());

    }
}