package de.karina.todolist;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import de.karina.todolist.model.IAfterTask;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;
import de.karina.todolist.model.impl.SyncedDataItemCrudOperations;
import de.karina.todolist.model.tasks.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class OverviewActivity extends AppCompatActivity {
	
	public static final int CALL_DETAILVIEW_FOR_CREATE = 0;
	public static final int CALL_DETAILVIEW_FOR_EDIT = 1;
	public static final int CALL_LOGIN = 2;
	
	private ViewGroup todoList;
	private ArrayAdapter<TodoItem> todoListArrayAdapter;
	private FloatingActionButton addItemButton;
	private ProgressBar progressBar;
	private ITodoItemCRUDOperations crudOperations;
	
	private List<TodoItem> items = new ArrayList<>();
	
	private Comparator<TodoItem> doneComparator = (l,r) -> {
		if (l.isDone() == r.isDone()) {
			return 0;
		} else if (!l.isDone() && r.isDone()) {
			return -1;
		} else {
			return 1;
		}
	};
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
		
		new CheckRemoteAvailableTask().run(available -> {
//			intent zu loginactivity todo
//			Intent loginIntent = new Intent(this, LoginActivity.class);
//			startActivity(loginIntent);
			
			TodoItemApplication todoItemApplication = (TodoItemApplication)getApplication();
			todoItemApplication.setRemoteCRUDMode(available);
			
			//initialise the view with data 
			this.crudOperations = todoItemApplication.getCRUDOperations();
			
			new ReadAllItemsTask(this.crudOperations, this.progressBar).run(items -> {
				todoListArrayAdapter.addAll(items);
				updateSortAndFocusItem(null);
				synchronizeItems();
			});
			
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
				
				Date currentTime = new Date();
				if (date.before(currentTime)) {
					todoTitleView.setTextColor(Color.RED);
				}
				
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
								Toast.makeText(OverviewActivity.this, getString(R.string.setItem) + " " + currentItem.getName() + " " + getString(R.string.setDone), Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(OverviewActivity.this, getString(R.string.setItem) + " " + currentItem.getName() + getString(R.string.setUndone), Toast.LENGTH_SHORT).show();
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
								Toast.makeText(OverviewActivity.this, getString(R.string.setItem) + " " + currentItem.getName() + getString(R.string.setFavorite), Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(OverviewActivity.this, getString(R.string.setItem) + " " + currentItem.getName() + getString(R.string.setNotFavorite), Toast.LENGTH_SHORT).show();
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
				new DeleteItemTask(this.crudOperations).run(itemId, success -> {
					if (success) {
						this.items.removeIf(currentItem -> currentItem.getId() == itemId);
						updateSortAndFocusItem(null);
					}
				});
				Toast.makeText(OverviewActivity.this, R.string.itemDeleted, Toast.LENGTH_SHORT).show();
			}
		} else {
			updateSortAndFocusItem(null);
		}
	}
	
	private void updateSortAndFocusItem(TodoItem item) {
		if (sortModus == 1) {
			sortItemsByFavorite();
			sortItemByDoneStatus();
		} else if (sortModus == 2) {
			sortItemsByDate();
			sortItemByDoneStatus();
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
	
	private void deleteAllItemsLocal() {
		ITodoItemCRUDOperations target = crudOperations;
		if(crudOperations instanceof SyncedDataItemCrudOperations) {
			// Set target to local crud of synced operations
			target = ((SyncedDataItemCrudOperations) crudOperations).getLocalCRUD();
		}
		new DeleteAllItemsTask(target).run(success -> {
			if (success) {
				todoListArrayAdapter.clear();
				Toast.makeText(OverviewActivity.this, getString(R.string.localItemsDeleted), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void deleteAllItemsRemote() {
		deleteAllItemsRemote(null, true);
	}
	
	private void deleteAllItemsRemote(IAfterTask afterDeletion, boolean showToast) {
		if(crudOperations instanceof SyncedDataItemCrudOperations) {
			ITodoItemCRUDOperations target =( (SyncedDataItemCrudOperations)crudOperations).getRemoteCRUD();
			new DeleteAllItemsTask(target).run(success -> {
				if (success && showToast) {
					Toast.makeText(OverviewActivity.this, getString(R.string.remoteItemsDeleted), Toast.LENGTH_SHORT).show();
				}
				if (afterDeletion != null) {
					afterDeletion.run(success);
				} 
			});
		}
	}
	
	private void synchronizeItems() {
		if (!(crudOperations instanceof SyncedDataItemCrudOperations)) {
			return;
		}
		if (todoListArrayAdapter.getCount() > 0) {
			deleteAllItemsRemote((boolean successful) -> {
				if (successful) {
					ITodoItemCRUDOperations remoteCrudOpertaions = ((SyncedDataItemCrudOperations) crudOperations).getRemoteCRUD();
					new Thread(() -> {
						for (int i = 0; i < todoListArrayAdapter.getCount(); i++) {
							remoteCrudOpertaions.createItem(todoListArrayAdapter.getItem(i));
						}
					}).start();
					Toast.makeText(OverviewActivity.this, R.string.synchronizedRemote, Toast.LENGTH_SHORT).show();
				}
			}, false);
		} else {
			ITodoItemCRUDOperations localCrudOperations = ((SyncedDataItemCrudOperations) crudOperations).getLocalCRUD();
			ITodoItemCRUDOperations remoteCrudOperations = ((SyncedDataItemCrudOperations) crudOperations).getRemoteCRUD();

			new ReadAllItemsTask(remoteCrudOperations, this.progressBar).run(items -> {
				new Thread(() -> {
					for (int i = 0; i < items.size(); i++) {
						localCrudOperations.createItem(items.get(i));
					}
				}).start();
				todoListArrayAdapter.addAll(items);
				Toast.makeText(OverviewActivity.this, getString(R.string.synchronizedLocal), Toast.LENGTH_SHORT).show();
			});
		}
		// prüfe, ob lokale todos vorhanden. 
		// wenn ja: lösche alle todos remote und füge die lokalen todos remote hinzu
		// wenn nein, übertrage alle remote todos auf lokale datenbank
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
		} else if (item.getItemId() == R.id.deleteLocal) {
			deleteAllItemsLocal();
			return true;
		} else if (item.getItemId() == R.id.deleteRemote) {
			deleteAllItemsRemote();
			return true;
		} else if (item.getItemId() == R.id.synchronize) {
			synchronizeItems();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
