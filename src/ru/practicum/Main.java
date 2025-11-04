package ru.practicum;

import ru.practicum.gson.Gsons;
import ru.practicum.http.HttpTaskServer;
import ru.practicum.manager.Managers;

public class Main {
    public static void main(String[] args) {
        try (var httpServer = new HttpTaskServer(Managers.getDefault(), Gsons.getDefault())) {
            httpServer.start();
        }
    }
}
