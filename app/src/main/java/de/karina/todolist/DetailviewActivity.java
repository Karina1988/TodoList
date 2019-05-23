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
    
    public ITodoItemCRUDOperations crudOperations;

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
	            TodoItem item = crudOperations.readItem(itemId);
	            if (item != null) {
	            	runOnUiThread(() -> {
		                todoTitle.setText(item.getTitle());
		                todoDescription.setText(item.getDescription());
		            });
	            }
	        }).start();
        	
        }
    }

    private void saveItem() {
        Intent returnIntent = new Intent();
    
        TodoItem todoItem = new TodoItem(todoTitle.getText().toString(), todoDescription.getText().toString());
        
        returnIntent.putExtra("item", todoItem);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
