package pl.rafalmanka.fiszki.shaker.model;

import java.util.ArrayList;

import pl.rafalmanka.fiszki.shaker.view.SettingsActivity;

import android.util.Log;

public class Word {
	public static final String TAG = Word.class.getSimpleName();

	private int mWordId;
	private String mWord;
	private String mLanguage;
	private ArrayList<Word> mTranslations = new ArrayList<Word>();
	private int mLanguageId;
	private int mWordsetId;
	private int mSetId;
	
	public static String mNameOfSet = SettingsActivity.DEFAULT_WORDSET;

	public Word() {
	}
	
	public Word(String word) {
		mWord = word;
	}

	/*public Word(int id, String word, String translation, String language) {
		Log.d(TAG, "4 argument constructor (id:" + id + ",word:" + word
				+ ",description:" + translation + ",languuage:" + language
				+ ")");
		mWordId = id;
		mWord = word;
//		Word translation = new Word();
//		translation.setWord(description);
//		mTranslations.add(translation);
		mLanguage = language;
	}

	public Word(String word, String translation, String language) {
		Log.d(TAG, "3 argument constructor (word:" + word + ",description:"
				+ translation + ",languuage:" + language + ")");
		mWord = word;
//		Word translation = new Word();
//		translation.setWord(description);
//		mTranslations.add(translation);
		mLanguage = language;
	}

	public Word(String nameOfSet, String word, String translation,
			String language) {
		mNameOfSet = nameOfSet;
		mWord = word;
//		Word _translation = new Word();
//		_translation.setWord(translation);
//		mTranslations.add(_translation);
		mLanguage = language;
	}

	public Word(int id, String nameOfSet, String word, String translation,
			String language) {
		mWordId = id;
		mNameOfSet = nameOfSet;
		mWord = word;
		Word _translation = new Word();
		_translation.setWord(translation);
		mTranslations.add(_translation);
		mLanguage = language;
	}

	public Word(String word) {
		mWord = word;
	}
*/
	public int getID() {
		return mWordId;
	}

	public void setID(int id) {
		mWordId = id;
	}

	public String getWord() {
		return mWord;
	}

	public void setWord(String word) {
		mWord = word;
	}

	public String getLanguage() {
		return mLanguage;
	}

	public void setLanguage(String language) {
		mLanguage = language;
	}

	public String getNameOfSet() {
		return mNameOfSet;
	}

	public void setSetName(String nameOfSet) {
		mNameOfSet = nameOfSet;
	}

	public ArrayList<Word> getTranslations() {
		return mTranslations;
	}

	public void setTranslations(ArrayList<Word> translations) {
		mTranslations = translations;
	}

	public void setLanguageId(int languageId) {
		mLanguageId = languageId;
		
	}
	
	public int getLanguageId() {
		return mLanguageId;
		
	}
	
	public int getWordsetId() {
		return mWordsetId;
		
	}
	
	public void setWordsetId(int wordsetId) {
		mWordsetId = wordsetId;
		
	}

	public String getConcatenatedTranslations() {
		Log.d(TAG, "start method getConcatenatedTranslations");
		String concatenatedTranslation="";		
		Log.d(TAG, "list size: "+mTranslations.size());
		for (int i = 0; i < ( mTranslations.size() -1 ) ; i++) {
			Log.d(TAG, "concatenating word: "+mTranslations.get(i).getWord());
			concatenatedTranslation+= mTranslations.get(i).getWord()+", ";
		}
		Log.d(TAG, "adding last word: "+mTranslations.get(mTranslations.size()-1).getWord());
		concatenatedTranslation+=mTranslations.get(mTranslations.size()-1).getWord();
		return concatenatedTranslation;
	}

	public void setSetId(int setId) {
		mSetId = setId;
		
	}
	
	public int getSetId(){
		return mSetId;
	}
}
