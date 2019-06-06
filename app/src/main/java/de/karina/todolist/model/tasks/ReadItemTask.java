package de.karina.todolist.model.tasks;

import android.os.AsyncTask;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;

import java.util.function.Consumer;

public class ReadItemTask extends AsyncTask<Long,Void,TodoItem> {
	
	private ITodoItemCRUDOperations crudOperations;
	private Consumer<TodoItem> callback;
	
	public ReadItemTask(ITodoItemCRUDOperations crudOperations) {
		this.crudOperations = crudOperations;
	}
	
	@Override
	protected TodoItem doInBackground(Long... longs) {
		return crudOperations.readItem(longs[0]);
	}
	
	@Override
	protected void onPostExecute(TodoItem todoItem) {
		callback.accept(todoItem);
	}
	
	public void run(long itemId,Consumer<TodoItem> callback) {
		this.callback = callback;
		super.execute(itemId);
	}
}
