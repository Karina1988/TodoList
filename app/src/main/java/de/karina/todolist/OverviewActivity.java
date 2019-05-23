package de.karina.todolist;

import android.content.Intent;
import androidx.room.Room;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;
import de.karina.todolist.model.impl.room.TodoItemDatabase;

import java.io.DataInput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private TodoDataSource dataSource;
    private FloatingActionButton addItemButton;
    private ArrayAdapter<TodoItem> todoListArrayAdapter;
    private ViewGroup todoList;
    
    private ITodoItemCRUDOperations crudOperations;
    
    private List<TodoItem> tasks = new ArrayList<>(); //(Arrays.asList(new TodoItem[]{new TodoItem("Task 1", "Desc1"),new TodoItem("Task 2", "Desc2")}));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener((view) -> {
            createNewItem();
        });

        TodoItem todoItem = new TodoItem(1, "test", "desc", false, false, 3119, 800);

        dataSource = new TodoDataSource(this);

        todoList = (ListView) findViewById(R.id.todoList);
        todoListArrayAdapter = new ArrayAdapter<TodoItem>(this, android.R.layout.simple_list_item_1, tasks) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View todoView = getLayoutInflater().inflate(R.layout.activity_overview_listitem, null);
                TextView todoTitleView = todoView.findViewById(R.id.todoTitle);
                TodoItem currentItem = getItem(position);
                todoTitleView.setText(currentItem.getTitle());
                return todoView;
            }
        };
        ((ListView)todoList).setAdapter(todoListArrayAdapter);
        ((ListView)todoList).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem clickedItem = todoListArrayAdapter.getItem(position);
            }
        });
        
        this.crudOperations = ((TodoItemApplication)getApplication()).getCrudOperations();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TodoItem> todoItems = crudOperations.readAllItems();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (TodoItem item : todoItems) {
                            addNewItemToList(item);
                        }
                    }
                });
            }
        }).start();
    }

    private void createNewItem() {
        Intent detailViewIntent = new Intent(this, DetailviewActivity.class);
        startActivityForResult(detailViewIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            TodoItem todoItem = (TodoItem)data.getSerializableExtra("item");
            createItemAndUpdateList(todoItem);
        }
    }
    
    private void createItemAndUpdateList(TodoItem item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TodoItem created = crudOperations.createItem(item);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addNewItemToList(created);
                    }
                });
            }
        }).start();
    }
    
    private void addNewItemToList(TodoItem todoItem) {
        todoListArrayAdapter.add(todoItem);
        
    }
    
}
