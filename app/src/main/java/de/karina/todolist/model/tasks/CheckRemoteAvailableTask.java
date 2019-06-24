package de.karina.todolist.model.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

public class CheckRemoteAvailableTask extends AsyncTask<Void,Void,Boolean> {
	
	private Consumer<Boolean> callback;
	
	@Override
	protected Boolean doInBackground(Void... voids) {
		try {
			HttpURLConnection connection = (HttpURLConnection)new URL("http://10.0.0.2:8080/api/todos").openConnection();
			connection.setReadTimeout(1000);
			connection.setConnectTimeout(1500);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			
			//connect
			connection.connect();
			InputStream is = connection.getInputStream();
			Log.i(getClass().getSimpleName(), "got input stream from server: " + is);
			
			return true;
		} catch (Throwable e) {
			return false;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean aBoolean) {
		this.callback.accept(aBoolean);
	}
	
	public void run(Consumer<Boolean> callback) {
		this.callback = callback;
		super.execute();
	}
}
