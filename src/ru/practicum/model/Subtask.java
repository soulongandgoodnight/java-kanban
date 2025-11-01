package ru.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int id, TaskStatus status, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Subtask{" +
                "epicId=" + epicId +
                "} " + super.toString();
    }
}
