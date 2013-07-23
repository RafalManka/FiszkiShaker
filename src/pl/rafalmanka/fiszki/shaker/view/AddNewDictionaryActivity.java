package pl.rafalmanka.fiszki.shaker.view;

import java.util.ArrayList;

import pl.rafalmanka.fiszki.shaker.R;
import pl.rafalmanka.fiszki.shaker.R.id;
import pl.rafalmanka.fiszki.shaker.R.layout;
import pl.rafalmanka.fiszki.shaker.R.string;
import pl.rafalmanka.fiszki.shaker.model.DatabaseHandler;
import pl.rafalmanka.fiszki.shaker.model.Word;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddNewDictionaryActivity extends Activity {

	private static String TAG = AddNewDictionaryActivity.class.getSimpleName();
	private EditText mEditTextWord;
	private EditText mEditTextDescription;
	private EditText mEditTextTitle;
	private CheckBox mCheckboxAllowOthersToUseSet;
	private ArrayList<Word> mWordsList = new ArrayList<Word>();
	private String mLanguage;

	private LinearLayout mLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_dictionary);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);		
		mLanguage = sharedPreferences.getString(SettingsActivity.CURRENT_WORDSET, SettingsActivity.DEFAULT_WORDSET);

		mEditTextTitle = (EditText) findViewById(R.id.editText_add_new_dictionary_enter_title);
		mEditTextTitle.setHint(R.string.title);

		mCheckboxAllowOthersToUseSet = (CheckBox) findViewById(R.id.checkBox_allow_other_to_use_set);
		mCheckboxAllowOthersToUseSet.setChecked(true);

		mEditTextWord = (EditText) findViewById(R.id.editText_add_new_dictionary_add_new_word);
		mEditTextWord.setHint(R.string.word);

		mEditTextDescription = (EditText) findViewById(R.id.editText_add_new_dictionary_add_new_translation);
		mEditTextDescription.setHint(R.string.translation);

		mLinearLayout = (LinearLayout) findViewById(R.id.test_this);

		Button buttonGoToNext = (Button) findViewById(R.id.button_add_new_dictionary_add_another);
		buttonGoToNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				String editTextTitle = mEditTextTitle.getText().toString();
				if(mEditTextTitle.isEnabled()){
					mEditTextTitle.setEnabled(false) ;
				}
				
				String editTextWord = mEditTextWord.getText().toString();
				String editTextDescription = mEditTextDescription.getText()
						.toString();
				if (!editTextWord.equals("") && !editTextDescription.equals("")) {
					//TODO 
					final Word wordInputed = new Word();
					
					wordInputed.setSetName(editTextTitle);
					wordInputed.setWord(editTextWord);
					wordInputed.setLanguage(mLanguage);
					wordInputed.setTranslations(DatabaseHandler.addWordToList(new ArrayList<Word>(), new Word(editTextDescription)));
					
					mWordsList.add(wordInputed);

					Log.d(TAG, "title: " + editTextTitle);
					Log.d(TAG, "word: " + editTextWord);
					Log.d(TAG, "description: " + editTextDescription);
					Log.d(TAG, "language: " + mLanguage);

					CheckBox checkBox = new CheckBox(view.getContext());
					checkBox.setText(editTextWord + " - " + editTextDescription);
					checkBox.setChecked(true);
					checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								Toast.makeText(
										buttonView.getContext(),
										getString(R.string.word)
												+ " "
												+ wordInputed.getWord()
												+ " "
												+ getString(R.string.word_has_ben_included),
										Toast.LENGTH_SHORT).show();
								mWordsList.add(wordInputed);
							} else {
								Toast.makeText(
										buttonView.getContext(),
										getString(R.string.word)
												+ " "
												+ wordInputed.getWord()
												+ " "
												+ getString(R.string.word_has_ben_excluded),
										Toast.LENGTH_SHORT).show();
								mWordsList.remove(wordInputed);
							}

						}

					});

					mLinearLayout.addView(checkBox);

					mEditTextWord.setText("");
					mEditTextWord.setHint(R.string.word);

					mEditTextDescription.setText("");
					mEditTextDescription.setHint(R.string.translation);
				}

			}
		});

		Button buttonSubmitSet = (Button) findViewById(R.id.button_add_new_dictionary_submit_set);
		buttonSubmitSet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				DatabaseHandler databaseHandler = new DatabaseHandler(
						getBaseContext());
				databaseHandler.addNewSet(mWordsList);

				Intent intent = new Intent(view.getContext(),
						MainActivity.class);
				startActivity(intent);

			}
		});

	}
}
