package pl.rafalmanka.fiszki.shaker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	public static final String TAG = DatabaseHandler.class.getSimpleName();
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 29;

	// Database Name
	private static final String DATABASE_NAME = "fiszki_shaker";

	// Contacts table name
	private static final String TABLE_DICTIONARY = "dictionary";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_WORD = "word";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_LANGUAGE = "language";
	private Context context;

	// private SQLiteDatabase db;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(TAG, "DatabaseHandler constructor");
		this.context = context;
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating clean table
		Log.d(TAG, "onCreated");
		Log.d(TAG, "Creating new table");
		createNewTable(db);

		Log.d(TAG, "creating instance of FileManager");

		// importing dictionaries from files to database
		FileHandler filemanager = new FileHandler(context);
		Log.d(TAG, "putting all rowsets to List");
		List<String[]> list = filemanager.getAllRecords();

		for (String[] record : list) {
			Log.d(TAG, "creating query");
			String insertStatement = "INSERT INTO " + TABLE_DICTIONARY + " ("
					+ KEY_WORD + "," + KEY_DESCRIPTION + "," + KEY_LANGUAGE
					+ ") VALUES ('" + TextUtils.htmlEncode(record[0]) + "','"
					+ TextUtils.htmlEncode(record[1]) + "','" + record[2]
					+ "');";
			Log.d(TAG, "executing statement: " + insertStatement);
			db.execSQL(insertStatement);
		}
		Log.d(TAG, "db successfully populated");

	}

	private void createNewTable(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_DICTIONARY + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_WORD + " TEXT,"
				+ KEY_DESCRIPTION + " TEXT," + KEY_LANGUAGE + " TEXT" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgraded");
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DICTIONARY);
		Log.d(TAG, "DROP TABLE IF EXISTS " + TABLE_DICTIONARY);
		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	void addWord(Word word) {
		Log.d(TAG, "addWord() function");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		Log.d(TAG, "putting word: " + word.getWord());
		values.put(KEY_WORD, word.getWord());
		Log.d(TAG, "putting desc: " + word.getDescription());
		values.put(KEY_DESCRIPTION, word.getDescription());
		Log.d(TAG, "putting language: " + word.getLanguage());
		values.put(KEY_LANGUAGE, word.getLanguage());
		Log.d(TAG, "inserting row into dictionary (word: " + word.getWord()
				+ ", description: " + word.getDescription() + ", language: "
				+ word.getLanguage() + ")");
		db.insert(TABLE_DICTIONARY, null, values);
		db.close();
	}

	// Getting single contact
	Word getWord(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_DICTIONARY, new String[] { KEY_ID,
				KEY_WORD, KEY_DESCRIPTION, KEY_LANGUAGE }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		db.close();
		if (cursor != null)
			cursor.moveToFirst();

		Word word = new Word(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3));
		// return contact
		return word;
	}

	// Getting All Contacts
	public List<Word> getWordsFromCSV() {
		List<Word> wordList = new ArrayList<Word>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_DICTIONARY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Word contact = new Word();
				contact.setID(Integer.parseInt(cursor.getString(0)));
				contact.setWord(cursor.getString(1));
				contact.setDescription(cursor.getString(2));
				contact.setLanguage(cursor.getString(3));
				// Adding contact to list
				wordList.add(contact);
			} while (cursor.moveToNext());
		}

		cursor.close();
		// return contact list
		return wordList;
	}

	public Word getRandom() {
		Log.d(TAG, "getRandom");
		SQLiteDatabase db = this.getReadableDatabase();
		Log.d(TAG, "performing querey");
		Cursor cursor = db.query(TABLE_DICTIONARY, null, KEY_LANGUAGE + " =? ",
				new String[] { getCurrentLanguageID() }, null, null,
				"RANDOM()", null);
		if (cursor != null)
			cursor.moveToFirst();

		Word word = new Word(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3));
		return word;
	}

	@Override
	public synchronized void close() {
		super.close();
	}

	public void createNewSet(JSONArray wordsJSONData) {
		Log.d(TAG, "createNewSet");
		Log.d(TAG, "mWordsData: " + wordsJSONData.toString());
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DICTIONARY);
		Log.d(TAG, "DROP TABLE IF EXISTS " + TABLE_DICTIONARY);
		createNewTable(db);
		Log.d(TAG, "creating Word objects");
		for (int i = 0; i < wordsJSONData.length(); i++) {
			JSONObject jsonWord;
			try {
				jsonWord = wordsJSONData.getJSONObject(i);

				String value = jsonWord.getString("value");
				Log.d(TAG, "value: " + value);
				String transtalion = jsonWord.getString("translation");
				Log.d(TAG, "transtalion: " + transtalion);
				String language = jsonWord.getString("language_id");
				Log.d(TAG, "language: " + language);

				addWord(new Word(value, transtalion, language));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}
	

	public String getCurrentLanguageID() {
		SQLiteDatabase db = this.getReadableDatabase();
		Log.d(TAG, "get current language");
		Cursor languageCursor = db.query(TABLE_DICTIONARY,
				new String[] { "language" }, null, null, null, null, "1");
		String languageID = "";
		if (languageCursor.moveToFirst())
			languageID = languageCursor.getString(languageCursor
					.getColumnIndex("language"));
		Log.d(TAG, "languageID: " + languageID);
		languageCursor.close();
		return languageID;
	}
}
