package de.karina.todolist.model.impl.room;

import androidx.room.*;
import de.karina.todolist.model.TodoItem;

import java.util.List;

@Dao
public interface TodoItemDao {
	
	@Query("select * from todoitem")
	public List<TodoItem> readAll();
	
	@Query("select * from todoitem where id == (:id)")
	public TodoItem readById(long id);
	
	@Insert
	public long create(TodoItem item);
	
	@Delete
	public void delete(TodoItem item);
	
	@Update
	public void update(TodoItem item);
}
