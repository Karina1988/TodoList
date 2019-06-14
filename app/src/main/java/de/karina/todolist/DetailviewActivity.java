package de.karina.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;
import de.karina.todolist.model.tasks.DeleteItemTask;
import de.karina.todolist.model.tasks.UpdateItemTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DetailviewActivity extends AppCompatActivity {
	
	private FloatingActionButton saveButton;
	private Button deleteButton;
	private EditText todoTitle;
	private EditText todoDescription;
	private CheckBox todoDone;
	private CheckBox todoFavourite;
	private EditText todoDate;
	private EditText todoTime;
	
	public static final String ARG_ITEM_ID = "itemId";
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_EDITED = 2;
	public static final int STATUS_DELETED = 3;
	
	private ITodoItemCRUDOperations crudOperations;
	private TodoItem item;
	
	final Calendar myCalendar = Calendar.getInstance();
	
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
			openDeleteAlertDialog();
		});
		
		todoTitle = findViewById(R.id.todoTitle);
		todoDescription = findViewById(R.id.todoDescription);
		todoDone = findViewById(R.id.todoDone);
		todoFavourite = findViewById(R.id.todoFavorite);
		todoDate = findViewById(R.id.todoDate);
		todoTime = findViewById(R.id.todoTime);
		
		//datepicker
		DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
			                      int dayOfMonth) {
				// TODO Auto-generated method stub
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateTodoDate();
			}
		};
		
		//timepicker
		TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				myCalendar.set(Calendar.MINUTE, minute);
				updateTodoTime();
			}
		};
		
		todoDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DatePickerDialog(DetailviewActivity.this, date, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		todoTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new TimePickerDialog(DetailviewActivity.this, time, 
						myCalendar.get(Calendar.HOUR_OF_DAY), 
						myCalendar.get(Calendar.MINUTE), true).show();
			}
		});
		
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
	
	private void updateTodoDate() {
		String myFormat = "dd.MM.yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		
		todoDate.setText(sdf.format(myCalendar.getTime()));
	}
	
	private void updateTodoTime() {
		String myFormat = "hh:mm";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		
		todoTime.setText(sdf.format(myCalendar.getTime()));
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
	
	private void openDeleteAlertDialog() {
		new AlertDialog.Builder(this)
			.setTitle("Delete item")
			.setMessage("Are you sure you want to delete this item?")
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					deleteItem();
				}
			})
			.setNegativeButton(android.R.string.no, null)
			.setIcon(android.R.drawable.ic_delete)
			.show();
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
		getMenuInflater().inflate(R.menu.detailview_menu, menu);
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
