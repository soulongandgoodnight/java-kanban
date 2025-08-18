import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class TaskManager {
    static int uniqueTaskId = 1;
    private final HashMap<Integer,Task> tasks;
    private final HashMap<Integer,Epic> epics;
    private final HashMap<Integer,Subtask> subtasks;

    public TaskManager(){
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }


    private int getUniqueTaskId() {
        var result = uniqueTaskId;
        uniqueTaskId = uniqueTaskId + 1;
        return result;
    }

    public Collection<Task> getAllTasks () {
        return tasks.values();
    }

    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
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
            epic.setStatus(TaskStatus.NEW);
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

    public void createTask(Task task) {
        task.setId(this.getUniqueTaskId());
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        var epicId = this.getUniqueTaskId();
        epic.setId(epicId);
        epics.put(epicId, epic);
        var resultSubtasks = new HashSet<Subtask>();
        var subtasksToCreate = epic.getSubtasks();
        if (subtasksToCreate != null) {
            for (Subtask subtask : subtasksToCreate) {
                subtask.setId(this.getUniqueTaskId());
                subtask.setEpicId(epicId);
                resultSubtasks.add(subtask);
                this.subtasks.put(subtask.getId(), subtask);
            }
        }

        epic.setSubtasks(resultSubtasks);
        recalculateEpicStatus(epic);
    }

    public void createSubtask(Subtask subtask) {
        var existingEpic = epics.get(subtask.getEpicId());
        if (existingEpic == null){
            return;
        }

        subtask.setId(this.getUniqueTaskId());
        subtasks.put(subtask.getId(), subtask);
        existingEpic.getSubtasks().add(subtask);

        recalculateEpicStatus(existingEpic);
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

        var existingEpicSubtasks = existingEpic.getSubtasks();
        var subtasksForUpdate = epic.getSubtasks();
        var resultSubtasks = new HashSet<Subtask>();
        for (var existingEpicSubtask: existingEpicSubtasks) {
            if (!subtasksForUpdate.contains(existingEpicSubtask)) {
                this.subtasks.remove(existingEpicSubtask.getId());
            }
        }

        for(var subtaskForUpdate: subtasksForUpdate) {
            subtaskForUpdate.setEpicId(epicId);
            if (!this.subtasks.containsKey(subtaskForUpdate.getId())) {
                subtaskForUpdate.setId(this.getUniqueTaskId());
            }

            resultSubtasks.add(subtaskForUpdate);
            this.subtasks.put(subtaskForUpdate.getId(), subtaskForUpdate);
        }

        epic.setSubtasks(resultSubtasks);
        this.recalculateEpicStatus(epic);
        this.epics.put(epicId, epic);
    }

    public void updateSubtask(Subtask subtask) {
        var existingSubtask = this.subtasks.get(subtask.getId());
        var existingEpic = this.epics.get(subtask.getEpicId());

        if (existingSubtask == null || existingEpic == null) {
            return;
        }

        this.subtasks.put(subtask.getId(), subtask);

        existingEpic.getSubtasks().remove(existingSubtask);
        existingEpic.getSubtasks().add(subtask);

        this.recalculateEpicStatus(existingEpic);
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
            this.subtasks.remove(epicSubtask.getId());
        }

        epics.remove(identifier);
    }

    public void deleteSubtask(int identifier) {
        var subtask = this.subtasks.get(identifier);
        if (subtask == null) {
            return;
        }

        var epic = this.epics.get(subtask.getEpicId());
        if (epic != null) {
            var epicSubtasks = epic.getSubtasks();
            epicSubtasks.remove(subtask);
            recalculateEpicStatus(epic);
        }
    }

    public Collection<Subtask> getSubtasksByEpic(int epicId) {
        var epic = epics.get(epicId);

        if (epic == null) {
            return new ArrayList<>();
        }

        return epic.getSubtasks();
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
