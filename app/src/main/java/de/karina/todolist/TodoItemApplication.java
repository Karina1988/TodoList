package de.karina.todolist;

import android.app.Application;
import android.widget.Toast;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.impl.RetrofitTodoItemCRUDOperationsImpl;
import de.karina.todolist.model.impl.RoomTodoItemCRUDOperationsImpl;
import de.karina.todolist.model.impl.SyncedDataItemCrudOperations;

public class TodoItemApplication extends Application {
	
	private ITodoItemCRUDOperations crudOperations;
	
	public ITodoItemCRUDOperations getCRUDOperations() {
		if (crudOperations == null) {
			Toast.makeText(this, "CRUD Operations have not been set. Local", Toast.LENGTH_SHORT).show();
			this.crudOperations = new RoomTodoItemCRUDOperationsImpl(this);
		}
		return crudOperations;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
//		crudOperations = new RoomTodoItemCRUDOperationsImpl(this);
//		crudOperations = new RetrofitTodoItemCRUDOperationsImpl();
		
		//synced
		crudOperations = new SyncedDataItemCrudOperations(new RoomTodoItemCRUDOperationsImpl(this), new RetrofitTodoItemCRUDOperationsImpl());
	}
	
	public void setRemoteCRUDMode(boolean remoteCRUDMode) {
		if (!remoteCRUDMode) {
			Toast.makeText(this, "Server not accessible. Use local CRUD implementation", Toast.LENGTH_SHORT).show();
			this.crudOperations = new RoomTodoItemCRUDOperationsImpl(this);
		} else {
			this.crudOperations = new SyncedDataItemCrudOperations(new RoomTodoItemCRUDOperationsImpl(this), new RetrofitTodoItemCRUDOperationsImpl());
		}
	}
}
