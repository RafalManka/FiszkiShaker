package pl.rafalmanka.harrypotter.spellshaker;

import pl.rafalmanka.fiszki.shaker.R;
import pl.rafalmanka.harrypotter.spellshaker.ShakeDetector.OnShakeListener;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private final String TAG = "MainActivity";
	private TextView mWordOrig;
	private TextView mWordTranslation;
	private ImageView mFiszkiImage;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;
	private boolean mTitleActive = false;
	private Word mWord;
	private String mLanguage = "pl";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		Log.d(TAG, "OnCreated");
		mWordOrig = (TextView) findViewById(R.id.textView1);
		mWordTranslation = (TextView) findViewById(R.id.TextView11);
		mFiszkiImage = (ImageView) findViewById(R.id.imageView1);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetector(new OnShakeListener() {
			@Override
			public void onShake() {
				handleNewAnswer();
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
		MediaPlayer player = MediaPlayer.create(this, R.raw.spell);
		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
	}

	private void animateBackgroundImage() {
		mFiszkiImage.setImageResource(R.drawable.magician_animation);
		AnimationDrawable fiszkiAnimation = (AnimationDrawable) mFiszkiImage
				.getDrawable();
		if (fiszkiAnimation.isRunning()) {
			fiszkiAnimation.stop();
		}
		fiszkiAnimation.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void handleNewAnswer() {
		Log.d(TAG, "onClicked");
		if (mTitleActive) {
			// if title is visible show description
			mWordTranslation.setText(Html.fromHtml(mWord.getDescription()));
			Log.d(TAG, "showing description: " + mWord.getDescription());
			animateFiszka(mWordTranslation, 1500);
			Log.d(TAG, "animating description");
			mTitleActive = false;
		} else {
			// if title and description is visible
			// generate new pair of strings and show title
			Log.d(TAG, "preparing database");
			DatabaseHandler db = new DatabaseHandler(this);
			Log.d(TAG, "fetching single random row");
			mWord = db.getRandom(mLanguage);
			String log = "Id: " + mWord.getID() + " ,word: " + mWord.getWord()
					+ " ,description: " + Html.fromHtml(mWord.getDescription())
					+ " , language: " + mWord.getLanguage();
			// Writing Contacts to log
			Log.d(TAG, log);

			mWordTranslation.setText("");
			mWordOrig.setText(Html.fromHtml(mWord.getWord()));
			animateFiszka(mWordOrig, 1500);
			mTitleActive = true;
		}

		animateBackgroundImage();

		playSound();
	}

}
