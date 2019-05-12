package de.karina.todolist;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.karina.todolist.model.TodoItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleBiFunction;

public class OverviewActivity extends AppCompatActivity {

    public static final String LOG_TAG = OverviewActivity.class.getSimpleName();

    private TodoDataSource dataSource;
    private FloatingActionButton addItemButton;
    private ArrayAdapter<TodoItem> todoListArrayAdapter;
    private ViewGroup todoList;

    private List<TodoItem> tasks = new ArrayList<>(Arrays.asList(new TodoItem[]{new TodoItem("Task 1"),new TodoItem("Task 2")}));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener((view) -> {
            createNewItem();
        });

        TodoItem todoItem = new TodoItem(1, "test", "desc", false, false, 3119, 800);
        Log.d(LOG_TAG, "Inhalt des Items: " + todoItem.toString());

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
    }

    private void createNewItem() {
        Intent detailViewIntent = new Intent(this, DetailviewActivity.class);
        startActivityForResult(detailViewIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            TodoItem todoItem = (TodoItem)data.getSerializableExtra("item");
            addNewItemToList(todoItem);
        }
    }
    
    private void addNewItemToList(TodoItem item) {
        todoListArrayAdapter.add(item);
        
    }
    
}
