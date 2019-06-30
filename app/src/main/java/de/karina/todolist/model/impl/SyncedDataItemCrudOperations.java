package de.karina.todolist.model.impl;

import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;
import de.karina.todolist.model.User;

import java.util.List;

public class SyncedDataItemCrudOperations implements ITodoItemCRUDOperations {
	
	private ITodoItemCRUDOperations remoteCRUD;
	private ITodoItemCRUDOperations localCRUD;
	
	public ITodoItemCRUDOperations getRemoteCRUD() {
		return remoteCRUD;
	}
	
	public ITodoItemCRUDOperations getLocalCRUD() {
		return localCRUD;
	}
	
	public SyncedDataItemCrudOperations(ITodoItemCRUDOperations localCRUD, ITodoItemCRUDOperations remoteCRUD) {
		this.localCRUD = localCRUD;
		this.remoteCRUD = remoteCRUD;
	}
	
	@Override
	public TodoItem createItem(TodoItem item) {
		TodoItem created = this.localCRUD.createItem(item);
		if (created != null) {
			this.remoteCRUD.createItem(created);
			return created;
		}
		return null;
	}
	
	@Override
	public List<TodoItem> readAllItems() {
		return this.localCRUD.readAllItems();
	}
	
	@Override
	public TodoItem readItem(long id) {
		return this.localCRUD.readItem(id);
	}
	
	@Override
	public boolean updateItem(TodoItem item) {
		if (localCRUD.updateItem(item)) {
			remoteCRUD.updateItem(item);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean deleteItem(long id) {
		if (localCRUD.deleteItem(id)) {
			remoteCRUD.deleteItem(id);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean deleteAllItems() {
		if (localCRUD.deleteAllItems()) {
			remoteCRUD.deleteAllItems();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean authenticateUser(User user) {
		return "s@bht.de".equals(user.getEmail()) && "000000".equals(user.getPwd());
	}
}
