package pl.rafalmanka.fiszki.shaker.view;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
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

public class LanguageListActivity extends ListActivity {

    public static final String TAG = LanguageListActivity.class.getSimpleName();
    private JSONArray mTopicsData = null;
    protected ProgressBar mProgressBar;
    private final String KEY_TITLE = "language_title";
    private TextView noItemsToDisplay;
    private String mLanguageId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        noItemsToDisplay = (TextView) findViewById(R.id.no_items_to_display);

        if (isNetworkAvaileable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
            getBlogPostsTask.execute();
        } else {
            // Toast.makeText(this, "Network is unavaileable!",
            // Toast.LENGTH_LONG) .show();
            updateDisplayForErrors();
            noItemsToDisplay.setText(R.string.no_items_to_display);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Log.d(TAG, position + "");

        JSONArray jsonPosts;
        try {
            jsonPosts = mTopicsData;
            JSONObject jsonPost = jsonPosts.getJSONObject(position);
            mLanguageId = jsonPost.getString("language_id");
            Log.d(TAG, "language id passed to another intent: " + jsonPost.getString("language_id"));
            Intent intent = new Intent(this, TopicsListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("language_id", Integer.parseInt(mLanguageId));
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            LogException(e);
        }

    }

    private void LogException(Exception e) {
        Log.e(TAG, "exception caught: ", e);
    }

    private void handleBlogResponse() {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mTopicsData.length() == 0) {
            updateDisplayForErrors();
        } else {
            try {
                JSONArray jsonPosts = mTopicsData;
                ArrayList<HashMap<String, String>> blogPosts = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < jsonPosts.length(); i++) {
                    JSONObject post = jsonPosts.getJSONObject(i);
                    String language = post.getString(KEY_TITLE);
                    language = Html.fromHtml(language).toString();

                    HashMap<String, String> blogPost = new HashMap<String, String>();
                    blogPost.put(KEY_TITLE, language);
                    blogPosts.add(blogPost);
                }

                String[] keys = {KEY_TITLE};
                int[] ids = {android.R.id.text1};
                SimpleAdapter adapter = new SimpleAdapter(this, blogPosts,
                        android.R.layout.simple_expandable_list_item_1, keys,
                        ids);

                setListAdapter(adapter);
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

    private class GetBlogPostsTask extends AsyncTask<Object, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Object... params) {
            int responseCode = -1;
            JSONArray jsonResponse = null;
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(
                    "http://api.rafalmanka.pl/api/fetchLanguages");

            try {
                HttpResponse response = client.execute(httpget);
                StatusLine statusLine = response.getStatusLine();
                Log.d(TAG, "status line: " + statusLine);
                responseCode = statusLine.getStatusCode();
                Log.i(TAG, responseCode + "");
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

                    jsonResponse = new JSONArray(builder.toString());
                    Log.i(TAG, builder.toString());
                } else {
                    Log.d(TAG, builder.toString());
                }
            } catch (JSONException e) {
                LogException(e);
            } catch (Exception e) {
                LogException(e);
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            mTopicsData = result;
            handleBlogResponse();
        }

    }

}
