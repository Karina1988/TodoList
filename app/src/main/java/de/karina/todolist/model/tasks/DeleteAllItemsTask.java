package de.karina.todolist.model.tasks;

import android.os.AsyncTask;
import de.karina.todolist.model.ITodoItemCRUDOperations;

import java.util.function.Consumer;

public class DeleteAllItemsTask extends AsyncTask<Long,Void,Boolean> {
	
	private ITodoItemCRUDOperations crudOperations;
	private Consumer<Boolean> callback;
	
	public DeleteAllItemsTask(ITodoItemCRUDOperations crudOperations) {
		this.crudOperations = crudOperations;
	}
	
	@Override
	protected Boolean doInBackground(Long... longs) {
		return this.crudOperations.deleteAllItems();
	}
	
	@Override
	protected void onPostExecute(Boolean aBoolean) {
		callback.accept(aBoolean);
	}
	
	public void run(Consumer<Boolean> callback) {
		this.callback = callback;
		super.execute();
	}
}
