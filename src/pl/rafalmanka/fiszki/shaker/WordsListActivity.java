package pl.rafalmanka.fiszki.shaker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WordsListActivity extends ListActivity {

	public static final int NUMBER_OF_LANGUAGES = 20;
	public static final String TAG = TopicsListActivity.class.getSimpleName();
	private JSONArray mTopicData = null;
	protected ProgressBar mProgressBar;
	private final String KEY_TITLE = "topic_title";
	private TextView noItemsToDisplay;
	private int mTopicId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language_list);
		Log.d(TAG, "onCreated");
		Bundle bundle = getIntent().getExtras();
		mTopicId = bundle.getInt("language_id");
		Log.d(TAG, "retrieving extras: " + mTopicId);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		Log.d(TAG, "process bar created");
		noItemsToDisplay = (TextView) findViewById(R.id.no_items_to_display);
		Log.d(TAG, "no items to display textarea created");

		if (isNetworkAvaileable()) {
			Log.d(TAG, "network is availeable, continue... ");
			mProgressBar.setVisibility(View.VISIBLE);
			Log.d(TAG, "progress bar set to visible");
			GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
			Log.d(TAG, "async task finished");
			getBlogPostsTask.execute();
		} else {			
			//just in case for future reference
			// Toast.makeText(this, "Network is unavaileable!",
			// Toast.LENGTH_LONG).show();
			Log.d(TAG, "no internet connection, generating alert");
			updateDisplayForErrors();
			Log.d(TAG, "setting text for no items to display textview");
			noItemsToDisplay.setText(R.string.no_items_to_display);
			Log.d(TAG, "setting progressbar to invisible");
			mProgressBar.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		try {
			JSONObject jsonPost = mTopicData.getJSONObject(position);
			mTopicId = Integer.parseInt(jsonPost.getString("topic_id"));
			Log.d(TAG, "decreasing value of topic_id by 1 since Table");
			Log.d(TAG, "topic id: "+mTopicId+" value: "+jsonPost.getString("topic_title")+"  passed to another intent: "+TopicsListActivity.class.getSimpleName());
			Intent intent = new Intent(this, WordsListActivity.class);
			Bundle bundle = new Bundle();
			Log.d(TAG, "topic_id put into new Bundle");
			bundle.putInt("topic_id", mTopicId); 
			Log.d(TAG, "Bundle put into intent");
			intent.putExtras(bundle); 
			Log.d(TAG, "start new activity and finish");
			startActivity(intent);
			finish();
		} catch (JSONException e) {
			LogException(e);
		}

	}
	
	private void LogVerbose(String verbose) {
		Log.v(TAG, verbose);
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
		if (mTopicData.length() == 0) {
			updateDisplayForErrors();
		} else {
			try {
				JSONArray jsonPosts = mTopicData;
				ArrayList<HashMap<String, String>> blogPosts = new ArrayList<HashMap<String, String>>();
				for (int i = 0; i < jsonPosts.length(); i++) {
					JSONObject post = jsonPosts.getJSONObject(i);
					String language = post.getString(KEY_TITLE);
					language = Html.fromHtml(language).toString();

					HashMap<String, String> blogPost = new HashMap<String, String>();
					blogPost.put(KEY_TITLE, language);
					blogPosts.add(blogPost);
				}

				String[] keys = { KEY_TITLE };
				int[] ids = { android.R.id.text1 };
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
					"http://api.rafalmanka.pl/api/fetchTopics?language="
							+ mTopicId);

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

					jsonResponse = new JSONArray(builder.toString());
					LogInfo("builder.toString(): "+builder.toString());
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
		protected void onPostExecute(JSONArray result) {
			mTopicData = result;
			handleBlogResponse();
		}

	}

}
