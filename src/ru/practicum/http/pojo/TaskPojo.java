package ru.practicum.http.pojo;

public class TaskPojo {
    private Integer id;
    private String name;
    private String description;
    private String status;
    private Integer durationInMinutes;
    private String startTime;

    public TaskPojo(Integer id, String name, String description, String status, Integer durationInMinutes, String startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.durationInMinutes = durationInMinutes;
        this.startTime = startTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDuration() {
        return durationInMinutes;
    }

    public void setDuration(Integer duration) {
        this.durationInMinutes = duration;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
