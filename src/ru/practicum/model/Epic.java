package ru.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;

public class Epic extends Task {
    private final HashSet<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, Integer id, TaskStatus status, LocalDateTime startTime, Duration duration) {
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
        this.startTime = subtasks.stream().map(a -> a.startTime).filter(Objects::nonNull).min(LocalDateTime::compareTo)
                .orElse(null);
        this.endTime = subtasks.stream().map(Task::getEndTime).filter(Objects::nonNull).max(LocalDateTime::compareTo)
                .orElse(null);
        this.duration = subtasks.stream().filter(a -> a.duration != null)
                .map(a -> a.duration).reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public String toString() {
        return "ru.practicum.model.Epic{" +
                "subtasks=" + subtasks +
                "} " + super.toString();
    }
}
