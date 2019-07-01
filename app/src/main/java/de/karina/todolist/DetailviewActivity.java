package de.karina.todolist;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import de.karina.todolist.model.ITodoItemCRUDOperations;
import de.karina.todolist.model.TodoItem;
import de.karina.todolist.model.tasks.DeleteItemTask;
import de.karina.todolist.model.tasks.UpdateItemTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DetailviewActivity extends AppCompatActivity {
	
	private FloatingActionButton saveButton;
	private Button deleteButton;
	private EditText todoTitle;
	private EditText todoDescription;
	private CheckBox todoDone;
	private CheckBox todoFavourite;
	private EditText todoDate;
	private EditText todoTime;
	private ViewGroup contactList;
	
	public static final String ARG_ITEM_ID = "itemId";
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_EDITED = 2;
	public static final int STATUS_DELETED = 3;
	public static final int CALL_CONTACT_PICKER = 4;
	
	private static final String LOGGING_TAG = DetailviewActivity.class.getSimpleName();
	
	private ArrayAdapter<String> contactsArrayAdapter;
	
	private ITodoItemCRUDOperations crudOperations;
	private TodoItem item;
	
	private List<String> contacts = new ArrayList<>();
	
	final Calendar myCalendar = Calendar.getInstance();
	
	private MenuItem saveButtoninOptions;
	
	
	String dateFormat = "dd.MM.yyyy";
	String timeFormat = "HH:mm";
	DateFormat df = new SimpleDateFormat(dateFormat);
	DateFormat tf = new SimpleDateFormat(timeFormat);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailview);
		
		crudOperations = ((TodoItemApplication) getApplication()).getCRUDOperations();
		
		saveButton = findViewById(R.id.saveButton);
		saveButton.setOnClickListener((view) -> {
			saveItem();
		});
		
		deleteButton = findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener((view) -> {
			openDeleteAlertDialog();
		});
		
		contactList = (ListView) findViewById(R.id.contactList);
		
		contactsArrayAdapter = createContactListAdapter();
		((ListView) contactList).setAdapter(contactsArrayAdapter);
		
		todoTitle = findViewById(R.id.todoTitle);
		todoDescription = findViewById(R.id.todoDescription);
		todoDone = findViewById(R.id.todoDone);
		todoFavourite = findViewById(R.id.todoFavorite);
		todoDate = findViewById(R.id.todoDate);
		todoTime = findViewById(R.id.todoTime);
		
		//datepicker
		DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
			                      int dayOfMonth) {
				// TODO Auto-generated method stub
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				setTodoDateToToday();
			}
		};
		
		//timepicker
		TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				myCalendar.set(Calendar.MINUTE, minute);
				setTodoTimeToToday();
			}
		};
		
		todoDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DatePickerDialog(DetailviewActivity.this, date, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		todoTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new TimePickerDialog(DetailviewActivity.this, time, 
						myCalendar.get(Calendar.HOUR_OF_DAY), 
						myCalendar.get(Calendar.MINUTE), true).show();
			}
		});
		
		long itemId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
		if (itemId != -1) {
			new Thread(() -> {
				this.item = crudOperations.readItem(itemId);
				if (this.item != null) {
					runOnUiThread(() -> {
						this.todoTitle.setText(this.item.getName());
						this.todoDescription.setText(this.item.getDescription());
						this.todoDone.setChecked(this.item.isDone());
						this.todoFavourite.setChecked(this.item.isFavourite());
						
						String todoDateAsString = df.format(new Date(this.item.getExpiry()));
						todoDate.setText(todoDateAsString);
						
						String todoTimeAsString = tf.format(new Date(this.item.getExpiry()));
						todoTime.setText(todoTimeAsString);
						
						if (this.item.getContacts() != null) {
							this.contactsArrayAdapter.addAll(this.item.getContacts());
						}
					});
				}
			}).start();
		} else {
			new Thread(() -> {
				runOnUiThread(() -> {
					setTodoDateToToday();
					setTodoTimeToToday();
				});
			}).start();
		}
	}
	
	private ArrayAdapter<String> createContactListAdapter() {
		return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts) {
			@NonNull
			@Override
			public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
				View contactView = convertView;
				if (contactView == null) {
					contactView = getLayoutInflater().inflate(R.layout.activity_detailview_contactitem, null);
				}
				TextView contactNameView = contactView.findViewById(R.id.contact);
				ImageButton contactDeleteButton = contactView.findViewById(R.id.deleteContactButton);
				ImageButton contactSMSButton = contactView.findViewById(R.id.contactSMS);
				ImageButton contactMailButton = contactView.findViewById(R.id.contactMail);
				
				String contactId = getItem(position);
				
				contactNameView.setText(getContactNameFromId(contactId));
				
				contactDeleteButton.setOnClickListener((view) -> {
					deleteContactFromItem(contactId);
				});
				contactSMSButton.setOnClickListener((view) -> {
					sendSMSToContact(contactId);
				});
				contactMailButton.setOnClickListener((view) -> {
					sendMailToContact(contactId);
				});
				
				return contactView;
			}
		};
	}
	
	private void setTodoDateToToday() {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
		todoDate.setText(sdf.format(myCalendar.getTime()));
	}
	
	private void setTodoTimeToToday() {
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.US);
		todoTime.setText(sdf.format(myCalendar.getTime()));
	}
	
	private void saveItem() {
		Intent returnIntent = new Intent();
		
		boolean create = false;
		if (this.item == null) {
			this.item = new TodoItem();
			create = true;
		}
		returnIntent.putExtra(ARG_ITEM_ID, item.getId());
		
		this.item.setName(todoTitle.getText().toString());
		this.item.setDescription(todoDescription.getText().toString());
		this.item.setDone(this.todoDone.isChecked());
		this.item.setFavourite(this.todoFavourite.isChecked());
		this.item.setExpiry(myCalendar.getTimeInMillis());
		this.item.setContacts(contacts);
		
		if (create) {
			new Thread(() -> {
				item = crudOperations.createItem(item);
				returnIntent.putExtra(ARG_ITEM_ID, item.getId());
				setResult(STATUS_CREATED, returnIntent);
				finish();
			}).start();
		} else {
			new UpdateItemTask(crudOperations).run(this.item, updated -> {
				returnIntent.putExtra(ARG_ITEM_ID, item.getId());
				setResult(STATUS_EDITED, returnIntent);
				finish();
			});
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == CALL_CONTACT_PICKER && resultCode == Activity.RESULT_OK) {
			Log.i(getClass().getSimpleName(), "got intent from contact picker: " + data);
			showContactDetails(data.getData());
		}
	}
	
	private void openDeleteAlertDialog() {
		new AlertDialog.Builder(this)
			.setTitle(R.string.deleteItem)
			.setMessage(R.string.sureDeleteItem)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					deleteItem();
				}
			})
			.setNegativeButton(android.R.string.no, null)
			.setIcon(android.R.drawable.ic_delete)
			.show();
	}
	
	private void deleteItem() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(ARG_ITEM_ID, item.getId());
		setResult(STATUS_DELETED, returnIntent);
		finish();
	}
	
	private void deleteContactFromItem(String position) {
		Toast.makeText(DetailviewActivity.this, getString(R.string.contact) + " " + getContactNameFromId(position) + " " + getString(R.string.deleted), Toast.LENGTH_SHORT).show();
		contactsArrayAdapter.remove(position);
	}
	
	private void sendSMSToContact(String contactId) {
		if (verifySendSMSPermission()) {
			Uri uri = Uri.parse("smsto:" + getContactPhoneNumberFromId(contactId));
			Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
			smsIntent.putExtra("sms_body", this.item.getName() + this.item.getDescription());
			startActivity(smsIntent);
		}
	}
	
	private void sendMailToContact(String contactId) {
		Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
		mailIntent.setData(Uri.parse("mailto:" + getContactMailFromId(contactId)));
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, this.item.getName());
		mailIntent.putExtra(Intent.EXTRA_TEXT, this.item.getDescription());
		startActivity(mailIntent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detailview_menu, menu);
		saveButtoninOptions = menu.findItem(R.id.saveItem);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.saveItem) {
			saveItem();
			return true;
		} else if (item.getItemId() == R.id.showContacts) {
			showContacts();
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}
	
	private void showContacts() {
		Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(contactsIntent, CALL_CONTACT_PICKER);
	}
	
	private Uri getContactUriFromId(String contactId) {
		Uri myPhoneUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, "" + contactId);
		return myPhoneUri;
	}
	
	private String getContactNameFromId(String contactId) {
		Cursor phoneCursor = managedQuery(getContactUriFromId(contactId), null, null, null, null);
		
		if (phoneCursor.moveToFirst()) {
			String contactName = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			return contactName;
		}
		return null;
	}
	private String getContactPhoneNumberFromId(String contactId) {
		Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId}, null);
		
		while (phoneCursor.moveToNext()) {
			String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			int phoneNumberType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2));
			
			if (phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
				Log.i(LOGGING_TAG, "phone number " + phoneNumber);
				return phoneNumber;
			}
		}
		return null;
	}
	private String getContactMailFromId(String contactId) {
		Cursor mailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[]{contactId}, null);
		
		if (mailCursor.moveToFirst()) {
			String contactMail = mailCursor.getString(mailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
			return contactMail;
		}
		return null;
	}
	
	private void showContactDetails(Uri contactUri) {
		Log.i(LOGGING_TAG, "got contact uri: " + contactUri);
		Cursor contactsCursor = getContentResolver().query(contactUri, null, null, null, null);
		if (contactsCursor.moveToFirst()) {
			String contactName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String contactId = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
			Log.i(LOGGING_TAG, "contact name " + contactName);
			Log.i(LOGGING_TAG, "contact id" + contactId);
			
			if (!contactExistsForItem(contactId)) {
				contactsArrayAdapter.add(contactId);
			}
			
			if (verifyReadContactsPermission()) {
				
				Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId}, null);
				
				while (phoneCursor.moveToNext()) {
					String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					int phoneNumberType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2));
					
					Log.i(LOGGING_TAG, "phone number " + phoneNumber);
					Log.i(LOGGING_TAG, "phone number type " + phoneNumberType);
					
					if (phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
						Log.i(LOGGING_TAG, "found mobile phone number!: " + phoneNumber);
					}
				}
				
				Cursor mailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[]{contactId}, null);
				
				while (mailCursor.moveToNext()) {
					String mail = mailCursor.getString(mailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
					
					Log.i(LOGGING_TAG, "mail " + mail);
				}
			}
		}
	}
	
	private boolean verifyReadContactsPermission() {
		int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
		if (hasReadContactsPermission == PackageManager.PERMISSION_GRANTED) {
			return true;
		} else {
			requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 4);
			return false;
		}
	}
	
	private boolean verifySendSMSPermission() {
		int hasSendSMSPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
		if (hasSendSMSPermission == PackageManager.PERMISSION_GRANTED) {
			return true;
		} else {
			requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 4);
			return false;
		}
	}
	
	private boolean contactExistsForItem(String contactId) {
		for(int i=0; i<contactsArrayAdapter.getCount(); i++){
			String contact = contactsArrayAdapter.getItem(i);
			if (((contact).equals(contactId))) {
				return true;
			}
		}
		return false;
	}
}
