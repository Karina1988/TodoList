package de.karina.todolist.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class TodoItem implements Serializable {
	
	@PrimaryKey(autoGenerate = true)
	private long id;
	private String name;
	private String description;
	
	@SerializedName("done")
	private boolean done;
	
	private boolean favourite;
	
	@SerializedName("expiry")
	private long expiry;
	
	public TodoItem() {
	}
	
	public TodoItem(String name) {
		this.setName(name);
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
	
	public boolean isDone() {
		return done;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public boolean isFavourite() {
		return favourite;
	}
	
	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}
	
	public long getExpiry() {
		return expiry;
	}
	
	public void setExpiry(long expiry) {
		this.expiry = expiry;
	}
	
	@Override
	public String toString() {
		return "TodoItem{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", done=" + done +
				", favourite=" + favourite +
				", expiry=" + expiry +
				'}';
	}
}
