import java.util.HashSet;

public class Epic extends Task {
    private HashSet<Subtask> subtasks;

    public Epic(String name, String description, int id, TaskStatus status, HashSet<Subtask> subtasks) {
        super(name, description, id, status);
        if (subtasks == null){
            this.subtasks = new HashSet<>();
        } else {
            this.subtasks = subtasks;
        }
    }

    public HashSet<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(HashSet<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                "} " + super.toString();
    }
}
