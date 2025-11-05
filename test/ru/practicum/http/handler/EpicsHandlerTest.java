package ru.practicum.http.handler;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.practicum.http.HttpTaskServerTest;
import ru.practicum.model.Epic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.model.ValidationExtensions.assertEpicsAreEqual;
import static ru.practicum.model.ValidationExtensions.assertTaskSetsAreEqual;

public class EpicsHandlerTest extends HttpTaskServerTest {

    @Test
    public void when_AddEpic_should_beOk() throws IOException, InterruptedException {
        // given
        var epic = createEpic(null);

        //do
        var response = makeRequest("/epics", "POST", epic);

        // expect
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = manager.getEpics();

        var actualepic = epicsFromManager.getFirst();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals(epic.getName(), actualepic.getName());
        assertEquals(epic.getDescription(), actualepic.getDescription());
        assertEquals(epic.getStatus(), actualepic.getStatus());
        assertEquals(epic.getStatus(), actualepic.getStatus());
    }

    @Test
    public void when_updateNonExistingepic_should_returnNotFound() throws IOException, InterruptedException {
        // given
        var epic = createEpic(rnd.nextInt());

        // do
        var response = makeRequest("/epics", "POST", epic);

        // expect
        assertEquals(404, response.statusCode());
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    public void when_updateNull_should_returnBadRequest() throws IOException, InterruptedException {
        // do
        var response = makeRequest("/epics", "POST", null);

        // expect
        assertEquals(400, response.statusCode());
    }

    @Test
    public void when_getepic_should_returnExpected() throws IOException, InterruptedException {
        // given
        var epic = createEpic(null);
        var epicId = manager.createEpic(epic);
        var expectedEpic = manager.getEpic(epicId);

        // do
        var response = makeRequest("/epics/" + epicId, "GET", null);

        // expect
        assertEquals(200, response.statusCode());
        var actualepic = gson.fromJson(response.body(), Epic.class);
        assertEpicsAreEqual(actualepic, expectedEpic);
    }

    @Test
    public void when_getepics_should_returnExpected() throws IOException, InterruptedException {
        // given
        manager.createEpic(createEpic(null));
        manager.createEpic(createEpic(null));
        manager.createEpic(createEpic(null));
        var expectedepics = manager.getEpics();

        // do
        var response = makeRequest("/epics", "GET", null);

        // expect
        assertEquals(200, response.statusCode());
        ArrayList<Epic> actualepics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        assertTaskSetsAreEqual(actualepics.stream(), expectedepics.stream());
    }

    @Test
    public void when_deleteepic_should_beOk() throws IOException, InterruptedException {
        // given
        var epicId = manager.createEpic(createEpic(null));
        assertEquals(1, manager.getEpics().size());

        // do
        var response = makeRequest("/epics/" + epicId, "DELETE", null);

        // expect
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getEpics().size());

    }
}