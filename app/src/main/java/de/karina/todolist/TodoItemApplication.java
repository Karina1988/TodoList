package de.karina.todolist;

import android.app.Application;
import android.widget.Toast;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.impl.RetrofitTodoItemCRUDOperationsImpl;
import de.karina.todolist.model.impl.RoomTodoItemCRUDOperationsImpl;
import de.karina.todolist.model.impl.SyncedDataItemCrudOperations;

import static de.karina.todolist.OverviewActivity.CALL_LOGIN;

public class TodoItemApplication extends Application {
	
	private ITodoItemCRUDOperations crudOperations;
	
	public ITodoItemCRUDOperations getCRUDOperations() {
		return crudOperations;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		crudOperations = new RoomTodoItemCRUDOperationsImpl(this);
//		crudOperations = new RetrofitTodoItemCRUDOperationsImpl();
//		crudOperations = new SyncedDataItemCrudOperations(new RoomTodoItemCRUDOperationsImpl(this), new RetrofitTodoItemCRUDOperationsImpl());
	}
	
	public void setRemoteCRUDMode(boolean remoteCRUDMode) {
		if (!remoteCRUDMode) {
			Toast.makeText(this, "Server not accessible. Use local CRUD implementation", Toast.LENGTH_SHORT).show();
		} else {
			
			Toast.makeText(this, "Server accessible. Use remote CRUD implementation", Toast.LENGTH_SHORT).show();
			// implement synchronization 
			this.crudOperations = new SyncedDataItemCrudOperations(crudOperations, new RetrofitTodoItemCRUDOperationsImpl());
		}
	}
}
