package ru.practicum.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.exception.IntersectedWIthOtherTasksException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RelatedEpicNotFoundException;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {
        var pathParts = exchange.getRequestURI().getPath().split("/");
        var subtaskId = Integer.parseInt(pathParts[2]);
        taskManager.deleteSubtask(subtaskId);
        sendOk(exchange);
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        var inputStream = exchange.getRequestBody();
        var body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        var subtask = gson.fromJson(body, Subtask.class);
        if (subtask == null) {
            this.sendBadRequest(exchange);
            return;
        }

        try {
            if (subtask.getId() == null || subtask.getId() == 0) {
                taskManager.createSubtask(subtask);
            } else {
                taskManager.updateSubtask(subtask);
            }

            this.sendCreated(exchange);
        } catch (IntersectedWIthOtherTasksException e) {
            this.sendHasInteractions(exchange);
        } catch (NotFoundException | RelatedEpicNotFoundException e) {
            this.sendNotFound(exchange);
        }
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        var pathParts = exchange.getRequestURI().getPath().split("/");
        switch (pathParts.length) {
            case 2:
                sendOk(exchange, taskManager.getSubtasks());
                break;
            case 3:
                var subtaskId = Integer.parseInt(pathParts[2]);
                try {
                    var subtask = taskManager.getSubtask(subtaskId);
                    this.sendOk(exchange, subtask);
                } catch (NotFoundException e) {
                    this.sendNotFound(exchange);
                }
                break;
            default:
                sendNotFound(exchange);
        }
    }
}
