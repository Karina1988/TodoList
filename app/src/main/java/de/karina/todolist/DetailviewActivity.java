package de.karina.todolist;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;

public class DetailviewActivity extends AppCompatActivity {

    private FloatingActionButton saveButton;
    private EditText todoTitle;
    private EditText todoDescription;
    
    public static final String ARG_ITEM_ID = "itemId";
    public static final int STATUS_CREATED = 1;
    public static final int STATUS_EDITED = 2;
    public static final int STATUS_DELETED = 3;
    
    public ITodoItemCRUDOperations crudOperations;
    public TodoItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);
        
        crudOperations = ((TodoItemApplication)getApplication()).getCRUDOperations();
    
    
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener((view) -> {
            saveItem();
        });
    
        todoTitle = findViewById(R.id.todoTitle);
        todoDescription = findViewById(R.id.todoDescription);
    
        long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        if (itemId != -1) {
        	new Thread(() -> {
	            this.item = crudOperations.readItem(itemId);
	            if (this.item != null) {
	            	runOnUiThread(() -> {
		                this.todoTitle.setText(this.item.getName());
		                this.todoDescription.setText(this.item.getDescription());
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
        this.item.setName(todoTitle.getText().toString());
        this.item.setDescription(todoDescription.getText().toString());
        
        if (create) {
	        new Thread(() -> {
		        item = crudOperations.createItem(item);
		        returnIntent.putExtra(ARG_ITEM_ID, item.getId());
		        setResult(STATUS_CREATED, returnIntent);
		        finish();
	        }).start();
        }
    }
}
