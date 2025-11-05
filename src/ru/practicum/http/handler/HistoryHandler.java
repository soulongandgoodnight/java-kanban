package ru.practicum.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.manager.TaskManager;

import java.io.IOException;
import java.util.Objects;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
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
