package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int uniqueTaskId = 1;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;
    protected final TreeSet<Task> tasksByStartTime;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        tasksByStartTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        historyManager = Managers.getDefaultHistory();
    }

    private int getUniqueTaskId() {
        return uniqueTaskId++;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(tasksByStartTime::remove);
        tasks.values().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(historyManager::remove);
        epics.clear();
        subtasks.values().forEach(tasksByStartTime::remove);
        subtasks.values().forEach(historyManager::remove);
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().forEach(tasksByStartTime::remove);
        subtasks.values().forEach(historyManager::remove);
        subtasks.clear();
        for (var epic : epics.values()) {
            epic.removeAllSubtasks();
            recalculateEpicStatus(epic);
        }
    }

    @Override
    public Task getTask(int id) {
        var task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        var epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        var subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int createTask(Task task) {
        if (task.getStartTime() != null && isTaskIntersectsWithExistingTasks(task)) {
            return -1;
        }

        var taskId = this.getUniqueTaskId();
        task.setId(taskId);
        tasks.put(taskId, task);
        if (task.getStartTime() != null) {
            tasksByStartTime.add(task);
        }

        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
        var epicId = this.getUniqueTaskId();
        epic.setId(epicId);
        epics.put(epicId, epic);
        epic.setStatus(TaskStatus.NEW);
        return epicId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (subtask.getStartTime() != null && isTaskIntersectsWithExistingTasks(subtask)) {
            return -1;
        }

        var existingEpic = epics.get(subtask.getEpicId());
        if (existingEpic == null || subtask.getId() == existingEpic.getId()) {
            return -1;
        }

        var subtaskId = this.getUniqueTaskId();
        subtask.setId(subtaskId);
        existingEpic.addSubtask(subtask);
        subtasks.put(subtaskId, subtask);
        if (subtask.getStartTime() != null) {
            tasksByStartTime.add(subtask);
        }

        recalculateEpicStatus(existingEpic);
        return subtaskId;
    }

    @Override
    public void updateTask(Task task) {
        if (task.getStartTime() != null && isTaskIntersectsWithExistingTasks(task)) {
            return;
        }

        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            tasksByStartTime.remove(task);
            if (task.getStartTime() != null) {
                tasksByStartTime.add(task);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        var epicId = epic.getId();
        var existingEpic = this.epics.get(epicId);
        if (existingEpic == null) {
            return;
        }

        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask.getStartTime() != null && isTaskIntersectsWithExistingTasks(subtask)) {
            return;
        }

        var existingSubtask = subtasks.get(subtask.getId());
        var existingEpic = epics.get(subtask.getEpicId());
        if (existingSubtask == null || existingEpic == null) {
            return;
        }

        subtasks.put(subtask.getId(), subtask);
        tasksByStartTime.remove(subtask);
        if (subtask.getStartTime() != null) {
            tasksByStartTime.add(subtask);
        }

        existingEpic.updateSubtask(subtask);
        recalculateEpicStatus(existingEpic);
    }

    @Override
    public void deleteTask(int identifier) {
        var task = tasks.get(identifier);
        if (task != null) {
            tasks.remove(identifier);
            tasksByStartTime.remove(task);
            historyManager.remove(task);
        }
    }

    @Override
    public void deleteEpic(int identifier) {
        var epic = epics.get(identifier);
        if (epic == null) {
            return;
        }

        var epicSubtasks = epic.getSubtasks();
        for (var epicSubtask : epicSubtasks) {
            subtasks.remove(epicSubtask.getId());
            historyManager.remove(epicSubtask);
            tasksByStartTime.remove(epicSubtask);
        }

        historyManager.remove(epic);
        epics.remove(identifier);
    }

    @Override
    public void deleteSubtask(int identifier) {
        var subtask = subtasks.get(identifier);
        if (subtask == null) {
            return;
        }

        subtasks.remove(identifier);
        historyManager.remove(subtask);
        tasksByStartTime.remove(subtask);
        var epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            var epicSubtasks = epic.getSubtasks();
            epicSubtasks.remove(subtask);
            recalculateEpicStatus(epic);
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        var epic = epics.get(epicId);

        if (epic == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(epic.getSubtasks());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return tasksByStartTime.stream().toList();
    }

    private Boolean isTaskIntersectsWithExistingTasks(Task task) {
        return tasksByStartTime.stream().anyMatch(existingTask -> existingTask.getId() != task.getId() &&
                areTasksIntersected(existingTask, task));
    }

    private Boolean areTasksIntersected(Task left, Task right) {
        var leftStartTime = left.getStartTime();
        var rightStartTime = right.getStartTime();
        var earliestTask = leftStartTime.isBefore(rightStartTime) ? left : right;
        var latestTask = leftStartTime.isBefore(rightStartTime) ? right : left;
        var e1 = earliestTask.getStartTime();
        var e2 = earliestTask.getEndTime();
        var l1 = latestTask.getStartTime();
        var l2 = latestTask.getEndTime();

        return e1.isAfter(l1) || e1.isAfter(l2) || e2.isAfter(l1) || e2.isAfter(l2);
    }

    private void recalculateEpicStatus(Epic epic) {
        var subtasks = epic.getSubtasks();
        if (subtasks == null || subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        var allSubtasksAreNew = true;
        var allSubtasksAreDone = true;
        for (Subtask subtask : subtasks) {
            switch (subtask.getStatus()) {
                case NEW:
                    allSubtasksAreDone = false;
                    break;
                case DONE:
                    allSubtasksAreNew = false;
                    break;
                case IN_PROGRESS:
                    allSubtasksAreDone = false;
                    allSubtasksAreNew = false;
                    break;
            }
        }

        if (allSubtasksAreNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allSubtasksAreDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
