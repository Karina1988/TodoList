package de.karina.todolist;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import de.karina.todolist.model.tasks.CheckRemoteAvailableTask;

import java.util.function.Consumer;

public class StartActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		new CheckRemoteAvailableTask().run(new Consumer<Boolean>() {
			@Override
			public void accept(Boolean available) {
				Intent intent;
				if (!available) {
					intent = new Intent(StartActivity.this, OverviewActivity.class);
				} else {
					intent = new Intent(StartActivity.this, LoginActivity.class);
				}
				StartActivity.this.startActivity(intent);
			}
		});
	}
}
