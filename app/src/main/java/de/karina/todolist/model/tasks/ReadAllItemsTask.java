package de.karina.todolist.model.tasks;

import android.os.AsyncTask;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;

import java.util.List;
import java.util.function.Consumer;

public class ReadAllItemsTask extends AsyncTask<Void,Void, List<TodoItem>> {
	
	private ITodoItemCRUDOperations crudOperations;
	private Consumer<List<TodoItem>> callback;
	
	public ReadAllItemsTask(ITodoItemCRUDOperations crudOperations) {
		this.crudOperations = crudOperations;
	}
	
	@Override
	protected List<TodoItem> doInBackground(Void... voids) {
		return crudOperations.readAllItems();
	}
	
	@Override
	protected void onPostExecute(List<TodoItem> todoItems) {
		callback.accept(todoItems);
	}
	
	public void run(Consumer<List<TodoItem>> consumer) {
		this.callback = consumer;
		super.execute();
	}
}
