package pl.rafalmanka.fiszki.shaker.view;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import pl.rafalmanka.fiszki.shaker.R;
import pl.rafalmanka.fiszki.shaker.services.PushWordsService;

/**
 * Created by r.manka on 24.07.13.
 */
public class StartingPointActivity extends Activity {

    private static final String TAG = StartingPointActivity.class.getSimpleName();
    private ImageView mBookImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.starting_point);


		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_bar);
		TextView titleBar = (TextView) findViewById(R.id.textView_titlebar);
		titleBar.setText(R.string.title_bar_starting_point_activity);

        TextView flipcards = (TextView) findViewById(R.id.textView_flipcards);
        flipcards.setText(R.string.activity_flipcards);

        TextView settings = (TextView) findViewById(R.id.textView_settings);
        settings.setText(R.string.activity_settings);

        TextView downloadWordset = (TextView) findViewById(R.id.textView_download);
        downloadWordset.setText(R.string.activity_download_wordset);

        TextView settingsActivity = (TextView) findViewById(R.id.textView_add_wordset);
        settingsActivity.setText(R.string.activity_add_new_wordset);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
           Log.d(TAG, "wifi is connected");
           startService(new Intent(this, PushWordsService.class));
        } else {
            Log.d(TAG, "wifi not connected");
        }

    }

    public void gotoFlipcards(View view){

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void gotoSettings(View view){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    public void gotoAddWordset(View view){
        Intent intent = new Intent(this, AddNewWordsetActivity.class);
        startActivity(intent);
    }

    public void gotoManageSets(View view){
        Intent intent = new Intent(this, ChooseLocalSetActivity.class);
        startActivity(intent);
    }

    public void gotoDownloadWordset(View view){
        Intent intent = new Intent(this, LanguageListActivity.class);
        startActivity(intent);
    }
}
