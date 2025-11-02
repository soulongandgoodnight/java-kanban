package ru.practicum.http.handler;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var method = exchange.getRequestMethod();
        switch (method) {
            case "GET": handleGetRequest(exchange);
            case "POST": handlePostRequest(exchange);
            case "DELETE": handleDeleteRequest(exchange);
            default: this.sendNotFound(exchange);
        }
    }

    protected abstract void handleDeleteRequest(HttpExchange exchange) throws IOException;

    protected abstract void handlePostRequest(HttpExchange exchange) throws IOException;

    protected abstract void handleGetRequest(HttpExchange exchange) throws IOException;

    protected void sendOk(HttpExchange h, Object obj) throws IOException {
        if (obj == null) {
            sendOk(h);
        }
        var text = toJson(obj);
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendOk(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        h.sendResponseHeaders(200, 0);
        h.close();
    }

    protected void sendCreated(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        h.sendResponseHeaders(201, 0);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        h.sendResponseHeaders(404, 0);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        h.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        h.sendResponseHeaders(406, 0);
        h.close();
    }

    protected String toJson(Object obj) {
        var gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }
}
