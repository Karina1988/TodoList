package de.karina.todolist.model;

import java.util.List;

public interface ITodoItemCRUDOperations {
	
	public TodoItem createItem(TodoItem item);
	
	public List<TodoItem> readAllItems();
	
	public TodoItem readItem(long id);
	
	public boolean updateItem(TodoItem item);
	
	public boolean deleteItem(long id);
}
