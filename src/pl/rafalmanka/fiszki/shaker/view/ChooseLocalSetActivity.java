package pl.rafalmanka.fiszki.shaker.view;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.rafalmanka.fiszki.shaker.R;
import pl.rafalmanka.fiszki.shaker.adapters.WordListViewAdapter;
import pl.rafalmanka.fiszki.shaker.adapters.WordsetsListViewAdapter;
import pl.rafalmanka.fiszki.shaker.model.DatabaseHandler;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ChooseLocalSetActivity extends ListActivity{

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		SharedPreferences mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = mSharedPreferences.edit();
		editor.putString(SettingsActivity.CURRENT_WORDSET, mWordtList[position]);
		editor.commit();
		
		Log.d(TAG, "preference set to: "+ mSharedPreferences.getString(SettingsActivity.CURRENT_WORDSET, "default"));
		
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
	}

	public static final String TAG =  ChooseLocalSetActivity.class.getSimpleName();
	protected ProgressBar mProgressBar;
	public String[] mWordtList;
	public static final String KEY_WORDSET = "wordset";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreated");

		setContentView(R.layout.activity_list);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mProgressBar.setVisibility(View.VISIBLE);
		GetWordsets getWordsets = new GetWordsets();
		getWordsets.execute();
	}
	
	private class GetWordsets extends AsyncTask<Object, Void, String[]>{

		@Override
		protected String[] doInBackground(Object... params) {	
			Log.d(TAG,"doInBackground");
			DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
			return databaseHandler.getWordsets();
		}

		@Override
		protected void onPostExecute(String[] result) {
			mWordtList = result;
			handleDisplayWordsets();
		}
		
	}

	public void handleDisplayWordsets() {
		
		mProgressBar.setVisibility(View.INVISIBLE);
		if (mWordtList.length == 0) {
			updateDisplayForErrors();
		} else {
			try {

				ArrayList<HashMap<String, String>> blogPosts = new ArrayList<HashMap<String, String>>();
				for (int i = 0; i < mWordtList.length ; i++) {
					String wordset = mWordtList[i];
					
					HashMap<String, String> blogPost = new HashMap<String, String>();
					blogPost.put(KEY_WORDSET , wordset);
					blogPosts.add(blogPost);
				}

				String[] keys = { KEY_WORDSET };
				int[] ids = { android.R.id.text1 };
				WordsetsListViewAdapter adapter = new WordsetsListViewAdapter(this, blogPosts);

				setListAdapter(adapter);
			} catch (Exception e) {
				Log.e(TAG, "Error: ", e);
			}
		}

		
	}

	private void updateDisplayForErrors() {
		Log.d(TAG, "updateDisplayForErrors");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.no_languages_alert_title));
		builder.setMessage(getString(R.string.no_languages_alert_description));
		builder.setPositiveButton(android.R.string.ok, null);
		AlertDialog dialog = builder.create();
		dialog.show();
		Log.d(TAG, "get emptyTextView");
		TextView emptyTextView = (TextView) findViewById(R.id.no_items_to_display);
		Log.d(TAG, "show alert");
		emptyTextView.setText(getString(R.string.no_items_to_display));
		Log.d(TAG, "done");
		
	}

}
