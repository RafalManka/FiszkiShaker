package pl.rafalmanka.fiszki.shaker.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pl.rafalmanka.fiszki.shaker.R;
import pl.rafalmanka.fiszki.shaker.model.DatabaseHandler;
import pl.rafalmanka.fiszki.shaker.model.Word;

public class AddNewWordsetActivity extends Activity {

    private static String TAG = AddNewWordsetActivity.class.getSimpleName();
    private EditText mEditTextWord;
    private EditText mEditTextDescription;
    private EditText mEditTextTitle;
    private ArrayList<Word> mWordsList = new ArrayList<Word>();
    private String mLanguage;

    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.add_new_wordset);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_bar);
        TextView titleBar = (TextView) findViewById(R.id.textView_titlebar);
        LinearLayout ll = (LinearLayout) findViewById(R.id.layout_titlebar);
        ll.setBackgroundColor(getResources().getColor(R.color.color_add_wordset_titlebar));
        titleBar.setText(getString(R.string.title_add_new_wordset));
        ImageButton ib = (ImageButton) findViewById(R.id.imageButton_titlebar);
        
        ib.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StartingPointActivity.class);
                startActivity(intent);
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.dropdown_language_chooser);
        spinner.setPadding(10, 0, 0, 0);
        
        ArrayList<String> arrayList = new ArrayList<String>();
        
        DatabaseHandler dbh = new DatabaseHandler(getApplicationContext());
        Cursor langCursor = dbh.getLanguageList();
        Log.d(TAG, "rafman: "+langCursor.getCount());
        Log.d(TAG, "rafman: "+langCursor.getString(langCursor.getColumnIndex(DatabaseHandler.COLUMN_LANGUAGE_SHORT)));
        do{
        	arrayList.add(langCursor.getString(langCursor.getColumnIndex(DatabaseHandler.COLUMN_LANGUAGE_SHORT)));
        }while(langCursor.moveToNext());
        langCursor.close();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item, arrayList);
        
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        spinner.setAdapter(dataAdapter);
        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLanguage = sharedPreferences.getString(SettingsActivity.CURRENT_LOCALE, SettingsActivity.DEFAULT_LOCALE);

        mEditTextTitle = (EditText) findViewById(R.id.editText_add_new_dictionary_enter_title);
        mEditTextTitle.setHint(R.string.title);

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

                String editTextWord = mEditTextWord.getText().toString();
                String editTextDescription = mEditTextDescription.getText().toString();

                if ( ( !editTextWord.equals("") ) &&  ( !editTextDescription.equals("") ) && ( !editTextTitle.equals("") ) ) {

                    if (mEditTextTitle.isEnabled()) {
                        mEditTextTitle.setEnabled(false);
                    }

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
                    
                    checkBox.setPadding(50, 0, 0, 0);
                    checkBox.setTextColor(getResources().getColor(R.color.colors_black));
                    checkBox.setBackgroundColor(getResources().getColor(R.color.colors_white));
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
                Log.d(TAG,"check if arraylist is empty");

                if(!mWordsList.isEmpty()){
                    Log.d(TAG,"no its not  empty");
                    DatabaseHandler databaseHandler = new DatabaseHandler(
                            getBaseContext());
                    databaseHandler.addNewSet( mWordsList.get(0).getLanguage() , mWordsList.get(0).getNameOfSet() , mWordsList);

                    Intent intent = new Intent(view.getContext(),
                            MainActivity.class);
                    startActivity(intent);
                }


            }
        });

    }
}
