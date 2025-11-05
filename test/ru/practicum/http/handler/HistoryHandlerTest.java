package ru.practicum.http.handler;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.practicum.http.HttpTaskServerTest;
import ru.practicum.model.Task;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.model.ValidationExtensions.assertTaskSetsAreEqual;

public class HistoryHandlerTest extends HttpTaskServerTest {

    @Test
    public void when_getHistory_should_returnExpected() throws IOException, InterruptedException {
        // given
        var tasks = createTasksInManager(5);
        for (var task : tasks) {
            manager.getTask(task.getId());
        }

        // do
        var response = makeRequest("/history", "GET", null);

        // expect
        ArrayList<Task> actualTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());

        assertTaskSetsAreEqual(tasks.stream(), actualTasks.stream());
    }

    @Test
    public void when_getHistoryByWrongPath_should_returnNofFound() throws IOException, InterruptedException {
        var response = makeRequest("/history/1", "GET", null);
        assertEquals(404, response.statusCode());
    }
}