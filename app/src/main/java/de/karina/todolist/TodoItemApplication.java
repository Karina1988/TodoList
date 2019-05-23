package de.karina.todolist;

import android.app.Application;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.impl.RoomTodoItemCRUDOperationsImpl;

public class TodoItemApplication extends Application {
	
	private ITodoItemCRUDOperations crudOperations;
	
	public ITodoItemCRUDOperations getCrudOperations() {
		return crudOperations;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		crudOperations = new RoomTodoItemCRUDOperationsImpl(this);
	}
}
