package ru.practicum.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.practicum.gson.Gsons;
import ru.practicum.manager.InMemoryTaskManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HttpTaskServerTest {
    protected final TaskManager manager = new InMemoryTaskManager();
    protected final Gson gson = Gsons.getDefault();
    protected final HttpTaskServer server = new HttpTaskServer(manager, gson);
    protected final Random rnd = new Random();
    private int daysCounter = 0;

    @BeforeEach
    public void beforeEach() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        server.start();
    }

    @AfterEach
    public void afterEach() {
        server.stop();
    }

    public HttpResponse<String> makeRequest(String path, String method, Object value) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            var json = gson.toJson(value);
            var url = URI.create("http://localhost:8080" + path);
            var request = HttpRequest.newBuilder().uri(url)
                    .method(method, HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }


    protected Task createTask(Integer id) {
        var task = new Task("Name #" + rnd.nextInt(), "Description #" + rnd.nextInt(), id,
                TaskStatus.NEW, LocalDateTime.of(2025, 11, 1, 0, 0).plusDays(daysCounter++), Duration.ofHours(3));
        return task;
    }

    protected Epic createEpic(Integer id) {
        var epic = new Epic("Name #" + rnd.nextInt(), "Description #" + rnd.nextInt(), id,
                TaskStatus.NEW, LocalDateTime.of(2025, 11, 1, 0, 0).plusDays(daysCounter++), Duration.ofHours(3));
        return epic;
    }

    protected Subtask createSubtask(Integer id, Integer epicId) {
        var subtask = new Subtask("Name #" + rnd.nextInt(), "Description #" + rnd.nextInt(), id,
                TaskStatus.NEW, epicId, LocalDateTime.of(2025, 11, 1, 0, 0).plusDays(daysCounter++), Duration.ofHours(3));
        return subtask;
    }

    protected List<Task> createTasksInManager(int count) {
        var result = new ArrayList<Task>(count);
        for (int i = 0; i < count; i++) {
            var task = createTask(null);
            var taskId = manager.createTask(task);
            task.setId(taskId);
            result.add(task);
        }

        return result;
    }
}