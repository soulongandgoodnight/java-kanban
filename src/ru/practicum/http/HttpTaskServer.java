package ru.practicum.http;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.http.handler.*;
import ru.practicum.manager.TaskManager;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer implements Closeable {
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start() throws IOException {
        if (httpServer != null) {
            httpServer.stop(0);
        }

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0); // связываем сервер с сетевым портом

        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));

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
