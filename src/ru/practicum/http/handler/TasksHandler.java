package ru.practicum.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.exception.IntersectedWIthOtherTasksException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        var pathParts = exchange.getRequestURI().getPath().split("/");
        switch (pathParts.length) {
            // `/tasks`
            case 2:
                sendOk(exchange, taskManager.getTasks());
                break;
            // `/tasks/{id}`
            case 3:
                var taskId = Integer.parseInt(pathParts[2]);
                try {
                    var task = taskManager.getTask(taskId);
                    this.sendOk(exchange, task);
                } catch (NotFoundException e) {
                    this.sendNotFound(exchange);
                }
                break;
            default:
                this.sendNotFound(exchange);
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        var inputStream = exchange.getRequestBody();
        var body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        var task = gson.fromJson(body, Task.class);

        try {
            if (task.getId() == null || task.getId() == 0) {
                taskManager.createTask(task);
            } else {
                taskManager.updateTask(task);
            }

            this.sendCreated(exchange);
        } catch (IntersectedWIthOtherTasksException e) {
            this.sendHasInteractions(exchange);
        } catch (NotFoundException e) {
            this.sendNotFound(exchange);
        }
    }

    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {
        var pathParts = exchange.getRequestURI().getPath().split("/");
        var taskId = Integer.parseInt(pathParts[2]);
        taskManager.deleteTask(taskId);
        sendOk(exchange);
    }
}
