package ru.practicum.manager;

import ru.practicum.exception.*;
import ru.practicum.model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public static final String FILE_HEADER = "id,type,name,status,description,startTime,duration,epic";
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    private FileBackedTaskManager(File file, HashMap<Integer, Task> tasks, HashMap<Integer, Epic> epics,
                                  HashMap<Integer, Subtask> subtasks) {
        super();
        this.file = file;

        this.uniqueTaskId = Stream.of(tasks.keySet(), epics.keySet(), subtasks.keySet())
                .flatMap(Collection::stream).max(Integer::compare)
                .map(integer -> integer + 1).orElse(1);

        this.tasks.putAll(tasks);
        this.epics.putAll(epics);
        this.subtasks.putAll(subtasks);

        for (var subtask : this.subtasks.values()) {
            var epicId = subtask.getEpicId();
            var epic = this.epics.get(epicId);
            if (epic != null) {
                epic.addSubtask(subtask);
            }
        }

        this.tasksByStartTime.addAll(tasks.values().stream().filter(t -> t.getStartTime() != null).toList());
        this.tasksByStartTime.addAll(subtasks.values().stream().filter(s -> s.getStartTime() != null).toList());
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        var tasks = new HashMap<Integer, Task>();
        var epics = new HashMap<Integer, Epic>();
        var subtasks = new HashMap<Integer, Subtask>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            var isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                var baseTask = fromString(line);
                switch (baseTask) {
                    case Epic epic -> epics.put(epic.getId(), epic);
                    case Subtask subtask -> subtasks.put(subtask.getId(), subtask);
                    case Task task -> tasks.put(task.getId(), task);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException();
        }

        return new FileBackedTaskManager(file, tasks, epics, subtasks);
    }

    @Override
    public int createTask(Task task) throws IntersectedWIthOtherTasksException {
        var taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
        var epicId = super.createEpic(epic);
        save();
        return epicId;
    }

    @Override
    public int createSubtask(Subtask subtask) throws IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
        var subtaskId = super.createSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public void updateTask(Task task) throws IntersectedWIthOtherTasksException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws NotFoundException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws NotFoundException, IntersectedWIthOtherTasksException, RelatedEpicNotFoundException {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int identifier) {
        super.deleteTask(identifier);
        save();
    }

    @Override
    public void deleteEpic(int identifier) {
        super.deleteEpic(identifier);
        save();
    }

    @Override
    public void deleteSubtask(int identifier) {
        super.deleteSubtask(identifier);
        save();
    }

    private void save() {
        try (var bw = new BufferedWriter(new FileWriter(file, false))) {
            bw.write(FILE_HEADER);
            bw.newLine();
            for (var task : tasks.values()) {
                bw.write(toString(task));
                bw.newLine();
            }

            for (var epic : epics.values()) {
                bw.write(toString(epic));
                bw.newLine();
            }

            for (var subtask : subtasks.values()) {
                bw.write(toString(subtask));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private String toString(Task task) {
        if (task instanceof Subtask subtask) {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s", subtask.getId(), task.getTaskType(), subtask.getName(),
                    subtask.getStatus(), subtask.getDescription(), subtask.getStartTime(), subtask.getDuration(),
                    subtask.getEpicId());
        } else {
            return String.format("%s,%s,%s,%s,%s,%s,%s,", task.getId(), task.getTaskType(), task.getName(),
                    task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration());
        }
    }

    private static Task fromString(String value) {
        Task result;
        var stringParts = value.split(",");
        var taskType = TaskType.valueOf(stringParts[1]);
        var startTime = LocalDateTime.parse(stringParts[5]);
        var duration = Duration.parse(stringParts[6]);
        switch (taskType) {
            case SUBTASK -> result = new Subtask(stringParts[2], stringParts[4], Integer.parseInt(stringParts[0]),
                    TaskStatus.valueOf(stringParts[3]), Integer.parseInt(stringParts[7]), startTime, duration);
            case EPIC -> result = new Epic(stringParts[2], stringParts[4], Integer.parseInt(stringParts[0]),
                    TaskStatus.valueOf(stringParts[3]), startTime, duration);
            default -> result = new Task(stringParts[2], stringParts[4], Integer.parseInt(stringParts[0]),
                    TaskStatus.valueOf(stringParts[3]), startTime, duration);
        }

        return result;
    }
}
