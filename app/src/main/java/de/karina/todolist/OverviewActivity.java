package de.karina.todolist;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;
import de.karina.todolist.model.tasks.DeleteItemTask;
import de.karina.todolist.model.tasks.ReadAllItemsTask;
import de.karina.todolist.model.tasks.ReadItemTask;
import de.karina.todolist.model.tasks.UpdateItemTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class OverviewActivity extends AppCompatActivity {
	
	public static final int CALL_DETAILVIEW_FOR_CREATE = 0;
	public static final int CALL_DETAILVIEW_FOR_EDIT = 1;
	private ViewGroup todoList;
	private ArrayAdapter<TodoItem> todoListArrayAdapter;
	private FloatingActionButton addItemButton;
	private ProgressBar progressBar;
	private ITodoItemCRUDOperations crudOperations;
	
	private MenuItem moreOptionsMenu;
	
	private List<TodoItem> items = new ArrayList<>();
	
	private Comparator<TodoItem> doneComparator = (l,r) -> Boolean.valueOf(l.isDone()).compareTo(r.isDone());
	private Comparator<TodoItem> favoriteComparator = (l,r) -> Boolean.valueOf(r.isFavourite()).compareTo(l.isFavourite());
	private Comparator<TodoItem> dateComparator = (l,r) -> Long.valueOf(l.getExpiry()).compareTo(r.getExpiry());
	
	DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	
	private int sortModus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		
		//read UI elements
		progressBar = findViewById(R.id.progressBar);
		todoList = (ListView) findViewById(R.id.todoList);
		addItemButton = findViewById(R.id.addItemButton);
		
		addItemButton.setOnClickListener((view) -> {
			createNewItem();
		});
		
		//set listener
		todoListArrayAdapter = createListviewAdapter();
		((ListView) todoList).setAdapter(todoListArrayAdapter);
		((ListView) todoList).setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TodoItem clickedItem = todoListArrayAdapter.getItem(position);
				handleSelectedItem(clickedItem);
			}
		});
		
		this.crudOperations = ((TodoItemApplication) getApplication()).getCRUDOperations();
		
		new ReadAllItemsTask(this.crudOperations, this.progressBar).run(items -> {
			todoListArrayAdapter.addAll(items);
			updateSortAndFocusItem(null);
		});
	}
	
	private ArrayAdapter<TodoItem> createListviewAdapter() {
		return new ArrayAdapter<TodoItem>(this, android.R.layout.simple_list_item_1, items) {
			@NonNull
			@Override
			public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
				View todoView = convertView;
				if (todoView == null) {
					todoView = getLayoutInflater().inflate(R.layout.activity_overview_listitem, null);
				}
				TextView todoTitleView = todoView.findViewById(R.id.todoTitle);
				CheckBox itemReadyView = todoView.findViewById(R.id.todoDone);
				CheckBox itemFavouriteView = todoView.findViewById(R.id.todoFavorite);
				TextView todoDate = todoView.findViewById(R.id.todoDate);
				TodoItem currentItem = getItem(position);
				
				todoTitleView.setText(currentItem.getName());
				
				long todoDateInMilliseconds = currentItem.getExpiry();
				Date date = new Date(todoDateInMilliseconds);
				String todoDateAsString = df.format(date);
				todoDate.setText(todoDateAsString);
				
				//remove listener before setChecked status in order to avoid problems when recycling the view
				itemReadyView.setOnCheckedChangeListener(null);
				itemReadyView.setChecked(currentItem.isDone());
				itemReadyView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						currentItem.setDone(isChecked);
						updateSortAndFocusItem(null);
						new UpdateItemTask(OverviewActivity.this.crudOperations).run(currentItem, updated -> {
							if (currentItem.isDone()) {
								Toast.makeText(OverviewActivity.this, "Set item with name " + currentItem.getName() + " as done", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(OverviewActivity.this, "Unset item with name " + currentItem.getName() + " as done", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
				
				//favorite star checkbox
				itemFavouriteView.setOnCheckedChangeListener(null);
				itemFavouriteView.setChecked(currentItem.isFavourite());
				itemFavouriteView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						currentItem.setFavourite(isChecked);
						updateSortAndFocusItem(null);
						new UpdateItemTask(OverviewActivity.this.crudOperations).run(currentItem, updated -> {
							if (currentItem.isFavourite()) {
								Toast.makeText(OverviewActivity.this, "Marked item with name " + currentItem.getName() + " as favorite.", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(OverviewActivity.this, "Unmarked item with name " + currentItem.getName() + " as favorite.", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
				return todoView;
			}
		};
	}
	
	private void handleSelectedItem(TodoItem item) {
		Intent detailViewIntent = new Intent(this, DetailviewActivity.class);
		detailViewIntent.putExtra(DetailviewActivity.ARG_ITEM_ID, item.getId());
		startActivityForResult(detailViewIntent, CALL_DETAILVIEW_FOR_EDIT);
	}
	
	private void createNewItem() {
		Intent detailViewIntent = new Intent(this, DetailviewActivity.class);
		startActivityForResult(detailViewIntent, CALL_DETAILVIEW_FOR_CREATE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == CALL_DETAILVIEW_FOR_CREATE && resultCode == DetailviewActivity.STATUS_CREATED) {
			long itemId = data.getLongExtra(DetailviewActivity.ARG_ITEM_ID, -1);
			new ReadItemTask(this.crudOperations).run(itemId, item -> {
				this.todoListArrayAdapter.add(item);
				updateSortAndFocusItem(item);
			});
		} else if (requestCode == CALL_DETAILVIEW_FOR_EDIT) {
			if (resultCode == DetailviewActivity.STATUS_EDITED) {
				new ReadItemTask(this.crudOperations).run(data.getLongExtra(DetailviewActivity.ARG_ITEM_ID, -1), item -> {
					this.items.removeIf(currentItem -> currentItem.getId() == item.getId());
					this.todoListArrayAdapter.add(item);
					updateSortAndFocusItem(item);
				});
			} else if (resultCode == DetailviewActivity.STATUS_DELETED) {
				long itemId = data.getLongExtra(DetailviewActivity.ARG_ITEM_ID, -1);
				new DeleteItemTask(this.crudOperations).run(itemId, item -> {
					this.items.removeIf(currentItem -> currentItem.getId() == itemId);
					updateSortAndFocusItem(null);
				});
				Toast.makeText(OverviewActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
			}
		} else {
			updateSortAndFocusItem(null);
		}
	}
	
	private void updateSortAndFocusItem(TodoItem item) {
		if (sortModus == 1) {
			sortItemsByFavorite();
		} else if (sortModus == 2) {
			sortItemsByDate();
		} else {
			sortItemByDoneStatus();
		}
		if (item != null) {
			((ListView)this.todoList).setSelection(this.todoListArrayAdapter.getPosition(item));
		}
	}
	
	private void sortItemByDoneStatus() {
		this.todoListArrayAdapter.sort(doneComparator);
	}
	
	private void sortItemsByFavorite() {
		this.todoListArrayAdapter.sort(favoriteComparator);
	}
	
	private void sortItemsByDate() {
		this.todoListArrayAdapter.sort(dateComparator);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.overview_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.sortItemsByFavorite) {
			sortModus = 1;
			sortItemsByFavorite();
			return true;
		} else if (item.getItemId() == R.id.sortItemsByDate) {
			sortModus = 2;
			sortItemsByDate();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
