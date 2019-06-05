package de.karina.todolist.model.tasks;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;

import java.util.List;
import java.util.function.Consumer;

public class ReadAllItemsTask extends AsyncTask<Void,Void, List<TodoItem>> {
	
	private ITodoItemCRUDOperations crudOperations;
	private Consumer<List<TodoItem>> callback;
	private ProgressBar progressBar;
	
	public ReadAllItemsTask(ITodoItemCRUDOperations crudOperations, ProgressBar progressBar) {
		this.crudOperations = crudOperations;
		this.progressBar = progressBar;
	}
	
	@Override
	protected void onPreExecute() {
		this.progressBar.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected List<TodoItem> doInBackground(Void... voids) {
		return crudOperations.readAllItems();
	}
	
	@Override
	protected void onPostExecute(List<TodoItem> todoItems) {
		this.progressBar.setVisibility(View.GONE);
		callback.accept(todoItems);
	}
	
	public void run(Consumer<List<TodoItem>> consumer) {
		this.callback = consumer;
		super.execute();
	}
}
