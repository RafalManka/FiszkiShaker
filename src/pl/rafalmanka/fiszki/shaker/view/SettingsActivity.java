package pl.rafalmanka.fiszki.shaker.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    public static final String CURRENT_LOCALE = "CURRENT_LANGUAGE";

    public static final String DEFAULT_WORDSET = "default";
    public static final String DEFAULT_LOCALE = "en";
    public static final String DEFAULT_WORD_STATUS = "assets_default";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreated");
        setContentView(R.layout.settings);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_bar);

        TextView titleBar = (TextView) findViewById(R.id.textView_titlebar);
        titleBar.setText(R.string.title_bar_starting_point_activity);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_titlebar);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.colors_titlebar_settings));

        ImageButton ib = (ImageButton) findViewById(R.id.imageButton_titlebar);
        ib.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StartingPointActivity.class);
                startActivity(intent);
            }
        });


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
