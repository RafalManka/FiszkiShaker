package pl.rafalmanka.fiszki.shaker.view;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import pl.rafalmanka.fiszki.shaker.R;
import pl.rafalmanka.fiszki.shaker.adapters.WordsetsListViewAdapter;
import pl.rafalmanka.fiszki.shaker.model.DatabaseHandler;

public class ChooseLocalSetActivity extends ListActivity {

    public static final String TAG = ChooseLocalSetActivity.class.getSimpleName();
    protected ProgressBar mProgressBar;
    public String[] mWordtList;
    public static final String KEY_WORDSET = "wordset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreated");
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.activity_list);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout_list);
        rl.setBackgroundColor(getResources().getColor(R.color.manage_sets_layout));

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_bar);
        TextView titleBar = (TextView) findViewById(R.id.textView_titlebar);
        titleBar.setText(R.string.manage_sets);
        titleBar.setTextColor(getResources().getColor(R.color.colors_black));

        LinearLayout ll = (LinearLayout) findViewById(R.id.layout_titlebar);
        ll.setBackgroundColor(getResources().getColor(R.color.manage_sets_title));

        ImageButton ib = (ImageButton) findViewById(R.id.imageButton_titlebar);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StartingPointActivity.class);
                startActivity(intent);
            }
        });



        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mProgressBar.setVisibility(View.VISIBLE);
        GetWordsets getWordsets = new GetWordsets();
        getWordsets.execute();
    }

    private class GetWordsets extends AsyncTask<Object, Void, String[]> {

        @Override
        protected String[] doInBackground(Object... params) {
            Log.d(TAG, "doInBackground");
            DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
            return databaseHandler.getWordsets();
        }

        @Override
        protected void onPostExecute(String[] result) {
            mWordtList = result;
            handleDisplayWordsets();
        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        SharedPreferences mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        Editor editor = mSharedPreferences.edit();
        editor.putString(SettingsActivity.CURRENT_WORDSET, mWordtList[position]);
        editor.commit();

        Log.d(TAG, "preference set to: " + mSharedPreferences.getString(SettingsActivity.CURRENT_WORDSET, "default"));

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void handleDisplayWordsets() {

        mProgressBar.setVisibility(View.INVISIBLE);
        if (mWordtList.length == 0) {
            updateDisplayForErrors();
        } else {
            try {

                ArrayList<HashMap<String, String>> blogPosts = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < mWordtList.length; i++) {
                    String wordset = mWordtList[i];

                    HashMap<String, String> blogPost = new HashMap<String, String>();
                    blogPost.put(KEY_WORDSET, wordset);
                    blogPosts.add(blogPost);
                }

                String[] keys = {KEY_WORDSET};
                int[] ids = {android.R.id.text1};
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
