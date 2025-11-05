package ru.practicum.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.http.handler.*;
import ru.practicum.manager.TaskManager;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer implements Closeable {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager, Gson gson) {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0); // связываем сервер с сетевым портом
            httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
            httpServer.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
            httpServer.createContext("/epics", new EpicsHandler(taskManager, gson));
            httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        httpServer.start(); // запускаем сервер
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    @Override
    public void close() {
        this.stop();
    }
}
