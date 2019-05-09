package de.karina.todolist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.karina.todolist.model.TodoItem;

public class OverviewActivity extends AppCompatActivity {

    public static final String LOG_TAG = OverviewActivity.class.getSimpleName();

    private TodoDataSource dataSource;

    ListView todoList;
    String[] tasks = {
            "Task1",
            "Task2",
            "Task3",
            "Task4",
            "Task5",
            "Task6",
            "Task7"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        TodoItem todoItem = new TodoItem(1, "test", "desc", false, false, 3119, 800);
        Log.d(LOG_TAG, "Inhalt des Items: " + todoItem.toString());

        dataSource = new TodoDataSource(this);

        todoList = (ListView) findViewById(R.id.todoList);
        todoList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tasks));
    }
}
