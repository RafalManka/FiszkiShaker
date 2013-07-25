package pl.rafalmanka.fiszki.shaker.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import pl.rafalmanka.fiszki.shaker.R;
import pl.rafalmanka.fiszki.shaker.model.DatabaseHandler;
import pl.rafalmanka.fiszki.shaker.model.Word;

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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //word.setLanguage(sharedPreferences.getString(SettingsActivity.CURRENT_LANGUAGE, SettingsActivity.DEFAULT_LANGUAGE));
        word.setSetName(sharedPreferences.getString(SettingsActivity.CURRENT_WORDSET, SettingsActivity.DEFAULT_WORDSET));

        word.setTranslations(DatabaseHandler.addWordToList(new ArrayList<Word>(), new Word(newTranslation.getText().toString())));
        databaseHandler.addWord(word);
        Toast.makeText(view.getContext(), R.string.word_succesfully_added,
                Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
