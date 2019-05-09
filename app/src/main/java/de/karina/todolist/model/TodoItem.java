package de.karina.todolist.model;

public class TodoItem {

    Integer id;
    String title;
    String description;
    Boolean done;
    Boolean favorite;
    Integer dueDate;
    Integer dueTime;

    public TodoItem(Integer id, String title, String description, Boolean done, Boolean favorite, Integer dueDate, Integer dueTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.done = done;
        this.favorite = favorite;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Integer getDueDate() {
        return dueDate;
    }

    public void setDueDate(Integer dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getDueTime() {
        return dueTime;
    }

    public void setDueTime(Integer dueTime) {
        this.dueTime = dueTime;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", done=" + done +
                ", favorite=" + favorite +
                ", dueDate=" + dueDate +
                ", dueTime=" + dueTime +
                '}';
    }
}
