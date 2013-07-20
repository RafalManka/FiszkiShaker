package pl.rafalmanka.fiszki.shaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

	public static final int DATABASE_VERSION = 76;
	public static final String DATABASE_NAME = "fiszki_shaker";
	public static final String COLUMN_WORD_ID = "id_word";
	public static final String COLUMN_TRANSLATION_ID = "id_translation";
	public static final String COLUMN_TRANSLATION = "translation";
	public static final String COLUMN_WORDSET_ID = "id_word_set";
	public static final String COLUMN_LANGUAGE_ID = "id_language";
	public static final String COLUMN_WORDSET_NAME = "wordset_name";
	public static final String COLUMN_WORD = "word";
	public static final String COLUMN_LANGUAGE = "language";

	public static final String TABLE_WORD = "word";
	public static final String TABLE_WORD_HAS_TRANSLATION = "word_has_translation";
	public static final String TABLE_WORDSET = "wordset";
	public static final String TABLE_WORD_HAS_SET = "word_has_wordset";
	public static final String TABLE_LANGUAGE = "language";

	private Context context;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(TAG, "constructing DatabaseHandler");
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreated");
		Log.i(TAG, "Creating new database");
		createDb(db);
		Log.i(TAG, "populating database from file manager");
		populateDbFromFileManager(db);
		// populateDbWithDummyWordset(db);
	}

	private void populateDbWithDummyWordset(SQLiteDatabase db) {

		ArrayList<Word> dummyWordset = new ArrayList<Word>();
		String language = "English";
		String nameOfSet = "Apartament";

		Word dummyWord_1 = new Word();
		dummyWord_1.setLanguageId(1);
		dummyWord_1.setLanguage(language);
		dummyWord_1.setSetName(nameOfSet);
		dummyWord_1.setWord("Living room");
		ArrayList<Word> dummyTranslations_1 = new ArrayList<Word>();
		String dummyTranslation_w1_1 = "Salon";
		String dummyTranslation_w1_2 = "Pokój goscinny";
		// dummyTranslations_1.add(new Word(dummyTranslation_w1_1));
		// dummyTranslations_1.add(new Word(dummyTranslation_w1_2));
		dummyWord_1.setTranslations(dummyTranslations_1);

		Word dummyWord_2 = new Word();
		dummyWord_2.setLanguageId(1);
		dummyWord_2.setLanguage(language);
		dummyWord_2.setSetName(nameOfSet);
		dummyWord_2.setWord("Study");
		ArrayList<Word> dummyTranslations_2 = new ArrayList<Word>();
		String dummyTranslation_w2_1 = "Gabinet";
		String dummyTranslation_w2_2 = "Pracownia";
		String dummyTranslation_w2_3 = "Czytelnia";
		// dummyTranslations_2.add(new Word(dummyTranslation_w2_1));
		// dummyTranslations_2.add(new Word(dummyTranslation_w2_2));
		// dummyTranslations_2.add(new Word(dummyTranslation_w2_3));
		dummyWord_2.setTranslations(dummyTranslations_2);

		dummyWordset.add(dummyWord_1);
		dummyWordset.add(dummyWord_2);

		populateDb(db, language, nameOfSet, dummyWordset);
	}

	private void populateDbFromFileManager(SQLiteDatabase db) {
		FileHandler filemanager = new FileHandler(context);
		Log.d(TAG, "putting all rowsets to List");
		List<String[]> list = filemanager.getAllRecords();

		ArrayList<Word> words = new ArrayList<Word>();
		for (String[] position : list) {
			Word word = new Word();
			word.setWord(position[0]);
			Word translation = new Word();
			translation.setWord(position[1]);
			ArrayList<Word> translations = new ArrayList<Word>();
			translations.add(translation);

			word.setTranslations(translations);

			words.add(word);
		}

		String mLanguage = "English";
		populateDb(db, mLanguage, Word.mNameOfSet, words);

	}

	public void createNewSet(ArrayList<Word> words) {
		Log.d(TAG, "entering method createNewSet");
		SQLiteDatabase db = this.getReadableDatabase();
		Log.d(TAG, "removing db");
		removeDb(db);
		Log.d(TAG, "creating db");
		createDb(db);
		Log.d(TAG, "populating db");
		populateDb(db, words.get(0).getLanguage(), words.get(0).getNameOfSet(),
				words);
		db.close();
	}

	private void populateDb(SQLiteDatabase db, String language,
			String nameOfSet, ArrayList<Word> words) {

		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_LANGUAGE, language);
		Log.d(TAG, "inserting value of language: " + language);
		long lastInsertedId_Language = db.insert(TABLE_LANGUAGE, null,
				contentValues);

		Log.d(TAG, "inserting value of wordset: " + nameOfSet);
		contentValues.clear();
		contentValues.put(COLUMN_WORDSET_NAME, nameOfSet);
		contentValues.put(COLUMN_LANGUAGE_ID, lastInsertedId_Language);
		long lastInsertedId_wordSet = db.insert(TABLE_WORDSET, null,
				contentValues);

		Log.d(TAG, "inserting words");
		for (Word word : words) {

			contentValues.clear();
			contentValues = new ContentValues();
			contentValues
					.put(COLUMN_WORD, TextUtils.htmlEncode(word.getWord()));
			long lastInsertedId_word = db.insert(TABLE_WORD, null,
					contentValues);

			ArrayList<Word> wordsList = word.getTranslations();
			for (Word description : wordsList) {

				contentValues.clear();
				contentValues.put(COLUMN_WORD,
						TextUtils.htmlEncode(description.getWord()));

				long lastInsertedId_translation = db.insert(TABLE_WORD, null,
						contentValues);

				contentValues.clear();
				contentValues.put(COLUMN_WORD_ID, lastInsertedId_word);
				contentValues.put(COLUMN_TRANSLATION_ID,
						lastInsertedId_translation);
				db.insert(TABLE_WORD_HAS_TRANSLATION, null, contentValues);

			}

			contentValues.clear();
			contentValues.put(COLUMN_WORD_ID, lastInsertedId_word);

			contentValues.put(COLUMN_WORDSET_ID, lastInsertedId_wordSet);
			db.insert(TABLE_WORD_HAS_SET, null, contentValues);

		}
		Log.d(TAG, "db successfully populated");
	}

	private void createDb(SQLiteDatabase db) {

		Log.d(TAG, "creating table word");
		String CREATE_TABLE_WORD = "CREATE TABLE " + TABLE_WORD + " ("
				+ COLUMN_WORD_ID + " INTEGER PRIMARY KEY, " + COLUMN_WORD
				+ " TEXT)";
		Log.d(TAG, "Executing: " + CREATE_TABLE_WORD);
		db.execSQL(CREATE_TABLE_WORD);

		Log.d(TAG, "creating table word_has_translation");
		String CREATE_TABLE_WORD_HAS_TRANSLATION = "CREATE TABLE "
				+ TABLE_WORD_HAS_TRANSLATION + " (" + COLUMN_WORD_ID
				+ " INTEGER REFERENCES " + TABLE_WORD + " (" + COLUMN_WORD_ID
				+ "), " + COLUMN_TRANSLATION_ID + " INTEGER REFERENCES "
				+ TABLE_WORD + " (" + COLUMN_WORD_ID + "))";
		Log.d(TAG, "Executing: " + CREATE_TABLE_WORD_HAS_TRANSLATION);
		db.execSQL(CREATE_TABLE_WORD_HAS_TRANSLATION);

		Log.d(TAG, "creating table language");
		String CREATE_TABLE_LANGUAGE = "CREATE TABLE " + TABLE_LANGUAGE + " ("
				+ COLUMN_LANGUAGE_ID + " INTEGER PRIMARY KEY, "
				+ COLUMN_LANGUAGE + " TEXT)";
		Log.d(TAG, "Executing: " + CREATE_TABLE_LANGUAGE);
		db.execSQL(CREATE_TABLE_LANGUAGE);

		Log.d(TAG, "creating table word_set");
		String CREATE_TABLE_WORD_SET = "CREATE TABLE " + TABLE_WORDSET + " ("
				+ COLUMN_WORDSET_ID + " INTEGER PRIMARY KEY, "
				+ COLUMN_WORDSET_NAME + " TEXT, " + COLUMN_LANGUAGE_ID
				+ " INTEGER REFERENCES " + TABLE_LANGUAGE + "("
				+ COLUMN_LANGUAGE_ID + "))";
		Log.d(TAG, "Executing: " + CREATE_TABLE_WORD_SET);
		db.execSQL(CREATE_TABLE_WORD_SET);

		Log.d(TAG, "creating table word_has_set");
		String CREATE_TABLE_WORD_HAS_SET = "CREATE TABLE " + TABLE_WORD_HAS_SET
				+ " (" + COLUMN_WORDSET_ID + " INTEGER REFERENCES "
				+ TABLE_WORDSET + "(" + COLUMN_WORDSET_ID + "), "
				+ COLUMN_WORD_ID + " INTEGER REFERENCES " + TABLE_WORD + " ("
				+ COLUMN_WORD_ID + "))";
		Log.d(TAG, "Executing: " + CREATE_TABLE_WORD_HAS_SET);
		db.execSQL(CREATE_TABLE_WORD_HAS_SET);

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgraded");

		removeDb(db);
		onCreate(db);
	}

	private void removeDb(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);
		Log.d(TAG, "DROP TABLE IF EXISTS " + TABLE_WORD);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD_HAS_TRANSLATION);
		Log.d(TAG, "DROP TABLE IF EXISTS " + TABLE_WORD_HAS_TRANSLATION);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE);
		Log.d(TAG, "DROP TABLE IF EXISTS " + TABLE_LANGUAGE);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDSET);
		Log.d(TAG, "DROP TABLE IF EXISTS " + TABLE_WORDSET);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD_HAS_SET);
		Log.d(TAG, "DROP TABLE IF EXISTS " + TABLE_WORD_HAS_SET);
	}

	public void addWord(Word word) {
		ArrayList<Word> wordInfo = new ArrayList<Word>();
		wordInfo.add(word);
		SQLiteDatabase db = getWritableDatabase();
		populateDb(db, word.getLanguage(), word.getNameOfSet(), wordInfo);
		db.close();
	}

	Word getWord(int id) {
		// TODO
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query("TABLE_DICTIONARY", new String[] {
				COLUMN_WORD_ID, COLUMN_WORD, COLUMN_TRANSLATION,
				COLUMN_LANGUAGE }, COLUMN_WORD_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		db.close();
		if (cursor != null)
			cursor.moveToFirst();

		// Word word = new Word(Integer.parseInt(cursor.getColumnName(0)),
		// cursor.getString(1), cursor.getString(2), cursor.getString(3));

		// return word;
		return null;
	}

	public Word getRandom() {
		Log.d(TAG, "starting method geeRandom");

		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Word> wordSet = getWordsFromDb(db);
		db.close();

		Random random = new Random();

		int rand = 0;

		rand = random.nextInt(wordSet.size());
		Word output = wordSet.get(rand);
		Log.d(TAG,
				"random word: " + output.getWord() + " - "
						+ output.getConcatenatedTranslations());
		return output;
	}

	public ArrayList<Word> getWordsFromDb(SQLiteDatabase db) {

		Log.d(TAG, "performing querey");

		String query = "SELECT " + " tw." + COLUMN_WORD + " AS \""
				+ COLUMN_WORD + "\", " + " tht." + COLUMN_TRANSLATION_ID
				+ " AS \"" + COLUMN_TRANSLATION_ID + "\", " + " tt."
				+ COLUMN_WORD + " AS \"" + COLUMN_TRANSLATION + "\", "
				+ " whs." + COLUMN_WORD_ID + " AS \"word_has_set_word_id\", "
				+ " tws." + COLUMN_WORDSET_NAME + " AS \""
				+ COLUMN_WORDSET_NAME + "\", " + " l." + COLUMN_LANGUAGE
				+ " AS \"" + COLUMN_LANGUAGE + "\" " + " FROM " + TABLE_WORD
				+ " as tw " + "INNER JOIN " + TABLE_WORD_HAS_TRANSLATION
				+ " AS tht ON tw." + COLUMN_WORD_ID + "=tht." + COLUMN_WORD_ID
				+ " " + "INNER JOIN " + TABLE_WORD + " AS tt ON tht."
				+ COLUMN_TRANSLATION_ID + "=tt." + COLUMN_WORD_ID + " "
				+ "INNER JOIN " + TABLE_WORD_HAS_SET + " AS whs ON whs."
				+ COLUMN_WORD_ID + "=tw." + COLUMN_WORD_ID + " "
				+ "INNER JOIN " + TABLE_WORDSET + " AS tws ON tws."
				+ COLUMN_WORDSET_ID + "=whs." + COLUMN_WORDSET_ID
				+ " INNER JOIN " + TABLE_LANGUAGE + " AS l ON l."
				+ COLUMN_LANGUAGE_ID + "=tws." + COLUMN_LANGUAGE_ID + " ";
		// + " ORDER BY RANDOM() LIMIT 1";

		// String query="SELECT * FROM word";
		Log.d(TAG, query);
		Cursor c = db.rawQuery(query, null);

		c.moveToFirst();
		ArrayList<Word> wordSet = new ArrayList<Word>();
		String wordTemp = "";

		Word word = null;
		ArrayList<Word> translationSet = new ArrayList<Word>();
		String translation;
		do {

			if (!wordTemp.equals(c.getString(c.getColumnIndex(COLUMN_WORD)))) {
				if (word != null) {
					wordSet.add(word);
				}

				// Log.d(TAG, "loop count = " + counter++);
				wordTemp = c.getString(c.getColumnIndex(COLUMN_WORD));

				word = new Word();
				word.setWord(wordTemp);
				word.setLanguage(c.getString(c.getColumnIndex(COLUMN_LANGUAGE)));
				word.setSetName(c.getString(c
						.getColumnIndex(COLUMN_WORDSET_NAME)));
				translationSet = new ArrayList<Word>();
				translation = c.getString(c.getColumnIndex(COLUMN_TRANSLATION));
				Word tempWord = new Word();
				tempWord.setWord(translation);
				translationSet.add(tempWord);
				word.setTranslations(translationSet);

			} else {

				translation = c.getString(c.getColumnIndex(COLUMN_TRANSLATION));
				Word tempWord = new Word();
				tempWord.setWord(translation);
				translationSet.add(tempWord);
				word.setTranslations(translationSet);

			}

		} while (c.moveToNext());
		wordSet.add(word);

		return wordSet;
	}

	@Override
	public synchronized void close() {
		super.close();
	}

	private void createNewSet(JSONArray wordsJSONData) {
		Log.d(TAG, "createNewSet");
		Log.d(TAG, "mWordsData: " + wordsJSONData.toString());
		SQLiteDatabase db = this.getReadableDatabase();
		createDb(db);
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

				// addWord(new Word(value, transtalion, language));
			} catch (JSONException e) {
				Log.e(TAG, "Error: ", e);
			}

		}

	}

	public String getValueOfCurrentSet(String tableName, String columnName) {
		Log.d(TAG, "entering getValueOfCurrentSet method");
		Log.d(TAG, "getting value of column: " + columnName);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor languageCursor = db.query(tableName,
				new String[] { columnName }, null, null, null, null, "1");
		String valFetched = "";
		if (languageCursor.moveToFirst())
			valFetched = languageCursor.getString(languageCursor
					.getColumnIndex(columnName));
		Log.d(TAG, "fetchet value: " + valFetched);
		languageCursor.close();
		return valFetched;
	}
	
	public static ArrayList<Word> addWordToList(ArrayList<Word> arrayList, Word word){		
		arrayList.add(word);		
		return arrayList;
	}
}
