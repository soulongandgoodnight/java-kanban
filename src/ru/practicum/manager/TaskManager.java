package ru.practicum.manager;

import ru.practicum.exception.IntersectedWIthOtherTasksException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RelatedEpicNotFoundException;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    Task getTask(int id) throws NotFoundException;

    Epic getEpic(int id) throws NotFoundException;

    Subtask getSubtask(int id) throws NotFoundException;

    int createTask(Task task) throws IntersectedWIthOtherTasksException;

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask) throws IntersectedWIthOtherTasksException, RelatedEpicNotFoundException;

    void updateTask(Task task) throws NotFoundException, IntersectedWIthOtherTasksException;

    void updateEpic(Epic epic) throws NotFoundException;

    void updateSubtask(Subtask subtask) throws NotFoundException, IntersectedWIthOtherTasksException, RelatedEpicNotFoundException;

    void deleteTask(int identifier);

    void deleteEpic(int identifier);

    void deleteSubtask(int identifier);

    List<Subtask> getSubtasksByEpic(int epicId) throws NotFoundException;

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
