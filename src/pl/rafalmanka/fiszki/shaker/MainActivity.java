package pl.rafalmanka.fiszki.shaker;

import pl.rafalmanka.fiszki.shaker.ShakeDetector.OnShakeListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

public class MainActivity extends Activity {
	private final String TAG = "MainActivity";
	private TextView mWordOrig;
	private TextView mWordTranslation;
	private TextView mBottomTextInstruction;
	private Typeface mTypeFace;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;
	private boolean mTitleActive = false;
	private Word mWord;
	//private Animation bounce;
	private MediaPlayer player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "OnCreated");
		super.onCreate(savedInstanceState);

		Log.d(TAG, "creating layout of an activity");
		setContentView(R.layout.activity_main);

		Log.d(TAG, "creating instance of bouncing animation");
		//bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

		Log.d(TAG, "assigning strings from layout to variables");

		mTypeFace = Typeface.createFromAsset(getAssets(),
				"ubuntu_font/Ubuntu-B.ttf");
		mWordOrig = (TextView) findViewById(R.id.bouncing_string);
		mWordOrig.setTypeface(mTypeFace);
		mWordTranslation = (TextView) findViewById(R.id.bouncing_description);
		mWordTranslation.setTypeface(mTypeFace);
		mBottomTextInstruction = (TextView) findViewById(R.id.shake_to_perform_action);
		mBottomTextInstruction.setTypeface(mTypeFace);

		Log.d(TAG, "creating instance of sensor event");
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Log.d(TAG, "creating onshakelistener");

		mShakeDetector = new ShakeDetector(new OnShakeListener() {

			@Override
			public void onShake() {
				Log.d(TAG, "onSheked");
				onEvent();
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
		player = MediaPlayer.create(this, R.raw.spell);
		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void onEvent() {
		Log.d(TAG, "onEvented");
		
		if (!mTitleActive) {
			Log.d(TAG, "preparing database");
			DatabaseHandler db = new DatabaseHandler(this);

			Log.d(TAG, "fetching single random row");
			mWord = db.getRandom();
			Log.d(TAG, "Id: " + mWord.getID() + " ,word: " + mWord.getWord()
					+ " ,description: " + Html.fromHtml(mWord.getDescription())
					+ " , language: " + mWord.getLanguage());

			Log.d(TAG,
					"deleting description text, setting word text to new value");
			mWordOrig.setText(Html.fromHtml(mWord.getWord()));
			mWordTranslation.setText("");

			Log.d(TAG, "animating title");
			animateFiszka(mWordOrig, 1500);
			//mWordOrig.startAnimation(bounce);

			Log.d(TAG, "setting flag, that the title is showing");
			mTitleActive = true;

		} else {

			Log.d(TAG, "showing description: " + mWord.getDescription());
			mWordTranslation.setText(Html.fromHtml(mWord.getDescription()));

			Log.d(TAG, "animating description");
			animateFiszka(mWordTranslation, 1500);

			Log.d(TAG, "setting flag, that the title is NOT showing");
			mTitleActive = false;

			Log.d(TAG, "animating background phase 2");
		}

		playSound();
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

}
