package ru.practicum;

import ru.practicum.http.HttpTaskServer;
import ru.practicum.manager.Managers;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        try (var httpServer = new HttpTaskServer(Managers.getDefault())) {
            httpServer.start();
        }
    }
}
