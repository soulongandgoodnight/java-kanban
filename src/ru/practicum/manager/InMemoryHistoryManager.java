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
        if (task == null) {
            return;
        } else if (task instanceof Epic epic) {
            var epicForHistory = new Epic(epic.getName(), epic.getDescription(), epic.getId(), epic.getStatus());
            tasksViewHistory.addLast(epicForHistory);
        } else if (task instanceof Subtask subtask) {
            var subtaskForHistory = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getId(),
                    subtask.getStatus(), subtask.getEpicId());
            tasksViewHistory.addLast(subtaskForHistory);
        } else {
            var taskForHistory = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus());
            tasksViewHistory.addLast(taskForHistory);
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
