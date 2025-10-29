package ru.practicum.model;

import java.util.HashSet;

public class Epic extends Task {
    private final HashSet<Subtask> subtasks;

    public Epic(String name, String description, int id, TaskStatus status) {
        super(name, description, id, status);
        this.subtasks = new HashSet<>();
    }

    public HashSet<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        subtasks.add(subtask);
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Epic{" +
                "subtasks=" + subtasks +
                "} " + super.toString();
    }
}
