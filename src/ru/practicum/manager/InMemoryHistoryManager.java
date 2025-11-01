package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> tasksViewHistory;
    private static final int historyMaxCount = 10;

    public InMemoryHistoryManager() {
        tasksViewHistory = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        switch (task) {
            case null -> {
                return;
            }
            case Epic epic -> {
                var epicForHistory = new Epic(epic.getName(), epic.getDescription(), epic.getId(), epic.getStatus(),
                        epic.getStartTime(), epic.getDuration());
                tasksViewHistory.addLast(epicForHistory);
            }
            case Subtask subtask -> {
                var subtaskForHistory = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getId(),
                        subtask.getStatus(), subtask.getEpicId(), subtask.getStartTime(), subtask.getDuration());
                tasksViewHistory.addLast(subtaskForHistory);
            }
            default -> {
                var taskForHistory = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus(),
                        task.getStartTime(), task.getDuration());
                tasksViewHistory.addLast(taskForHistory);
            }
        }

        if (tasksViewHistory.size() > historyMaxCount) {
            tasksViewHistory.removeFirst();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(tasksViewHistory);
    }
}
