package pl.rafalmanka.fiszki.shaker;

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

public class SettingsActivity extends Activity implements OnClickListener {

	public static String TAG = SettingsActivity.class.getSimpleName();
	CheckBox mCheckBox;
	Button mButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreated");
		setContentView(R.layout.settings);
		Log.d(TAG, "layout set");
		
		
		mCheckBox = (CheckBox) findViewById(R.id.checkbox_sound);
		Log.d(TAG, "checkbox set: "+mCheckBox);
		mButton = (Button) findViewById(R.id.button_save_settings);
		Log.d(TAG, "button set");
		mButton.setOnClickListener(this);
		Log.d(TAG, "onclicklistener set");
		
		loadPreferences();
		Log.d(TAG, "preferences set");
	}

	private void loadPreferences() {
		Log.d(TAG, "loadPreferences");
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Log.d(TAG, "SharedPreferences accessed");
		boolean checkBoxValue = sharedPreferences.getBoolean(
				"SOUND_PREFERENCE", false);
		Log.d(TAG, "checkbox value fetched");
		if (checkBoxValue) {
			mCheckBox.setChecked(true);
		} else {
			Log.d(TAG, "setting unchecked");
			mCheckBox.setChecked(false);
		}
		Log.d(TAG, "checkbox value set");
	}

	private void savePreferences(String key, boolean value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	@Override
	public void onClick(View view) {
		savePreferences("SOUND_PREFERENCE", mCheckBox.isChecked());
		finish();
	}
}
