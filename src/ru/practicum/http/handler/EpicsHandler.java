package ru.practicum.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.exception.NotFoundException;
import ru.practicum.http.pojo.EpicPojo;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;
import ru.practicum.model.TaskStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {
        var pathParts = exchange.getRequestURI().getPath().split("/");
        var epicId = Integer.parseInt(pathParts[2]);
        taskManager.deleteEpic(epicId);
        sendOk(exchange);
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        var inputStream = exchange.getRequestBody();
        var body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        var epicFromJson = new Gson().fromJson(body, EpicPojo.class);
        var epicId = epicFromJson.getId() == null ? 0 : epicFromJson.getId();
        var epic = new Epic(epicFromJson.getName(), epicFromJson.getDescription(), epicId,
                TaskStatus.valueOf(epicFromJson.getStatus()), epicFromJson.getStartTime(), epicFromJson.getDuration());
        try {
            if (epic.getId() == 0) {
                taskManager.createEpic(epic);
            } else {
                taskManager.updateEpic(epic);
            }

            this.sendCreated(exchange);
        } catch (NotFoundException e) {
            this.sendNotFound(exchange);
        }
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        var pathParts = exchange.getRequestURI().getPath().split("/");
        switch (pathParts.length) {
            // `/epics`
            case 2:
                sendOk(exchange, taskManager.getEpics());
                break;
                // `/epics/{id}`
            case 3:
                var epicId = Integer.parseInt(pathParts[2]);
                try {
                    var task = taskManager.getEpic(epicId);
                    this.sendOk(exchange, task);
                } catch (NotFoundException e) {
                    this.sendNotFound(exchange);
                }
                break;
                // `/epics/{id}/subtasks`
            case 4:
                var epicIdForSubtasks = Integer.parseInt(pathParts[2]);
                try {
                    var task = taskManager.getSubtasksByEpic(epicIdForSubtasks);
                    this.sendOk(exchange, task);
                } catch (NotFoundException e) {
                    this.sendNotFound(exchange);
                }
                break;
            default: this.sendNotFound(exchange);
        }
    }
}
