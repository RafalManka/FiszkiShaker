package pl.rafalmanka.fiszki.shaker;

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
		EditText newDescription = (EditText) findViewById(R.id.editText_add_new_word_description);
		Log.d(TAG, "new description: " + newDescription);
		DatabaseHandler databaseHandler = new DatabaseHandler(view.getContext());
		databaseHandler.addWord(new Word(newWord.getText().toString(),
				newDescription.getText().toString(), databaseHandler.getCurrentLanguageID()));
		Toast.makeText(view.getContext(), R.string.word_succesfully_added,
				Toast.LENGTH_LONG).show();
	}

}
