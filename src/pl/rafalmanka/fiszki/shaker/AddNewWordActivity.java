package pl.rafalmanka.fiszki.shaker;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewWordActivity extends Activity {

	public static String TAG = AddNewWordActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_word);
	}

	public void addNewWord(View view) {
		EditText newWord = (EditText) findViewById(R.id.editText_add_new_word_word);
		Log.d(TAG, "new word: " + newWord.getText().toString());
		EditText newTranslation = (EditText) findViewById(R.id.editText_add_new_word_description);
		Log.d(TAG, "new description: " + newTranslation);
		DatabaseHandler databaseHandler = new DatabaseHandler(view.getContext());
		Word word = new Word();
		word.setWord(newWord.getText().toString());
		word.setLanguageId(Integer.parseInt(databaseHandler.getValueOfCurrentSet(DatabaseHandler.TABLE_LANGUAGE,DatabaseHandler.COLUMN_LANGUAGE_ID)));
		word.setLanguage(databaseHandler.getValueOfCurrentSet(DatabaseHandler.TABLE_LANGUAGE,DatabaseHandler.COLUMN_LANGUAGE));
		word.setSetName(databaseHandler.getValueOfCurrentSet(DatabaseHandler.TABLE_WORDSET,DatabaseHandler.COLUMN_WORDSET_NAME));
		word.setSetId(Integer.parseInt(databaseHandler.getValueOfCurrentSet(DatabaseHandler.TABLE_WORDSET,DatabaseHandler.COLUMN_WORDSET_ID)));		

		word.setTranslations(DatabaseHandler.addWordToList(new ArrayList<Word>(), new Word(newTranslation.getText().toString())));		
		databaseHandler.addWord(word);
		Toast.makeText(view.getContext(), R.string.word_succesfully_added,
				Toast.LENGTH_LONG).show();
	}

}
