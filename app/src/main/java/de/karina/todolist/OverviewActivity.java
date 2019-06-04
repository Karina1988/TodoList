package de.karina.todolist;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {
    
    public static final int CALL_DETAILVIEW_FOR_CREATE = 0;
    public static final int CALL_DETAILVIEW_FOR_EDIT = 1;
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
                todoTitleView.setText(currentItem.getName());
                return todoView;
            }
        };
        ((ListView)todoList).setAdapter(todoListArrayAdapter);
        ((ListView)todoList).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem clickedItem = todoListArrayAdapter.getItem(position);
                handleSelectedItem(clickedItem);
            }
        });
        
        this.crudOperations = ((TodoItemApplication)getApplication()).getCRUDOperations();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TodoItem> todoItems = crudOperations.readAllItems();
                
                
                todoItems.sort(new Comparator<TodoItem>() {
                    @Override
                    public int compare(TodoItem o1, TodoItem o2) {
                        return String.valueOf(o1.getName()).compareTo(o2.getName());
                    }
                });
                
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
            readCreatedItemAndUpdateList(itemId);
        }
    }
    
    private void readCreatedItemAndUpdateList(long itemId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TodoItem created = crudOperations.readItem(itemId);
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
