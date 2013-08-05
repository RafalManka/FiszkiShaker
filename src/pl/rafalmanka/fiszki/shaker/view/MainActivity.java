package pl.rafalmanka.fiszki.shaker.view;

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
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import pl.rafalmanka.fiszki.shaker.R;
import pl.rafalmanka.fiszki.shaker.animations.FlipAnimation;
import pl.rafalmanka.fiszki.shaker.model.DatabaseHandler;
import pl.rafalmanka.fiszki.shaker.model.Word;
import pl.rafalmanka.fiszki.shaker.utils.ShakeDetector;
import pl.rafalmanka.fiszki.shaker.utils.ShakeDetector.OnShakeListener;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {


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
    private SharedPreferences mSharedPreferences;
    private int mCounter = 0;
    private TextToSpeech mTextToSpeech;
    private String mCurrentWord;
    private ImageButton mVoiceButton;
    private int mStatus;
    private String mLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreated");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);      
        setContentView(R.layout.activity_main);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title_bar);
        
        
        
        TextView titleBar = (TextView) findViewById(R.id.textView_titlebar);
        titleBar.setText(R.string.activity_flipcards);
        LinearLayout ll = (LinearLayout) findViewById(R.id.layout_titlebar);
        ll.setBackgroundColor(getResources().getColor(R.color.colors_titlebar_purple));
        ImageButton ib = (ImageButton) findViewById(R.id.imageButton_titlebar);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StartingPointActivity.class);
                startActivity(intent);
            }
        });


        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        mTypeFace = Typeface.createFromAsset(getAssets(),
                "ubuntu_font/Ubuntu-B.ttf");
        mWordOrig = (TextView) findViewById(R.id.flipcard_front);
        mWordOrig.setTypeface(mTypeFace);
        mWordTranslation = (TextView) findViewById(R.id.flipcard_back);
        mWordTranslation.setTypeface(mTypeFace);
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
        onEvent();

        mShakeDetector = new ShakeDetector(new OnShakeListener() {

            @Override
            public void onShake() {
                Log.d(TAG, "onSheked");
                onEvent();
                clearAllButtons();
            }
        });


        mTextToSpeech = new TextToSpeech(this, this);

        mVoiceButton = (ImageButton) findViewById(R.id.imageButton_voice);
        mVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mStatus == TextToSpeech.SUCCESS) {

                    Log.d(TAG, "Locale.US: "+mLanguage);
                    int result = mTextToSpeech.setLanguage(new Locale(mLanguage));

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    } else {
                        mVoiceButton.setEnabled(true);
                        speakOut();
                    }

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }

            }
        });

    }

    private void speakOut() {
        mTextToSpeech.speak(mCurrentWord, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {
        mStatus=status;

    }



    @Override
    protected void onPause() {

        if( mTextToSpeech != null ){
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }

        if(mSensorManager != null ){
            mSensorManager.unregisterListener(mShakeDetector);
        }

        super.onPause();

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

        if (mSharedPreferences.getBoolean(SettingsActivity.SOUND_PREFERENCE, false)) {
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





    public void gotoAddNewWord(View v) {
        Log.i(TAG, "onClicked");

        try {
            Intent intent = new Intent(this, AddNewWordActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error: ", e);
        }
    }


    private void onEvent() {

        if (mAllowNextWord) {
            mAllowNextWord = false;

            mButtonNextWord.setClickable(false);
            mButtonNextWord.setBackgroundResource(R.drawable.arrow_blocked);

            mButtonMarkCorrect.setClickable(true);
            mButtonMarkCorrect.setBackgroundResource(R.drawable.button_plus);

            mButtonMarkIncorrect.setClickable(true);
            mButtonMarkIncorrect.setBackgroundResource(R.drawable.button_minus);

            DatabaseHandler db = new DatabaseHandler(this);

            if (mSharedPreferences.getBoolean(SettingsActivity.RANDOMIZE_PREFERENCE, false)) {
                mWord = db.getRandom();
            } else {
                mWord = db.getNext(mCounter);
                mCounter++;
            }
            mCurrentWord = Html.fromHtml(mWord.getWord()).toString();
            mLanguage = mWord.getLanguage();
            mWordOrig.setText(mCurrentWord);
            animateFiszka(mWordOrig, 1500);
            mWordTranslation.setText(Html.fromHtml(mWord
                    .getConcatenatedTranslations()));

            playSound();
        } else {
            Log.d(TAG, "you have to mark answer before going to next word");
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

        mButtonMarkCorrect.setClickable(true);
        mButtonMarkCorrect.setBackgroundResource(R.drawable.button_plus);

        mButtonMarkIncorrect.setClickable(true);
        mButtonMarkIncorrect.setBackgroundResource(R.drawable.button_minus);

        mButtonNextWord.setClickable(false);
        mButtonNextWord.setBackgroundResource(R.drawable.arrow_blocked);
        mUndo=false;
    }

    public void addScore(View view) {
        Log.d(TAG, "addScore for id: " + view.getId());

        switch (view.getId()) {
            case R.id.button_word_known:
                if (mUndo) {
                    mUndo=false;
                    mWordCorrect--;
                    mAllowNextWord = false;

                    mButtonMarkCorrect.setClickable(true);
                    mButtonMarkCorrect.setBackgroundResource(R.drawable.button_plus);

                    mButtonMarkIncorrect.setClickable(true);
                    mButtonMarkIncorrect.setBackgroundResource(R.drawable.button_minus);

                    mButtonNextWord.setClickable(false);
                    mButtonNextWord.setBackgroundResource(R.drawable.arrow_blocked);

                } else {
                    mUndo=true;
                    mWordCorrect++;
                    mAllowNextWord = true;

                    mButtonMarkCorrect.setClickable(true);
                    mButtonMarkCorrect.setBackgroundResource(R.drawable.button_undo);

                    mButtonMarkIncorrect.setClickable(false);
                    mButtonMarkIncorrect.setBackgroundResource(R.drawable.minus_blocked);

                    mButtonNextWord.setClickable(true);
                    mButtonNextWord.setBackgroundResource(R.drawable.button_arrow);

                }
                mCorrectTotal.setText(mWordCorrect + "");
                break;
            case R.id.button_word_unknown:
                if (mUndo) {
                    mUndo=false;
                    mWordIncorrect--;
                    mAllowNextWord = false;

                    mButtonMarkCorrect.setClickable(true);
                    mButtonMarkCorrect.setBackgroundResource(R.drawable.button_plus);

                    mButtonMarkIncorrect.setClickable(true);
                    mButtonMarkIncorrect.setBackgroundResource(R.drawable.button_minus);

                    mButtonNextWord.setClickable(false);
                    mButtonNextWord.setBackgroundResource(R.drawable.arrow_blocked);


                } else {
                    mUndo=true;
                    mWordIncorrect++;
                    mAllowNextWord = true;

                    mButtonMarkCorrect.setClickable(false);
                    mButtonMarkCorrect.setBackgroundResource(R.drawable.plus_blocked);

                    mButtonMarkIncorrect.setClickable(true);
                    mButtonMarkIncorrect.setBackgroundResource(R.drawable.button_undo);

                    mButtonNextWord.setClickable(true);
                    mButtonNextWord.setBackgroundResource(R.drawable.button_arrow);

                }
                mIncorrectTotal.setText(mWordIncorrect + "");
                break;
        }

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
                mSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(this);
                String language = mSharedPreferences.getString("LANGUAGE", "");
                Intent add_new_dictionary_intent = new Intent(this,
                        AddNewWordsetActivity.class);
                add_new_dictionary_intent.putExtra("LANGUAGE", language);
                startActivity(add_new_dictionary_intent);
                break;
            case R.id.menu_choose_wordset:
                Intent choose_wordset_intent = new Intent(this,
                        ChooseLocalSetActivity.class);
                Log.d(TAG, "intent created");
                startActivity(choose_wordset_intent);
                break;
        }

        return false;
    }


}
