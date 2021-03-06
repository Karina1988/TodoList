package de.karina.todolist.model.impl;

import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;
import de.karina.todolist.model.User;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.io.IOException;
import java.util.List;

public class RetrofitTodoItemCRUDOperationsImpl implements ITodoItemCRUDOperations {
	
	private TodoItemWebAPI webAPIClient;
	
	public RetrofitTodoItemCRUDOperationsImpl() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://10.0.2.2:8080/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		webAPIClient = retrofit.create(TodoItemWebAPI.class);
	}
	
	@Override
	public TodoItem createItem(TodoItem item) {
		try {
			return webAPIClient.createItem(item).execute().body();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<TodoItem> readAllItems() {
		try {
			return webAPIClient.readAllItems().execute().body();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public TodoItem readItem(long id) {
		try {
			return webAPIClient.readItem(id).execute().body();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean updateItem(TodoItem item) {
		try {
			if (webAPIClient.updateItem(item.getId(),item).execute().body() != null) {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false; 
			//todo
		}
		return false;
	}
	
	@Override
	public boolean deleteItem(long id) {
		try {
			return webAPIClient.deleteItem(id).execute().body();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean deleteAllItems() {
		try {
			return webAPIClient.deleteAllItems().execute().body();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean authenticateUser(User user) {
		return "s@bht.de".equals(user.getEmail()) && "000000".equals(user.getPwd());
	}
	
	public static interface TodoItemWebAPI {
		
		@POST("api/todos")
		public Call<TodoItem> createItem(@Body TodoItem item);
		
		@GET("api/todos")
		public Call<List<TodoItem>> readAllItems();
		
		@GET("api/todos/{itemId}")
		public Call<TodoItem> readItem(@Path("itemId")long id);
		
		@PUT("api/todos/{itemId}")
		public Call<TodoItem> updateItem(@Path("itemId")long id, @Body TodoItem item);
		
		@DELETE("api/todos/{itemId}")
		public Call<Boolean> deleteItem(@Path("itemId")long id);
		
		@DELETE("api/todos")
		public Call<Boolean> deleteAllItems();
		
		@PUT("/api/users/auth")
		public Call<Boolean> authenticateUser(@Body User user);
	}
}
