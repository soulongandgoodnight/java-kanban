package ru.practicum.manager;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int uniqueTaskId = 1;
    private final HashMap<Integer,Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer,Subtask> subtasks;

    public TaskManager(){
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int getUniqueTaskId() {
        return uniqueTaskId++;
    }

    public List<Task> getAllTasks () {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for(var epic : epics.values()) {
            epic.removeAllSubtasks();
            recalculateEpicStatus(epic);
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public int createTask(Task task) {
        var taskId = this.getUniqueTaskId();
        task.setId(taskId);
        tasks.put(taskId, task);
        return taskId;
    }

    public int createEpic(Epic epic) {
        var epicId = this.getUniqueTaskId();
        epic.setId(epicId);
        epics.put(epicId, epic);
        epic.setStatus(TaskStatus.NEW);
        return epicId;
    }

    public int createSubtask(Subtask subtask) {
        var existingEpic = epics.get(subtask.getEpicId());
        if (existingEpic == null) {
            return -1;
        }

        var subtaskId = this.getUniqueTaskId();
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        existingEpic.addSubtask(subtask);
        recalculateEpicStatus(existingEpic);
        return subtaskId;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        var epicId = epic.getId();
        var existingEpic = this.epics.get(epicId);
        if (existingEpic == null)  {
            return;
        }

        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
    }

    public void updateSubtask(Subtask subtask) {
        var existingSubtask = subtasks.get(subtask.getId());
        var existingEpic = epics.get(subtask.getEpicId());
        if (existingSubtask == null || existingEpic == null) {
            return;
        }

        subtasks.put(subtask.getId(), subtask);
        var epicSubtasks = existingEpic.getSubtasks();
        epicSubtasks.remove(existingSubtask);
        existingEpic.updateSubtask(subtask);
        recalculateEpicStatus(existingEpic);
    }

    public void deleteTask(int identifier) {
        tasks.remove(identifier);
    }

    public void deleteEpic(int identifier) {
        var epic = epics.get(identifier);
        if (epic == null) {
            return;
        }

        var epicSubtasks = epic.getSubtasks();
        for (var epicSubtask: epicSubtasks) {
            subtasks.remove(epicSubtask.getId());
        }

        epics.remove(identifier);
    }

    public void deleteSubtask(int identifier) {
        var subtask = subtasks.get(identifier);
        if (subtask == null) {
            return;
        }

        subtasks.remove(identifier);
        var epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            var epicSubtasks = epic.getSubtasks();
            epicSubtasks.remove(subtask);
            recalculateEpicStatus(epic);
        }
    }

    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        var epic = epics.get(epicId);

        if (epic == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(epic.getSubtasks());
    }

    private void recalculateEpicStatus(Epic epic){
        var subtasks = epic.getSubtasks();
        if (subtasks == null || subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        var allSubtasksAreNew = true;
        var allSubtasksAreDone = true;
        for (Subtask subtask : subtasks)
        {
            switch (subtask.getStatus())
            {
                case NEW : allSubtasksAreDone = false; break;
                case DONE : allSubtasksAreNew = false; break;
                case IN_PROGRESS :
                    allSubtasksAreDone = false;
                    allSubtasksAreNew = false;
                    break;
            }
        }

        if (allSubtasksAreNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allSubtasksAreDone){
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
