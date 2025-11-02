package ru.practicum.http.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.manager.TaskManager;

import java.io.IOException;
import java.util.Objects;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {
        this.sendNotFound(exchange);
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        this.sendNotFound(exchange);
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        var pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length == 2 && Objects.equals(pathParts[1], "history")) {
            sendOk(exchange, taskManager.getHistory());
        } else {
            this.sendNotFound(exchange);
        }
    }
}
