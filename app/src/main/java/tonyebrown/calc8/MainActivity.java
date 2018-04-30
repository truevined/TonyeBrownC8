package tonyebrown.calc8;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tonyebrown.calc8.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import net.objecthunter.exp4j.operator.Operator;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;
import java.util.UUID;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import me.grantland.widget.AutofitHelper;

public class MainActivity extends AppCompatActivity implements RecognitionListener {
    private static final String LOG_TAG = "Main Activity";
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 3;
    private static final String EXTRA_EVENT_ID = "Calc8Event";
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    TextView display, hDisplay, activeF1, activeF2, activeF3, activeF4, activeF5;

    Vibrator feedback;

    //toggles
    int start = 0;//checks if calculation has started
    int shiftListener = 0; //helps toggle shift mode, on and off
    int degListener = 0; //helps toggle degree mode, on and off
    int radListener = 0; //helps toggle radian mode, on and off
    int hypListener = 0; //helps toggle hyp mode, on and off

    int decimalPointCount = 0;
    int decimalPointCount2 = 0;

    float number1 = 0; //holds first number
    float number2 = 0; //holds second number
    String backup = ""; //temporal backup of textview-display's contents
    String result = "0"; //holds the result
    String speechResult = ""; //holds the string value of speech detected
    char current; //current character from string display
    boolean calculating = false;
    private boolean mIsListening;

    Stack<Float> MemoryFunction = new Stack<>(); //holds values user stores in memory

    private InterstitialAd mInterstitialAd;
    private Handler adHandler;       // Handler to display the ad on the UI thread
    private Runnable displayAd;     // Code to execute to perform this operation

    TextToSpeech t1;
    Boolean shouldVibrate = true; // whether buttons should vibrate on press

    /**********************
     * overriden onCreate method initializing textviews, buttons, etc.
     *
     * @param savedInstanceState helps with preserving data on resume and on start
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        feedback = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); //vibrator service start

        //push a few zeros into the memory stack
       /* MemoryFunction.push((float) 0);
        MemoryFunction.push((float) 0);
        MemoryFunction.push((float) 0);*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sets status bar and navigation bar color to #212121
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#212121"));
            getWindow().setStatusBarColor(Color.parseColor("#212121"));
        }

        // get all views to display stuff on
        display = (TextView) findViewById(R.id.textViewLCD);
        hDisplay = (TextView) findViewById(R.id.textViewHistory);
        activeF1 = (TextView) findViewById(R.id.textViewActiveFn1);
        activeF2 = (TextView) findViewById(R.id.textViewActiveFn2);
        activeF3 = (TextView) findViewById(R.id.textViewActiveFn3);
        activeF4 = (TextView) findViewById(R.id.textViewActiveFn4);
        activeF5 = (TextView) findViewById(R.id.textViewActiveFn5);

        // set font for all displays
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/quartzbold.ttf");
        display.setTypeface(font);
        hDisplay.setTypeface(font);
        activeF1.setTypeface(font);
        activeF2.setTypeface(font);
        activeF3.setTypeface(font);
        activeF4.setTypeface(font);
        activeF5.setTypeface(font);

        //dynamically control the maxLength of mainLCD and history textviews
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(49);
        display.setFilters(filterArray);
        hDisplay.setFilters(filterArray);

        /*
         *     I'm using an onTouchListener to control button image state changes - different image
         *     for when an image is being held down, and for when it's released or not being pressed
         */

        //numbers
        findViewById(R.id.buttonOne).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonTwo).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonThree).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonFour).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonFive).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonSix).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonSeven).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonEight).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonNine).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonZero).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonDecimalPoint).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonPlusMinus).setOnTouchListener(handleButtons2);

        //basicOperations
        findViewById(R.id.buttonMultiply).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonDivide).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonSubtraction).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonAddition).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonEquals).setOnTouchListener(handleButtons2);

        //resets
        findViewById(R.id.buttonCLR).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonDel).setOnTouchListener(handleButtons2);

        //scifi operations (lol)
        findViewById(R.id.buttonShift).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonDeg).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonRad).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonPi).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonRand).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonSin).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonCos).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonTan).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonLog).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonMPlus).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonXSquared).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonSquareRoot).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonLeftBracket).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonRightBracket).setOnTouchListener(handleButtons2);
        findViewById(R.id.buttonMR).setOnTouchListener(handleButtons2);

        display.setOnClickListener(new View.OnClickListener() { //TODO: currently not working
            @Override
            public void onClick(View current) {
                //TextView btn = (TextView) current;
                display.setFocusable(true);
                display.setEnabled(true);
                display.setFocusableInTouchMode(true);
                display.setClickable(true);
                display.requestFocus();
            }
        });

        /*
         *     I'm using an onClickListener to control calculations, input and main calculator logic
         */

        //numbers
        findViewById(R.id.buttonOne).setOnClickListener(handleButtons);
        findViewById(R.id.buttonTwo).setOnClickListener(handleButtons);
        findViewById(R.id.buttonThree).setOnClickListener(handleButtons);
        findViewById(R.id.buttonFour).setOnClickListener(handleButtons);
        findViewById(R.id.buttonFive).setOnClickListener(handleButtons);
        findViewById(R.id.buttonSix).setOnClickListener(handleButtons);
        findViewById(R.id.buttonSeven).setOnClickListener(handleButtons);
        findViewById(R.id.buttonEight).setOnClickListener(handleButtons);
        findViewById(R.id.buttonNine).setOnClickListener(handleButtons);
        findViewById(R.id.buttonZero).setOnClickListener(handleButtons);
        findViewById(R.id.buttonDecimalPoint).setOnClickListener(handleButtons);
        findViewById(R.id.buttonPlusMinus).setOnClickListener(handleButtons);

        //basicOperations
        findViewById(R.id.buttonMultiply).setOnClickListener(handleButtons);
        findViewById(R.id.buttonDivide).setOnClickListener(handleButtons);
        findViewById(R.id.buttonSubtraction).setOnClickListener(handleButtons);
        findViewById(R.id.buttonAddition).setOnClickListener(handleButtons);
        findViewById(R.id.buttonEquals).setOnClickListener(handleButtons); //equals button
        findViewById(R.id.buttonEquals).setOnLongClickListener(handleSpeech); //long click / long press equals button for speech
        findViewById(R.id.buttonShift).setOnLongClickListener(handleSpeech); //long click / long press equals button for speech
        findViewById(R.id.textViewLCD).setOnLongClickListener(handleDisplay); //long click / long press display button for speech

        //progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);

        //resets
        findViewById(R.id.buttonCLR).setOnClickListener(handleButtons);
        findViewById(R.id.buttonDel).setOnClickListener(handleButtons);

        //scifi operations (lol)
        findViewById(R.id.buttonShift).setOnClickListener(handleButtons);
        findViewById(R.id.buttonDeg).setOnClickListener(handleButtons);
        findViewById(R.id.buttonRad).setOnClickListener(handleButtons);
        findViewById(R.id.buttonPi).setOnClickListener(handleButtons);
        findViewById(R.id.buttonRand).setOnClickListener(handleButtons);
        findViewById(R.id.buttonSin).setOnClickListener(handleButtons);
        findViewById(R.id.buttonCos).setOnClickListener(handleButtons);
        findViewById(R.id.buttonTan).setOnClickListener(handleButtons);
        findViewById(R.id.buttonLog).setOnClickListener(handleButtons);
        findViewById(R.id.buttonMPlus).setOnClickListener(handleButtons);
        findViewById(R.id.buttonXSquared).setOnClickListener(handleButtons);
        findViewById(R.id.buttonSquareRoot).setOnClickListener(handleButtons);
        findViewById(R.id.buttonLeftBracket).setOnClickListener(handleButtons);
        findViewById(R.id.buttonRightBracket).setOnClickListener(handleButtons);
        findViewById(R.id.buttonMR).setOnClickListener(handleButtons);

        //makes text fit / resize to fit the main display. Added effect to history and mainLCD
        AutofitHelper.create(display);
        AutofitHelper.create(hDisplay);

        //get permission to use microphone
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

            }
        }

        display.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {//prevent more than one decimal point
                String mBackup = display.getText().toString();
                if (mBackup.contains("..")) //if the check for index of .. is not -1 (-1 means index doesn't exist
                {
                    String temp = display.getText().toString().replace("..", ".");
                    display.setText(temp);
                }

                if (mBackup.equals("00")) {
                    String temp = display.getText().toString().replace("00", "0");
                    display.setText(temp);
                }

                if (mBackup.contains("//")) {
                    String temp = display.getText().toString().replace("//", "/");
                    display.setText(temp);
                }

                if (mBackup.contains("**")) {
                    String temp = display.getText().toString().replace("**", "*");
                    display.setText(temp);
                }

                if (mBackup.contains("++")) {
                    String temp = display.getText().toString().replace("++", "+");
                    display.setText(temp);
                }

                /*if (decimalPointCount>=1 && mBackup.endsWith("..")){
                    deleteNumber();
                    display.append(".");
                }*/
            }
        });

        //text to speech stuff
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                setTextToSpeechLanguage();
            }
        });

        //Check if the Application is Running for the First time
        SharedPreferences appIntro = getSharedPreferences("vibrate", 0);  //load the preferences
        //shouldVibrate = appIntro.getBoolean("shouldVibrate", false); //see if it's run before, default no

    }

    private boolean ready;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setTextToSpeechLanguage() {

        Locale language = null;// = t1.getVoice().getLocale();

        // Check if we're running on Android 5.0 or higher
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            language = t1.getVoice().getLocale();

            if (language == null) {
                this.ready = false;
                Toast.makeText(this, "No language selected!", Toast.LENGTH_SHORT).show();
                return;
            }
            int result = t1.setLanguage(language);
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                this.ready = false;
                Toast.makeText(this, "Missing language data; cannot speak result!", Toast.LENGTH_SHORT).show();
                return;
            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                this.ready = false;
                Toast.makeText(this, "Your language is not supported!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                this.ready = true;
                Locale currentLanguage = t1.getVoice().getLocale();
                //Toast.makeText(this, "Language " + currentLanguage, Toast.LENGTH_SHORT).show();
            }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            if (speech != null)
                speech.destroy();//destroy speech listener


            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Speech Recognition is not available on this device! A newer version of Android is required!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Okay",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private void displayAd() {
        /***************** Show ads stuff
         *
         */

        /*mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1325276054254531/2987378679");*/

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1325276054254531/2987378679");

        AdRequest adRequest = new AdRequest.Builder().build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                //requestNewInterstitial();
                //text to speech stuff
                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        setTextToSpeechLanguage();
                    }
                });

            }

            public void onAdLoaded() {
                // Call displayInterstitial() function
                //if (calc8AdsRandom() >= 7)
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();

                    //hack to make sure text to speech keeps working
                    if (t1 != null) {
                        t1.stop();
                        t1.shutdown();
                    }
                }
            }
        });

        /*if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }*/
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "You\'re awesome!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "Text to speech function disabled!", Toast.LENGTH_SHORT).show();
                    updateLCD("Text to speech function disabled!");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }

        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        Toast.makeText(MainActivity.this, "Say something to calculate!", Toast.LENGTH_SHORT).show();
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        //progressBar.setIndeterminate(true);

        //***** MAYBE
        //***** I'm just testing here
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    protected long mSpeechRecognizerStartListeningTime = 0;

    protected synchronized void speechRecognizerStartListening(Intent intent) {
        if (speech != null) {
            this.mSpeechRecognizerStartListeningTime = System.currentTimeMillis();
            Log.d(LOG_TAG, "speechRecognizerStartListening");
            this.speech.startListening(intent);
        }
    }

    @Override
    public synchronized void onError(int error) {
        //Log.i(LOG_TAG, this.hashCode() + " - onError:" + error);

        boolean mSuccess = true;
        // Sometime onError will get called after onResults so we keep a boolean to ignore error also
        if (mSuccess) {
            Log.w(LOG_TAG, "Already success, ignoring error");

            if (error != 9)
                return;
        }

        long duration = System.currentTimeMillis() - mSpeechRecognizerStartListeningTime;
        if (duration < 500 && ((error == SpeechRecognizer.ERROR_NO_MATCH) || (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY))) {
            Log.w(LOG_TAG, "Doesn't seem like the system tried to listen at all. duration = " + duration + "ms. This might be a bug with onError and startListening methods of SpeechRecognizer");
            Log.w(LOG_TAG, "Going to ignore the error");

            if (error != 9)
                return;
        }

    /*
    // -- actual error handing code goes here.
    }

    @Override
    public void onError(int error) {
*/

        /*speech.cancel();*/


        String issue = null;
        if (error == 1) {
            issue = "ERROR_NETWORK_TIMEOUT";
        }
        if (error == 2) {
            issue = "ERROR_NETWORK";
        }
        if (error == 3) {
            issue = "ERROR_AUDIO";
        }
        if (error == 4) {
            issue = "ERROR_SERVER";
        }
        if (error == 5) {
            issue = "ERROR_CLIENT";
        }
        if (error == 6) {
            issue = "ERROR_SPEECH_TIMEOUT";
        }
        if (error == 7) {
            issue = "ERROR_NO_MATCH";
        }
        if (error == 8) {
            issue = "ERROR_RECOGNIZER_BUSY";
            speech.cancel();
            speech.startListening(recognizerIntent);
        }

        if (error == 9) {
            issue = "ERROR_INSUFFICIENT_PERMISSIONS";
        }

            //Log.d(LOG_TAG, "FAILED " + issue);

        switch (issue) {
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6": {
                calculatorError("Network Error! \nAre you connected to the internet?");
            }
            case "7": {
                calculatorError(issue);
            }
            case "8": {
                calculatorError(issue);
            }
            case "9": {
                //get permission to use microphone
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.RECORD_AUDIO)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {


                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                    }
                }
            }
        }
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
        mIsListening = false;
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        mIsListening = false;

        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";

        speechResult = matches.get(0);

        //ImageButton temp = (ImageButton) findViewById(R.id.buttonShift);
        //calc8Muscles(temp);

        String parsedSpeech = parseSpeechResults(speechResult); //parse speech for operators and more
        parsedSpeech = parsedSpeech.replace(" ", "");//remove spaces from speech string

        String speechCalculation = "";// = null;

        /*//for debugging
        speechCalculation = calc8(parsedSpeech);
        updateLCD(speechCalculation);
        updateHistory(speechResult);
        //end for debugging*/


        try {
            speechCalculation = calc8(parsedSpeech);
            updateLCD(speechCalculation);
            updateHistory(parsedSpeech);
        } catch (Exception e) {
            calculatorError("Could not understand speech! Try again!");
        }

        //speak results

        String finalToSpeak = speechResult + " is " + speechCalculation; //making the result more human

        /*if ((Double.parseDouble(speechCalculation)
            finalToSpeak = String.format("%.3e", finalToSpeak);*/

        if (finalToSpeak.contains("E"))
            finalToSpeak.replace("E", "raised to the power ");

        if (finalToSpeak.contains("-"))
            finalToSpeak.replace("-", "negative");

        if (speechCalculation.equals(""))
            finalToSpeak = "Please try again; I couldn\'t understand you!";

        // A random String (Unique ID).
        String utteranceId = UUID.randomUUID().toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            t1.speak(finalToSpeak, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }
        else
            t1.speak(finalToSpeak, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }


    private String parseSpeechResults(String speechResult) {
        String parsedResult = "";

        if (speechResult.contains("times")) {
            parsedResult = speechResult.replace("times", "*");
        }

        if (speechResult.contains("multiplied by")) {
            parsedResult = speechResult.replace("multiplied by", "*");
        }

        if (speechResult.contains("multiply by")) {
            parsedResult = speechResult.replace("multiply by", "*");
        }

        if (speechResult.contains("divided by")) {
            parsedResult = speechResult.replace("divided by", "/");
        }

        if (speechResult.contains("divide by")) {
            parsedResult = speechResult.replace("divide by", "-");
        }

        if (speechResult.contains("minus")) {
            parsedResult = speechResult.replace("minus", "-");
        }

        if (speechResult.contains("subtract")) {
            parsedResult = speechResult.replace("subtract", "-");
        }

        if (speechResult.contains("plus")) {
            parsedResult = speechResult.replace("plus", "+");
        }

        if (speechResult.contains("point")) {
            parsedResult = speechResult.replace("point", ".");
        }

        if (speechResult.contains("negative")) {
            parsedResult = speechResult.replace("negative", "-");
        }

        if (speechResult.contains("sign")) {
            parsedResult = speechResult.replace("sign", "sin");
        }

        if (speechResult.contains("sine")) {
            parsedResult = speechResult.replace("sine", "sin");
        }

        if (speechResult.contains("sign inverse")) {
            parsedResult = speechResult.replace("sign inverse", "asin");
        }

        if (speechResult.contains("cosin")) {
            parsedResult = speechResult.replace("cosin", "cos");
        }

        if (speechResult.contains("cosin inverse")) {
            parsedResult = speechResult.replace("cosin inverse", "acos");
        }

        if (speechResult.contains("cause")) {
            parsedResult = speechResult.replace("cause", "cos");
        }

        if (speechResult.contains("and half")) {
            parsedResult = speechResult.replace("and half", ".5");
        }

        if (speechResult.contains("and a quarter")) {
            parsedResult = speechResult.replace("and a quarter", ".25");
        }

        if (speechResult.contains("cosine")) {
            parsedResult = speechResult.replace("cosine", "cos");
        }

        if (speechResult.contains("of")) {
            parsedResult = speechResult.replace("of", " ");
        }

        if (speechResult.contains("find the")) {
            parsedResult = speechResult.replace("find the", " ");
        }

        if (speechResult.contains("calculate")) {
            parsedResult = speechResult.replace("calculate", " ");
        }

        if (speechResult.contains("what is")) {
            parsedResult = speechResult.replace("what is", " ");
        }

        if (speechResult.contains("the")) {
            parsedResult = speechResult.replace("the", " ");
        }

        if (speechResult.contains("tangent")) {
            parsedResult = speechResult.replace("tangent", "tan");
        }

        if (speechResult.contains("tangent inverse")) {
            parsedResult = speechResult.replace("tangent inverse", "atan");
        }

        if (speechResult.contains("log")) {
            parsedResult = speechResult.replace("log", "log10");
        }

        if (speechResult.contains("lin")) {
            parsedResult = speechResult.replace("lin", "ln");
        }

        if (speechResult.contains("lean")) {
            parsedResult = speechResult.replace("lean", "ln");
        }

        if (speechResult.contains("log inverse")) {
            parsedResult = speechResult.replace("log inverse", "ln");
        }

        if (speechResult.contains("square")) {
            parsedResult = speechResult.replace("square", "^2");
        }

        if (speechResult.contains("squared")) {
            parsedResult = speechResult.replace("squared", "^2");
        }

        if (speechResult.contains("raised to power")) {
            parsedResult = speechResult.replace("raised to power", "^");
        }

        if (speechResult.contains("raise to power")) {
            parsedResult = speechResult.replace("raise to power", "^");
        }

        if (speechResult.contains("raised to the power of")) {
            parsedResult = speechResult.replace("raised to the power of", "^");
        }

        if (speechResult.contains("square root of")) {
            parsedResult = speechResult.replace("square root of", "sqrt");
        }

        /*if (speechResult.contains("root")) {
            parsedResult = speechResult.replace("root", "|");
        }*/

        if (speechResult.contains("percent")) {
            parsedResult = speechResult.replace("percent", "%");
        }

        if (speechResult.contains("factorial")) {
            parsedResult = speechResult.replace("factorial", "!");
        }

        if (speechResult.contains("equals")) {
            parsedResult = speechResult.replace("equals", "");
        }

        if (speechResult.contains("is")) {
            parsedResult = speechResult.replace("is", "");
        }

        if (speechResult.contains("equals to")) {
            parsedResult = speechResult.replace("equals to", "");
        }

        if (parsedResult.equals("") || parsedResult.equals(" ") || parsedResult == null || parsedResult.isEmpty())
            return speechResult;
        else
            return parsedResult;
    }

    /*
     * onTouchListener handleButtons2 controls the button image "animation": A different image for when an image is pressed, and released
     * Used onTouchListener instead of XML to control button image changes based on my personal preference.
     * onTouchListener does not function in a way capable of accurately determining clicks for some functions (e.g shift toggle, etc.) in the Calculator,
     * so I'll use an onCLickListener to capture and process inputs.
     */
    private View.OnTouchListener handleButtons2 = new View.OnTouchListener() {
        public boolean onTouch(View current, MotionEvent event) {
            ImageButton btn = (ImageButton) current;

            /*switch (theme) {
                //theme 1
                case "Light":
                    default:
                {*/
                    /*View lightBg = findViewById(R.id.LinearLayoutAll);
                    lightBg.setBackgroundResource(R.drawable.calc8light_bg1);*/

            //Determine if button pressed is SHIFT button
            if (btn.getId() == R.id.buttonShift) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_35);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_35);
                }
            }

            //Determine if button pressed is DEGREE button
            if (btn.getId() == R.id.buttonDeg) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_37);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_37);
                }
            }

            //Determine if button pressed is RADIAN button
            if (btn.getId() == R.id.buttonRad) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_39);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_39);
                }
            }

            //Determine if button pressed is PI button
            if (btn.getId() == R.id.buttonPi) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_41);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_41);
                }
            }

            //Determine if button pressed is RANDOM button
            if (btn.getId() == R.id.buttonRand) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_43);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_43);
                }
            }

            //Determine if button pressed is SIN button
            if (btn.getId() == R.id.buttonSin) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_57);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_57);
                }
            }

            //Determine if button pressed is COS button
            if (btn.getId() == R.id.buttonCos) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_59);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_59);
                }
            }

            //Determine if button pressed is TAN button
            if (btn.getId() == R.id.buttonTan) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_61);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_61);
                }
            }

            //Determine if button pressed is LOG button
            if (btn.getId() == R.id.buttonLog) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_63);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_63);
                }
            }

            //Determine if button pressed is MemoryPLUS button
            if (btn.getId() == R.id.buttonMPlus) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_65);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_65);
                }
            }

            //Determine if button pressed is X SQUARED button
            if (btn.getId() == R.id.buttonXSquared) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_79);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_79);
                }
            }

            //Determine if button pressed is Square root button
            if (btn.getId() == R.id.buttonSquareRoot) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_81);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_81);
                }
            }

            //Determine if button pressed is left bracket button
            if (btn.getId() == R.id.buttonLeftBracket) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_83);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_83);
                }
            }

            //Determine if button pressed is right bracket button
            if (btn.getId() == R.id.buttonRightBracket) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_85);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_85);
                }
            }

            //Determine if button pressed is Memory R button
            if (btn.getId() == R.id.buttonMR) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_87);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_87);
                }
            }

            //Determine if button pressed is multiply button
            if (btn.getId() == R.id.buttonMultiply) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_112);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_112);
                }
            }

            //Determine if button pressed is seven button
            if (btn.getId() == R.id.buttonSeven) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_114);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_114);
                }
            }

            //Determine if button pressed is eight button
            if (btn.getId() == R.id.buttonEight) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_116);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_116);
                }
            }

            //Determine if button pressed is nine button
            if (btn.getId() == R.id.buttonNine) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_118);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_118);
                }
            }

            //Determine if button pressed is CLR button
            if (btn.getId() == R.id.buttonCLR) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_120);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_120);
                }
            }

            //Determine if button pressed is division button
            if (btn.getId() == R.id.buttonDivide) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_134);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_134);
                }
            }

                    /*//Determine if button pressed is division button
                    if (btn.getId() == R.id.buttonDivide) {
                        //button pressed and released animation
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_134);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            btn.setBackgroundResource(R.drawable.calc8darkmockup_134);
                        }
                    }*/

            //Determine if button pressed is FOUR button
            if (btn.getId() == R.id.buttonFour) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_136);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_136);
                }
            }

            //Determine if button pressed is FIVE button
            if (btn.getId() == R.id.buttonFive) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_138);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_138);
                }
            }

            //Determine if button pressed is six button
            if (btn.getId() == R.id.buttonSix) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_140);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_140);
                }
            }

            //Determine if button pressed is delete button
            if (btn.getId() == R.id.buttonDel) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_142);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_142);
                }
            }

            //Determine if button pressed is subtract button
            if (btn.getId() == R.id.buttonSubtraction) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_156);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_156);
                }
            }

            //Determine if button pressed is ONE button
            if (btn.getId() == R.id.buttonOne) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_158);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_158);
                }
            }

            //Determine if button pressed is TWO button
            if (btn.getId() == R.id.buttonTwo) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_160);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_160);
                }
            }

            //Determine if button pressed is THREE button
            if (btn.getId() == R.id.buttonThree) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_162);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_162);
                }
            }

            //Determine if button pressed is Addition button
            if (btn.getId() == R.id.buttonAddition) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_177);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_178);
                }
            }

            //Determine if button pressed is Decimal point button
            if (btn.getId() == R.id.buttonDecimalPoint) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_179);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_180);
                }
            }

            //Determine if button pressed is Zero button
            if (btn.getId() == R.id.buttonZero) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_181);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_182);
                }
            }

            //Determine if button pressed is PLUS MINUS button
            if (btn.getId() == R.id.buttonPlusMinus) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_183);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_184);
                }
            }

            //Determine if button pressed is EQUALS button
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
            int deviceWidth = displayMetrics.widthPixels;
            int deviceHeight = displayMetrics.heightPixels;

            if ((btn.getId() == R.id.buttonEquals) && (deviceWidth <= 320)) { //use different equals icon on wearable devices
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.microphone);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.microphone);
                }
            } else if (btn.getId() == R.id.buttonEquals) {
                //button pressed and released animation
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockuppressed_164);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundResource(R.drawable.calc8darkmockup_164);
                }
            }

            return false;
        }
    };

    /************
     * OnClickListener captures inputs from all 34 buttons and processes each button's function
     */
    protected View.OnClickListener handleButtons = new View.OnClickListener() {

        /*******
         * Controls calculator functions on each click
         * @param current - current is the active/current button clicked
         */
        public void onClick(View current) {
            ImageButton btn = (ImageButton) current;

            if (shouldVibrate) { feedback.vibrate(50); }

            calc8Muscles(btn);
        }
    };

    /********
     * Controls what happens to each calculator button when it is pressed.
     * This methd is called from the handleButtons onClickListener's onClick method above. Soli Deo Gloria
     *
     * @param btn
     */
    private void calc8Muscles(ImageButton btn) {
        //Determine if button pressed is SHIFT button
        if (btn.getId() == R.id.buttonShift) {
            shiftListener++; //increase shiftListener to 1 - indicates shift has been pressed
            enableShift(); //notifies user that shift is on

            if (shiftActive()) { //if shiftListener is greater than 1, assume user wants to disable shift
                disableShift();//turn off shift display
            }
        }

        //Determine if button pressed is DEGREE button
        if ((btn.getId() == R.id.buttonDeg)) {
            degListener++; //increase shiftListener to 1 - indicates shift has been pressed
            enableDeg();//notifies user that shift is on

            //determine if button pressed is factorial
            if ((btn.getId() == R.id.buttonDeg) && shiftListener != 0) {
                if ((display.getText().toString().contains("E"))) {//don't allow factorial calculation of big numbers
                    calculatorError("Number is too big to factorial!");
                } else if (TextUtils.isEmpty(display.getText()))//if empty, make screen 0! for better user experience
                    display.setText("0!");
                else
                    display.append("!");

                disableShift();
                disableDeg();
            } else if (degActive()) { //if shiftListener is greater than 1, assume user wants to disable degree mode
                disableDeg();
            }
        }

        //Determine if button pressed is RADIAN button
        if ((btn.getId() == R.id.buttonRad)) {
            radListener++; //increase radListener to 1 - indicates rad has been pressed
            enableRad();

            //determine if hype mode - turn up (LOL)
            if ((btn.getId() == R.id.buttonRad) && shiftListener != 0) {
                enableHype();//enable hyp mode
                disableShift();
                disableRad();

                if (hypActive()) {//if hyp mode is active, assume user wants to disable hyp mode
                    disableHype();//call disable hype function
                    disableShift();
                }
            } else if (radActive()) { //if radListener is greater than 1, assume user wants to disable rad
                disableRad();
            }
        }

        //Determine if button pressed is PI button
        if ((btn.getId() == R.id.buttonPi)) {
            start();

            //determine if button pressed is permutation
            if ((btn.getId() == R.id.buttonPi) && shiftListener != 0) {
                display.append(">");
                disableShift();
            } else
                display.append(Html.fromHtml("<font color=\"#70300E\">" + "π</font>"));
        }

        //Determine if button pressed is RANDOM button
        if ((btn.getId() == R.id.buttonRand)) {
            start();

            //determine if button pressed is combination
            if ((btn.getId() == R.id.buttonRand) && shiftListener != 0) {
                display.append("<");
                disableShift();
            } else
                display.append(Html.fromHtml("<font color=\"Black\">" + "random</font>"));
        }

        //Determine if button pressed is SIN button
        if ((btn.getId() == R.id.buttonSin)) {
            start();

            //determine if button pressed is SIN inverse
            if ((btn.getId() == R.id.buttonSin) && shiftListener != 0) {
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "asin" + "</font>"));
                disableShift();
            } else
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "sin" + "</font>"));

            //determine if button pressed is SIN hypotenus
            if (hypListener != 0) {
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "h" + "</font>"));
                disableShift();
            }

            //determine if button pressed is SIN radian
            if (radListener != 0) {
                if (!(display.getText().toString().endsWith("n") || display.getText().toString().endsWith("N")))
                    deleteNumber();

                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "d" + "</font>"));
                disableShift();
            }

        }

        //Determine if button pressed is COS button
        if ((btn.getId() == R.id.buttonCos)) {
            start();

            //determine if button pressed is COS inverse
            if ((btn.getId() == R.id.buttonCos) && shiftListener != 0) {
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "acos" + "</font>"));
                disableShift();
            } else
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "cos" + "</font>"));

            //determine if button pressed is COS hypotenus
            if (hypListener != 0) {
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "h" + "</font>"));
                disableShift();
            }

            //determine if button pressed is Cos radian
            if (radListener != 0) {
                if (!(display.getText().toString().endsWith("s") || display.getText().toString().endsWith("S")))
                    deleteNumber();

                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "d" + "</font>"));
                disableShift();
            }
        }

        //Determine if button pressed is TAN button
        if ((btn.getId() == R.id.buttonTan)) {
            start();

            //determine if button pressed is TAN inverse
            if ((btn.getId() == R.id.buttonTan && shiftListener != 0)) {
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "atan" + "</font>"));
                disableShift();
            } else
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "tan" + "</font>"));

            //determine if button pressed is TAN hypotenus
            if (hypListener != 0) {
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "h" + "</font>"));
                disableShift();
            }

            //determine if button pressed is TAN radian
            if (radListener != 0) {
                if (!(display.getText().toString().endsWith("n") || display.getText().toString().endsWith("N")))
                    deleteNumber();

                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "d" + "</font>"));
                disableShift();
            }
        }

        //Determine if button pressed is LOG button
        if ((btn.getId() == R.id.buttonLog)) {
            start();

            //determine if button pressed is LN
            if ((btn.getId() == R.id.buttonLog && shiftListener != 0)) {
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "ln" + "</font>"));
                disableShift();
            } else
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "log10" + "</font>"));
        }

        try {
            //Determine if button pressed is MemoryPLUS button
            if ((btn.getId() == R.id.buttonMPlus)) {
                //Determine if button pressed is Memory minus button
                if ((btn.getId() == R.id.buttonMPlus) && shiftListener != 0) {
                    deleteMemory();
                    Toast.makeText(MainActivity.this, MemoryFunction.lastElement().toString() + "removed from memory!", Toast.LENGTH_SHORT).show();
                    disableShift();
                } else {
                    MemoryFunction.push(Float.valueOf(result)); //add to stack

                    SharedPreferences pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE); //holds most recent value stored in memory
                    SharedPreferences.Editor edit = pref.edit(); // We need an editor object to make changes
                    edit.putString("memory", result);// Set/Store data
                    edit.commit();// Commit the changes
                    Toast.makeText(MainActivity.this, result + " added to memory!", Toast.LENGTH_SHORT).show(); //inform user of success
                }
            }
        } catch (Exception e) {
            calculatorError("Nothing to remove from memory!");
        }

        //Determine if button pressed is X SQUARED button
        if ((btn.getId() == R.id.buttonXSquared)) {
            //Determine if button pressed is y ^ x button
            if ((btn.getId() == R.id.buttonXSquared) && shiftListener != 0) {
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "^" + "</font>"));
                disableShift();
            } else
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "^2" + "</font>"));

        }

        //Determine if button pressed is Square root button
        if ((btn.getId() == R.id.buttonSquareRoot)) {
            start();

            //determine if button pressed is y sqrt x
            if ((btn.getId() == R.id.buttonSquareRoot) && shiftListener != 0) {
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "|" + "</font>"));
                disableShift();
            } else
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "sqrt" + "</font>"));
        }

        //Determine if button pressed is left bracket button
        if ((btn.getId() == R.id.buttonLeftBracket)) {
            start();

            //determine if button pressed is modulus ( % ) - percent
            if ((btn.getId() == R.id.buttonLeftBracket) && shiftListener != 0) {
                display.append(Html.fromHtml("<font color=\"#6F848E\">" + "%" + "</font>"));
                disableShift();
            } else
                display.append(Html.fromHtml("<font color=\"#777777\">" + "(" + "</font>"));
        }

        //Determine if button pressed is right bracket button
        if ((btn.getId() == R.id.buttonRightBracket)) {
            start();

            //determine if button pressed is e
            if ((btn.getId() == R.id.buttonRightBracket) && shiftListener != 0) {
                display.append(Html.fromHtml("<font color=\"#70300E\">" + "e" + "</font>"));
                disableShift();
            } else
                display.append(Html.fromHtml("<font color=\"#777777\">" + ")" + "</font>"));
        }

        try {
            //Determine if button pressed is Memory R button
            if ((btn.getId() == R.id.buttonMR)) {
                start();

                //Determine if button pressed is Memory clear button or memory recall button
                if ((btn.getId() == R.id.buttonMR) && shiftListener != 0) {
                    //MemoryFunction.clear(); //clear memory
                    deleteMemory();
                    Toast.makeText(MainActivity.this, "Memory Cleared!", Toast.LENGTH_SHORT).show();
                    disableShift();
                } else {
                    SharedPreferences pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE); //holds most recent value stored in memory
                    String memoryRecall = pref.getString("memory", "0");

                    if (display.getText().toString().endsWith("*") || display.getText().toString().endsWith("/") ||
                            display.getText().toString().endsWith("-") || display.getText().toString().endsWith("+")) {
                        display.append(memoryRecall); //memory recall
                    } else {
                        display.append(memoryRecall); //memory recall
                        //display.setText(MemoryFunction.lastElement().toString()); //memory recall
                    }
                }
            }
        } catch (Exception e) {
            //calculatorError(e.getMessage().toString()); //for debugging
            calculatorError("Memory is already empty!");
        }

        /*********************
         * call help activity
         */
        if ((btn.getId() == R.id.buttonEquals) && shiftListener != 0) {
            Intent intent = new Intent(MainActivity.this, Calc8HelpActivity.class);
            startActivity(intent);
            disableShift();
        }

        //Determine if button pressed is multiply button
        if (btn.getId() == R.id.buttonMultiply) {
            decimalPointCount = 0;
            if (TextUtils.isEmpty(display.getText())) {
                display.setText(Html.fromHtml("0" + "<font color=\"#000\">" + "*" + "</font>"));
            } else {
                display.append(Html.fromHtml("<font color=\"#000\">" + "*" + "</font>"));
            }
        }

        //Determine if button pressed is seven button
        if (btn.getId() == R.id.buttonSeven) {
            display.append(btn.getContentDescription());
            start();
        }

        //Determine if button pressed is eight button
        if (btn.getId() == R.id.buttonEight) {
            display.append(btn.getContentDescription());
            start();
        }

        //Determine if button pressed is nine button
        if (btn.getId() == R.id.buttonNine) {
            display.append(btn.getContentDescription());
            start();
        }

        //Determine if button pressed is CLR button
        if (btn.getId() == R.id.buttonCLR) {
            clr();
        }

        //Determine if button pressed is division button
        if (btn.getId() == R.id.buttonDivide) {
            decimalPointCount = 0;
            if (TextUtils.isEmpty(display.getText())) {
                display.setText(Html.fromHtml("0" + "<font color=\"#000\">" + "/" + "</font>"));
            } else {
                display.append(Html.fromHtml("<font color=\"#000\">" + "/" + "</font>"));
            }
        }

        //Determine if button pressed is FOUR button
        if (btn.getId() == R.id.buttonFour) {
            display.append(btn.getContentDescription());
            start();
        }

        //Determine if button pressed is FIVE button
        if (btn.getId() == R.id.buttonFive) {
            display.append(btn.getContentDescription());
            start();
        }

        //Determine if button pressed is six button
        if (btn.getId() == R.id.buttonSix) {
            display.append(btn.getContentDescription());
            start();
        }

        //Determine if button pressed is delete button
        if (btn.getId() == R.id.buttonDel) {
            deleteNumber();
        }

        //Determine if button pressed is subtract button
        if (btn.getId() == R.id.buttonSubtraction) {
            decimalPointCount = 0;
            if (TextUtils.isEmpty(display.getText())) {
                display.setText(Html.fromHtml("0" + "<font color=\"#000\">" + "-" + "</font>"));
            } else {
                display.append(Html.fromHtml("<font color=\"#000\">" + "-" + "</font>"));
            }
        }

        //Determine if button pressed is ONE button
        if (btn.getId() == R.id.buttonOne) {
            display.append(btn.getContentDescription());
            start();
        }

        //Determine if button pressed is TWO button
        if (btn.getId() == R.id.buttonTwo) {
            display.append(btn.getContentDescription());
            start();
        }

        //Determine if button pressed is THREE button
        if (btn.getId() == R.id.buttonThree) {
            display.append(btn.getContentDescription());
            start();
        }

        //Determine if button pressed is Addition button
        if (btn.getId() == R.id.buttonAddition) {
            decimalPointCount = 0;
            if (TextUtils.isEmpty(display.getText()))
                display.setText(Html.fromHtml("0" + "<font color=\"#000\">+" + "</font>"));
            else
                display.append(Html.fromHtml("<font color=\"#000\">" + "+" + "</font>"));
        }

        //Determine if button pressed is Decimal point button
        if (btn.getId() == R.id.buttonDecimalPoint) {

            if (TextUtils.isEmpty(display.getText())) {
                display.setText("0.");
                decimalPointCount++;
            } else {
                display.append(".");
                decimalPointCount++;
            }

            /*if (!display.getText().toString().contains(".")) {
                display.append(".");
                decimalPointCount++;
            }


            else
                display.append(".");*/

            /*else {
                if (decimalPointCount>=1)
                    display.append("");
            }*/

            if (display.getText().toString().contains("+") || display.getText().toString().contains("-")
                    || display.getText().toString().contains("*") || display.getText().toString().contains("/")) {
                if (decimalPointCount >= 1) {
                    display.append(".");
                }
            }
        }

        //Determine if button. pressed is Zero button
        if (btn.getId() == R.id.buttonZero) {
            if (TextUtils.isEmpty(display.getText())) {
                display.setText("");
            } else
                display.append("0");
        }

        //Determine if button pressed is PLUS MINUS button
        if (btn.getId() == R.id.buttonPlusMinus) {
            if (TextUtils.isEmpty(display.getText())) {
                display.setText(Html.fromHtml("<font color=\"#6F848E\">" + "-" + "</font>"));
            }
            if (display.getText().toString().equals("-")) {
                display.setText("-");
            } else
                display.append("-");
        }

        try {
            //if equals button is pressed, perform calculation and get result
            if (btn.getId() == R.id.buttonEquals) {
                disableShift();//first disable shift display
                decimalPointCount = 0;
                backup = display.getText().toString();

                if (TextUtils.isEmpty(display.getText().toString())) {
                    clr();
                } else {
                    //new Calc8ResultsTask().execute(display.getText().toString()); calculate in thread not working properly
                    Runnable runnable = new Runnable() {
                        public void run() {
                            Runnable runnable2 = new Runnable() {
                                public void run() {
                                    result = calc8(display.getText().toString());
                                }
                            };
                            runnable2.run();

                            updateHistory(backup);
                            updateLCD(result);
                            calculating = true;//resets start

                            Runnable runnable1 = new Runnable() {
                                public void run() {
                                    if (calc8AdsRandom() >= 22) {
                                        displayAd();
                                    }
                                }
                            };
                            runnable1.run();//create ad on new thread

                        }
                    };
                    runnable.run();
                }
            }
                /*else {
                    calculating = false;
                }*/
        } //catch any exception that might occur when numbers are being entered
        catch (Exception e) {
            calculatorError("");//generic error when calculating because of either incomplete calculations, etc.
        }
    }

    /************
     * deletes whatever is stored in calc8's memory by replacing the shared preference variable's content with '0' - zero.
     */
    private void deleteMemory() {
        SharedPreferences pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE); //holds most recent value stored in memory
        SharedPreferences.Editor edit = pref.edit(); // We need an editor object to make changes
        edit.putString("memory", "0");// Set/Store data
        edit.commit();// Commit the changes
    }

    private class Calc8ResultsTask extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        String calculation;

        @Override
        protected void onPreExecute() {
            //showLoadingDialog();
            calculation = display.getText().toString();
        }

        protected String doInBackground(String... localResult) {
            result = localResult[0];
            try {
                result = calc8(calculation); //get the result of calculation
            } catch (Exception e) {
                calculatorError("error while calculating in background thread");
            }
            return result;
        }

        protected void onPostExecute(String... finalResult) {
            result = finalResult[0];
            dismissLoadingDialog();
            Log.d("TAG", "Entered onPostExecute");
        }

        private void showLoadingDialog() {
            if (dialog == null) {
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setProgressStyle(R.style.MyAlertDialogStyle);
                dialog.setTitle("Please wait...");
                dialog.setMessage("I\'m Calc⁸ing...");
                dialog.setIndeterminate(true);
                dialog.show();
            }
            dialog.show();
        }

        public void dismissLoadingDialog() {
            if (dialog != null && dialog.isShowing()) {
                dialog.cancel();
                dialog.dismiss();
            }
        }
    }

    private boolean shiftActive() {
        return shiftListener > 1;
    }

    private void enableShift() {
        activeF1.setAlpha(1); //set opacity of shift display to 1
    }

    private void disableShift() {
        activeF1.setAlpha((float) 0.2); //set opacity of shift display to 0.2
        shiftListener = 0;
    }

    private boolean degActive() {
        return degListener > 1;
    }

    private void enableDeg() {
        activeF2.setAlpha(1); //set opacity of degree display to 1
    }

    private void disableDeg() {
        activeF2.setAlpha((float) 0.2); //set opacity of degree display to 0.2
        degListener = 0;
    }

    private boolean radActive() {
        return radListener > 1;
    }

    private void enableRad() {
        activeF3.setAlpha(1); //set opacity of rad display to 1
    }

    private void disableRad() {
        activeF3.setAlpha((float) 0.2); //set opacity of rad display to 0.2
        radListener = 0;
    }

    private boolean hypActive() {
        return hypListener > 1;
    }

    private void enableHype() {
        activeF4.setAlpha(1); //set opacity of hyp display to 1
        hypListener++;
    }

    private void disableHype() {
        activeF4.setAlpha((float) 0.2); //set opacity of hyp display to 0.2
        hypListener = 0;
    }

    protected View.OnLongClickListener handleSpeech = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ImageButton btn = (ImageButton) v;


            if (shouldVibrate) {
                feedback.vibrate(200);
            }

            if (btn.getId() == R.id.buttonShift) {
                try {
                    //if shift button is pressed, disable or enable vibration/haptic feedback 
                    enableDisableVibrate();
                    disableShift();
                } //catch any exception that might occur when numbers are being entered
                catch (Exception e) {
                    calculatorError("");//generic error when calculating because of either incomplete calculations, etc.
                }
            }
            
            else {
            /* if (speech.)
            speech.destroy();*/

                speech = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
                speech.setRecognitionListener(MainActivity.this);


                recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplication().getPackageName());
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                //recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(1000));
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

                //if (!mIsListening) {
                mIsListening = true;
                speech.startListening(recognizerIntent);
                //}

                Log.d(LOG_TAG, "Start listening to input");
                
            }
            
            return false;
        }
    };

    private void enableDisableVibrate() {
        if (shouldVibrate) {
            disableShift();


            /*//Save information that this application has run for the first time
            SharedPreferences settings = getSharedPreferences("vibrate", 0);
            SharedPreferences.Editor edit = settings.edit();
            edit.putBoolean("shouldVibrate", true);
            edit.commit(); //apply

            //*/shouldVibrate = false;
            Toast.makeText(MainActivity.this, "Calc⁸ buttons will no longer vibrate!", Toast.LENGTH_SHORT).show();
        }
        else {
            disableShift();
            /*//Save information that this application has run for the first time
            SharedPreferences settings = getSharedPreferences("vibrate", 0);
            SharedPreferences.Editor edit = settings.edit();
            edit.putBoolean("shouldVibrate", false);
            edit.commit(); //apply

            //*/shouldVibrate = true;
            Toast.makeText(MainActivity.this, "Calc⁸ buttons will vibrate!", Toast.LENGTH_SHORT).show();
        }
    }

    protected View.OnLongClickListener handleDisplay = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (shouldVibrate) { feedback.vibrate(200); }

            String clipData = display.getText().toString();

            //adding data to clipboard
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Calc8 result", clipData);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(MainActivity.this, "Calc⁸ added " +clipData +" to Clipboard!", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "Written data to clipboard");

            return false;
        }
    };

    private String calc8(String passedExpression) {
        Expression e;
        e = new ExpressionBuilder(passedExpression)
                .operator(combination)
                .operator(permutation)
                .operator(factorial)
                .operator(percentage)
                .operator(customRoot)
                .function(ln)
                .function(sindFunc)
                .function(cosdFunc)
                .function(tandFunc)
                .variables("π", "random", "e")
                .build()
                .setVariable("π", Math.PI)
                .setVariable("e", Math.E)
                .setVariable("random", calc8random());

        result = String.valueOf(e.evaluate());

        //removes .0 if calculation has integer result
        if (result.endsWith(".0")) {
            result = result.substring(0, result.length() - 2);
        }

        //trying to change color of 'E' whenever it's encountered in big/large number results
        int ePosition = result.indexOf("E");
        final char[] mChars = result.toCharArray();

        final StringBuilder builder = new StringBuilder(result.length());
        for (int i = 0; i < result.length(); i++) {
            String current = String.valueOf(mChars[i]);
            if (current.contains("E") || current.contains("e")) {
                builder.append(wrapInColor("#466943", current));
            } else {
                builder.append(current);
            }
        }

        return Html.fromHtml(builder.toString()).toString();
    }

    private double calc8random() {
        return (Math.random() * 7777777) + 1;
    }


    private int calc8AdsRandom() {
        return (int) (Math.random() * 26) + 1;
    }

    Function sindFunc = new Function("sind", 1) {
        @Override
        public double apply(double... args) {
            return Math.sin(Math.toRadians(args[0]));
        }
    };

    Function cosdFunc = new Function("cosd", 1) {
        @Override
        public double apply(double... args) {
            return Math.cos(Math.toRadians(args[0]));
        }
    };

    Function tandFunc = new Function("tand", 1) {
        @Override
        public double apply(double... args) {
            return Math.tan(Math.toRadians(args[0]));
        }
    };

    Function ln = new Function("ln", 1) {

        @Override
        public double apply(double... args) {
            int R = (int) args[0];

            if ((double) R != args[0]) {
                calculatorError("");
            }
            if (R < 0) {
                calculatorError("Cannot calculate ln of negative numbers!");
            }


            return Math.log(R);
        }
    };

    Operator combination = new Operator("<", 2, true, Operator.PRECEDENCE_POWER + 1) {

        @Override
        public double apply(double... args) {
            int R = (int) args[0];
            int N = (int) args[1];

            if (((double) R != args[0]) || ((double) N != args[0])) {
                calculatorError("");
            }
            if ((R < 0) || (N < 0)) {
                calculatorError("");
            }


            return Double.parseDouble(choose(R, N).toString());
        }
    };

    public static BigInteger choose(int x, int y) {
        if (y < 0 || y > x) return BigInteger.ZERO;
        if (y == 0 || y == x) return BigInteger.ONE;

        BigInteger answer = BigInteger.ONE;
        for (int i = x - y + 1; i <= x; i++) {
            answer = answer.multiply(BigInteger.valueOf(i));
        }
        for (int j = 1; j <= y; j++) {
            answer = answer.divide(BigInteger.valueOf(j));
        }
        return answer;
    }

    Operator customRoot = new Operator("|", 2, true, Operator.PRECEDENCE_POWER + 1) {

        @Override
        public double apply(double... args) {
            double ans;
            double R = args[0];
            double N = args[1];

            ans = Math.pow(N, 1 / R);

            return ans;
        }
    };


    Operator factorial = new Operator("!", 1, true, Operator.PRECEDENCE_POWER + 1) {

        @Override
        public double apply(double... args) {
            final int arg = (int) args[0];
            if ((double) arg != args[0]) {
                calculatorError("");
            }
            if (arg < 0) {
                calculatorError("");
            }
            double result = 1;
            for (int i = 1; i <= arg; i++) {
                result *= i;
            }

            return CombinatoricsUtils.factorialDouble(arg);
        }
    };

    Operator percentage = new Operator("%", 1, true, Operator.PRECEDENCE_POWER + 1) {

        @Override
        public double apply(double... args) {
            return args[0] / 100;
        }
    };

    Operator permutation = new Operator(">", 2, true, Operator.PRECEDENCE_POWER + 1) {

        @Override
        public double apply(double... args) {
            final int arg = (int) args[0];
            final int arg2 = (int) args[1];

            if ((double) arg != args[0]) {
                calculatorError("");
            }
            if (arg < 0) {
                calculatorError("");
            }
            double result = 1;
            for (int i = 1; i <= arg; i++) {
                result *= i;
            }

            if ((double) arg2 != args[1]) {
                calculatorError("");
            }
            if (arg2 < 0) {
                calculatorError("");
            }
            double result2 = 1;
            for (int i = 1; i <= arg2; i++) {
                result2 *= i;
            }
            return (result / result2);
        }
    };

    public String wrapInColor(String color, String toWrap) {
        return "<font color='" + color + "'>" + toWrap + "</font>";
    }

    /************
     * Deletes the last number on the display
     */
    private void deleteNumber() {
        if (display.getText().toString().isEmpty() || display.getText().toString().length() == 0) {
            display.setText("");//display error if user tries to delete nothing
        } else {
            //using Strings substring function, delete the last character in string
            display.setText(display.getText().toString().substring(0, display.getText().toString().length() - 1));
        }
    }


    /*****************
     * updates the history display for current calculation
     *
     * @param lBackup - holds the data to be placed on the calculation history textview
     */
    private void updateHistory(String lBackup) {
        if (lBackup.contains("E")) {
            lBackup.replace("E", "<font color=\"#662015\">E</font>");
        }

        if (lBackup.contains("e")) {
            lBackup.replace("e", "<font color=\"#662015\">E</font>");
        }

        //display.setText(mBackup);
        hDisplay.setText(Html.fromHtml(lBackup));
        //hDisplay.setText(lBackup);
    }

    /*****************
     * updates the main screen's display for current calculation
     *
     * @param mBackup - holds the data to be placed on the main LCD textview
     */
    private void updateLCD(String mBackup) {
        /*if (mBackup.contains("E")) {
            mBackup.replace("E", "<font color=\"#662015\">E</font>");
            Log.d(LOG_TAG, "replaced E");
        }

        if (mBackup.contains("e")) {
            mBackup.replace("e", "<font color=\"#662015\">e</font>");
            Log.d(LOG_TAG, "replaced e");
        }*/

        display.setText(mBackup);
        //display.setText(Html.fromHtml(mBackup));
        //display.setHint(Html.fromHtml(builder.toString()).toString());

        //calculating=false;
        resetError();

        if(mBackup.equals(""))
            mBackup = "0";

        updateCalc8Wear(mBackup);
    }

    private void updateCalc8Wear(String mBackup) {
        int notificationId = 001;
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        viewIntent.putExtra(EXTRA_EVENT_ID, "");
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.calc8logo2small100px)
                        .setContentTitle("Calc8 Result:")
                        .setContentText(mBackup)
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    /********************
     * Notifies the user that an error has occurred by setting the opacity/alpha of activeF5 to 1
     * Receives error message as string. If string received is empty, no toast is own, else, string is toasted to user.
     *
     * @return boolean true
     */
    private boolean calculatorError(String toastErrorMessage) {
        activeF5.setAlpha((float) 1); //set error display message visible
        if (toastErrorMessage != null && toastErrorMessage != "")
            Toast.makeText(MainActivity.this, toastErrorMessage, Toast.LENGTH_SHORT).show();//toast appropriate error message
        return true;
    }

    /********************
     * Clears the error display back to default by resetting the opacity of activeF5 to 0.1
     */
    private void resetError() {
        activeF5.setAlpha((float) 0.1); //set error display
    }

    /******************
     * CLR Button
     * Resets the display to zero, and resets all important variables to 0
     */
    private void clr() {
        display.setHint("0");
        start = 0;
        decimalPointCount = 0;
        result = "0";
        number1 = 0;
        number2 = 0;
        current = '\0';
        //updateHistory("");//resets history display
        updateLCD("");//resets main display
        resetError();//resets error display
        calculating = false;
    }

    /**********
     * Determines if calculation has started
     *
     * @return boolean
     */
    private boolean start() {
        if (start == 0) {
            resetError();//reset error message display
            start = 1; //start=1 means calculation has started
            if (calculating) {
                updateLCD("");
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (speech != null)
            speech.destroy();//destroy speech listener

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        /* SnackBar.make(findViewById(android.R.id.content).getRootView(), "Press BACK again to exit", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show(); */
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }
}