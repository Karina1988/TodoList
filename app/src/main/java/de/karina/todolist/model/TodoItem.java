package de.karina.todolist.model;

import android.widget.ArrayAdapter;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.google.gson.annotations.SerializedName;
import de.karina.todolist.ListToStringTypeConverters;

import java.io.Serializable;
import java.util.List;

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
	
	@TypeConverters(ListToStringTypeConverters.class)
	private List<String> contacts;
	
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
	
	public List<String> getContacts() {
		return contacts;
	}
	
	public void setContacts(List<String> contacts) {
		this.contacts = contacts;
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
