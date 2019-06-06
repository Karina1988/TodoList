package de.karina.todolist.model.tasks;

import android.os.AsyncTask;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;

import java.util.function.Consumer;

public class UpdateItemTask extends AsyncTask<TodoItem,Void,Boolean> {
	
	private ITodoItemCRUDOperations crudOperations;
	private Consumer<Boolean> callback;
	
	public UpdateItemTask(ITodoItemCRUDOperations crudOperations) {
		this.crudOperations = crudOperations;
	}
	
	@Override
	protected Boolean doInBackground(TodoItem... todoItems) {
		return this.crudOperations.updateItem(todoItems[0]);
	}
	
	@Override
	protected void onPostExecute(Boolean aBoolean) {
		callback.accept(aBoolean);
	}
	
	public void run(TodoItem itemToBeUpdated, Consumer<Boolean> callback) {
		this.callback = callback;
		super.execute(itemToBeUpdated);
	}
	
}
