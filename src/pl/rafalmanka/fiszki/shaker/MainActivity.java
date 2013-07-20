package pl.rafalmanka.fiszki.shaker;

import java.util.ArrayList;

import pl.rafalmanka.fiszki.shaker.ShakeDetector.OnShakeListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private final String TAG = "MainActivity";
	private TextView mWordOrig;
	private TextView mWordTranslation;
	private Typeface mTypeFace;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;
	private Word mWord;
	// private Animation bounce;
	private MediaPlayer player;
	private int mWordCorrect = 0;
	private int mWordIncorrect = 0;
	private TextView mCorrectTotal;
	private TextView mIncorrectTotal;
	private View mButtonMarkCorrect;
	private View mButtonMarkIncorrect;
	private boolean mUndo = false;
	private Button mButtonNextWord;
	private boolean mAllowNextWord = true;
	private boolean mFlipcardFace = true;
	private FlipAnimation mFlipAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "OnCreated");
		super.onCreate(savedInstanceState);

		DatabaseHandler ndm = new DatabaseHandler(getApplicationContext());

		Log.d(TAG, "creating preferences");

		Log.d(TAG, "creating layout of an activity");
		setContentView(R.layout.activity_main);

		Log.d(TAG, "creating instance of bouncing animation");
		// bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

		Log.d(TAG, "assigning strings from layout to variables");

		mTypeFace = Typeface.createFromAsset(getAssets(),
				"ubuntu_font/Ubuntu-B.ttf");
		mWordOrig = (TextView) findViewById(R.id.flipcard_front);
		mWordOrig.setTypeface(mTypeFace);
		mWordTranslation = (TextView) findViewById(R.id.flipcard_back);
		mWordTranslation.setTypeface(mTypeFace);

		Log.d(TAG, "creating instance of sensor event");
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Log.d(TAG, "creating onshakelistener");

		mCorrectTotal = (TextView) findViewById(R.id.textView_correct_total);
		mIncorrectTotal = (TextView) findViewById(R.id.textView_incorrect_total);

		mButtonMarkCorrect = (TextView) findViewById(R.id.button_word_known);
		mButtonMarkIncorrect = (TextView) findViewById(R.id.button_word_unknown);
		mButtonNextWord = (Button) findViewById(R.id.button_next_word);
		mButtonNextWord.setClickable(false);
		mButtonNextWord.setTextColor(Color.DKGRAY);
		// mButtonNextWord.setTextColor(Color.DKGRAY);
		onEvent();

		mShakeDetector = new ShakeDetector(new OnShakeListener() {

			@Override
			public void onShake() {
				Log.d(TAG, "onSheked");
				onEvent();
				clearAllButtons();
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mShakeDetector);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mShakeDetector, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI);
	}

	private void animateFiszka(TextView textView, int duration) {
		AlphaAnimation fadeInAnimation = new AlphaAnimation(0, 1);
		fadeInAnimation.setDuration(duration);
		fadeInAnimation.setFillAfter(true);
		textView.setAnimation(fadeInAnimation);
	}

	private void playSound() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (sharedPreferences.getBoolean("SOUND_PREFERENCE", false)) {
			player = MediaPlayer.create(this, R.raw.spell);
			player.start();
			player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent settings_intent = new Intent(this, SettingsActivity.class);
			startActivity(settings_intent);
			break;
		case R.id.menu_add_new_word:
			Intent add_new_word_intent = new Intent(this,
					AddNewWordActivity.class);
			startActivity(add_new_word_intent);
			break;
		case R.id.menu_add_new_dictionary:
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			String language = sharedPreferences.getString("LANGUAGE", "");
			Intent add_new_dictionary_intent = new Intent(this,
					AddNewDictionaryActivity.class);
			add_new_dictionary_intent.putExtra("LANGUAGE", language);
			startActivity(add_new_dictionary_intent);
			break;
		}

		return false;
	}

	private void onEvent() {
		if (mAllowNextWord) {
			Log.d(TAG, "allowed");
			mAllowNextWord = false;
			Log.d(TAG, "mAllowNextWord changed to false 2");
			Log.d(TAG, "onEvented");

			Log.d(TAG, "preparing database");
			DatabaseHandler db = new DatabaseHandler(this);

			Log.d(TAG, "fetching single random row");
			mWord = db.getRandom();

			Log.d(TAG, "name of set: " + mWord.getNameOfSet() + " ,word: "
					+ mWord.getWord() + " , language: " + mWord.getLanguage());

			Log.d(TAG, "setting new title: " + mWord.getWord());
			mWordOrig.setText(Html.fromHtml(mWord.getWord()));

			Log.d(TAG, "animating title");
			animateFiszka(mWordOrig, 1500);
			// mWordOrig.startAnimation(bounce);

			Log.d(TAG,
					"showing description: "
							+ mWord.getConcatenatedTranslations());
			mWordTranslation.setText(Html.fromHtml(mWord
					.getConcatenatedTranslations()));

			playSound();
		} else {
			Log.d(TAG, "not allowed");
		}
	}

	public void gotoSelectLanguage(View v) {
		Log.i(TAG, "onClicked");

		try {
			Intent intent = new Intent(this, LanguageListActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "Error: ", e);
		}
	}

	public void onNextWordClick(View view) {
		if (!mFlipcardFace) {
			onCardClick(view);

			mFlipAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					onEvent();
					clearAllButtons();
				}

			});
		} else {
			onEvent();
			clearAllButtons();
		}

	}

	private void clearAllButtons() {
		clearButton(mButtonMarkCorrect, R.string.know_this_word,
				new View[] { mButtonMarkIncorrect });
		clearButton(mButtonMarkIncorrect, R.string.dont_know_this_word,
				new View[] { mButtonMarkCorrect });
	}

	private void clearButton(View button, int resource, View[] unblockButtons) {
		Log.d(TAG, "clearing button: " + button.getId());
		if (unblockButtons != null) {
			for (View unblockButton : unblockButtons) {
				Log.d(TAG, "unblocking button: " + unblockButton.getId());
				unblockButton.setClickable(true);
				((TextView) unblockButton).setTextColor(Color.BLACK);
			}
		}
		Log.d(TAG, "setting text of button back to " + getText(resource));
		((TextView) button).setText(resource);
		Log.d(TAG, "setting undo to false ");
		mUndo = false;
		mButtonNextWord.setClickable(false);
		mButtonNextWord.setTextColor(Color.DKGRAY);
		mAllowNextWord = false;
		Log.d(TAG, "mAllowNextWord changed to false 1");
	}

	private void changeButtonToUndo(View button, View[] blockedButtons) {
		Log.d(TAG, "changing button to undo button: " + button.getId());
		if (blockedButtons != null) {
			for (View blockedButton : blockedButtons) {
				Log.d(TAG, "blopcking button " + button.getId());
				blockedButton.setClickable(false);
				((TextView) blockedButton).setTextColor(Color.DKGRAY);
			}
		}
		((TextView) button).setText(R.string.undo);
		mUndo = true;
		mButtonNextWord.setClickable(true);
		mButtonNextWord.setTextColor(Color.BLACK);
		mAllowNextWord = true;
		Log.d(TAG, "mAllowNextWord changed to true");
	}

	public void onCardClick(View view) {
		if (mFlipcardFace) {
			mFlipcardFace = false;
		} else {
			mFlipcardFace = true;
		}
		flipCard();
	}

	private void flipCard() {
		View rootLayout = (View) findViewById(R.id.main_activity_root);
		View cardFace = (View) findViewById(R.id.flipcard_front);
		View cardBack = (View) findViewById(R.id.flipcard_back);

		mFlipAnimation = new FlipAnimation(cardFace, cardBack);
		if (cardFace.getVisibility() == View.GONE) {
			mFlipAnimation.reverse();
		}

		rootLayout.startAnimation(mFlipAnimation);
	}

	public void addScore(View view) {
		Log.d(TAG, "addScore for id: " + view.getId());

		switch (view.getId()) {
		case R.id.button_word_known:
			Log.d(TAG, "word known id: " + R.id.button_word_known);
			if (mUndo) {
				mWordCorrect--;
				clearButton(mButtonMarkCorrect, R.string.know_this_word,
						new View[] { mButtonMarkIncorrect });
			} else {
				mWordCorrect++;
				changeButtonToUndo(mButtonMarkCorrect,
						new View[] { mButtonMarkIncorrect });
			}
			mCorrectTotal.setText(mWordCorrect + "");
			break;
		case R.id.button_word_unknown:
			if (mUndo) {
				mWordIncorrect--;
				clearButton(mButtonMarkIncorrect, R.string.dont_know_this_word,
						new View[] { mButtonMarkCorrect });
			} else {
				mWordIncorrect++;
				changeButtonToUndo(mButtonMarkIncorrect,
						new View[] { mButtonMarkCorrect });
			}
			mIncorrectTotal.setText(mWordIncorrect + "");
			break;
		}

	}

}
