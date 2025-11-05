package ru.practicum.http.handler;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.practicum.http.HttpTaskServerTest;
import ru.practicum.model.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.model.ValidationExtensions.assertTaskSetsAreEqual;

class PrioritizedHandlerTest extends HttpTaskServerTest {
    @Test
    public void when_getPrioritizedHandler_should_returnExpected() throws IOException, InterruptedException {
        // given
        var tasks = createTasksInManager(10);

        // do
        var response = makeRequest("/prioritized", "GET", null);

        // expect
        ArrayList<Task> actualTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertEquals(200, response.statusCode());
        assertTaskSetsAreEqual(tasks.stream(), actualTasks.stream());
        final LocalDateTime[] previous = {LocalDateTime.MIN};
        actualTasks.forEach(x -> {
            assertTrue(x.getStartTime().isAfter(previous[0]));
            previous[0] = x.getStartTime();
        });

    }
}