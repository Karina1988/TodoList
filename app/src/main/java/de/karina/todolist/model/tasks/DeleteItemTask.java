package de.karina.todolist.model.tasks;

import android.os.AsyncTask;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;

import java.util.function.Consumer;

public class DeleteItemTask extends AsyncTask<Long,Void,Boolean> {
	
	private ITodoItemCRUDOperations crudOperations;
	private Consumer<Boolean> callback;
	
	public DeleteItemTask(ITodoItemCRUDOperations crudOperations) {
		this.crudOperations = crudOperations;
	}
	
	@Override
	protected Boolean doInBackground(Long... longs) {
		return this.crudOperations.deleteItem(longs[0]);
	}
	
	@Override
	protected void onPostExecute(Boolean aBoolean) {
		callback.accept(aBoolean);
	}
	
	public void run(long itemId, Consumer<Boolean> callback) {
		this.callback = callback;
		super.execute(itemId);
	}
}
