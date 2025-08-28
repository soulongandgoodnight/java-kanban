package ru.practicum.manager;

import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> tasksViewHistory;
    private final int historyMaxCount = 10;

    public InMemoryHistoryManager(){
        tasksViewHistory = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        var taskForHistory = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus());
        tasksViewHistory.addLast(taskForHistory);
        while (tasksViewHistory.size() > historyMaxCount) {
            tasksViewHistory.removeFirst();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(tasksViewHistory);
    }
}
