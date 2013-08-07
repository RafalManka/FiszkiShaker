package pl.rafalmanka.fiszki.shaker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.rafalmanka.fiszki.shaker.R;
import pl.rafalmanka.fiszki.shaker.view.SettingsActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String TAG = DatabaseHandler.class.getSimpleName();

    public static final int DATABASE_VERSION = 128;
    public static final String DATABASE_NAME = "fiszki_shaker";
    public static final String COLUMN_WORD_ID = "id_word";
    public static final String COLUMN_TRANSLATION_ID = "id_translation";
    public static final String COLUMN_TRANSLATION = "translation";
    public static final String COLUMN_WORDSET_ID = "id_word_set";
    public static final String COLUMN_LANGUAGE_ID = "id_language";
    public static final String COLUMN_WORDSET_NAME = "wordset_name";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_LANGUAGE_SHORT = "language";
    public static final String COLUMN_LANGUAGE_LONG = "language_long";
    public static final String TABLE_WORD = "word";
    public static final String TABLE_WORD_HAS_TRANSLATION = "word_has_translation";
    public static final String TABLE_WORDSET = "wordset";
    public static final String TABLE_WORD_HAS_SET = "word_has_wordset";
    public static final String TABLE_LANGUAGE = "language";
    public static final String COLUMN_WORD_STATUS = "word_status";
    
    private static String mLanguage = "en";

    private Context mContext;

	


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "constructing DatabaseHandler");
        this.mContext = context;
    }

    private void createDb(SQLiteDatabase db) {

        Log.d(TAG, "creating table word");
        String CREATE_TABLE_WORD = "CREATE TABLE " + TABLE_WORD + " ("
                + COLUMN_WORD_ID + " INTEGER PRIMARY KEY, " + COLUMN_WORD
                + " TEXT, "+COLUMN_WORD_STATUS+" TEXT)";
        Log.d(TAG, "Executing: " + CREATE_TABLE_WORD);
        db.execSQL(CREATE_TABLE_WORD);

        Log.d(TAG, "creating table word_has_translation");
        String CREATE_TABLE_WORD_HAS_TRANSLATION = "CREATE TABLE "
                + TABLE_WORD_HAS_TRANSLATION + "  (" + COLUMN_WORD_ID
                + " INTEGER REFERENCES " + TABLE_WORD + " (" + COLUMN_WORD_ID
                + "), " + COLUMN_TRANSLATION_ID + " INTEGER REFERENCES "
                + TABLE_WORD + " (" + COLUMN_WORD_ID + ")   ) ";
        Log.d(TAG, "Executing: " + CREATE_TABLE_WORD_HAS_TRANSLATION);
        db.execSQL(CREATE_TABLE_WORD_HAS_TRANSLATION);

        Log.d(TAG, "creating table language");
        String CREATE_TABLE_LANGUAGE = "CREATE TABLE " + TABLE_LANGUAGE + " ("
                + COLUMN_LANGUAGE_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_LANGUAGE_SHORT + " TEXT, " 
                + COLUMN_LANGUAGE_LONG + " TEXT)";
        Log.d(TAG, "Executing: " + CREATE_TABLE_LANGUAGE);
        db.execSQL(CREATE_TABLE_LANGUAGE);

        Log.d(TAG, "creating table word_set");
        String CREATE_TABLE_WORD_SET = "CREATE TABLE " + TABLE_WORDSET + " ("
                + COLUMN_WORDSET_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_WORDSET_NAME + " TEXT, " + COLUMN_LANGUAGE_ID
                + " INTEGER REFERENCES " + TABLE_LANGUAGE + "("
                + COLUMN_LANGUAGE_ID + ")   )";
        Log.d(TAG, "Executing: " + CREATE_TABLE_WORD_SET);
        db.execSQL(CREATE_TABLE_WORD_SET);

        Log.d(TAG, "creating table word_has_set");
        String CREATE_TABLE_WORD_HAS_SET = "CREATE TABLE " + TABLE_WORD_HAS_SET
                + " (" + COLUMN_WORDSET_ID + " INTEGER REFERENCES "
                + TABLE_WORDSET + "(" + COLUMN_WORDSET_ID
                + ")  , " + COLUMN_WORD_ID
                + " INTEGER REFERENCES " + TABLE_WORD + " (" + COLUMN_WORD_ID
                + ")   )";
        Log.d(TAG, "Executing: " + CREATE_TABLE_WORD_HAS_SET);
        db.execSQL(CREATE_TABLE_WORD_HAS_SET);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreated");
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        Editor editor = sharedPreferences.edit();
        editor.putString(SettingsActivity.CURRENT_WORDSET,
                SettingsActivity.DEFAULT_WORDSET);
        editor.commit();

        Log.i(TAG, "Creating new database");
        createDb(db);
        Log.i(TAG, "populating database from file manager");
        populateDbFromFileManager(db);
        // populateDbWithDummyWordset(db);
    }

    private void populateDbWithDummyWordset(SQLiteDatabase db) {

        ArrayList<Word> dummyWordset = new ArrayList<Word>();
        String nameOfSet = "Apartament";

        Word dummyWord_1 = new Word();
        dummyWord_1.setLanguageId(1);
        dummyWord_1.setLanguage(mLanguage);
        dummyWord_1.setSetName(nameOfSet);
        dummyWord_1.setWord("Living room");
        ArrayList<Word> dummyTranslations_1 = new ArrayList<Word>();
        String dummyTranslation_w1_1 = "Salon";
        String dummyTranslation_w1_2 = "Pokój goscinny";
        dummyTranslations_1.add(new Word(dummyTranslation_w1_1));
        dummyTranslations_1.add(new Word(dummyTranslation_w1_2));
        dummyWord_1.setTranslations(dummyTranslations_1);

        Word dummyWord_2 = new Word();
        dummyWord_2.setLanguageId(1);
        dummyWord_2.setLanguage(mLanguage);
        dummyWord_2.setSetName(nameOfSet);
        dummyWord_2.setWord("Study");
        ArrayList<Word> dummyTranslations_2 = new ArrayList<Word>();
        String dummyTranslation_w2_1 = "Gabinet";
        String dummyTranslation_w2_2 = "Pracownia";
        String dummyTranslation_w2_3 = "Czytelnia";
        dummyTranslations_2.add(new Word(dummyTranslation_w2_1));
        dummyTranslations_2.add(new Word(dummyTranslation_w2_2));
        dummyTranslations_2.add(new Word(dummyTranslation_w2_3));
        dummyWord_2.setTranslations(dummyTranslations_2);

        dummyWordset.add(dummyWord_1);
        dummyWordset.add(dummyWord_2);

        insertIntoDb(db, mLanguage, nameOfSet, dummyWordset);
    }

    private void populateDbFromFileManager(SQLiteDatabase db) {
        FileHandler filemanager = new FileHandler(mContext);
        Log.d(TAG, "putting all rowsets to List");
        List<String[]> list = filemanager.getAllRecords();
        Log.d(TAG, "list size: " + list.size());
        ArrayList<Word> words = new ArrayList<Word>();
        for (String[] position : list) {
            Word word = new Word();
            word.setWord(position[0]);
            word.setSetName(SettingsActivity.DEFAULT_WORDSET);
            word.setStatus(SettingsActivity.DEFAULT_WORD_STATUS);
            Word translation = new Word();
            translation.setWord(position[1]);
            ArrayList<Word> translations = new ArrayList<Word>();
            translations.add(translation);
            word.setTranslations(translations);
            words.add(word);
        }


        insertIntoDb(db, mLanguage, SettingsActivity.DEFAULT_WORDSET, words);

    }

    public void addNewSet(String language, String wordset, ArrayList<Word> words) {

        // check if set exists
        Log.d(TAG, "entering method createNewSet");
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, "populating db");
        insertIntoDb(db, language , wordset, words);
        db.close();
    }

    private void insertIntoDb(SQLiteDatabase db, String language,
                              String nameOfSet, ArrayList<Word> words) {   	
    	
        long lastInsertedId_Language = insertOrfetchLanguageID(db, language);
        long lastInsertedId_wordSet = insertOrFetchWordset(db, nameOfSet,
                lastInsertedId_Language);
        insertOrFetchWordsId(db, words, lastInsertedId_wordSet);
        Log.d(TAG, "db successfully populated");
    }

    private long insertOrfetchLanguageID(SQLiteDatabase db, String language) {
        Log.d(TAG, "insertOrfetchLanguageID()");        
        String query = "SELECT * FROM "+TABLE_LANGUAGE+" WHERE "+COLUMN_LANGUAGE_SHORT+" = '"+language+"'";
        Log.d(TAG, "insertOrfetchLanguageID() query: "+query);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0 ){
        	Log.d(TAG, "insertOrfetchLanguageID() cursor.getColumnIndex(COLUMN_LANGUAGE_ID): "+cursor.getString(cursor.getColumnIndex(COLUMN_LANGUAGE_ID)));
        	long language_id = cursor.getLong(cursor.getColumnIndex(COLUMN_LANGUAGE_ID));
        	Log.d(TAG, "insertOrfetchLanguageID language_id: "+language_id);
        	cursor.close();
        	return language_id;
        } else{
        	ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_LANGUAGE_SHORT, language);
            Log.d(TAG, "insertLanguageIntoDb() inserting language: " + language);
            long languageId = db.insert(TABLE_LANGUAGE, null, contentValues);
            Log.d(TAG, "insertLanguageIntoDb() languageId: " + languageId);
            return languageId;
        }        
    }

    private long insertOrFetchWordset(SQLiteDatabase db, String nameOfSet,
                                     long lastInsertedId_Language) {
        Log.d(TAG, "insertOrFetchWordset()");
        String query = "SELECT * FROM " + TABLE_WORDSET + " WHERE "+COLUMN_WORDSET_NAME+" = '"+nameOfSet+"'";
        Log.d(TAG, query);
        Cursor crsr = db.rawQuery(query, null);
        Log.d(TAG, "crsr.getCount(): "+crsr.getCount());
        crsr.moveToFirst();
        Log.d(TAG, "crsr.getCount(): "+crsr.getCount());
        
        long lastInsertedId_wordSet = -1;
        if (crsr.getCount()==0) {
            Log.d(TAG, "inserting value of wordset: " + nameOfSet+" lastInsertedId_Language: "+lastInsertedId_Language);
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_WORDSET_NAME, nameOfSet);
            contentValues.put(COLUMN_LANGUAGE_ID, lastInsertedId_Language);
            lastInsertedId_wordSet = db.insert(TABLE_WORDSET, null,
                    contentValues);
            Log.d(TAG, "wordset didnt exist before, creatd now with id: "
                    + lastInsertedId_wordSet);
        } else {
            lastInsertedId_wordSet = crsr.getLong(crsr.getColumnIndex(COLUMN_WORDSET_ID));
            Log.d(TAG, "wordset exists in database under index: "+ lastInsertedId_wordSet);
        }
        crsr.close();
        return lastInsertedId_wordSet;
    }

    private void insertOrFetchWordsId(SQLiteDatabase db, ArrayList<Word> words,
                                   long lastInsertedId_wordSet) {
    	
        ContentValues contentValues = new ContentValues();
        for (Word word : words) {

        	String query ="SELECT * FROM "+ TABLE_WORD +" WHERE "+COLUMN_WORD+" = '"+
        	TextUtils.htmlEncode(word.getWord())+"'";
        	
        	Cursor cursor = db.rawQuery(query, null);
        	cursor.moveToFirst();
        	long lastInsertedId_word=-1;
        	if(cursor.getCount()>0){
        		lastInsertedId_word = cursor.getLong(cursor.getColumnIndex(COLUMN_WORD_ID));
        	} else {
                contentValues.clear();
                contentValues = new ContentValues();
                contentValues
                        .put(COLUMN_WORD, TextUtils.htmlEncode(word.getWord()));
                contentValues.put(COLUMN_WORD_STATUS, word.getStatus());
                lastInsertedId_word = db.insert(TABLE_WORD, null,
                        contentValues);
        	}
        	cursor.close();


            ArrayList<Word> wordsList = word.getTranslations();
            for (Word translation : wordsList) {

            	query = "SELECT * FROM "+TABLE_WORD+" WHERE "+COLUMN_WORD+" = '"+TextUtils.htmlEncode(translation.getWord())+"'";
                cursor = db.rawQuery(query, null);
                cursor.moveToFirst();                
                long lastInsertedId_translation=-1;
                if(cursor.getCount()>0){
                	lastInsertedId_translation = cursor.getLong(cursor.getColumnIndex(COLUMN_WORD_ID));
                	Log.d(TAG,"cursor.getCount()>0, lastInsertedId_translation: "+lastInsertedId_translation);
                } else {
                	contentValues.clear();
                    contentValues.put(COLUMN_WORD,
                            TextUtils.htmlEncode(translation.getWord()));
                    lastInsertedId_translation = db.insert(TABLE_WORD, null,
                            contentValues);
                    Log.d(TAG,"cursor.getCount()=0, lastInsertedId_translation: "+lastInsertedId_translation);
                }
            	cursor.close();
            	query = "SELECT * FROM "+TABLE_WORD_HAS_TRANSLATION+" WHERE "+COLUMN_WORD_ID+" = "+lastInsertedId_word+" AND " +
            			COLUMN_TRANSLATION_ID+" = "+lastInsertedId_translation+"";
            	
            	cursor = db.rawQuery(query, null);
            	cursor.moveToFirst();
            	Log.d(TAG, "TABLE_WORD_HAS_TRANSLATION cursor.getCount(): "+cursor.getCount());
            	if(cursor.getCount()==0){
            		contentValues.clear();
                    contentValues.put(COLUMN_WORD_ID, lastInsertedId_word);
                    contentValues.put(COLUMN_TRANSLATION_ID,
                            lastInsertedId_translation);
                    db.insert(TABLE_WORD_HAS_TRANSLATION, null, contentValues);
            	}
            	cursor.close();        
            }

            query ="SELECT * FROM " + TABLE_WORD_HAS_SET + "  WHERE "+COLUMN_WORD_ID
            		+"="+lastInsertedId_word+" AND "+COLUMN_WORDSET_ID+"="+lastInsertedId_wordSet+"";
            Log.d(TAG, "query1:"+query);
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            Log.d(TAG, "cursor.getCount()1:"+cursor.getCount());
            if(cursor.getCount()==0){
            	contentValues.clear();
                contentValues.put(COLUMN_WORD_ID, lastInsertedId_word);

                contentValues.put(COLUMN_WORDSET_ID, lastInsertedId_wordSet);
                db.insert(TABLE_WORD_HAS_SET, null, contentValues);
            }
            cursor.close();   

        }
    }



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
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        String currentWordset = sharedPrefs.getString(
                SettingsActivity.CURRENT_WORDSET,
                SettingsActivity.DEFAULT_WORDSET);

        Log.d(TAG,"language: "+word.getLanguage());
        insertIntoDb(db, word.getLanguage(), currentWordset, wordInfo);
        db.close();
    }

    public ArrayList<Word> getWordsFromDb(SQLiteDatabase db) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        Log.d(TAG, "performing querey");
        Log.d(TAG,
                "shared preference: "
                        + sharedPreferences.getString(
                        SettingsActivity.CURRENT_WORDSET,
                        SettingsActivity.DEFAULT_WORDSET));

        String query = "SELECT " + " tw."
                + COLUMN_WORD
                + " AS \""
                + COLUMN_WORD
                + "\", "
                + " tht."
                + COLUMN_TRANSLATION_ID
                + " AS \""
                + COLUMN_TRANSLATION_ID
                + "\", "
                + " tt."
                + COLUMN_WORD
                + " AS \""
                + COLUMN_TRANSLATION
                + "\", "
                + " whs."
                + COLUMN_WORD_ID
                + " AS \"word_has_set_word_id\", "
                + " tws."
                + COLUMN_WORDSET_NAME
                + " AS \""
                + COLUMN_WORDSET_NAME
                + "\", "
                + " l."
                + COLUMN_LANGUAGE_SHORT
                + " AS \""
                + COLUMN_LANGUAGE_SHORT
                + "\" "
                + " FROM "
                + TABLE_WORD
                + " as tw "
                + "INNER JOIN "
                + TABLE_WORD_HAS_TRANSLATION
                + " AS tht ON tw."
                + COLUMN_WORD_ID
                + "=tht."
                + COLUMN_WORD_ID
                + " "
                + "INNER JOIN "
                + TABLE_WORD
                + " AS tt ON tht."
                + COLUMN_TRANSLATION_ID
                + "=tt."
                + COLUMN_WORD_ID
                + " "
                + "INNER JOIN "
                + TABLE_WORD_HAS_SET
                + " AS whs ON whs."
                + COLUMN_WORD_ID
                + "=tw."
                + COLUMN_WORD_ID
                + " "
                + "INNER JOIN "
                + TABLE_WORDSET
                + " AS tws ON tws."
                + COLUMN_WORDSET_ID
                + "=whs."
                + COLUMN_WORDSET_ID
                + " INNER JOIN "
                + TABLE_LANGUAGE
                + " AS l ON l."
                + COLUMN_LANGUAGE_ID
                + "=tws."
                + COLUMN_LANGUAGE_ID
                + " "
                + " WHERE tws."
                + COLUMN_WORDSET_NAME
                + " = '"
                + sharedPreferences.getString(SettingsActivity.CURRENT_WORDSET,
                SettingsActivity.DEFAULT_WORDSET) + "' ";

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

                wordTemp = c.getString(c.getColumnIndex(COLUMN_WORD));

                word = new Word();
                word.setWord(wordTemp);
                word.setLanguage(c.getString(c.getColumnIndex(COLUMN_LANGUAGE_SHORT)));
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
        c.close();
        wordSet.add(word);

        return wordSet;
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    private String getValueOfCurrentSet(String tableName, String columnName) {
        Log.d(TAG, "entering getValueOfCurrentSet method");
        Log.d(TAG, "getting value of column: " + columnName);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor languageCursor = db.query(tableName,
                new String[] { columnName }, null, null, null, null, "1");
        db.close();
        String valFetched = "";
        if (languageCursor.moveToFirst())
            valFetched = languageCursor.getString(languageCursor
                    .getColumnIndex(columnName));
        Log.d(TAG, "fetchet value: " + valFetched);
        languageCursor.close();
        return valFetched;
    }

    public Word getNext(int counter) {
        Log.d(TAG, "starting method getNext");

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Word> wordSet = getWordsFromDb(db);
        db.close();

        int size = counter % wordSet.size();
        Log.d(TAG, "fetching word number: " + size);
        return wordSet.get(size);
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

    public static ArrayList<Word> addWordToList(ArrayList<Word> arrayList,
                                                Word word) {
        arrayList.add(word);
        return arrayList;
    }

    public String[] getWordsets() {
        Log.d(TAG, "getWordsets method");

        String query = "SELECT " + COLUMN_WORDSET_NAME + " FROM "
                + TABLE_WORDSET + " ";

        Log.d(TAG, query);
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        db.close();
        Log.d(TAG, "size of wordsets4: " + c.getCount());

        String[] wordsets = new String[c.getCount()];

        int counter = 0;
        do {
            wordsets[counter] = c.getString(c
                    .getColumnIndex(COLUMN_WORDSET_NAME));
            Log.d(TAG,
                    "wordset: "
                            + c.getString(c.getColumnIndex(COLUMN_WORDSET_NAME)));
            counter++;
        } while (c.moveToNext());
        c.close();
        return wordsets;
    }

    public void deleteWordset(String columnValue) {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        String[] wordsets = getWordsets();
        if (wordsets.length > 1) {

            if (sharedPreferences.getString(SettingsActivity.CURRENT_WORDSET,
                    SettingsActivity.DEFAULT_WORDSET).equals(columnValue)) {
                Editor editor = sharedPreferences.edit();
                if (sharedPreferences.getString(
                        SettingsActivity.CURRENT_WORDSET,
                        SettingsActivity.DEFAULT_WORDSET).equals(
                        SettingsActivity.DEFAULT_WORDSET)) {
                    editor.putString(SettingsActivity.CURRENT_WORDSET,
                            wordsets[0]);
                } else {

                    editor.putString(SettingsActivity.CURRENT_WORDSET,
                            SettingsActivity.DEFAULT_WORDSET);

                }
                editor.commit();
            }

        } else {
            Toast.makeText(mContext,
                    R.string.you_have_to_have_at_least_one_wordset,
                    Toast.LENGTH_LONG).show();
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT "+COLUMN_WORDSET_ID+" FROM "+TABLE_WORDSET+" WHERE "+COLUMN_WORDSET_NAME+" = '" + columnValue + "'";
        Log.d(TAG,"select wordset id query: "+query);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int wordsetId = cursor.getInt(cursor.getColumnIndex(COLUMN_WORDSET_ID));
        Log.d(TAG,"id_word_set wordset id: "+wordsetId);
        query = "DELETE FROM "+TABLE_WORDSET+" WHERE "+COLUMN_WORDSET_ID+" = '" + wordsetId + "' ";
        Log.d(TAG, "delete query: "+query);
        db.rawQuery(query, null).moveToFirst();

        query = "SELECT "+COLUMN_WORD_ID+" FROM "+TABLE_WORD_HAS_SET+" WHERE "+COLUMN_WORDSET_ID+" = '" + wordsetId + "'";
        Log.d(TAG, "select id of deleted words: "+query);
        cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        do{

            int wordId = cursor.getInt(cursor.getColumnIndex(COLUMN_WORD_ID));
            query = "SELECT "+COLUMN_TRANSLATION_ID+" FROM "+TABLE_WORD_HAS_TRANSLATION+" WHERE "+COLUMN_WORD_ID+"="+wordId+"";
            Log.d(TAG, "get translations id query: "+query);
            Cursor cursor2 = db.rawQuery(query,null);
            cursor2.moveToFirst();

            do{

                int translationId = cursor2.getInt(cursor2.getColumnIndex(COLUMN_TRANSLATION_ID));
                query = "DELETE FROM "+TABLE_WORD+" WHERE "+COLUMN_WORD_ID+"="+translationId+"";
                db.rawQuery(query,null).moveToFirst();
                query = "DELETE FROM "+TABLE_WORD_HAS_TRANSLATION+" WHERE "+COLUMN_TRANSLATION_ID+"="+translationId+"";
                db.rawQuery(query,null).moveToFirst();
                Log.d(TAG, "deleted translation id: "+translationId);

            } while(cursor2.moveToNext());

            query = "DELETE FROM "+TABLE_WORD_HAS_TRANSLATION+" WHERE "+COLUMN_WORD_ID+"="+wordId+"";
            db.rawQuery(query,null).moveToFirst();
            query = "DELETE FROM "+TABLE_WORD+" WHERE "+COLUMN_WORD_ID+"="+wordId+"";
            db.rawQuery(query,null).moveToFirst();
            Log.d(TAG, "deleted word id: "+wordId);
            cursor2.close();

        } while (cursor.moveToNext());

        query = "DELETE FROM "+TABLE_WORD_HAS_SET+" WHERE "+COLUMN_WORDSET_ID+"="+wordsetId+"";
        db.rawQuery(query,null).moveToFirst();
        Log.d(TAG, "deleted word_has_set id: "+wordsetId);

        cursor.close();
        db.close();

    }

    public String getlanguageFromWordset(String wordsetName) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT "+COLUMN_LANGUAGE_SHORT+" FROM "
                +TABLE_LANGUAGE+" AS l INNER JOIN "
                +TABLE_WORDSET+" AS ws ON ws."
                +COLUMN_LANGUAGE_ID+"=l."
                +COLUMN_LANGUAGE_ID+" WHERE ws."+COLUMN_WORDSET_NAME+"='"+wordsetName+"'";

        Log.d(TAG,"query: "+query);

        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        String language = cursor.getString(cursor.getColumnIndex(COLUMN_LANGUAGE_SHORT));
        Log.d(TAG, "language: " + language);
        cursor.close();
        db.close();
        return language;
    }
    
    public Cursor getlanguageFromWordset(String wordsetName, String smth) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT "+COLUMN_LANGUAGE_SHORT+" FROM "
                +TABLE_LANGUAGE+" AS l INNER JOIN "
                +TABLE_WORDSET+" AS ws ON ws."
                +COLUMN_LANGUAGE_ID+"=l."
                +COLUMN_LANGUAGE_ID+" WHERE ws."+COLUMN_WORDSET_NAME+"='"+wordsetName+"'";

        Log.d(TAG,"query: "+query);

        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        return cursor;
    }
    
    public Cursor getWord(String word){
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT * FROM "+TABLE_WORD+" WHERE "+COLUMN_WORD+" = '"+word+"'";
		Cursor cursor = db.rawQuery(query, null);   
		cursor.moveToFirst();
		db.close();
    	return cursor;    	
    }

	public Cursor getWordsetByName(String wordsetName) {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_WORDSET +" WHERE "+COLUMN_WORDSET_NAME+" = '"+wordsetName+"'";
		Cursor cursor = db.rawQuery(query, null);		
		cursor.moveToFirst();			
		db.close();
		return cursor;
	}

	public Cursor getWordTranslationRelation(String word, String translation) {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT * FROM "+TABLE_WORD_HAS_TRANSLATION+" AS wht INNER JOIN "+COLUMN_WORD+" AS w ON w."+COLUMN_WORD_ID+"=wht."
				+COLUMN_WORD_ID+" INNER JOIN "+TABLE_WORD+" AS t ON wht."+COLUMN_TRANSLATION_ID+"=t."+COLUMN_WORD_ID+" WHERE w."+COLUMN_WORD+"='"+word+"' AND " +
						"t."+COLUMN_WORD+" =  '"+translation+"'";
		Log.d(TAG, "query: "+query);
		Cursor cursor = db.rawQuery(query, null);		
		cursor.moveToFirst();
		db.close();
		return cursor;
	}

	public Cursor getWordWordsetRelation(String word, String wordsetName) {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT * FROM "+TABLE_WORD_HAS_SET+" AS whw INNER JOIN "+COLUMN_WORD+" AS w ON w."+COLUMN_WORD_ID+"=whw."
				+COLUMN_WORD_ID+" INNER JOIN "+TABLE_WORDSET+" AS ws ON whw."+COLUMN_WORDSET_ID+"=ws."+COLUMN_WORDSET_ID
				+" WHERE w."+COLUMN_WORD+"='"+word+"' AND " +
						"ws."+COLUMN_WORDSET_NAME+" =  '"+wordsetName+"'";
		
		Log.d(TAG, "query: "+query);
		Cursor cursor = db.rawQuery(query, null);		
		cursor.moveToFirst();
		db.close();
		return cursor;
	}

	
}
