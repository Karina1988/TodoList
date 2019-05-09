package de.karina.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TodoDataSource {

    public static final String LOG_TAG = TodoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private TodoDbHelper dbHelper;

    public TodoDataSource(Context context) {
        Log.d(LOG_TAG, "DataSoure erzeugt DBHelper");
        dbHelper = new TodoDbHelper(context);
    }

    public void open() {
    Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
    database = dbHelper.getWritableDatabase();
    Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
  }

  public void close() {
    dbHelper.close();
    Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
  }
}
