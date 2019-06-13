package de.karina.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;
import de.karina.todolist.model.tasks.DeleteItemTask;
import de.karina.todolist.model.tasks.UpdateItemTask;

public class DetailviewActivity extends AppCompatActivity {
	
	private FloatingActionButton saveButton;
	private Button deleteButton;
	private EditText todoTitle;
	private EditText todoDescription;
	private CheckBox todoDone;
	private CheckBox todoFavourite;
	
	public static final String ARG_ITEM_ID = "itemId";
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_EDITED = 2;
	public static final int STATUS_DELETED = 3;
	
	private ITodoItemCRUDOperations crudOperations;
	private TodoItem item;
	
	private MenuItem saveButtoninOptions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailview);
		
		crudOperations = ((TodoItemApplication) getApplication()).getCRUDOperations();
		
		saveButton = findViewById(R.id.saveButton);
		saveButton.setOnClickListener((view) -> {
			saveItem();
		});
		
		deleteButton = findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener((view) -> {
			deleteItem();
		});
		
		todoTitle = findViewById(R.id.todoTitle);
		todoDescription = findViewById(R.id.todoDescription);
		todoDone = findViewById(R.id.todoDone);
		todoFavourite = findViewById(R.id.todoFavorite);
		
//		todoTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//			@Override
//			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
//					if (textView.getText().length() > 0) {
//						saveButtoninOptions.setEnabled(true);
//						saveButtoninOptions.getIcon().setAlpha(255);
//						return true;
//					}
//				}
//				return false;
//			}
//		});
		
		long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
		if (itemId != -1) {
			new Thread(() -> {
				this.item = crudOperations.readItem(itemId);
				if (this.item != null) {
					runOnUiThread(() -> {
						this.todoTitle.setText(this.item.getName());
						this.todoDescription.setText(this.item.getDescription());
						this.todoDone.setChecked(this.item.isDone());
						this.todoFavourite.setChecked(this.item.isFavourite());
					});
				}
			}).start();
		}
	}
	
	private void saveItem() {
		Intent returnIntent = new Intent();
		
		boolean create = false;
		if (this.item == null) {
			this.item = new TodoItem();
			create = true;
		}
		returnIntent.putExtra(ARG_ITEM_ID, item.getId());
		
		this.item.setName(todoTitle.getText().toString());
		this.item.setDescription(todoDescription.getText().toString());
		this.item.setDone(this.todoDone.isChecked());
		this.item.setFavourite(this.todoFavourite.isChecked());
		
		if (create) {
			new Thread(() -> {
				item = crudOperations.createItem(item);
				returnIntent.putExtra(ARG_ITEM_ID, item.getId());
				setResult(STATUS_CREATED, returnIntent);
				finish();
			}).start();
		} else {
			new UpdateItemTask(crudOperations).run(this.item, updated -> {
				returnIntent.putExtra(ARG_ITEM_ID, item.getId());
				setResult(STATUS_EDITED, returnIntent);
				finish();
			});
		}
	}
	
	private void deleteItem() {
		new DeleteItemTask(crudOperations).run(item.getId(), deleted -> {
			Intent returnIntent = new Intent();
			returnIntent.putExtra(ARG_ITEM_ID, item.getId());
			setResult(STATUS_DELETED, returnIntent);
			finish();
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.overview_menu, menu);
		saveButtoninOptions = menu.findItem(R.id.saveItem);
//		saveButtoninOptions.setEnabled(false);
//		saveButtoninOptions.getIcon().setAlpha(255);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.saveItem) {
			saveItem();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
