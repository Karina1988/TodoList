package de.karina.todolist;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ListToStringTypeConverters {
	
	
	@TypeConverter
	public List<String> stringToList(String concatenatedStrings) {
		if (concatenatedStrings == null) {
			return Collections.emptyList();
		}
		Gson gson = new Gson();
		Type listType = new TypeToken<List<String>>() {}.getType();
		
		return gson.fromJson(concatenatedStrings, listType);
	}
	
	@TypeConverter
	public String ListToString(List<String> strings) {
		Gson gson = new Gson();
		return gson.toJson(strings);
	}
}
