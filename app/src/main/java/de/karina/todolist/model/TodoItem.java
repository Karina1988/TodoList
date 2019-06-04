package de.karina.todolist.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class TodoItem implements Serializable {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String description;
    private Boolean done;
    private Boolean favorite;
    private Integer dueDate;
    private Integer dueTime;
    
    public TodoItem() {
    }
    
    @Ignore
    public TodoItem(String name) {
       this.setName(name);
    }
    
    public TodoItem(String name, String description) {
        this.setName(name);
        this.setDescription(description);
    }
    
    @Ignore
    public TodoItem(Integer id, String name, String description, Boolean done, Boolean favorite, Integer dueDate, Integer dueTime) {
        this.setId(id);
        this.setName(name);
        this.setDescription(description);
        this.setDone(done);
        this.setFavorite(favorite);
        this.setDueDate(dueDate);
        this.setDueTime(dueTime);
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", done=" + done +
                ", favorite=" + favorite +
                ", dueDate=" + dueDate +
                ", dueTime=" + dueTime +
                '}';
    }
}
