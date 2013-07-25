package pl.rafalmanka.fiszki.shaker.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

import pl.rafalmanka.fiszki.shaker.R;

public class SettingsActivity extends Activity implements OnClickListener {

    public static String TAG = SettingsActivity.class.getSimpleName();
    private CheckBox mSoundCheckBox;
    private CheckBox mRandomCheckBox;
    private Button mButton;
    private SharedPreferences mSharedPreferences;
    public final static String SOUND_PREFERENCE = "SOUND_PREFERENCE";
    public final static String RANDOMIZE_PREFERENCE = "RANDOMIZE_PREFERENCE";
    public final static String CURRENT_WORDSET = "CURRENT_WORDSET";
    public final static String DEFAULT_WORDSET = "default";
    public static final String DEFAULT_LANGUAGE = "English";
    public static final String CURRENT_LANGUAGE = "CURRENT_LANGUAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.d(TAG, "onCreated");
        setContentView(R.layout.settings);
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        mSoundCheckBox = (CheckBox) findViewById(R.id.checkbox_sound);
        mRandomCheckBox = (CheckBox) findViewById(R.id.checkBox_random_fetching);

        mButton = (Button) findViewById(R.id.button_save_settings);
        mButton.setOnClickListener(this);

        loadPreferences();
    }

    private void loadPreferences() {
        Log.d(TAG, "loadPreferences");
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        Log.d(TAG, "SharedPreferences accessed");
        setCheckboxes(mSoundCheckBox, SOUND_PREFERENCE, false);
        setCheckboxes(mRandomCheckBox, RANDOMIZE_PREFERENCE, false);
    }

    private void setCheckboxes(CheckBox checkBox, String preferenceName, boolean defaultVal) {
        boolean checkBoxValue = mSharedPreferences.getBoolean(
                preferenceName, defaultVal);
        Log.d(TAG, "checkbox value fetched");
        if (checkBoxValue) {
            checkBox.setChecked(true);
        } else {
            Log.d(TAG, "setting unchecked");
            checkBox.setChecked(false);
        }
        Log.d(TAG, "checkbox value set");
    }

    private void savePreferences(String key, boolean value) {
        Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    @Override
    public void onClick(View view) {
        savePreferences(SOUND_PREFERENCE, mSoundCheckBox.isChecked());
        savePreferences(RANDOMIZE_PREFERENCE, mRandomCheckBox.isChecked());
        finish();
    }
}
