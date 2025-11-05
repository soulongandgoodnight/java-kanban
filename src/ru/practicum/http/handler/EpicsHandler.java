package ru.practicum.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.TaskManager;
import ru.practicum.model.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
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
        var epic = gson.fromJson(body, Epic.class);

        if (epic == null) {
            this.sendBadRequest(exchange);
            return;
        }

        if (epic.getId() == null) {
            taskManager.createEpic(epic);
            this.sendCreated(exchange);
        }

        this.sendNotFound(exchange);
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
            default:
                this.sendNotFound(exchange);
        }
    }
}
