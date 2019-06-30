package de.karina.todolist.model.impl;

import android.content.Context;
import androidx.room.Room;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;
import de.karina.todolist.model.User;
import de.karina.todolist.model.impl.room.TodoItemDao;
import de.karina.todolist.model.impl.room.TodoItemDatabase;

import java.util.List;

public class RoomTodoItemCRUDOperationsImpl implements ITodoItemCRUDOperations {
	
	private TodoItemDao todoItemDao;
	
	public RoomTodoItemCRUDOperationsImpl(Context context) {
		TodoItemDatabase db = Room.databaseBuilder(context.getApplicationContext(), TodoItemDatabase.class, "todoitem-db").build();
		todoItemDao = db.getDao();
	}
	
	@Override
	public TodoItem createItem(TodoItem item) {
		long id = todoItemDao.create(item);
		item.setId(id);
		return item;
	}
	
	@Override
	public List<TodoItem> readAllItems() {
		return todoItemDao.readAll();
	}
	
	@Override
	public TodoItem readItem(long id) {
		return todoItemDao.readById(id);
	}
	
	@Override
	public boolean updateItem(TodoItem item) {
		todoItemDao.update(item);
		return true;
	}
	
	@Override
	public boolean deleteItem(long id) {
		TodoItem item = todoItemDao.readById(id);
		if (item == null) {
			return false;
		}
		todoItemDao.delete(item);
		return true;
	}
	
	@Override
	public boolean deleteAllItems() {
		todoItemDao.deleteAll();
		return true;
	}
	
	@Override
	public boolean authenticateUser(User user) {
		return "s@bht.de".equals(user.getEmail()) && "000000".equals(user.getPwd());
	}
}
