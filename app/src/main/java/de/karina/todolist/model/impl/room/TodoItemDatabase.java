package de.karina.todolist.model.impl.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import de.karina.todolist.model.TodoItem;

@Database(entities = {TodoItem.class}, version = 1)
public abstract class TodoItemDatabase extends RoomDatabase {
	
	public abstract TodoItemDao getDao();
	
}
