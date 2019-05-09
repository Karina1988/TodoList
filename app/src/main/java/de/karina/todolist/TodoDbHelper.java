package de.karina.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TodoDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = TodoDbHelper.class.getSimpleName();
    public static final String DB_NAME = "todoList.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_TODOLIST_ITEM = "todoListItem";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_DONE = "done";
    public static final String COLUMN_FAVORITE = "favorite";
    public static final String COLUMN_DUEDATE = "dueDate";
    public static final String COLUMN_DUETIME = "dueTime";

    public static final String SQL_CREATE =
        "CREATE TABLE " + TABLE_TODOLIST_ITEM +
        "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
              COLUMN_TITLE + " TEXT NOT NULL, " +
              COLUMN_DESC + " TEXT NOT NULL, " +
              COLUMN_DONE + " BOOLEAN NOT NULL, " +
              COLUMN_FAVORITE + " BOOLEAN NOT NULL, " +
              COLUMN_DUEDATE + " INTEGER NOT NULL, " +
              COLUMN_DUETIME + " INTEGER NOT NULL);";


    public TodoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

   // Die onCreate-Methode wird nur aufgerufen, falls die Datenbank noch nicht existiert
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
            db.execSQL(SQL_CREATE);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
