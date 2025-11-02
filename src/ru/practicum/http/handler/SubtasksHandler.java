package ru.practicum.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.exception.IntersectedWIthOtherTasksException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RelatedEpicNotFoundException;
import ru.practicum.http.pojo.SubtaskPojo;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Subtask;
import ru.practicum.model.TaskStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
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
        var subtaskPojo = new Gson().fromJson(body, SubtaskPojo.class);
        var subtaskId = subtaskPojo.getId() == null ? 0 : subtaskPojo.getId();
        var epicId = subtaskPojo.getEpicId() == null ? 0 : subtaskPojo.getEpicId();
        var subtask = new Subtask(subtaskPojo.getName(), subtaskPojo.getDescription(), subtaskId,
                TaskStatus.valueOf(subtaskPojo.getStatus()), epicId, subtaskPojo.getStartTime(), subtaskPojo.getDuration());
        try {
            if (subtask.getId() == 0) {
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
            default: sendNotFound(exchange);
        }
    }
}
