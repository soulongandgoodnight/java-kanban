package ru.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;

public class Epic extends Task {
    private final HashSet<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, int id, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.subtasks = new HashSet<>();
        recalculateTimeAndDuration();
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public HashSet<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        recalculateTimeAndDuration();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        recalculateTimeAndDuration();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        recalculateTimeAndDuration();
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        subtasks.add(subtask);
        recalculateTimeAndDuration();
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        //ignored
    }

    @Override
    public void setDuration(Duration duration) {
        //ignored
    }

    private void recalculateTimeAndDuration() {
        this.startTime = subtasks.stream().map(a -> a.startTime).min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);
        this.endTime = subtasks.stream().map(Task::getEndTime).max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);

        this.duration = subtasks.stream().map(a -> a.duration).reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Epic{" +
                "subtasks=" + subtasks +
                "} " + super.toString();
    }
}
