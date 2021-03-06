package pl.rafalmanka.fiszki.shaker.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import pl.rafalmanka.fiszki.shaker.R;
import pl.rafalmanka.fiszki.shaker.adapters.WordListViewAdapter;
import pl.rafalmanka.fiszki.shaker.model.DatabaseHandler;
import pl.rafalmanka.fiszki.shaker.model.Word;

public class WordsListActivity extends Activity {

    public static final String TAG = WordsListActivity.class.getSimpleName();
    public JSONObject mWordsData = null;
    protected ProgressBar mProgressBar;
    public static final String KEY_WORDSET_ID = "wordset_id";
    public static final String KEY_WORDSET = "wordset";
    public static final String KEY_LANGUAGE_ID = "language_id";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_WORDS = "words";
    public static final String KEY_WORD = "word";
    public static final String KEY_TRANSLATION = "translation";

    private ArrayList<Word> wordInfo = new ArrayList<Word>();

    private TextView noItemsToDisplay;
    private int mWordsetId;
    private String mWordset;
    private Button saveWordsButton;
    public int mLanguageId;
    public String mLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreated");
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

        LinearLayout ll = (LinearLayout) findViewById(R.id.layout_titlebar);
        ll.setBackgroundColor(getResources().getColor(R.color.colors_titlebar_green));

        TextView tv = (TextView) findViewById(R.id.textView_titlebar);
        tv.setText(R.string.titlebar_word_list);

        ImageButton ib = (ImageButton) findViewById(R.id.imageButton_titlebar);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StartingPointActivity.class);
                startActivity(intent);
            }
        });


        Bundle bundle = getIntent().getExtras();
        mLanguageId = bundle.getInt("language_id");
        mWordsetId = bundle.getInt("topic_id");

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        noItemsToDisplay = (TextView) findViewById(R.id.no_items_to_display);

        if (isNetworkAvaileable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            GetWordsFromAPITask getBlogPostsTask = new GetWordsFromAPITask();
            getBlogPostsTask.execute();
        } else {
            Log.d(TAG, "no internet connection, generating alert");
            updateDisplayForErrors();
            Log.d(TAG, "setting text for no items to display textview");
            noItemsToDisplay.setText(R.string.no_items_to_display);
            Log.d(TAG, "setting progressbar to invisible");
            mProgressBar.setVisibility(View.INVISIBLE);
        }

    }

    private void LogInfo(String info) {
        Log.i(TAG, info);
    }

    private void LogDebug(String debug) {
        Log.d(TAG, debug);
    }

    private void LogException(Exception e) {
        Log.e(TAG, "exception caught: ", e);
    }

    private void handleBlogResponse() {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mWordsData.length() == 0) {
            updateDisplayForErrors();
        } else {
            try {
                mLanguageId = mWordsData.getInt(KEY_LANGUAGE_ID);
                mLanguage = Html.fromHtml(mWordsData.getString(KEY_LANGUAGE))
                        .toString();
                mWordsetId = mWordsData.getInt(KEY_WORDSET_ID);
                mWordset = Html.fromHtml(mWordsData.getString(KEY_WORDSET))
                        .toString();
                JSONArray jsonWords = mWordsData.getJSONArray(KEY_WORDS);

                ArrayList<HashMap<String, String>> blogPosts = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < jsonWords.length(); i++) {

                    JSONObject word = jsonWords.getJSONObject(i);
                    String word_title = Html.fromHtml(word.getString(KEY_WORD))
                            .toString();

                    Word wordData = new Word();
                    wordData.setWord(word_title);
                    wordData.setLanguageId(mLanguageId);
                    wordData.setLanguage(mLanguage);
                    wordData.setSetName(mWordset);
                    wordData.setWordsetId(mWordsetId);
                    ArrayList<Word> translations = new ArrayList<Word>();

                    Log.d(TAG, "word title: " + word_title);
                    JSONArray jsonTranslations = word
                            .getJSONArray(KEY_TRANSLATION);
                    String translation = "";
                    Word wordTmp;
                    for (int j = 0; j < (jsonTranslations.length() - 1); j++) {
                        translation += Html.fromHtml(jsonTranslations
                                .getString(j)) + ", ";
                        wordTmp = new Word();
                        wordTmp.setWord(Html.fromHtml(
                                jsonTranslations.getString(j)).toString());
                        translations.add(wordTmp);
                    }
                    translation += jsonTranslations.getString(jsonTranslations
                            .length() - 1);
                    Log.d(TAG, "concatenated translation: " + translation);

                    wordTmp = new Word();
                    wordTmp.setWord(Html.fromHtml(
                            jsonTranslations.getString(jsonTranslations
                                    .length() - 1)).toString());
                    translations.add(wordTmp);
                    wordData.setTranslations(translations);
                    wordInfo.add(wordData);
                    HashMap<String, String> blogPost = new HashMap<String, String>();
                    blogPost.put(KEY_WORD, word_title);
                    blogPost.put(KEY_TRANSLATION, translation);
                    blogPosts.add(blogPost);
                }

                ListView lv = (ListView) findViewById(R.id.list_view);

                saveWordsButton = new Button(this);

                saveWordsButton.setText(R.string.submit);
                saveWordsButton.setBackgroundResource(R.drawable.button_import_wordset);
                saveWordsButton.setId(12312);
                saveWordsButton.setPadding(15,15,15,15);
                saveWordsButton.setTextColor(getResources().getColor(R.color.colors_white));

                saveWordsButton.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        DatabaseHandler dbHandler = new DatabaseHandler(v
                                .getContext());
                        dbHandler.addNewSet( wordInfo.get(0).getLanguage(), wordInfo.get(0).getNameOfSet() , wordInfo);

                        SharedPreferences mSharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        Editor editor = mSharedPreferences.edit();
                        editor.putString(SettingsActivity.CURRENT_WORDSET, wordInfo.get(0).getNameOfSet());
                        editor.commit();

                        Log.d(TAG, "preference set to: " + mSharedPreferences.getString(SettingsActivity.CURRENT_WORDSET, "default"));

                        Intent intent = new Intent(v.getContext(),
                                MainActivity.class);
                        startActivity(intent);
                    }

                });

                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 10, 30, 10);
                ll.addView(saveWordsButton, layoutParams);

                lv.addHeaderView(ll);
                WordListViewAdapter adapter = new WordListViewAdapter(this,
                        blogPosts);
                lv.setAdapter(adapter);

            } catch (JSONException e) {
                LogException(e);
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

    private boolean isNetworkAvaileable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }

    }

    private class GetWordsFromAPITask extends
            AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {
            int responseCode = -1;
            JSONObject jsonResponse = null;
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            String query = "http://api.rafalmanka.pl/api/fetchWords?language="
                    + mLanguageId + "&topic=" + mWordsetId;
            HttpGet httpget = new HttpGet(query);
            LogDebug("query sent to API: " + query);

            try {
                HttpResponse response = client.execute(httpget);
                StatusLine statusLine = response.getStatusLine();
                Log.d(TAG, "status line: " + statusLine);
                responseCode = statusLine.getStatusCode();
                LogDebug(responseCode + "");
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Log.d(TAG, "line: " + line);
                        builder.append(line);
                    }

                    jsonResponse = new JSONObject(builder.toString());
                    LogInfo("builder.toString(): " + builder.toString());
                } else {
                    Log.e(TAG, builder.toString());
                }
            } catch (JSONException e) {
                LogException(e);
            } catch (Exception e) {
                LogException(e);
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mWordsData = result;
            handleBlogResponse();
        }

    }

}
